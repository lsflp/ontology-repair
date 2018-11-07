package main.operations.incisionfunction;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

/**
 * Interface for selection functions.
 *
 * Given a belief set X and a formula α, an incision function that chooses at
 * least one element of the kernel set, that is,
 * ∅ ≠ σ(X ⊥⊥ (↓↓) α) ⊆ X ⊥⊥ (↓↓) α if X ⊥⊥ (↓↓) α is not empty,
 * or {X} otherwise
 *
 * @author Luís F. de M. C. Silva
 */
public interface IncisionFunction {

    /**
     * Returns the result of the incision function, which is a subset of the
     * given set of subsets of the ontology.
     *
     * @param ontology
     *            the original ontology
     * @param setOfSets
     *            the remainder set
     * @return the selected elements
     */
    public Set<OWLAxiom> incise(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets);
}
