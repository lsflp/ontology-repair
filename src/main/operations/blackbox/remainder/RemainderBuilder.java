package main.operations.blackbox.remainder;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.remainder.expansionstrategies.ClassicalBlackBoxRemainderExpansionStrategy;
import main.operations.blackbox.remainder.full.ClassicalResinaRemainderBuilder;
import main.operations.blackbox.remainder.shrinkingstrategies.TrivialBlackBoxRemainderShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Provides the computation of the remainder set.
 *
 * Basically, this class just encapsulates the real implementation.
 *
 * @author Vinícius B. Matos
 *
 */
public class RemainderBuilder {

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
    public RemainderBuilder(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
    }

    /**
     * Computes the full remainder set of an ontology in relation to a formula.
     * The result may not be the full remainder set if the limit of the queue
     * capacity or the limit of the computed remainder set size is too slow.
     *
     * @param kb
     *            the belief set
     * @param entailment
     *            the formula that must not be implied by the elements of the
     *            remainder set
     * @return the computed remainder set
     * @throws OWLOntologyChangeException
     *             OWLOntologyChangeException
     *
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public Set<Set<OWLAxiom>> remainderSet(Set<OWLAxiom> kb, OWLAxiom entailment)
            throws OWLOntologyChangeException, OWLOntologyCreationException {
        AbstractBlackBox blackbox = new BlackBoxRemainder(
                new ClassicalBlackBoxRemainderExpansionStrategy(manager, reasonerFactory),
                new TrivialBlackBoxRemainderShrinkingStrategy(manager, reasonerFactory));
        ClassicalResinaRemainderBuilder rb = new ClassicalResinaRemainderBuilder(blackbox, manager, reasonerFactory);
        rb.setMaxQueueSize(maxQueueSize);
        rb.setMaxRemainderElements(maxRemainderElements);
        return rb.remainderSet(kb, entailment);
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
