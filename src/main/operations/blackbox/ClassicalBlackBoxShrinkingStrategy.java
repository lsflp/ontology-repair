package main.operations.blackbox;

import java.util.Set;

import main.operations.blackbox.remainder.AbstractBlackBoxRemainderShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
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
        OWLOntology ontology = this.manager.createOntology(kb);
        for (OWLAxiom axiom : kb) {
            if (keep.contains(axiom))
                continue;
            manager.removeAxiom(ontology, axiom);
            if (isEntailed(ontology, entailment))
                remains.add(axiom);
        }
        return ontology.getAxioms();
    }
}
