package main.operations.blackbox.remainder.shrinkingstrategies;

import java.util.HashSet;
import java.util.Set;

import main.operations.blackbox.remainder.AbstractBlackBoxRemainderShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Implements the trivial strategy for shrinking in BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class TrivialBlackBoxRemainderShrinkingStrategy extends AbstractBlackBoxRemainderShrinkingStrategy {
    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public TrivialBlackBoxRemainderShrinkingStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    @Override
    public Set<OWLAxiom> shrink(Set<OWLAxiom> ontology, OWLAxiom entailment, Set<OWLAxiom> keep)
            throws OWLOntologyCreationException {
        Set<OWLAxiom> rem = new HashSet<OWLAxiom>(ontology);
        rem.removeAll(keep);
        remains = rem;
        return new HashSet<OWLAxiom>(keep);
    }
}
