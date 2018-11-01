package main.operations.blackbox.kernel;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import main.operations.blackbox.AbstractBlackBoxShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class computes a single of kernel sets for the revision.
 *
 * @author Luis F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 */
public class RevisionBlackBoxKernel extends AbstractBlackBox {

    private OWLOntologyManager manager;
    private OWLReasonerFactory reasonerFactory;

    /**
     * Creates a variation of the BlackBox algorithm with the given expansion
     * and shrinking strategies.
     *
     * @param expansionStrategy
     *             the expansion strategy
     * @param shrinkingStrategy
     *             the shrinking strategy
     */
    public RevisionBlackBoxKernel(AbstractBlackBoxExpansionStrategy expansionStrategy, AbstractBlackBoxShrinkingStrategy shrinkingStrategy) {
        super(expansionStrategy, shrinkingStrategy);
    }


    @Override
    public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment, Set<OWLAxiom> initialSet) throws OWLOntologyCreationException {
        Set<OWLAxiom> expansionResult = this.expansionStrategy.expand(ontology, null);
        Set<OWLAxiom> shrinkingResult = this.shrinkingStrategy.shrink(expansionResult, null);

        return shrinkingResult;
    }
}
