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
	 * The capacity of the queue used by this algorithm.
	 */
	private int maxQueueSize = Integer.MAX_VALUE;

	/**
	 * The maximum number of elements of the kernel set that will be computed.
	 */
	private int maxKernelElements = Integer.MAX_VALUE;

	private Set<Set<OWLAxiom>> cut = new HashSet<>();

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

	/**
	 * {@inheritDoc}
	 *
	 * The result may not be the full kernel set if the limit of the queue
	 * capacity or the limit of the computed kernel set size is too slow.
	 */
	public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<Set<OWLAxiom>> kernelSet = new HashSet<>();

		Queue<Set<OWLAxiom>> queue = new LinkedList<>();
		Set<OWLAxiom> element, candidate, hn;

		OWLOntology ontology = manager.createOntology(kb);

		if (!isEntailed(ontology, entailment)) {
			return kernelSet;
		}

		element = this.blackBox.blackBox(ontology.getAxioms(), entailment);
		kernelSet.add(element);
		for (OWLAxiom axiom : element) {
			if (queue.size() >= maxQueueSize)
				break;
			Set<OWLAxiom> set = new HashSet<>();
			set.add(axiom);
			queue.add(set);
		}

		if (kernelSet.size() >= maxKernelElements)
			return kernelSet;

		// Reiter's algorithm
		while (!queue.isEmpty()) {
			hn = queue.remove();
			for (OWLAxiom axiom : hn) {
				manager.removeAxiom(ontology, axiom);
			}
			if (isEntailed(ontology, entailment)) {
				candidate = blackBox.blackBox(ontology.getAxioms(), entailment);
				kernelSet.add(candidate);
				for (OWLAxiom axiom : candidate) {
					if (queue.size() >= maxQueueSize)
						break;
					Set<OWLAxiom> set2 = new HashSet<>();
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
		return kernelSet;
	}

	/**
	 * Sets the capacity of the queue used by the algorithm.
	 *
	 * @param maxQueueSize
	 *            the limit of the size of the queue
	 *
	 */
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}

	/**
	 * Gets the capacity of the queue used by the algorithm.
	 *
	 * @return the limit of the size of the queue
	 *
	 */
	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	/**
	 *
	 * Gets the maximum number of elements in the computed kernel set.
	 *
	 * @return the maximum size of the computed kernel set
	 */
	public int getMaxKernelElements() {
		return maxKernelElements;
	}

	/**
	 *
	 * Sets the maximum number of elements in the computed kernel set.
	 *
	 * @param maxKernelElements
	 *            the maximum size of the computed kernel set
	 */
	public void setMaxKernelElements(int maxKernelElements) {
		this.maxKernelElements = maxKernelElements;
	}

	/**
	 *
	 * Gets a cut generated during the execution of kernelSet.
	 *
	 * @return cut
	 */
	public Set<Set<OWLAxiom>> getCut() {
		return cut;
	}
}
