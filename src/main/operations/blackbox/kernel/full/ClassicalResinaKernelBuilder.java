package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Implements the classical Resina algorithm for computing the full remainder
 * set.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos and Luís F. de M. C. Silva)
 */
public class ClassicalResinaKernelBuilder extends AbstractResinaKernelBuilder {

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the remainder set that will be computed.
     */
    private int maxRemainderElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param blackBox
     *            an implementation of the BlackBox algorithm
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public ClassicalResinaKernelBuilder(AbstractBlackBox blackBox,
                                           OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(blackBox, manager, reasonerFactory);
    }

    @Override
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
        Queue<Set<OWLAxiom>> queue = new LinkedList<>();

        Set<OWLAxiom> S = this.blackBox.blackBox(kb, entailment);

        Set<Set<OWLAxiom>> Kernel = new HashSet<>();
        Kernel.add(S);

        for (OWLAxiom s : S) {
            HashSet<OWLAxiom> sSet = new HashSet<>();
            sSet.add(s);
            queue.add(sSet);
        }

        while (!queue.isEmpty()) {
            Set<OWLAxiom> Hn = queue.remove();

            // Criando a ontologia kb \ Hn (B \ Hn)
            Set<OWLAxiom> ontology = new HashSet<>();
            ontology.addAll(kb);
            ontology.removeAll(Hn);
            OWLReasoner reasoner = reasonerFactory.createReasoner
                                   (manager.createOntology(ontology));

            if (reasoner.isEntailed(entailment)) {
                S = this.blackBox.blackBox(ontology, entailment);
                Kernel.add(S);
                for (OWLAxiom s : S) {
                    Set<OWLAxiom> tmp = new HashSet<>();
                    tmp.addAll(Hn);
                    tmp.add(s);
                    queue.add(tmp);
                }
            }
        }

        return Kernel;
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
