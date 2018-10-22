package main.operations;

import main.operations.auxiliars.AlternativeOWLExpressionParser;
import main.operations.revision.Revisor;
import main.operations.selectionfunctions.SelectionFunctionFull;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class of the revisor.
 *
 * @author Luís F. de M. C. Silva
 *
 */
public class Revision {
    private String inputFileName;
    private String outputFileName;
    private String formulaString;
    private Integer maxQueueSize;
    private Integer maxSetSize;
    private Integer success;
    private Boolean coreRetainment;

    public Revision (String inputFileName, String outputFileName, String formulaString,
                     Integer maxQueueSize, Integer maxSetSize, Integer success,
                     Boolean coreRetainment) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.formulaString = formulaString;
        this.maxQueueSize = maxQueueSize;
        this.maxSetSize = maxSetSize;
        this.success = success;
        this.coreRetainment = coreRetainment;
    }

    public void run() {
        OWLOntologyManager manager;
        OWLOntology ontology;
        Logger.getLogger("RV").log(Level.INFO, "Opening the ontology...");
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(inputFileName));
        } catch (Exception e) {
            Logger.getLogger("RV").log(Level.SEVERE, String
                    .format("Could not open the ontology file '%s'.\n", inputFileName));
            return;
        }
        Logger.getLogger("RV").log(Level.INFO, "Parsing the formula...");
        OWLAxiom entailment = AlternativeOWLExpressionParser.parse(manager, ontology,
                formulaString);
        if (entailment == null) {
            Logger.getLogger("RV").log(Level.SEVERE,
                    String.format("Bad formula: \n\t%s\n", formulaString));
            return;
        }
        Logger.getLogger("RV").log(Level.INFO, "Creating the pseudo-contractor...");
        Revisor revisor = new Revisor(manager, new ReasonerFactory(), new SelectionFunctionFull(),
                                      success, coreRetainment);
        revisor.setMaxSetElements(maxSetSize);
        revisor.setMaxQueueSize(maxQueueSize);
        Logger.getLogger("RV").log(Level.INFO, "Executing the operation...");
        OWLOntology inferredOntology;
        try {
            inferredOntology = manager.createOntology(
                    revisor.revise(ontology, entailment));
        } catch (OWLException e) {
            e.printStackTrace();
            return;
        }
        Logger.getLogger("RV").log(Level.INFO, "Saving...");
        try {
            OutputStream s = new FileOutputStream(outputFileName);
            manager.saveOntology(inferredOntology, s);
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            Logger.getLogger("RV").log(Level.SEVERE, String
                    .format("Could not save the ontology into '%s.'\n", outputFileName));
            e.printStackTrace();
            return;
        }
        Logger.getLogger("RV").log(Level.INFO,
                String.format("Success! Ontology saved to '%s'.\n", outputFileName));
    }
}
