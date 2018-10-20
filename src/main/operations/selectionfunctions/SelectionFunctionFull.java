package main.operations.selectionfunctions;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * This selection function returns all of the elements of the remainder set
 * (like in full meet contraction).
 *
 * @author Vinícius B. Matos
 */
public class SelectionFunctionFull implements SelectionFunction {

    @Override
    public Set<Set<OWLAxiom>> select(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets) {
        if (setOfSets.isEmpty()) {
            Set<Set<OWLAxiom>> result = new HashSet<Set<OWLAxiom>>();
            result.add(ontology.getAxioms());
            return result;
        }
        return setOfSets;
    }
}
