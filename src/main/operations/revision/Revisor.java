package main.operations.revision;

import main.operations.blackbox.kernel.RevisionKernelBuilder;
import main.operations.selectionfunctions.SelectionFunction;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements a Belief Revision operation called Revision.
 *
 * The resulting set must have the formula α and can not be inconsistent.
 *
 * @author Luís F. de M. C. Silva
 */
public class Revisor {

    protected String UniformityType = "no uniformity";
    protected String SuccessType = "strong success";
    protected String MinimalityType = "core retainment";

    private OWLOntologyManager manager;
    private ReasonerFactory reasonerFactory;
    private SelectionFunction sigma;

    private Set<Set<OWLAxiom>> cut;

    public Revision(OWLModelManager man, HashMap<String, String> options){
        if (options != null){
            if(options.containsKey("Success")){
                SuccessType = options.get("Success");
            }
            if(options.containsKey("Uniformity")){
                UniformityType = options.get("Uniformity");
            }
            if(options.containsKey("Minimality Type")){
                MinimalityType = options.get("Minimality Type");
            }
        }
    }

    public Revisor(OWLOntologyManager manager, ReasonerFactory reasonerFactory, SelectionFunction sigma) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.sigma = sigma;
    }

    public Set<Set<OWLAxiom>> revise (OWLOntology B, OWLAxiom sentence)
            throws OWLOntologyChangeException, OWLOntologyCreationException{

        HashMap<String, String> opt = new HashMap<String, String>();
        opt.put("MinimalityType", MinimalityType);

        manager.addAxiom(B, sentence);

        RevisionKernelBuilder revisionKernelBuilder = new RevisionKernelBuilder(manager, reasonerFactory);
        this.cut = revisionKernelBuilder.getCut();
        Set<Set<OWLAxiom>> revision;

        try {
            if(MinimalityType == "core retainment")
                revision = revisionKernelBuilder.kernelSet(B.getAxioms(), null);
            else{
                revision = reiter(B);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }


        if (SuccessType == "no success")
            return revision;

        Set< Set <OWLAxiom> > containedAlpha = new HashSet< Set<OWLAxiom> >();
        Set< Set <OWLAxiom> > notContainedAlpha = new HashSet< Set<OWLAxiom> >();

        for (Set<OWLAxiom>X: revision){
            if (X.contains(sentence)){
                X.remove(sentence);
                containedAlpha.add(X);

                if(X.isEmpty()){
                    if (SuccessType == "strong success")
                        X.add(sentence);
                    else if (SuccessType == "weak success")
                        revision.remove(X);
                }
            }
            else
                notContainedAlpha.add(X);
        }


        //TODO testar essa parte
        if(UniformityType == "weak uniformity"){
            Set<OWLAxiom> toBeProtected = new HashSet<OWLAxiom>();
            for (Set<OWLAxiom>cA: containedAlpha){
                for (Set<OWLAxiom>nCA: notContainedAlpha){
                    if (nCA.containsAll(cA)){
                        nCA.removeAll(cA);
                        if (nCA.size() == 1){
                            for(OWLAxiom beta: nCA){
                                OWLOntologyManager managerAlpha = OWLManager.createOWLOntologyManager();
                                OWLOntologyManager managerBeta = OWLManager.createOWLOntologyManager();
                                OWLOntology ontAlpha = managerAlpha.createOntology(IRI.create("alpha.owl"));
                                OWLOntology ontBeta = managerBeta.createOntology(IRI.create("beta.owl"));
                                PelletReasoner reasonerAlpha = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontAlpha);
                                PelletReasoner reasonerBeta = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontBeta);
                                managerAlpha.addOntologyChangeListener(reasonerAlpha);
                                managerBeta.addOntologyChangeListener(reasonerBeta);

                                AddAxiom addBeta = new AddAxiom(ontAlpha, beta);
                                AddAxiom addAlpha = new AddAxiom(ontBeta, alpha);
                                managerBeta.applyChange(addBeta);
                                managerAlpha.applyChange(addAlpha);
                                if (reasonerAlpha.isEntailed(beta) && reasonerBeta.isEntailed(alpha))
                                    toBeProtected.add(beta);
                            }
                        }
                    }
                }
            }
            for (Set<OWLAxiom>X: revision){
                for (OWLAxiom beta: toBeProtected)
                    X.remove(beta);
            }
        }


        return revision;
    }

//    public Set<Set<OWLAxiom>> mips(OWLOntology B){
//        try {
//            Set<Set<OWLAxiom>> mip = kernelMips(B);
//            if(MinimalityType == "core retainment")
//                return mip;
//            else{
//                return reiter(B);
//            }
//        } catch (OWLOntologyCreationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (OWLOntologyChangeException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }

//    /**
//     * Method that compute, for revision, all the elements of the kernel of B
//     * using one element of the kernel (obtained by a black-box algorithm through
//     * the method kernelMipsElement in this class) and applying to it an adaptation
//     * of Reiter's algorithm.
//     *
//     * @param B - an inconsistent ontology for which we will compute its kernel
//     *
//     * @return mips - the kernel of B
//     */
//    public Set< Set<OWLAxiom> > kernelMips(OWLOntology B) throws OWLOntologyChangeException, OWLOntologyCreationException{
//
//        Set< Set<OWLAxiom> > mips = new HashSet<Set <OWLAxiom> >();
//        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//
//        PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(B);
//        manager.addOntologyChangeListener(reasoner);
//
//        Queue<Set<OWLAxiom>> queue = new LinkedList<Set<OWLAxiom>>();
//        Set<OWLAxiom> candidate = null;
//        Set<OWLAxiom> hn;
////    	boolean haveToContinue = false;
//
////    	AxiomConverter converter = new AxiomConverter( (KnowledgeBase) B, factory );
////		pellet.getKB().setDoExplanation( true );
////		set returned by the tracing
////		exp = convertExplanation( converter, pellet.getKB().getExplanationSet() );
////
////		exp = convertExplanation( factory, converter, pellet.getKB().getExplanationSet() );
////
//        Set<OWLAxiom> exp = null;
////		exp = convertExplanation(factory, converter, reasoner.getKB().getExplanationSet() );
//        exp = B.getAxioms();
//
//        //Se a ontologia já é consistente não há o que calcular
//        if (reasoner.isConsistent()){
//            return mips;
//        }
//
//        Set<OWLAxiom> X = new HashSet<OWLAxiom>();
//        X = kernelMipsElement(exp);
//        mips.add(X);
//
//        for(OWLAxiom axiom : X){
//            Set<OWLAxiom> set = new HashSet<OWLAxiom>();
//            set.add(axiom);
//            queue.add(set);
//        }
//        //Reiter's algorithm
//        while(!queue.isEmpty()) {
//            hn = queue.remove();
//
////			haveToContinue = false;
////			for(Set<OWLAxiom> set : cut) {
////				//Check if there is an element of cut that is in hn
////    			if(hn.containsAll(set)) {
////    				haveToContinue = true;
////    				break;
////    			}
////			}
////    		if(haveToContinue)
////    			continue;
//            for(OWLAxiom axiom : hn) {
//                RemoveAxiom removeAxiom = new RemoveAxiom(B, axiom);
//                manager.applyChange(removeAxiom);
//            }
//            if(!reasoner.isConsistent()) {
//                exp = B.getAxioms();
//                candidate = this.kernelMipsElement(exp);
//                kernel.add(candidate);
//                for(OWLAxiom axiom : candidate) {
//                    Set<OWLAxiom> set2 = new HashSet<OWLAxiom>();
//                    set2.addAll(hn);
//                    set2.add(axiom);
//                    queue.add(set2);
//                }
//            }
////    		else cut.add(hn);
//
//            //Restore to the ontology the axioms removed so it can be used again
//            for(OWLAxiom axiom : hn) {
//                AddAxiom addAxiom = new AddAxiom(B, axiom);
//                manager.applyChange(addAxiom);
//            }
//        }
//
//        return mips;
//    }

//    /**
//     * Method that compute, for revision, one element of the kernel of exp
//     * using the strategy expand-shrink
//     *
//     * @param exp - a set of axioms from which we will extract one element of its kernel
//     *
//     * @return X - a kernel element
//     */
//    private Set<OWLAxiom> kernelMipsElement(Set<OWLAxiom> exp) throws OWLOntologyCreationException, OWLOntologyChangeException{
//        // X é um elemento do kernel
//        Set<OWLAxiom> X = new HashSet<OWLAxiom>();
//
//        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        OWLOntology ont = manager.createOntology(IRI.create("mips.owl"));
//        PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
//        manager.addOntologyChangeListener(reasoner);
//
//        // First Part: EXPAND
//        // Adicionamos os axiomas de exp na ontologia criada até que ela
//        // seja inconsistente
//        for (OWLAxiom axiom: exp){
//            AddAxiom addAxiom = new AddAxiom(ont, axiom);
//            manager.applyChange(addAxiom);
//            if (!reasoner.isConsistent()){
//                break;
//            }
//        }
//
//        // Second Part: SHRINK
//        // Para cada axioma em exp, removemo-lo da ontologia ont (se contido) e
//        // verificamos se ela não é mais inconsistente. Nesse caso, o axioma é
//        // necessário para gerar a inconsistência e, portanto, deve fazer parte
//        // de X, que pertence ao kernel
//        for (OWLAxiom axiom : exp){
//            if(ont.containsAxiom(axiom)) {
//                RemoveAxiom removeAxiom = new RemoveAxiom(ont, axiom);
//                manager.applyChange(removeAxiom);
//                if (reasoner.isConsistent()){
//                    X.add(axiom);
//                    AddAxiom addAxiom = new AddAxiom(ont, axiom);
//                    manager.applyChange(addAxiom);
//                }
//            }
//        }
//        return X;
//    }

    private Set<Set<OWLAxiom>> reiter(OWLOntology ontology){
        Set<Set<OWLAxiom>> remainderSets = new HashSet<>();

        try {
            for(Set<OWLAxiom> set : cut) {
                manager.removeAxioms(ontology, set);
                remainderSets.add(ontology.getAxioms());
                manager.addAxioms(ontology, set);
            }
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        }

        return remainderSets;
    }
}
