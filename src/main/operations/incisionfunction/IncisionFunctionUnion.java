package main.operations.incisionfunction;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * This incision function returns the union of all the elements of the kernel
 * set.
 *
 * @author Luís F. de M. C. Silva
 */
public class IncisionFunctionUnion implements IncisionFunction {

    @Override
    public Set<OWLAxiom> incise(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets) {
        if (setOfSets.isEmpty()) {
            return ontology.getAxioms();
        }

        HashSet<OWLAxiom> result = new HashSet<>();
        for (Set<OWLAxiom> set : setOfSets) {
            result.addAll(set);
        }

        return result;
    }
}
