package main.operations.revision;

import main.operations.auxiliars.AxiomGenerators;
import main.operations.auxiliars.HumanReadableAxiomExpressionGenerator;
import main.operations.blackbox.kernel.RevisionKernelBuilder;
import main.operations.selectionfunctions.SelectionFunction;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a Belief Revision operation called Revision.
 *
 * The resulting set must have the formula α and can not be inconsistent.
 *
 * @author Luís F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 *
 */
public class Revisor {

    private OWLOntologyManager manager;
    private ReasonerFactory reasonerFactory;
    private SelectionFunction sigma;
    private Integer success;
    private Boolean coreRetainment;

    private int maxQueueSize;
    private int maxSetElements;

    public Revisor(OWLOntologyManager manager, ReasonerFactory reasonerFactory, SelectionFunction sigma,
                   Integer success, Boolean coreRetainment) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.sigma = sigma;
        this.success = success;
        this.coreRetainment = coreRetainment;
    }

    /**
     * Executes the revision operation on the ontology.
     *
     * @param ontology
     *            the initial ontology
     * @param sentence
     *            the sentence to be revised
     * @return the resulting belief set, consistent and implying the sentence
     * @throws OWLException
     *             OWLException
     */
    public Set<OWLAxiom> revise(OWLOntology ontology, OWLAxiom sentence)
            throws OWLOntologyChangeException, OWLException {
        if (Logger.getLogger("RV").isLoggable(Level.FINE)) {
            Logger.getLogger("RV").log(Level.FINE,
                    "\n---------- ORIGINAL ONTOLOGY: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(ontology.getAxioms()));
            Logger.getLogger("RV").log(Level.FINE,
                    "\n---------- FORMULA TO BE REVISED: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpression(sentence));
        }
        // create reasoner
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        manager.addAxiom(ontology, sentence);
        // close under Cn
        OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = AxiomGenerators.allAxiomGenerators();
        InferredOntologyGenerator ontologyGenerator = new InferredOntologyGenerator(
                reasoner, gens);
        ontologyGenerator.fillOntology(manager.getOWLDataFactory(), inferredOntology);
        manager.addAxioms(inferredOntology, ontology.getAxioms()); // keep asserted axioms
        if (Logger.getLogger("RV").isLoggable(Level.FINE)) {
            Logger.getLogger("RV").log(Level.FINE,
                    "\n---------- ONTOLOGY CLOSED UNDER Cn: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(
                                    inferredOntology.getAxioms()));
        }
        // obtain set
        RevisionKernelBuilder revisionKernelBuilder = new RevisionKernelBuilder(manager, reasonerFactory);
        revisionKernelBuilder.setMaxQueueSize(maxQueueSize);
        revisionKernelBuilder.setMaxKernelElements(maxSetElements);
        Set<Set<OWLAxiom>> revisionSet = revisionKernelBuilder.kernelSet(inferredOntology.getAxioms(), sentence);
        
        // apply a selection function
        Set<Set<OWLAxiom>> best = sigma.select(ontology, revisionSet);
        if (Logger.getLogger("RV").isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder(
                    "\n---------- " + (best.size()) + " SELECTED ELEMENT"
                            + (best.size() != 1 ? "S" : "") + ": \n");
            int i = 0;
            for (Set<OWLAxiom> s : best) {
                sb.append(String.format("\n[%d/%d]:\n", ++i, best.size())
                        + HumanReadableAxiomExpressionGenerator
                        .generateExpressionForSet(s));
            }
            Logger.getLogger("RV").log(Level.FINER, sb.toString());
        }

        // removes set of axioms from the ontology
        Iterator<Set<OWLAxiom>> it = best.iterator();
        Set<OWLAxiom> toRemove = it.next();
        Set<OWLAxiom> axioms = ontology.getAxioms();
        axioms.removeAll(toRemove);

        if (Logger.getLogger("RV").isLoggable(Level.FINE)) {
            Logger.getLogger("RV").log(Level.FINE,
                    "\n---------- FINAL ONTOLOGY: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(axioms));
        }

        return axioms;
    }


    /**
     * Finishes the operation according to the success
     *
     * @param revisionSet
     *            the set that represents the kernel (or remainder0
     * @param sentence
     *            the sentence to perform revision
     * @return revisionSet
     *            the altered form of the input
     */
    private Set<Set<OWLAxiom>> checkForSuccess(Set<Set<OWLAxiom>> revisionSet, OWLAxiom sentence) {
        if (success > 0) {
            for (Set<OWLAxiom> X: revisionSet) {
                if (X.contains(sentence)) {
                    X.remove(sentence);

                    if(X.isEmpty()){
                        if (success == 2) {
                            X.add(sentence);
                        }
                        else if (success == 1) {
                            revisionSet.remove(X);
                        }
                    }
                }
            }
        }

        return revisionSet;
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
    public int getMaxSetElements() {
        return maxSetElements;
    }

    /**
     *
     * Sets the maximum number of elements in the computed remainder set.
     *
     * @param maxSetElements
     *            the maximum size of the computed remainder set
     */
    public void setMaxSetElements(int maxSetElements) {
        this.maxSetElements = maxSetElements;
    }
}
