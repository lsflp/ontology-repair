package main.operations.selectionfunctions;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Interface for selection functions.
 *
 * Given a belief set X and a formula α, a selection function γ chooses some
 * elements of the remainder set, that is, ∅ ≠ γ(X ⊥ α) ⊆ X ⊥ α if X ⊥ α is not
 * empty, or {X} otherwise
 *
 * @author Vinícius B. Matos
 */
public interface SelectionFunction {

    /**
     * Returns the result of the selection function, which is a subset of the
     * given set of subsets of the ontology.
     *
     * @param ontology
     *            the original ontology
     * @param setOfSets
     *            the remainder set
     * @return the selected elements
     */
    public Set<Set<OWLAxiom>> select(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets);
}
