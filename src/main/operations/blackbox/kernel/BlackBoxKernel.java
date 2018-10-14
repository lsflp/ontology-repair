package main.operations.blackbox.kernel;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import main.operations.blackbox.AbstractBlackBoxShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.Set;

/**
 * This class computes a single of kernel sets.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos and Luis F. de M. C. Silva)
 */
public class BlackBoxKernel extends AbstractBlackBox {
    public BlackBoxKernel(AbstractBlackBoxExpansionStrategy expansionStrategy,
                          AbstractBlackBoxShrinkingStrategy shrinkingStrategy) {
        super(expansionStrategy, shrinkingStrategy);

    }

    @Override
    public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment,
                                  Set<OWLAxiom> initialSet)
                                  throws OWLOntologyCreationException {
        Set<OWLAxiom> expansionResult = this.expansionStrategy.expand(ontology, entailment);
        return this.shrinkingStrategy.shrink(expansionResult, entailment);
    }
}
