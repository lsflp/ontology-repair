package main.operations.blackbox.kernel;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.kernel.expansionstrategies.ClassicalBlackBoxKernelExpansionStrategy;
import main.operations.blackbox.kernel.expansionstrategies.RevisionBlackBoxKernelExpansionStrategy;
import main.operations.blackbox.kernel.full.ClassicalReiterKernelBuilder;
import main.operations.blackbox.kernel.full.ClassicalRevisionKernelBuilder;
import main.operations.blackbox.kernel.shrinkingstrategies.ClassicalBlackBoxKernelShrinkingStrategy;
import main.operations.blackbox.kernel.shrinkingstrategies.RevisionBlackBoxKernelShrinkingStrategy;
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
public class RevisionKernelBuilder {

    /**
     * The ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * The factory that constructs the kernel.
     */
    private OWLReasonerFactory reasonerFactory;

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the kernel set that will be
     * computed.
     */
    private int maxKernelElements = Integer.MAX_VALUE;

    private Set<Set<OWLAxiom>> cut;

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public RevisionKernelBuilder(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
    }

    /**
     * Computes the kernel set of an ontology in relation to a formula.
     * The result may not be the kernel set if the limit of the queue
     * capacity or the limit of the computed kernel set size is too slow.
     *
     * @param kb
     *            the belief set
     * @param entailment
     *            the formula that must not be implied by the elements of the
     *            kernel set
     * @return the computed kernel set
     * @throws OWLOntologyChangeException
     *             OWLOntologyChangeException
     *
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment)
            throws OWLOntologyChangeException, OWLOntologyCreationException {
        AbstractBlackBox blackbox = new RevisionBlackBoxKernel(
                new RevisionBlackBoxKernelExpansionStrategy(manager, reasonerFactory),
                new RevisionBlackBoxKernelShrinkingStrategy(manager, reasonerFactory));
        ClassicalRevisionKernelBuilder kn = new ClassicalRevisionKernelBuilder(blackbox, manager, reasonerFactory);
        this.setCut(kn.getCut());
        kn.setMaxQueueSize(maxQueueSize);
        kn.setMaxKernelElements(maxKernelElements);
        return kn.kernelSet(kb, entailment);
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

    public Set<Set<OWLAxiom>> getCut() {
        return cut;
    }

    public void setCut(Set<Set<OWLAxiom>> cut) {
        this.cut = cut;
    }
}
