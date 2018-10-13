package main.operations.blackbox.remainder.shrinkingstrategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.operations.blackbox.remainder.AbstractBlackBoxRemainderShrinkingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Implements the sliding window strategy for shrinking in BlackBox algorithm.
 *
 * @author Raphael M. Cóbe (adapted by Vinícius B. Matos)
 */
public class SlidingWindowBlackBoxRemainderShrinkingStrategy
        extends AbstractBlackBoxRemainderShrinkingStrategy {

    /**
     * The size of the sliding window.
     */
    private int windowSize = 10;

    /**
     * Gets the window size.
     *
     * @return the window size
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Sets the sliding window size.
     *
     * @param windowSize
     *            the window size
     */
    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     */
    public SlidingWindowBlackBoxRemainderShrinkingStrategy(OWLOntologyManager manager,
            OWLReasonerFactory reasonerFactory) {
        super(manager, reasonerFactory);
    }

    @Override
    public Set<OWLAxiom> shrink(Set<OWLAxiom> kb, OWLAxiom entailment, Set<OWLAxiom> keep)
            throws OWLOntologyCreationException {
        List<OWLAxiom> kbList = new ArrayList<>(kb);
        for (OWLAxiom x : keep)
            kbList.remove(x);
        int windowStart = 0;
        OWLOntology ontology = manager.createOntology(kb);
        while (windowStart < kbList.size()) {
            int windowEnd = windowStart + windowSize;
            if (windowEnd > (kbList.size() - 1)) {
                windowEnd = kbList.size() - 1;
            }
            HashSet<OWLAxiom> window = new HashSet<>(
                    kbList.subList(windowStart, windowEnd));
            manager.removeAxioms(ontology, window);
            remains.addAll(window);
            if (!isEntailed(ontology, entailment)) {
                break;
            } else {
                windowStart = windowStart + 1;
            }
        }
        return ontology.getAxioms();
    }
}
