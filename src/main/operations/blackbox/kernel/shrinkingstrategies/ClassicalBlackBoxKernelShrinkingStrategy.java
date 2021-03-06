package main.operations.blackbox.kernel.shrinkingstrategies;

import main.operations.blackbox.kernel.AbstractBlackBoxKernelShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Implements the classical method for the shrinking part of BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public class ClassicalBlackBoxKernelShrinkingStrategy extends AbstractBlackBoxKernelShrinkingStrategy {

	/**
	 * Instantiate the class.
	 *
	 * @param manager
	 *            the OWL ontology manager
	 * @param reasonerFactory
	 *            a factory that constructs the reasoner
	 */
	public ClassicalBlackBoxKernelShrinkingStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
		super(manager, reasonerFactory);
	}

	@Override
	public Set<OWLAxiom> shrink(Set<OWLAxiom> kb, OWLAxiom entailment, Set<OWLAxiom> keep) throws OWLOntologyCreationException {
		OWLOntology ontology= manager.createOntology(kb);
		for (OWLAxiom axiom : kb){
			manager.removeAxiom(ontology, axiom);
			if(!isEntailed(ontology, entailment)){
				manager.addAxiom(ontology, axiom);
			}
		}
		return ontology.getAxioms();
	}


}
