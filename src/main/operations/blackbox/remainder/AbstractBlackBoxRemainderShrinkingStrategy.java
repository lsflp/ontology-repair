package main.operations.blackbox.remainder;

import java.util.HashSet;
import java.util.Set;

import main.operations.blackbox.AbstractBlackBoxShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Represents a variation of the shrinking part of BlackBox algorithm used to
 * compute elements of remainder sets.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public abstract class AbstractBlackBoxRemainderShrinkingStrategy extends AbstractBlackBoxShrinkingStrategy {

    /**
     * The elements that have been removed in the shrinking.
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
    public AbstractBlackBoxRemainderShrinkingStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
        this.remains = new HashSet<OWLAxiom>();
    }

    /**
     * Gets the elements removed in shrinking.
     *
     * @return the axioms that have been removed
     */
    public Set<OWLAxiom> getRemains() {
        return remains;
    }

}
