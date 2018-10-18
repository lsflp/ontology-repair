package main.operations.blackbox.remainder.full;

import java.util.Set;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.MatosOperatorStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Represents a generic algorithm for computing the full remainder set.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public abstract class AbstractResinaRemainderBuilder extends MatosOperatorStrategy {

    /**
     * An implementation of the BlackBox algorithm.
     */
    protected AbstractBlackBox blackBox;

    /**
     * A factory that constructs the reasoner.
     */
    protected OWLReasonerFactory reasonerFactory;

    /**
     * Computes the full remainder set of an ontology in relation to a formula.
     *
     * @param kb
     *            the axioms in the ontology
     * @param entailment
     *            the formula that must not be implied by the elements of the
     *            remainder set
     * @return the full remainder set
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public abstract Set<Set<OWLAxiom>> remainderSet(Set<OWLAxiom> kb, OWLAxiom entailment)
            throws OWLOntologyCreationException;

    /**
     * Instantiates the class.
     *
     * @param blackBox
     *            an implementation of the blackbox algorithm
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that builds the reasoner
     */
    protected AbstractResinaRemainderBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager,
            OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
        this.blackBox = blackBox;
        this.reasonerFactory = reasonerFactory;
    }
}
