package main.operations.blackbox.remainder;

import java.util.Set;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import main.operations.blackbox.AbstractBlackBoxShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * This class computes a single of remainder sets.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class BlackBoxRemainder extends AbstractBlackBox {

    /**
     * Instantiates the class.
     *
     * @param expansionStrategy
     *            the expansion strategy
     * @param shrinkingStrategy
     *            the shrinking strategy
     */
    public BlackBoxRemainder(AbstractBlackBoxExpansionStrategy expansionStrategy,
                             AbstractBlackBoxShrinkingStrategy shrinkingStrategy) {
        super(expansionStrategy, shrinkingStrategy);
    }

    @Override
    public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment,
            Set<OWLAxiom> initialSet) throws OWLOntologyCreationException {
        Set<OWLAxiom> contractionResult = this.shrinkingStrategy.shrink(ontology,
                entailment, initialSet);
        Set<OWLAxiom> remains = ((AbstractBlackBoxRemainderShrinkingStrategy) this.shrinkingStrategy)
                .getRemains();
        ((AbstractBlackBoxRemainderExpansionStrategy) this.expansionStrategy)
                .setRemains(remains);
        return this.expansionStrategy.expand(contractionResult, entailment);
    }
}
