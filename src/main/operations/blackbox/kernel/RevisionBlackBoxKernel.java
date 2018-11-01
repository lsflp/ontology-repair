package main.operations.blackbox.kernel;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class computes a single of kernel sets for the revision.
 *
 * @author Luis F. de M. C. Silva (inspired by Fillipe M. X. Resina)
 */
public class RevisionBlackBoxKernel {

    private OWLOntologyManager manager;
    private OWLReasonerFactory reasonerFactory;

    /**
     * Creates a variation of the BlackBox algorithm with the given expansion
     * and shrinking strategies.
     *
     * @param expansionStrategy
     *             the expansion strategy
     * @param shrinkingStrategy
     *             the shrinking strategy
     */
    public RevisionBlackBoxKernel(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
    }

    public Set<OWLAxiom> blackBox(Set<OWLAxiom> expandedOntology) throws OWLOntologyCreationException {
        Set<OWLAxiom> B = new HashSet<>();

        for (OWLAxiom beta : expandedOntology) {
            B.add(beta);
            if (!isConsistent(B)) {
                break;
            }
        }

        HashSet<OWLAxiom> aux = new HashSet<>();
        aux.addAll(B);

        for (OWLAxiom epsilon : B) {
            aux.remove(epsilon);
            if (isConsistent(aux)) {
                aux.add(epsilon);
            }
        }

        return aux;
    }

    private boolean isConsistent(Set<OWLAxiom> b) throws OWLOntologyCreationException {
        OWLOntology ontology = this.manager.createOntology(b);
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

        return reasoner.isConsistent();
    }
}
