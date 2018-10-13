package main.operations.blackbox.remainder;

import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Represents a variation of the expansion part of BlackBox algorithm used to
 * compute elements of remainder sets.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public abstract class AbstractBlackBoxRemainderExpansionStrategy extends AbstractBlackBoxExpansionStrategy {
    /**
     * The elements that the expander will try to add to the set.
     */
    protected Set<OWLAxiom> remains;

    /**
     *
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public AbstractBlackBoxRemainderExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    /**
     *
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     * @param remains
     *            the elements that the expander will try to add to the set
     */
    public AbstractBlackBoxRemainderExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory,
            Set<OWLAxiom> remains) {
        super(manager, reasonerFactory);
        this.remains = remains;
    }

    /**
     *
     * Gets the remaining elements.
     *
     * @return the elements that the expander will try to add to the set
     */
    public Set<OWLAxiom> getRemains() {
        return remains;
    }

    /**
     *
     * Sets the remaining elements.
     *
     * @param remains
     *            the elements that the expander will try to add to the set
     */
    public void setRemains(Set<OWLAxiom> remains) {
        this.remains = remains;
    }

}
