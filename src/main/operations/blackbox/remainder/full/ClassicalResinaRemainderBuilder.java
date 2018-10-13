package main.operations.blackbox.remainder.full;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.srwpseudocontraction.HumanReadableAxiomExpressionGenerator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Implements the classical Resina algorithm for computing the full remainder
 * set.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class ClassicalResinaRemainderBuilder extends AbstractResinaRemainderBuilder {

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
    public ClassicalResinaRemainderBuilder(AbstractBlackBox blackBox,
                                           OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(blackBox, manager, reasonerFactory);
    }

    /**
     * {@inheritDoc}
     *
     * The result may not be the full remainder set if the limit of the queue
     * capacity or the limit of the computed remainder set size is too slow.
     */
    @Override
    public Set<Set<OWLAxiom>> remainderSet(Set<OWLAxiom> kb, OWLAxiom entailment)
            throws OWLOntologyCreationException, OWLOntologyChangeException {
        HashSet<Set<OWLAxiom>> remainderSet = new HashSet<>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLReasoner reasoner = reasonerFactory
                .createNonBufferingReasoner(manager.createOntology(kb));
        Set<OWLAxiom> rem, hn;
        // create an empty queue
        Queue<Set<OWLAxiom>> queue = new LinkedList<>();
        // if the formula is not entailed, then there is nothing to do
        if (!reasoner.isEntailed(entailment)) {
            Set<Set<OWLAxiom>> unit = new HashSet<Set<OWLAxiom>>();
            unit.add(kb);
            return unit;
        }
        // get one element of the remainder set
        rem = this.blackBox.blackBox(kb, entailment);
        remainderSet.add(rem);
        if (Logger.getLogger("SRW").isLoggable(Level.FINER)) {
            Logger.getLogger("SRW").log(Level.FINER,
                    "\n---------- REMAINDER ELEMENT: \n"
                            + HumanReadableAxiomExpressionGenerator
                                    .generateExpressionForSet(rem));
        }
        // for each axiom s in kb and not in the remainder, add {s} to the queue
        for (OWLAxiom axiom : kb) {
            if (queue.size() >= maxQueueSize)
                break;
            if (rem.contains(axiom))
                continue;
            Set<OWLAxiom> set = new HashSet<>();
            set.add(axiom);
            queue.add(set);
        }
        // prepare reasoner and ontology to be used in the main loop
        OWLOntology ont = manager.createOntology();
        if (remainderSet.size() >= maxRemainderElements)
            return remainderSet;
        // while the queue is not empty...
        while (!queue.isEmpty()) {
            // hn <- queue.pop()
            hn = queue.remove();
            // check if hn entails the formula
            manager.addAxioms(ont, hn);
            reasoner = reasonerFactory.createReasoner(ont); // TODO: avoid recreation of
                                                            // the reasoner
            boolean entails = reasoner.isEntailed(entailment);
            manager.removeAxioms(ont, hn);
            if (entails)
                continue;
            // if hn does not entail the formula...
            // find a remainder containing all the elements of hn
            rem = this.blackBox.blackBox(kb, entailment, hn);
            // add it to the remainder set
            remainderSet.add(rem);
            if (Logger.getLogger("SRW").isLoggable(Level.FINER)) {
                Logger.getLogger("SRW").log(Level.FINER,
                        "\n---------- REMAINDER ELEMENT: \n"
                                + HumanReadableAxiomExpressionGenerator
                                        .generateExpressionForSet(rem));
            }
            if (remainderSet.size() >= maxRemainderElements)
                break;
            // for each axiom s in kb \ rem, add hn U {s} to the queue
            for (OWLAxiom s : kb) {
                if (queue.size() >= maxQueueSize)
                    break;
                if (rem.contains(s))
                    continue;
                Set<OWLAxiom> set = new HashSet<>();
                set.addAll(hn);
                set.add(s);
                queue.add(set);
            }
        }
        // return the computed remainder set
        return remainderSet;
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
