package main.operations.blackbox.kernel;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import main.operations.blackbox.AbstractBlackBoxShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.Set;

public class RevisionBlackBoxKernel extends AbstractBlackBox {

    /**
     * Creates a variation of the BlackBox algorithm with the given expansion
     * and shrinking strategies.
     *
     * @param expansionStrategy the expansion strategy
     * @param shrinkingStrategy the shrinking strategy
     */
    public RevisionBlackBoxKernel(AbstractBlackBoxExpansionStrategy expansionStrategy, AbstractBlackBoxShrinkingStrategy shrinkingStrategy) {
        super(expansionStrategy, shrinkingStrategy);
    }

    @Override
    public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment, Set<OWLAxiom> initialSet) throws OWLOntologyCreationException {
        Set<OWLAxiom> expansionResult = expansionStrategy.expand(ontology, entailment);
        Set<OWLAxiom> shrinkingResult = shrinkingStrategy.shrink(ontology, entailment, expansionResult);

        return shrinkingResult;
    }
}
