package main.operations.blackbox;

import main.operations.blackbox.remainder.AbstractBlackBoxRemainderShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;
/**
 * Implements the classical method for the shrinking part of BlackBox algorithm.
 * Basically, the function removes aximos from the ontology until it does not
 * imply a given formula.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class ClassicalBlackBoxShrinkingStrategy
        extends AbstractBlackBoxRemainderShrinkingStrategy {

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that consturcts the reasoner
     */
    public ClassicalBlackBoxShrinkingStrategy(OWLOntologyManager manager,
                                              OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    @Override
    public Set<OWLAxiom> shrink(Set<OWLAxiom> kb, OWLAxiom entailment, Set<OWLAxiom> keep)
            throws OWLOntologyCreationException {

        HashSet<OWLAxiom> shrinkingResult = new HashSet<>();
        shrinkingResult.addAll(kb);

        for (OWLAxiom beta : kb) {
            shrinkingResult.remove(beta);
            OWLReasoner reasoner = reasonerFactory.createReasoner
                                   (manager.createOntology(shrinkingResult));
            if (!reasoner.isEntailed(entailment)) {
                shrinkingResult.add(beta);
            }
        }

        return shrinkingResult;
    }
}
