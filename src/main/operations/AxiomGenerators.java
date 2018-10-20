package main.operations;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements methods that returns axioms generators in order to create
 * a inferred ontology for performing the belief revision operations.
 *
 * @author Vinícius B. Matos (adapted by Luís F. de M. C. Silva)
 */
public class AxiomGenerators {

    /**
     * Returns the axiom generators that will be used by the reasoner to close the
     * belief set under a alternative (tarskian) consequence operator.
     *
     * @return the list of axiom generators
     */
    public static List<InferredAxiomGenerator<? extends OWLAxiom>> alternativeAllAxiomGenerators() {
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        // classes
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        // individuals
        gens.add(new InferredPropertyAssertionGenerator());
        return gens;
    }

    /**
     * Returns the axiom generators that will be used by the reasoner to close the
     * belief set under its consequence operator.
     *
     * @return the list of axiom generators
     */
    public static List<InferredAxiomGenerator<? extends OWLAxiom>> allAxiomGenerators() {
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        // classes
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        gens.add(new InferredDisjointClassesAxiomGenerator());
        // data properties
        gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
        gens.add(new InferredSubDataPropertyAxiomGenerator());
        // object properties
        gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
        gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredSubObjectPropertyAxiomGenerator());
        // individuals
        gens.add(new InferredPropertyAssertionGenerator());
        return gens;
    }
}
