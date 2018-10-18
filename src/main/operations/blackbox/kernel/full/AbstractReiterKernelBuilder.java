package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.OperatorStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Represents a generic algorithm for computing the kernel set.
 *
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public abstract class AbstractReiterKernelBuilder extends OperatorStrategy {

	/**
	 * An implementation of the BlackBox algorithm.
	 */
	protected AbstractBlackBox blackBox;

	/**
     * A factory that constructs the reasoner.
	 */
	protected OWLReasonerFactory reasonerFactory;

	/**
	 * Computes the kernel set of an ontology in relation to a formula.
	 *
	 * @param kb
	 *            the axioms in the ontology
	 * @param entailment
	 *            the formula that must not be implied by the elements of the
	 *            remainder set
	 * @return the full remainder set
	 * @throws OWLOntologyCreationException
	 *             OWLOntologyCreationException
	 */
	public abstract Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment)
			throws OWLOntologyCreationException;

	/**
	 * Instantiates the class.
	 *
	 * @param blackBox
	 *            an implementation of the blackbox algorithm
	 * @param manager
	 *            the ontology manager
	 * @param reasonerFactory
	 *            a factory that builds the reasoner
	 */
	protected AbstractReiterKernelBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager,
											 OWLReasonerFactory reasonerFactory) {
		super(manager, reasonerFactory);
		this.blackBox = blackBox;
		this.reasonerFactory = reasonerFactory;
	}

}
