package main.operations.blackbox;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Represents a variation of the shrinking part of BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public abstract class AbstractBlackBoxShrinkingStrategy extends OperatorStrategy {

    /**
     *
     * Creates a variation of the shrinking part of BlackBox algorithm.
     *
     * @param manager
     *            the OWL ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public AbstractBlackBoxShrinkingStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    /**
     *
     * Performs the shrinking part of BlackBox algorithm.
     *
     * @param ontology
     *            the initial ontology
     * @param entailment
     *            the formula in question
     * @return the result
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public Set<OWLAxiom> shrink(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException {
        return shrink(ontology, entailment, Collections.emptySet());
    }

    /**
     *
     * Performs the shrinking part of BlackBox algorithm.
     *
     * @param ontology
     *            the initial ontology
     * @param entailment
     *            the formula in question
     * @param keep
     *            the set of elements that should not be removed
     * @return the result
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public abstract Set<OWLAxiom> shrink(Set<OWLAxiom> ontology, OWLAxiom entailment, Set<OWLAxiom> keep)
            throws OWLOntologyCreationException;
}
