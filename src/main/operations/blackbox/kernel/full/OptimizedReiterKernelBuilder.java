package main.operations.blackbox.kernel.full;

import main.operations.blackbox.AbstractBlackBox;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Implements the optimized Reiter algorithm for computing the kernel
 * set.
 *
 * @author Raphael M. Cóbe (adapted by Luís F. de M. C. Silva)
 */
public class OptimizedReiterKernelBuilder extends AbstractReiterKernelBuilder {

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
	public OptimizedReiterKernelBuilder(AbstractBlackBox blackBox, OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
		super(blackBox, manager, reasonerFactory);
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<Set<OWLAxiom>> kernel = new HashSet<Set<OWLAxiom>>();

		Set<Set<OWLAxiom>> cut = new HashSet<Set<OWLAxiom>>();

		Stack<Set<OWLAxiom>> stack = new Stack<>();
		Set<OWLAxiom> element;
		Set<OWLAxiom> candidate;
		Set<OWLAxiom> hn;

		Set<Set<OWLAxiom>> cache = new HashSet<Set<OWLAxiom>>();

		OWLOntology ontology = manager.createOntology(kb);

		if (!isEntailed(ontology, entailment)) {
			return kernel;
		}
		element = this.blackBox.blackBox(ontology.getAxioms(), entailment);
		cache.add(element);
		kernel.add(element);
		for (OWLAxiom axiom : element) {
			Set<OWLAxiom> set = new HashSet<OWLAxiom>();
			set.add(axiom);
			stack.add(set);
		}
		// Reiter's algorithm
		while (!stack.isEmpty()) {
			hn = stack.pop();
			for (OWLAxiom axiom : hn) {
				manager.removeAxiom(ontology, axiom);
			}
			if (isEntailed(ontology, entailment)) {
				Set<OWLAxiom> kernelCache = findCache(cache, hn);
				if(kernelCache!= null){
					candidate = kernelCache;
				}
				else {
					candidate = blackBox.blackBox(ontology.getAxioms(), entailment);
				}
				kernel.add(candidate);
				for (OWLAxiom axiom : candidate) {
					Set<OWLAxiom> set2 = new HashSet<OWLAxiom>();
					set2.addAll(hn);
					set2.add(axiom);
					if (!stack.contains(set2)) {
						stack.add(set2);
					}

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

	private Set<OWLAxiom> findCache(Set<Set<OWLAxiom>> cache, Set<OWLAxiom> hn) {
		for (Set<OWLAxiom> eachKernel : cache){
			Set<OWLAxiom> copy = new HashSet<OWLAxiom>(eachKernel);
			copy.retainAll(hn);
			if(copy.isEmpty()){
				return eachKernel;
			}
		}
		return null;
	}
}
