package main.operations.selectionfunctions;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * This selection function returns a singleton, i.e., a single element of the
 * remainder set. No preference is given: the chosen element depends on the
 * internal Java data structures.
 *
 * @author Vinícius B. Matos
 */
public class SelectionFunctionAny implements SelectionFunction {

    @Override
    public Set<Set<OWLAxiom>> select(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets) {
        if (setOfSets.isEmpty()) {
            Set<Set<OWLAxiom>> result = new HashSet<Set<OWLAxiom>>();
            result.add(ontology.getAxioms());
            return result;
        }
        Set<Set<OWLAxiom>> set = new HashSet<Set<OWLAxiom>>();
        set.add(setOfSets.iterator().next());
        return set;
    }
}
