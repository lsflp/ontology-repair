package main.operations;

import main.operations.auxiliars.AlternativeOWLExpressionParser;
import main.operations.contraction.KernelContractor;
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
 * Main class of the kernel contractor.
 *
 * @author Luís F. de M. C. Silva (inspired by Vinícius B. Matos)
 *
 */
public class KernelContraction {

    private String inputFileName;
    private String outputFileName;
    private String formulaString;
    private Integer maxKernelSize;
    private Integer maxQueueSize;

    public KernelContraction(String inputFileName, String outputFileName, String formulaString,
                             Integer maxQueueSize, Integer maxKernelSize) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.formulaString = formulaString;
        this.maxKernelSize = maxKernelSize;
        this.maxQueueSize = maxQueueSize;
    }

    public void run() {
        OWLOntologyManager manager;
        OWLOntology ontology;
        Logger.getLogger("KC").log(Level.INFO, "Opening the ontology...");
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(inputFileName));
        } catch (Exception e) {
            Logger.getLogger("KC").log(Level.SEVERE, String
                    .format("Could not open the ontology file '%s'.\n", inputFileName));
            return;
        }
        Logger.getLogger("KC").log(Level.INFO, "Parsing the formula...");
        OWLAxiom entailment = AlternativeOWLExpressionParser.parse(manager, ontology,
                formulaString);
        if (entailment == null) {
            Logger.getLogger("SRW").log(Level.SEVERE,
                    String.format("Bad formula: \n\t%s\n", formulaString));
            return;
        }
        Logger.getLogger("KC").log(Level.INFO, "Creating the kernel contractor...");
        KernelContractor kernelContractor = new KernelContractor(manager,
                new ReasonerFactory(), new SelectionFunctionAny());
        kernelContractor.setMaxKernelElements(maxKernelSize);
        kernelContractor.setMaxQueueSize(maxQueueSize);
        Logger.getLogger("KC").log(Level.INFO, "Executing the operation...");
        OWLOntology inferredOntology;
        try {
            inferredOntology = manager.createOntology(
                    kernelContractor.kernelContract(ontology, entailment));
        } catch (OWLException e) {
            e.printStackTrace();
            return;
        }
        Logger.getLogger("KC").log(Level.INFO, "Saving...");
        try {
            OutputStream s = new FileOutputStream(outputFileName);
            manager.saveOntology(inferredOntology, s);
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            Logger.getLogger("KC").log(Level.SEVERE, String
                    .format("Could not save the ontology into '%s.'\n", outputFileName));
            e.printStackTrace();
            return;
        }
        Logger.getLogger("KC").log(Level.INFO,
                String.format("Success! Ontology saved to '%s'.\n", outputFileName));
    }
}
