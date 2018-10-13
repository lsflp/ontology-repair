package main.operations.blackbox;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Represents a variation of the BlackBox algorithm, used to compute elements of
 * the remainder set.
 *
 * Given a belief set X and a formula α, the remainder set X ⊥ α is defined as
 * the set of the maximal subsets of X that do not imply α.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */

public abstract class AbstractBlackBox {

    /**
     * The strategy used for expanding.
     */
    public AbstractBlackBoxExpansionStrategy expansionStrategy;

    /**
     *
     * The strategy used for expanding.
     */
    public AbstractBlackBoxShrinkingStrategy shrinkingStrategy;

    /**
     *
     * Creates a variation of the BlackBox algorithm with the given expansion
     * and shrinking strategies.
     *
     * @param expansionStrategy
     *            the expansion strategy
     * @param shrinkingStrategy
     *            the shrinking strategy
     */
    public AbstractBlackBox(AbstractBlackBoxExpansionStrategy expansionStrategy,
            AbstractBlackBoxShrinkingStrategy shrinkingStrategy) {
        this.expansionStrategy = expansionStrategy;
        this.shrinkingStrategy = shrinkingStrategy;
    }

    /**
     * Gets the expansion strategy.
     *
     * @return the expansion strategy.
     */
    public AbstractBlackBoxExpansionStrategy getExpansionStrategy() {
        return expansionStrategy;
    }

    /**
     * Gets the shrinking strategy.
     *
     * @return the shrinking strategy.
     */
    public AbstractBlackBoxShrinkingStrategy getContractionStrategy() {
        return shrinkingStrategy;
    }

    /**
     *
     * Executes the BlackBox algorithm.
     *
     * @param ontology
     *            the initial ontology
     *
     * @param entailment
     *            the formula used by the algorithm
     * @param initialSet
     *            the set of items that must be in the result
     * @return the result of the operation
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public abstract Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment, Set<OWLAxiom> initialSet)
            throws OWLOntologyCreationException;

    /**
     *
     * Executes the BlackBox algorithm.
     *
     * @param ontology
     *            the initial ontology
     *
     * @param entailment
     *            the formula used by the algorithm
     * @return the result of the operation
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException {
        return blackBox(ontology, entailment, Collections.emptySet());
    }

}
