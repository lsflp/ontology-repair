package main.operations.blackbox.kernel.expansionstrategies;

import main.operations.blackbox.kernel.AbstractBlackBoxKernelExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an alternative algorithm for computing the kernel set,
 * used for the revision operation.
 *
 * @author Luís F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 */
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
    public Set<OWLAxiom> expand(Set<OWLAxiom> expandedOntology, OWLAxiom entailment) throws OWLOntologyCreationException {
        Set<OWLAxiom> B = new HashSet<>();

        for (OWLAxiom beta : expandedOntology) {
            B.add(beta);
            if (!isConsistent(B)) {
                break;
            }
        }

        return B;
    }
}
