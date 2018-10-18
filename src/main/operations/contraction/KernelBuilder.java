package main.operations.contraction;

import main.operations.blackbox.AbstractBlackBox;
import main.operations.blackbox.kernel.BlackBoxKernel;
import main.operations.blackbox.kernel.expansionstrategies.ClassicalBlackBoxKernelExpansionStrategy;
import main.operations.blackbox.kernel.full.ClassicalReiterKernelBuilder;
import main.operations.blackbox.kernel.shrinkingstrategies.ClassicalBlackBoxKernelShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Provides the computation of the kernel set.
 *
 * Basically, this class just encapsulates the real implementation.
 *
 * @author Luís F. de M. C. Silva (inspired by Luís F. de M. C. Silva)
 *
 */
public class KernelBuilder {

    /**
     * The ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * The factory that constructs the remainder.
     */
    private OWLReasonerFactory reasonerFactory;

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the remainder set that will be
     * computed.
     */
    private int maxRemainderElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public KernelBuilder(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
    }

    /**
     * Computes the full remainder set of an ontology in relation to a formula.
     * The result may not be the full remainder set if the limit of the queue
     * capacity or the limit of the computed remainder set size is too slow.
     *
     * @param kb
     *            the belief set
     * @param entailment
     *            the formula that must not be implied by the elements of the
     *            remainder set
     * @return the computed remainder set
     * @throws OWLOntologyChangeException
     *             OWLOntologyChangeException
     *
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     */
    public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment)
            throws OWLOntologyChangeException, OWLOntologyCreationException {
        AbstractBlackBox blackbox = new BlackBoxKernel(
                new ClassicalBlackBoxKernelExpansionStrategy(manager, reasonerFactory),
                new ClassicalBlackBoxKernelShrinkingStrategy(manager, reasonerFactory));
        ClassicalReiterKernelBuilder kn = new ClassicalReiterKernelBuilder(blackbox, manager, reasonerFactory);
        return kn.kernelSet(kb, entailment);
    }

}
