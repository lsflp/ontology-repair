package main.operations.contraction;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.kernel.BlackBoxKernel;
import main.operations.blackbox.ClassicalBlackBoxExpansionStrategy;
import main.operations.blackbox.ClassicalBlackBoxShrinkingStrategy;
import main.operations.blackbox.kernel.full.ClassicalResinaKernelBuilder;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Provides the computation of the kernel set.
 *
 * Basically, this class just encapsulates the real implementation.
 *
 * @author Luís F. de M. C. Silva (inspired by Vinícius B. Matos)
 *
 */
public class KernelBuilder {

    /**
     * The ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * The factory that constructs the remainder.
     */
    private OWLReasonerFactory reasonerFactory;

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the remainder set that will be
     * computed.
     */
    private int maxRemainderElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public KernelBuilder(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
    }

    /**
     * Computes the kernel set of an ontology in relation to a formula.
     * The result may not be the full kernel set if the limit of the queue
     * capacity or the limit of the computed kernel set size is too slow.
     *
     * @param kb
     *            the belief set
     * @param sentence
     *            the formula that must not be implied by the elements of the
     *            remainder set
     * @return the computed remainder set
     * @throws OWLOntologyChangeException
     *             OWLOntologyChangeException
     *
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom sentence) throws OWLOntologyChangeException, OWLOntologyCreationException {
        AbstractBlackBox blackBox = new BlackBoxKernel(
                new ClassicalBlackBoxExpansionStrategy(manager, reasonerFactory),
                new ClassicalBlackBoxShrinkingStrategy(manager, reasonerFactory));
        ClassicalResinaKernelBuilder rkb = new ClassicalResinaKernelBuilder(blackBox, manager, reasonerFactory);
        rkb.setMaxQueueSize(maxQueueSize);
        rkb.setMaxRemainderElements(maxRemainderElements);
        return rkb.remainderSet(kb, sentence);

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
