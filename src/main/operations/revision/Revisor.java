package main.operations.revision;

import main.operations.auxiliars.AxiomGenerators;
import main.operations.auxiliars.HumanReadableAxiomExpressionGenerator;
import main.operations.blackbox.kernel.RevisionKernelBuilder;
import main.operations.incisionfunction.IncisionFunction;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a Belief Revision operation called Kernel Revision.
 *
 * The operation is defined as:
 *
 * B \ σ(K ↓↓ α),
 *
 * where σ is a incision function that chooses at least one element of the kernel set,
 * denoted by ↓↓.
 *
 * The resulting set must have the formula α and can not be inconsistent.
 *
 * @author Luís F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 *
 */
public class Revisor {

    /**
     * A factory that constructs the reasoner.
     */
    private ReasonerFactory reasonerFactory;

    /**
     * The ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * An incision function implementation.
     */
    private IncisionFunction sigma;

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the kernel set that will be computed.
     */
    private int maxSetElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     * @param sigma
     *            an incision function implementation
     */
    public Revisor(OWLOntologyManager manager, ReasonerFactory reasonerFactory, IncisionFunction sigma) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.sigma = sigma;
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
        Set<OWLAxiom> best = sigma.incise(ontology, revisionSet);
        if (Logger.getLogger("RV").isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder(
                    "\n---------- " + (best.size()) + " SELECTED ELEMENT"
                            + (best.size() != 1 ? "S" : "") + ": \n");
            sb.append(String.format("\n[%d/%d]:\n", best.size())
                    + HumanReadableAxiomExpressionGenerator
                    .generateExpressionForSet(best));
            Logger.getLogger("RV").log(Level.FINER, sb.toString());
        }

        // removes set of axioms from the ontology
        Set<OWLAxiom> axioms = ontology.getAxioms();
        axioms.removeAll(best);

        // adds sentence to revise back, to guarantee success
        axioms.add(sentence);

        if (Logger.getLogger("RV").isLoggable(Level.FINE)) {
            Logger.getLogger("RV").log(Level.FINE,
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
    public int getMaxSetElements() {
        return maxSetElements;
    }

    /**
     *
     * Sets the maximum number of elements in the computed kernel set.
     *
     * @param maxSetElements
     *            the maximum size of the computed kernel set
     */
    public void setMaxSetElements(int maxSetElements) {
        this.maxSetElements = maxSetElements;
    }
}
