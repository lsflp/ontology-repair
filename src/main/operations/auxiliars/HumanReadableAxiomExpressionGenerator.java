package main.operations.auxiliars;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * Generates human-readable expressions for axioms, following the usual
 * Description Logics notation.
 * 
 * @author Vinícius B. Matos
 */
public class HumanReadableAxiomExpressionGenerator {

    /**
     * Returns a human-readable expression for an individual (usually its name).
     * 
     * @param individual
     *            the individual
     * @return the expression
     */
    protected static String getReadableIndividual(OWLIndividual individual) {
        return cleanURIs(individual.toString());
    }

    /**
     * Returns a human-readable expression for a class.
     * 
     * @param c
     *            the class
     * @return the expression
     */
    protected static String getReadableClass(OWLClassExpression c) {
        if (c instanceof OWLObjectSomeValuesFrom) {
            OWLObjectPropertyExpression r = ((OWLObjectSomeValuesFrom) c).getProperty();
            OWLClassExpression cc = ((OWLObjectSomeValuesFrom) c).getFiller();
            return cleanURIs("(∃" + getReadableObjectProperty(r)
                    + (cc.isTopEntity() ? "" : ("." + getReadableClass(cc))) + ")");
        }
        if (c instanceof OWLObjectAllValuesFrom) {
            OWLObjectPropertyExpression r = ((OWLObjectAllValuesFrom) c).getProperty();
            OWLClassExpression cc = ((OWLObjectAllValuesFrom) c).getFiller();
            return cleanURIs("(∀" + getReadableObjectProperty(r)
                    + (cc.isTopEntity() ? "" : ("." + getReadableClass(cc))) + ")");
        }
        if (c instanceof OWLObjectMinCardinality) {
            OWLObjectPropertyExpression r = ((OWLObjectMinCardinality) c).getProperty();
            OWLClassExpression cc = ((OWLObjectMinCardinality) c).getFiller();
            int n = ((OWLObjectMinCardinality) c).getCardinality();
            return cleanURIs("(≥" + n + " " + getReadableObjectProperty(r)
                    + (cc.isTopEntity() ? "" : ("." + getReadableClass(cc))) + ")");
        }
        if (c instanceof OWLObjectMaxCardinality) {
            OWLObjectPropertyExpression r = ((OWLObjectMaxCardinality) c).getProperty();
            OWLClassExpression cc = ((OWLObjectMaxCardinality) c).getFiller();
            int n = ((OWLObjectMaxCardinality) c).getCardinality();
            return cleanURIs("(≤" + n + " " + getReadableObjectProperty(r)
                    + (cc.isTopEntity() ? "" : ("." + getReadableClass(cc))) + ")");
        }
        if (c instanceof OWLObjectExactCardinality) {
            OWLObjectPropertyExpression r = ((OWLObjectExactCardinality) c).getProperty();
            OWLClassExpression cc = ((OWLObjectExactCardinality) c).getFiller();
            int n = ((OWLObjectExactCardinality) c).getCardinality();
            return cleanURIs("(=" + n + " " + getReadableObjectProperty(r)
                    + (cc.isTopEntity() ? "" : ("." + getReadableClass(cc))) + ")");
        }
        if (c instanceof OWLObjectIntersectionOf) {
            Set<OWLClassExpression> classSet = ((OWLObjectIntersectionOf) c)
                    .getOperands();
            if (classSet.isEmpty())
                return "";
            StringBuilder sb = new StringBuilder("(");
            for (OWLClassExpression cc : classSet)
                sb.append(getReadableClass(cc)).append(" ⊓ ");
            sb.setLength(sb.length() - 3);
            return sb.toString() + ")";
        }
        if (c instanceof OWLObjectUnionOf) {
            Set<OWLClassExpression> classSet = ((OWLObjectUnionOf) c).getOperands();
            if (classSet.isEmpty())
                return "";
            StringBuilder sb = new StringBuilder("(");
            for (OWLClassExpression cc : classSet)
                sb.append(getReadableClass(cc)).append(" ⊔ ");
            sb.setLength(sb.length() - 3);
            return sb.toString() + ")";
        }
        if (c instanceof OWLObjectComplementOf) {
            return "(¬" + getReadableClass(((OWLObjectComplementOf) c).getOperand())
                    + ")";
        }
        return cleanURIs(c.toString());
    }

    /**
     * Returns a human-readable expression for an object property.
     * 
     * @param r
     *            the object property
     * @return the expression
     */
    protected static String getReadableObjectProperty(OWLObjectPropertyExpression r) {
        return cleanURIs(r.toString());
    }

    /**
     * Given an expression, replaces URIs with short names.
     * 
     * @param s
     *            the expression to be cleaned
     * @return the cleaned expression
     */
    protected static String cleanURIs(String s) {
        return s.replaceAll("<http:(?:[^#]+)#([^:]+)>", "$1").replaceAll("^owl:", "");
    }

    /**
     * 
     * Returns a human-readable expression for an axiom.
     * 
     * @param axiom
     *            the axiom
     * @return the expression
     */
    public static String generateExpression(OWLAxiom axiom) {
        if (axiom instanceof OWLSubClassOfAxiom) {
            OWLClassExpression c1 = ((OWLSubClassOfAxiom) axiom).getSubClass();
            OWLClassExpression c2 = ((OWLSubClassOfAxiom) axiom).getSuperClass();
            return getReadableClass(c1) + " ⊑ " + getReadableClass(c2);
        }
        if (axiom instanceof OWLClassAssertionAxiom) {
            OWLIndividual x = ((OWLClassAssertionAxiom) axiom).getIndividual();
            OWLClassExpression c = ((OWLClassAssertionAxiom) axiom).getClassExpression();
            return getReadableIndividual(x) + " : " + getReadableClass(c);
        }
        if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
            OWLIndividual x = ((OWLObjectPropertyAssertionAxiom) axiom).getSubject();
            OWLObjectPropertyExpression r = ((OWLObjectPropertyAssertionAxiom) axiom)
                    .getProperty();
            OWLIndividual y = ((OWLObjectPropertyAssertionAxiom) axiom).getObject();
            return String.format("%s %s %s", getReadableIndividual(x),
                    getReadableObjectProperty(r), getReadableIndividual(y));
        }
        if (axiom instanceof OWLObjectPropertyDomainAxiom) {
            OWLObjectPropertyExpression r = ((OWLObjectPropertyDomainAxiom) axiom)
                    .getProperty();
            OWLClassExpression c = ((OWLObjectPropertyDomainAxiom) axiom).getDomain();
            return String.format("DOMAIN[%s] = %s", getReadableObjectProperty(r),
                    getReadableClass(c));
        }
        if (axiom instanceof OWLObjectPropertyRangeAxiom) {
            OWLObjectPropertyExpression r = ((OWLObjectPropertyRangeAxiom) axiom)
                    .getProperty();
            OWLClassExpression c = ((OWLObjectPropertyRangeAxiom) axiom).getRange();
            return String.format("RANGE[%s] = %s", getReadableObjectProperty(r),
                    getReadableClass(c));
        }
        if (axiom instanceof OWLDifferentIndividualsAxiom) {
            Set<OWLIndividual> individualSet = ((OWLDifferentIndividualsAxiom) axiom)
                    .getIndividuals();
            if (individualSet.isEmpty())
                return "";
            StringBuilder sb = new StringBuilder("DIFFERENT: {");
            for (OWLIndividual x : individualSet)
                sb.append(getReadableIndividual(x)).append(", ");
            sb.setLength(sb.length() - 2);
            sb.append("}");
            return sb.toString();
        }
        if (axiom instanceof OWLDeclarationAxiom) {
            OWLEntity e = ((OWLDeclarationAxiom) axiom).getEntity();
            String type = e.getEntityType().getPrintName();
            return String.format("DECLARATION OF %s: %s", type.toUpperCase(),
                    cleanURIs(e.toString()));
        }
        return cleanURIs(axiom.toString());
    }

    /**
     * Returns a human-readable expression for a set of axioms.
     * 
     * @param axioms
     *            the set of axioms
     * @param separator
     *            the separator between axioms
     * @return the expression
     */
    public static String generateExpressionForSet(Set<OWLAxiom> axioms,
            String separator) {
        if (axioms.isEmpty())
            return "{ }";
        StringBuilder sb = new StringBuilder("{ ");
        for (OWLAxiom axiom : axioms) // declarations first
            if (axiom instanceof OWLDeclarationAxiom)
                sb.append(generateExpression(axiom)).append(separator);
        for (OWLAxiom axiom : axioms)
            if (!(axiom instanceof OWLDeclarationAxiom))
                sb.append(generateExpression(axiom)).append(separator);
        sb.setLength(sb.length() - separator.length());
        if (sb.indexOf("\n") > -1)
            sb.append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Returns a human-readable expression for a set of axioms.
     * 
     * @param axioms
     *            the set of axioms
     * @return the expression
     */
    public static String generateExpressionForSet(Set<OWLAxiom> axioms) {
        return generateExpressionForSet(axioms, ",\n  ");
    }
}
