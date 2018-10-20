package main.operations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.operations.auxiliars.AlternativeOWLExpressionParser;
import main.operations.selectionfunctions.SelectionFunctionFull;
import main.operations.srwpseudocontraction.SRWPseudoContractor;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * Main class of the SRW pseudo-contractor.
 *
 * @author Vinícius B. Matos
 *
 */
public class SRWPseudoContraction {


    private String inputFileName;
    private String outputFileName;
    private String formulaString;
    private Integer maxQueueSize;
    private Integer maxRemainderSize;

    public SRWPseudoContraction(String inputFileName, String outputFileName, String formulaString,
                                Integer maxQueueSize, Integer maxRemainderSize) {

        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.formulaString = formulaString;
        this.maxQueueSize = maxQueueSize;
        this.maxRemainderSize = maxRemainderSize;
    }

    public void run() {
        OWLOntologyManager manager;
        OWLOntology ontology;
        Logger.getLogger("SRW").log(Level.INFO, "Opening the ontology...");
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(inputFileName));
        } catch (Exception e) {
            Logger.getLogger("SRW").log(Level.SEVERE, String
                    .format("Could not open the ontology file '%s'.\n", inputFileName));
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO, "Parsing the formula...");
        OWLAxiom entailment = AlternativeOWLExpressionParser.parse(manager, ontology,
                formulaString);
        if (entailment == null) {
            Logger.getLogger("SRW").log(Level.SEVERE,
                    String.format("Bad formula: \n\t%s\n", formulaString));
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO, "Creating the pseudo-contractor...");
        SRWPseudoContractor pseudoContractor = new SRWPseudoContractor(manager,
                new ReasonerFactory(), new SelectionFunctionFull());
        pseudoContractor.setMaxRemainderElements(maxRemainderSize);
        pseudoContractor.setMaxQueueSize(maxQueueSize);
        Logger.getLogger("SRW").log(Level.INFO, "Executing the operation...");
        OWLOntology inferredOntology;
        try {
            inferredOntology = manager.createOntology(
                    pseudoContractor.pseudocontract(ontology, entailment));
        } catch (OWLException e) {
            e.printStackTrace();
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO, "Saving...");
        try {
            OutputStream s = new FileOutputStream(outputFileName);
            manager.saveOntology(inferredOntology, s);
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            Logger.getLogger("SRW").log(Level.SEVERE, String
                    .format("Could not save the ontology into '%s.'\n", outputFileName));
            e.printStackTrace();
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO,
                String.format("Success! Ontology saved to '%s'.\n", outputFileName));
    }

}
