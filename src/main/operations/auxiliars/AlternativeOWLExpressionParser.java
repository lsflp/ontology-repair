package main.operations.auxiliars;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.OWLExpressionParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxInlineAxiomParser;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;

/**
 * This is a parser of OWL axioms in Manchester Syntax.
 *
 * This class basically encapsulates the class
 * {@code ManchesterOWLSyntaxInlineAxiomParser}, but it uses an alternative
 * short form entity checker.
 *
 * @author Vinícius B. Matos
 */
public class AlternativeOWLExpressionParser {

    /**
     * The OWL ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * The ontology.
     */
    private OWLOntology ontology;

    /**
     * Creates the parser.
     *
     * @param manager
     *            the ontology manager
     * @param ontology
     *            the ontology
     */
    public AlternativeOWLExpressionParser(OWLOntologyManager manager,
            OWLOntology ontology) {
        this.manager = manager;
        this.ontology = ontology;
    }

    /**
     * Parses the axiom.
     *
     * @param expression
     *            the expression to parse
     * @return the parsed axiom in the form of an OWLAxiom object, or {@code null}
     *         if errors occur
     */
    public OWLAxiom parse(String expression) {
        return parse(manager, ontology, expression);
    }

    /**
     * Parses the axiom.
     *
     * @param manager
     *            the ontology manager
     * @param ontology
     *            the ontology
     * @param expression
     *            the expression to parse
     * @return the parsed axiom in the form of an OWLAxiom object, or {@code null}
     *         if errors occur
     */
    public static OWLAxiom parse(OWLOntologyManager manager, OWLOntology ontology,
            String expression) {
        Set<String> prefixes = manager.getOntologyFormat(ontology)
                .asPrefixOWLOntologyFormat().getPrefixName2PrefixMap().keySet();
        BidirectionalShortFormProviderAdapter sfp = new BidirectionalShortFormProviderAdapter(
                manager, Collections.singleton(ontology),
                new ManchesterOWLSyntaxPrefixNameShortFormProvider(ontology));
        OWLEntityChecker oec = new AlternativeShortFormEntityChecker(prefixes, sfp,
                ontology);
        OWLExpressionParser<OWLAxiom> parser = new ManchesterOWLSyntaxInlineAxiomParser(
                manager.getOWLDataFactory(), oec);
        try {
            return parser.parse(expression);
        } catch (OWLParserException e) {
            e.printStackTrace();
            return null;
        }
    }
}
