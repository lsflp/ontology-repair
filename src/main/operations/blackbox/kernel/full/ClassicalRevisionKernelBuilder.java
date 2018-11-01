package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.kernel.RevisionBlackBoxKernel;
import main.operations.blackbox.kernel.RevisionKernelBuilder;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Implements an alternative algorithm for computing the kernel
 * set, aimed to the revision operation.
 *
 * @author Luís F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 */
public class ClassicalRevisionKernelBuilder {

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the kernel set that will be computed.
     */
    private int maxKernelElements = Integer.MAX_VALUE;

    private Set<Set<OWLAxiom>> cut = new HashSet<>();
    private OWLOntologyManager manager;
    private OWLReasonerFactory reasonerFactory;
    private RevisionBlackBoxKernel revisionBlackBoxKernel;

    /**
     * Instantiates the class.
     *
     * @param blackBox        an implementation of the blackbox algorithm
     * @param manager         the ontology manager
     * @param reasonerFactory
     */
    public ClassicalRevisionKernelBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager, OWLReasonerFactory reasonerFactory, RevisionBlackBoxKernel revisionBlackBoxKernel) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.revisionBlackBoxKernel = revisionBlackBoxKernel;
    }

    /**
     * {@inheritDoc}
     *
     * The result may not be the full kernel set if the limit of the queue
     * capacity or the limit of the computed kernel set size is too slow.
     */
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb) throws OWLOntologyCreationException {

        Set<Set<OWLAxiom>> kernel = new HashSet<>();

        if (reasonerFactory.createNonBufferingReasoner(manager.createOntology(kb)).isConsistent()) {
            return kernel;
        }

        Set<OWLAxiom> min = revisionBlackBoxKernel.blackBox(kb);
        kernel.add(min);

        HashSet<OWLAxiom> aux = new HashSet<>();
        aux.addAll(kb);

        for (OWLAxiom beta : min) {
            aux.remove(beta);
            kernel.addAll(kernelSet(aux));
            aux.add(beta);
        }

        return kernel;
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
}
