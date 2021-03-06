package main.operations;

import main.operations.auxiliars.AlternativeOWLExpressionParser;
import main.operations.contraction.PartialMeetContractor;
import main.operations.selectionfunctions.SelectionFunctionAny;
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
 * Main class of the partial meet contractor.
 *
 * @author Luís F. de M. C. Silva (inspired by Vinícius B. Matos)
 *
 */
public class PartialMeetContraction {

    private String inputFileName;
    private String outputFileName;
    private String formulaString;
    private Integer maxQueueSize;
    private Integer maxRemainderSize;

    public PartialMeetContraction(String inputFileName, String outputFileName, String formulaString,
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
        Logger.getLogger("PMC").log(Level.INFO, "Opening the ontology...");
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(inputFileName));
        } catch (Exception e) {
            Logger.getLogger("PMC").log(Level.SEVERE, String
                    .format("Could not open the ontology file '%s'.\n", inputFileName));
            return;
        }
        Logger.getLogger("PMC").log(Level.INFO, "Parsing the formula...");
        OWLAxiom entailment = AlternativeOWLExpressionParser.parse(manager, ontology,
                formulaString);
        if (entailment == null) {
            Logger.getLogger("PMC").log(Level.SEVERE,
                    String.format("Bad formula: \n\t%s\n", formulaString));
            return;
        }
        Logger.getLogger("PMC").log(Level.INFO, "Creating the partial meet contractor...");
        PartialMeetContractor partialMeetContractor = new PartialMeetContractor(manager,
                new ReasonerFactory(), new SelectionFunctionAny());
        partialMeetContractor.setMaxRemainderElements(maxRemainderSize);
        partialMeetContractor.setMaxQueueSize(maxQueueSize);
        Logger.getLogger("PMC").log(Level.INFO, "Executing the operation...");
        OWLOntology inferredOntology;
        try {
            inferredOntology = manager.createOntology(
                    partialMeetContractor.partialMeetContract(ontology, entailment));
        } catch (OWLException e) {
            e.printStackTrace();
            return;
        }
        Logger.getLogger("PMC").log(Level.INFO, "Saving...");
        try {
            OutputStream s = new FileOutputStream(outputFileName);
            manager.saveOntology(inferredOntology, s);
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            Logger.getLogger("PMC").log(Level.SEVERE, String
                    .format("Could not save the ontology into '%s.'\n", outputFileName));
            e.printStackTrace();
            return;
        }
        Logger.getLogger("PMC").log(Level.INFO,
                String.format("Success! Ontology saved to '%s'.\n", outputFileName));
    }

}
