package main.operations.blackbox.kernel;

import main.operations.blackbox.AbstractBlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Set;

/**
 * Represents a variation of the expansion part of BlackBox algorithm used to
 * compute elements of remainder sets.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public abstract class AbstractBlackBoxKernelExpansionStrategy extends AbstractBlackBoxExpansionStrategy {
    /**
     * The elements that the expander will try to add to the set.
     */
    protected Set<OWLAxiom> kernel;

    /**
     *
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public AbstractBlackBoxKernelExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    /**
     *
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     * @param remains
     *            the elements that the expander will try to add to the set
     */
    public AbstractBlackBoxKernelExpansionStrategy(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory,
                                                   Set<OWLAxiom> remains) {
        super(manager, reasonerFactory);
        this.kernel = remains;
    }

    /**
     *
     * Gets the remaining elements.
     *
     * @return the elements that the expander will try to add to the set
     */
    public Set<OWLAxiom> getKernel() {
        return kernel;
    }

    /**
     *
     * Sets the remaining elements.
     *
     * @param kernel
     *            the elements that the expander will try to add to the set
     */
    public void setKernel(Set<OWLAxiom> kernel) {
        this.kernel = kernel;
    }

}
