package main.operations.blackbox.kernel.expansionstrategies;

import main.operations.blackbox.kernel.AbstractBlackBoxKernelExpansionStrategy;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

public class RevisionBlackBoxKernelExpansionStrategy extends AbstractBlackBoxKernelExpansionStrategy {

    /**
     * Creates a variation of the expansion part of BlackBox algorithm.
     *
     * @param manager
     *            the OWL ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public RevisionBlackBoxKernelExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    @Override
    public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
        OWLOntology ontology = manager.createOntology(IRI.create("mips.owl"));
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

        for (OWLAxiom axiom : kb) {
            manager.addAxiom(ontology, axiom);
            if (!reasoner.isConsistent()) {
                break;
            }
        }

        return ontology.getAxioms();
    }
}
