package main.operations.blackbox;

import java.util.Set;

import main.operations.blackbox.remainder.AbstractBlackBoxRemainderExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Implements the classical expansion part of BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class ClassicalBlackBoxExpansionStrategy extends AbstractBlackBoxRemainderExpansionStrategy {

    /**
     * Instantiates the class.
     * 
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public ClassicalBlackBoxExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    @Override
    public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
        OWLOntology ontology = manager.createOntology(kb);
        for (OWLAxiom axiom : remains) {
            manager.addAxiom(ontology, axiom);
            if (isEntailed(ontology, entailment)) {
                manager.removeAxiom(ontology, axiom);
            }
        }
        return ontology.getAxioms();
    }
}
