package main.operations.blackbox;

import main.operations.blackbox.remainder.AbstractBlackBoxRemainderExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;

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
        Set<OWLAxiom> expansionResult = new HashSet<>();

        OWLOntology ontology = manager.createOntology(expansionResult);
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        for (OWLAxiom beta : kb) {
            manager.addAxiom(ontology, beta);
            if (reasoner.isEntailed(entailment)) {
                break;
            }
        }

        return expansionResult;
    }
}
