package main.operations.contraction;

import main.operations.HumanReadableAxiomExpressionGenerator;
import main.operations.SelectionFunction;
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
 * The resulting set must not imply the formula α.
 *
 * @author Luis F. de M. C. Silva (inspired by Vinícius B. Matos)
 */
public class KernelContractor {
    private OWLOntologyManager manager;
    private ReasonerFactory reasonerFactory;
    private SelectionFunction gamma;

    private Integer maxRemainderElements;
    private Integer maxQueueSize;

    public KernelContractor(OWLOntologyManager manager, ReasonerFactory reasonerFactory, SelectionFunction gamma) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.gamma = gamma;
    }

    /**
     * Executes the kernel contraction operation on the ontology.
     *
     * @param ontology
     *            the initial ontology
     * @param sentence
     *            the sentence to be pseudo-contracted
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
        // close under Cn*
        OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = allAxiomGenerators();
        InferredOntologyGenerator ontologyGenerator = new InferredOntologyGenerator(
                reasoner, gens);
        ontologyGenerator.fillOntology(manager.getOWLDataFactory(), inferredOntology);
        manager.addAxioms(inferredOntology, ontology.getAxioms()); // keep asserted axioms
        if (Logger.getLogger("KC").isLoggable(Level.FINE)) {
            Logger.getLogger("KC").log(Level.FINE,
                    "\n---------- ONTOLOGY CLOSED UNDER Cn*: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(
                                    inferredOntology.getAxioms()));
        }
        // obtain remainder
        KernelBuilder kernelBuilder = new KernelBuilder(
                OWLManager.createOWLOntologyManager(), reasonerFactory);
        Set<OWLAxiom> kb = inferredOntology.getAxioms();
        if (kb.isEmpty())
            throw new OWLException("The reasoner has failed to find the logic closure.");
        Set<Set<OWLAxiom>> kernelSet = kernelBuilder.kernelSet(kb, sentence);
        // apply a selection function
        Set<Set<OWLAxiom>> best = gamma.select(ontology, kernelSet);
        if (Logger.getLogger("KC").isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder(
                    "\n---------- " + (best.size()) + " SELECTED REMAINDER ELEMENT"
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
     * Returns the axiom generators that will be used by the reasoner to close the
     * belief set under its consequence operator.
     *
     * @return the list of axiom generators
     */
    private static List<InferredAxiomGenerator<? extends OWLAxiom>> allAxiomGenerators() {
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        // classes
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        // individuals
        gens.add(new InferredPropertyAssertionGenerator());
        return gens;
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
     * Gets the maximum number of elements in the computed remainder set.
     *
     * @return the maximum size of the computed remainder set
     */
    public int getMaxRemainderElements() {
        return maxRemainderElements;
    }

    /**
     *
     * Sets the maximum number of elements in the computed remainder set.
     *
     * @param maxRemainderElements
     *            the maximum size of the computed remainder set
     */
    public void setMaxRemainderElements(int maxRemainderElements) {
        this.maxRemainderElements = maxRemainderElements;
    }

}
