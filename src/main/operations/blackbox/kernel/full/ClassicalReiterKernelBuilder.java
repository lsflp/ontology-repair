package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Implements the classical Reiter algorithm for computing the kernel
 * set.
 *
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public class ClassicalReiterKernelBuilder extends AbstractReiterKernelBuilder {

	/**
	 * Instantiates the class.
	 *
	 * @param blackBox
	 *            an implementation of the blackbox algorithm
	 * @param manager
	 *            the ontology manager
	 * @param reasonerFactory
	 *            a factory that constructs the reasoner
	 */
	public ClassicalReiterKernelBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
		super(blackBox, manager, reasonerFactory);
	}

	public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<Set<OWLAxiom>> kernel = new HashSet<Set<OWLAxiom>>();

		Set<Set<OWLAxiom>> cut = new HashSet<Set<OWLAxiom>>();

		Queue<Set<OWLAxiom>> queue = new LinkedList<Set<OWLAxiom>>();
		Set<OWLAxiom> element;
		Set<OWLAxiom> candidate;
		Set<OWLAxiom> hn;

		OWLOntology ontology = manager.createOntology(kb);

		if (!isEntailed(ontology, entailment)) {
			return kernel;
		}
		element = this.blackBox.blackBox(ontology.getAxioms(), entailment);
		kernel.add(element);
		for (OWLAxiom axiom : element) {
			Set<OWLAxiom> set = new HashSet<OWLAxiom>();
			set.add(axiom);
			queue.add(set);
		}
		// Reiter's algorithm
		while (!queue.isEmpty()) {
			hn = queue.remove();
			for (OWLAxiom axiom : hn) {
				manager.removeAxiom(ontology, axiom);
			}
			if (isEntailed(ontology, entailment)) {
				candidate = blackBox.blackBox(ontology.getAxioms(), entailment);
				kernel.add(candidate);
				for (OWLAxiom axiom : candidate) {
					Set<OWLAxiom> set2 = new HashSet<OWLAxiom>();
					set2.addAll(hn);
					set2.add(axiom);
					queue.add(set2);
				}
			} else
				cut.add(hn);

			// Restore to the ontology the axioms removed so it can be used
			// again
			for (OWLAxiom axiom : hn) {
				manager.addAxiom(ontology, axiom);
			}
		}
		return kernel;
	}

}
