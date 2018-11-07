package main.operations.incisionfunction;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

/**
 * This incision function returns a singleton, i.e., a single element of the
 * kernel set. No preference is given: the chosen element depends on the
 * internal Java data structures.
 *
 * @author Luís F. de M. C. Silva
 */
public class IncisionFunctionAny implements IncisionFunction {

    @Override
    public Set<OWLAxiom> incise(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets) {
        if (setOfSets.isEmpty()) {
            return ontology.getAxioms();
        }

        return setOfSets.iterator().next();
    }
}
