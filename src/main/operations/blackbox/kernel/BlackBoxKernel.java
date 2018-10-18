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
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public class BlackBoxKernel extends AbstractBlackBox {

	/**
	 * Instantiates the class.
	 *
	 * @param expansionStrategy
	 *            the expansion strategy
	 * @param shrinkingStrategy
	 *            the shrinking strategy
	 */
	public BlackBoxKernel (AbstractBlackBoxExpansionStrategy expansionStrategy,
						   AbstractBlackBoxShrinkingStrategy shrinkingStrategy) {
		super(expansionStrategy, shrinkingStrategy);
	}

	@Override
	public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment, Set<OWLAxiom> initialSet) throws OWLOntologyCreationException {
		return blackBox(ontology, entailment);
	}

	@Override
	public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<OWLAxiom> expansionResult = expansionStrategy.expand(ontology, entailment);
		return shrinkingStrategy.shrink(expansionResult, entailment);
	}
}
