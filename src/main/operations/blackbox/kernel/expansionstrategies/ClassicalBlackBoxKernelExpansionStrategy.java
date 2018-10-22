package main.operations.blackbox.kernel.expansionstrategies;

import main.operations.blackbox.kernel.AbstractBlackBoxKernelExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Collections;
import java.util.Set;

/**
 * Implements the classical method for the expansion part of BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public class ClassicalBlackBoxKernelExpansionStrategy extends AbstractBlackBoxKernelExpansionStrategy {

	/**
	 * Creates a variation of the expansion part of BlackBox algorithm.
	 *
	 * @param manager
	 *            the OWL ontology manager
	 * @param reasonerFactory
	 *            a factory that constructs the reasoner
	 */
	public ClassicalBlackBoxKernelExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
		super(manager, reasonerFactory);
	}

	@Override
	public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<OWLAxiom> toReturn;
		OWLOntology ontology = manager.createOntology(kb);
		if (isEntailed(ontology, entailment)) {
			toReturn = kb;
		}
		else {
			toReturn = Collections.emptySet();
		}
		return toReturn;
	}

}
