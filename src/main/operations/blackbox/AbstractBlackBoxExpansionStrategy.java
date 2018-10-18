package main.operations.blackbox;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Represents a variation of the expansion part of BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public abstract class AbstractBlackBoxExpansionStrategy extends OperatorStrategy {

    /**
     *
     * Creates a variation of the expansion part of BlackBox algorithm.
     *
     * @param manager
     *            the OWL ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public AbstractBlackBoxExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    /**
     *
     * Performs the expansion part of BlackBox algorithm.
     *
     * @param ontology
     *            the initial ontology
     * @param entailment
     *            the formula in question
     * @return the result
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public abstract Set<OWLAxiom> expand(Set<OWLAxiom> ontology, OWLAxiom entailment)
            throws OWLOntologyCreationException;

}
