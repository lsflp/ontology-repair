package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
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

public class ClassicalRevisionKernelBuilder extends AbstractReiterKernelBuilder {

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the kernel set that will be computed.
     */
    private int maxKernelElements = Integer.MAX_VALUE;

    private Set<Set<OWLAxiom>> cut = new HashSet<>();

    /**
     * Instantiates the class.
     *
     * @param blackBox        an implementation of the blackbox algorithm
     * @param manager         the ontology manager
     * @param reasonerFactory
     */
    public ClassicalRevisionKernelBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(blackBox, manager, reasonerFactory);
    }

    @Override
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
        Set<Set<OWLAxiom>> kernelSet = new HashSet<>();

        OWLOntology ontology = manager.createOntology(kb);
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

        Queue<Set<OWLAxiom>> queue = new LinkedList<>();

        Set<OWLAxiom> candidate, hn;
        Set<OWLAxiom> exp = new HashSet<>();
        exp.addAll(kb);

        if (reasoner.isConsistent()) {
            return kernelSet;
        }

        Set<OWLAxiom> X = blackBox.blackBox(exp, null);
        kernelSet.add(X);

        for (OWLAxiom axiom : X){
            if (queue.size() >= maxQueueSize)
                break;
            Set<OWLAxiom> set = new HashSet<>();
            set.add(axiom);
            queue.add(set);
        }

        if (kernelSet.size() >= maxKernelElements)
            return kernelSet;

        // Reiter's algorithm
        while (!queue.isEmpty()) {
            hn = queue.remove();

            manager.removeAxioms(ontology, hn);

            if (!reasoner.isConsistent()) {
                exp = ontology.getAxioms();
                candidate = blackBox.blackBox(exp, null);
                for (OWLAxiom axiom : candidate) {
                    if (queue.size() >= maxQueueSize)
                        break;
                    Set<OWLAxiom> set2 = new HashSet<>();
                    set2.addAll(hn);
                    set2.add(axiom);
                    queue.add(set2);
                }
            }
            else {
                cut.add(hn);
            }

            manager.addAxioms(ontology, hn);
        }

        return kernelSet;
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
