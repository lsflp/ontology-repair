package main.operations.auxiliars;

import java.util.Set;

import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;

/**
 * An entity checker that maps from string to entities using a bidirectional
 * short form provider.
 *
 * This is a workaround that makes the Protégé plugin work without typing the
 * full URIs.
 *
 * @author Vinícius B. Matos
 *
 */
public class AlternativeShortFormEntityChecker extends ShortFormEntityChecker {

    /**
     * The prefixes in the ontology.
     */
    private Set<String> prefixes;

    /**
     * The ontology.
     */
    private OWLOntology ontology;

    /**
     * Instantiates the entity checker.
     *
     * @param prefixes
     *            the prefixes in the ontology
     * @param shortFormProvider
     *            the short form provider
     * @param ontology
     *            the ontology
     */
    public AlternativeShortFormEntityChecker(Set<String> prefixes, BidirectionalShortFormProvider shortFormProvider,
            OWLOntology ontology) {
        super(shortFormProvider);
        this.prefixes = prefixes;
        this.ontology = ontology;
    }

    @Override
    public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
        OWLAnnotationProperty o = super.getOWLAnnotationProperty(name);
        if (o != null)
            return o;
        for (String prefix : prefixes) {
            o = super.getOWLAnnotationProperty(prefix + name);
            if (o != null)
                return o;
        }
        for (OWLAnnotationProperty e : ontology.getAnnotationPropertiesInSignature())
            if (e.toStringID().endsWith("#" + name))
                return e;
        return null;
    }

    @Override
    public OWLClass getOWLClass(String name) {
        OWLClass o = super.getOWLClass(name);
        if (o != null)
            return o;
        for (String prefix : prefixes) {
            o = super.getOWLClass(prefix + name);
            if (o != null)
                return o;
        }
        for (OWLClass e : ontology.getClassesInSignature())
            if (e.toStringID().endsWith("#" + name))
                return e;
        return null;
    }

    @Override
    public OWLDataProperty getOWLDataProperty(String name) {
        OWLDataProperty o = super.getOWLDataProperty(name);
        if (o != null)
            return o;
        for (String prefix : prefixes) {
            o = super.getOWLDataProperty(prefix + name);
            if (o != null)
                return o;
        }
        for (OWLDataProperty e : ontology.getDataPropertiesInSignature())
            if (e.toStringID().endsWith("#" + name))
                return e;
        return null;
    }

    @Override
    public OWLDatatype getOWLDatatype(String name) {
        OWLDatatype o = super.getOWLDatatype(name);
        if (o != null)
            return o;
        for (String prefix : prefixes) {
            o = super.getOWLDatatype(prefix + name);
            if (o != null)
                return o;
        }
        for (OWLDatatype e : ontology.getDatatypesInSignature())
            if (e.toStringID().endsWith("#" + name))
                return e;
        return null;
    }

    @Override
    public OWLNamedIndividual getOWLIndividual(String name) {
        OWLNamedIndividual o = super.getOWLIndividual(name);
        if (o != null)
            return o;
        for (String prefix : prefixes) {
            o = super.getOWLIndividual(prefix + name);
            if (o != null)
                return o;
        }
        for (OWLNamedIndividual e : ontology.getIndividualsInSignature())
            if (e.toStringID().endsWith("#" + name))
                return e;
        return null;
    }

    @Override
    public OWLObjectProperty getOWLObjectProperty(String name) {
        OWLObjectProperty o = super.getOWLObjectProperty(name);
        if (o != null)
            return o;
        for (String prefix : prefixes) {
            o = super.getOWLObjectProperty(prefix + name);
            if (o != null)
                return o;
        }
        for (OWLObjectProperty e : ontology.getObjectPropertiesInSignature())
            if (e.toStringID().endsWith("#" + name))
                return e;
        return null;
    }

}
