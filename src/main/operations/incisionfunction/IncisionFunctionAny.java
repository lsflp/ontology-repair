package main.operations.incisionfunction;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

public class IncisionFunctionAny implements IncisionFunction {

    @Override
    public Set<OWLAxiom> incise(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets) {
        if (setOfSets.isEmpty()) {
            return ontology.getAxioms();
        }

        return setOfSets.iterator().next();
    }
}
