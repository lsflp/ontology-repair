package main.operations.contraction;

import main.operations.auxiliars.AxiomGenerators;
import main.operations.auxiliars.HumanReadableAxiomExpressionGenerator;
import main.operations.blackbox.kernel.KernelBuilder;
import main.operations.incisionfunction.IncisionFunction;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.List;
import java.util.Set;
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
    private Integer maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the kernel set that will be computed.
     */
    private Integer maxKernelElements = Integer.MAX_VALUE;

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
    public KernelContractor(OWLOntologyManager manager, ReasonerFactory reasonerFactory, IncisionFunction sigma) {
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
        Set<OWLAxiom> best = sigma.incise(ontology, kernelSet);
        if (Logger.getLogger("KC").isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder(
                    "\n---------- " + (best.size()) + " SELECTED KERNEL ELEMENT"
                            + (best.size() != 1 ? "S" : "") + ": \n");
            int i = 0;
            sb.append(String.format("\n[%d/%d]:\n", ++i, best.size())
                    + HumanReadableAxiomExpressionGenerator
                    .generateExpressionForSet(best));
            Logger.getLogger("KC").log(Level.FINER, sb.toString());
        }

        // removes set of axioms from the ontology
        Set<OWLAxiom> axioms = ontology.getAxioms();
        axioms.removeAll(best);

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
