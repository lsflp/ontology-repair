package main.operations.blackbox.kernel.shrinkingstrategies;

import main.operations.blackbox.kernel.AbstractBlackBoxKernelShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an alternative method for the shrinking part of BlackBox algorithm.
 *
 * @author Luís F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 */
public class RevisionBlackBoxKernelShrinkingStrategy extends AbstractBlackBoxKernelShrinkingStrategy {

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the OWL ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public RevisionBlackBoxKernelShrinkingStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    @Override
    public Set<OWLAxiom> shrink(Set<OWLAxiom> kb, OWLAxiom entailment, Set<OWLAxiom> inconsistent) throws OWLOntologyCreationException {
        Set<OWLAxiom> kernelElement = new HashSet<OWLAxiom>();

        OWLOntology ontology = manager.createOntology(inconsistent);
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

        for (OWLAxiom axiom : kb) {
            if (inconsistent.contains(axiom)) {
                manager.removeAxiom(ontology, axiom);
                if (isConsistent(ontology)) {
                    kernelElement.add(axiom);
                    manager.addAxiom(ontology, axiom);
                }
            }
        }

        return kernelElement;
    }
}
