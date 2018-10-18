package main.operations.blackbox;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Represents a variation of some part (expansion or shrinking) of BlackBox
 * algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class MatosOperatorStrategy {
    /**
     * The OWL ontology manager.
     */
    protected OWLOntologyManager manager;

    /**
     * A factory that constructs the reasoner.
     */
    protected OWLReasonerFactory reasonerFactory;

    /**
     *
     * Creates a variation of some part of BlackBox algorithm.
     *
     * @param manager
     *            the OWL ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public MatosOperatorStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
    }

    /**
     * Checks if a given ontology entails a given formula.
     *
     * @param ontology
     *            the ontology
     * @param entailment
     *            the formula
     * @return {@code true} if the formula is entailed, {@code false} otherwise
     */
    public boolean isEntailed(OWLOntology ontology, OWLAxiom entailment) {
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
        boolean isEntailed = reasoner.isEntailed(entailment);
        return isEntailed;
    }

}
