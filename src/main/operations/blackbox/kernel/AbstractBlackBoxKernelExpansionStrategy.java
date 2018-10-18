package main.operations.blackbox.kernel;

import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Represents a variation of the expansion part of BlackBox algorithm used to
 * compute elements of kernel sets.
 *
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public abstract class AbstractBlackBoxKernelExpansionStrategy extends AbstractBlackBoxExpansionStrategy {

	/**
	 * Creates a variation of the expansion part of BlackBox algorithm.
	 *
	 * @param manager
	 *            the OWL ontology manager
	 * @param reasonerFactory
	 *            a factory that constructs the reasoner
	 */
	public AbstractBlackBoxKernelExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
		super(manager, reasonerFactory);
	}

}
