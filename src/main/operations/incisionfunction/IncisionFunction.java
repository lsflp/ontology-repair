package main.operations.incisionfunction;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

public interface IncisionFunction {

    public Set<OWLAxiom> incise(OWLOntology ontology, Set<Set<OWLAxiom>> setOfSets);
}
