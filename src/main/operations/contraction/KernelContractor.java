package main.operations.contraction;

import main.operations.auxiliars.AxiomGenerators;
import main.operations.auxiliars.HumanReadableAxiomExpressionGenerator;
import main.operations.blackbox.kernel.KernelBuilder;
import main.operations.selectionfunctions.SelectionFunction;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a Belief Revision operation called Contraction, using the Kernel Constructor.
 *
 * It is defined as:
 *
 * K \ σ(K ⊥⊥ α),
 *
 * where σ is a incision function that chooses at least one element of the kernel set,
 * denoted by ⊥⊥.
 *
 * The resulting set must not imply the formula α.
 *
 * @author Luis F. de M. C. Silva (inspired by Vinícius B. Matos)
 */
public class KernelContractor {
    private OWLOntologyManager manager;
    private ReasonerFactory reasonerFactory;
    private SelectionFunction sigma;

    private Integer maxKernelElements;
    private Integer maxQueueSize;

    public KernelContractor(OWLOntologyManager manager, ReasonerFactory reasonerFactory, SelectionFunction sigma) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.sigma = sigma;
    }

    /**
     * Executes the kernel contraction operation on the ontology.
     *
     * @param ontology
     *            the initial ontology
     * @param sentence
     *            the sentence to be kernel contracted
     * @return the resulting belief set, not implying the sentence
     * @throws OWLException
     *             OWLException
     */
    public Set<OWLAxiom> kernelContract(OWLOntology ontology, OWLAxiom sentence)
            throws OWLException {
        if (Logger.getLogger("KC").isLoggable(Level.FINE)) {
            Logger.getLogger("KC").log(Level.FINE,
                    "\n---------- ORIGINAL ONTOLOGY: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(ontology.getAxioms()));
            Logger.getLogger("KC").log(Level.FINE,
                    "\n---------- FORMULA TO BE CONTRACTED: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpression(sentence));
        }
        // create reasoner
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        // close under Cn
        OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = AxiomGenerators.allAxiomGenerators();
        InferredOntologyGenerator ontologyGenerator = new InferredOntologyGenerator(
                reasoner, gens);
        ontologyGenerator.fillOntology(manager.getOWLDataFactory(), inferredOntology);
        manager.addAxioms(inferredOntology, ontology.getAxioms()); // keep asserted axioms
        if (Logger.getLogger("KC").isLoggable(Level.FINE)) {
            Logger.getLogger("KC").log(Level.FINE,
                    "\n---------- ONTOLOGY CLOSED UNDER Cn: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(
                                    inferredOntology.getAxioms()));
        }
        // obtain kernel
        KernelBuilder kernelBuilder = new KernelBuilder(
                OWLManager.createOWLOntologyManager(), reasonerFactory);
        kernelBuilder.setMaxQueueSize(maxQueueSize);
        kernelBuilder.setMaxKernelElements(maxKernelElements);
        Set<OWLAxiom> kb = inferredOntology.getAxioms();
        if (kb.isEmpty())
            throw new OWLException("The reasoner has failed to find the logic closure.");
        Set<Set<OWLAxiom>> kernelSet = kernelBuilder.kernelSet(kb, sentence);
        // apply a selection function
        Set<Set<OWLAxiom>> best = sigma.select(ontology, kernelSet);
        if (Logger.getLogger("KC").isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder(
                    "\n---------- " + (best.size()) + " SELECTED KERNEL ELEMENT"
                            + (best.size() != 1 ? "S" : "") + ": \n");
            int i = 0;
            for (Set<OWLAxiom> s : best) {
                sb.append(String.format("\n[%d/%d]:\n", ++i, best.size())
                        + HumanReadableAxiomExpressionGenerator
                        .generateExpressionForSet(s));
            }
            Logger.getLogger("KC").log(Level.FINER, sb.toString());
        }

        // removes set of axioms from the ontology
        Iterator<Set<OWLAxiom>> it = best.iterator();
        Set<OWLAxiom> toRemove = it.next();
        Set<OWLAxiom> axioms = ontology.getAxioms();
        axioms.removeAll(toRemove);

        if (Logger.getLogger("KC").isLoggable(Level.FINE)) {
            Logger.getLogger("KC").log(Level.FINE,
                    "\n---------- FINAL ONTOLOGY: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(axioms));
        }

        return axioms;
    }

    /**
     * Sets the capacity of the queue used by the algorithm.
     *
     * @param maxQueueSize
     *            the limit of the size of the queue
     *
     */
    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * Gets the capacity of the queue used by the algorithm.
     *
     * @return the limit of the size of the queue
     *
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     *
     * Gets the maximum number of elements in the computed kernel set.
     *
     * @return the maximum size of the computed kernel set
     */
    public int getMaxKernelElements() {
        return maxKernelElements;
    }

    /**
     *
     * Sets the maximum number of elements in the computed kernel set.
     *
     * @param maxKernelElements
     *            the maximum size of the computed kernel set
     */
    public void setMaxKernelElements(int maxKernelElements) {
        this.maxKernelElements = maxKernelElements;
    }

}
