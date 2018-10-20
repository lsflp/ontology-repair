package main.operations.contraction;

import main.operations.auxiliars.AxiomGenerators;
import main.operations.auxiliars.HumanReadableAxiomExpressionGenerator;
import main.operations.blackbox.remainder.RemainderBuilder;
import main.operations.selectionfunctions.SelectionFunction;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a Belief Revision operation called Contraction, using the Kernel Constructor.
 *
 * It is defined as:
 *
 * ∩ γ(B ⊥ α),
 *
 * where γ is a selection function that chooses some elements of the remainder
 * set
 *
 * The resulting set must not imply the formula α.
 *
 * @author Luís F. de M. C. Silva (highly inspired by Vinícius B. Matos)
 *
 */
public class PartialMeetContractor {

    /**
     * A factory that constructs the reasoner.
     */
    private OWLReasonerFactory reasonerFactory;

    /**
     * The ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * A selection function implementation.
     */
    private SelectionFunction gamma;

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the remainder set that will be computed.
     */
    private int maxRemainderElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     * @param gamma
     *            a selection function implementation
     */
    public PartialMeetContractor(OWLOntologyManager manager,
                               OWLReasonerFactory reasonerFactory, SelectionFunction gamma) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.gamma = gamma;
    }

    /**
     * Executes the partial meet contraction operation on the ontology.
     *
     * @param ontology
     *            the initial ontology
     * @param sentence
     *            the sentence to be partial meet contracted
     * @return the resulting belief set, not implying the sentence
     * @throws OWLException
     *             OWLException
     */
    public Set<OWLAxiom> partialMeetContract(OWLOntology ontology, OWLAxiom sentence)
            throws OWLException {
        if (Logger.getLogger("PMC").isLoggable(Level.FINE)) {
            Logger.getLogger("PMC").log(Level.FINE,
                    "\n---------- ORIGINAL ONTOLOGY: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(ontology.getAxioms()));
            Logger.getLogger("PMC").log(Level.FINE,
                    "\n---------- FORMULA TO BE CONTRACTED: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpression(sentence));
        }
        // create reasoner
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        // close under Cn
        OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = AxiomGenerators.allAxiomGenerators();
        InferredOntologyGenerator ontologyGenerator = new InferredOntologyGenerator(
                reasoner, gens);
        ontologyGenerator.fillOntology(manager.getOWLDataFactory(), inferredOntology);
        manager.addAxioms(inferredOntology, ontology.getAxioms()); // keep asserted axioms
        if (Logger.getLogger("PMC").isLoggable(Level.FINE)) {
            Logger.getLogger("PMC").log(Level.FINE,
                    "\n---------- ONTOLOGY CLOSED UNDER Cn*: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(
                                    inferredOntology.getAxioms()));
        }
        // obtain remainder
        RemainderBuilder remainderBuilder = new RemainderBuilder(
                OWLManager.createOWLOntologyManager(), reasonerFactory);
        remainderBuilder.setMaxQueueSize(maxQueueSize);
        remainderBuilder.setMaxRemainderElements(maxRemainderElements);
        Set<OWLAxiom> kb = inferredOntology.getAxioms();
        if (kb.isEmpty())
            throw new OWLException("The reasoner has failed to find the logic closure.");
        Set<Set<OWLAxiom>> remainderSet = remainderBuilder.remainderSet(kb, sentence);
        // apply a selection function
        Set<Set<OWLAxiom>> best = gamma.select(ontology, remainderSet);
        if (Logger.getLogger("PMC").isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder(
                    "\n---------- " + (best.size()) + " SELECTED REMAINDER ELEMENT"
                            + (best.size() != 1 ? "S" : "") + ": \n");
            int i = 0;
            for (Set<OWLAxiom> s : best) {
                sb.append(String.format("\n[%d/%d]:\n", ++i, best.size())
                        + HumanReadableAxiomExpressionGenerator
                        .generateExpressionForSet(s));
            }
            Logger.getLogger("PMC").log(Level.FINER, sb.toString());
        }
        // find intersection
        Iterator<Set<OWLAxiom>> it = best.iterator();
        Set<OWLAxiom> intersection = it.next();
        while (it.hasNext()) {
            Set<OWLAxiom> s = it.next();
            Set<OWLAxiom> removing = new HashSet<OWLAxiom>();
            for (OWLAxiom a : intersection) {
                if (!s.contains(a))
                    removing.add(a);
            }
            for (OWLAxiom a : removing)
                intersection.remove(a);
        }
        // remove "disjoint of owl:Nothing" axioms
        // (this prevents Protégé from showing owl:Nothing as a subclass of
        // owl:Thing)
        for (Iterator<OWLAxiom> i = intersection.iterator(); i.hasNext();) {
            OWLAxiom axiom = i.next();
            if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)) {
                for (OWLClass c : ((OWLDisjointClassesAxiom) axiom)
                        .getClassesInSignature())
                    if (c.isBottomEntity()) {
                        i.remove();
                        break;
                    }
            }
        }
        if (Logger.getLogger("PMC").isLoggable(Level.FINE)) {
            Logger.getLogger("PMC").log(Level.FINE,
                    "\n---------- FINAL ONTOLOGY: \n"
                            + HumanReadableAxiomExpressionGenerator
                            .generateExpressionForSet(intersection));
        }
        // return intersection as an ontology
        return intersection;
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
     * Gets the maximum number of elements in the computed remainder set.
     *
     * @return the maximum size of the computed remainder set
     */
    public int getMaxRemainderElements() {
        return maxRemainderElements;
    }

    /**
     *
     * Sets the maximum number of elements in the computed remainder set.
     *
     * @param maxRemainderElements
     *            the maximum size of the computed remainder set
     */
    public void setMaxRemainderElements(int maxRemainderElements) {
        this.maxRemainderElements = maxRemainderElements;
    }
}
