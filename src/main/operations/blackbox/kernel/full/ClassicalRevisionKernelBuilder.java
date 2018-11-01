package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an alternative algorithm for computing the kernel
 * set, aimed to the revision operation.
 *
 * @author Luís F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 */
public class ClassicalRevisionKernelBuilder extends AbstractReiterKernelBuilder {

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the kernel set that will be computed.
     */
    private int maxKernelElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param blackBox        an implementation of the blackbox algorithm
     * @param manager         the ontology manager
     * @param reasonerFactory a factory that builds the reasoner
     */
    public ClassicalRevisionKernelBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(blackBox, manager, reasonerFactory);
    }


    /**
     * {@inheritDoc}
     *
     * The result may not be the full kernel set if the limit of the queue
     * capacity or the limit of the computed kernel set size is too slow.
     */
    @Override
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {

        Set<Set<OWLAxiom>> kernel = new HashSet<>();

        if (reasonerFactory.createNonBufferingReasoner(manager.createOntology(kb)).isConsistent()) {
            return kernel;
        }

        Set<OWLAxiom> min = this.blackBox.blackBox(kb, null);
        kernel.add(min);

        HashSet<OWLAxiom> aux = new HashSet<>();
        aux.addAll(kb);

        for (OWLAxiom beta : min) {
            aux.remove(beta);
            kernel.addAll(kernelSet(aux, null));
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

}
