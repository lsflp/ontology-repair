package main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import main.operations.KernelContraction;
import main.operations.PartialMeetContraction;
import main.operations.SRWPseudoContraction;
import main.operations.Revision;
import test.PerformanceTest;

/**
 * Command-line interface of the plug-in.
 *
 * @author Vinícius B. Matos (adapted by Luís F. de M. C. Silva)
 *
 */
public class OntologyRepair {

    @Parameter(names = { "-i", "--input" },
               description = "Input file name (OWL ontology)", required = true)
    private String inputFileName;

    @Parameter(names = { "-o", "--output" },
               description = "Output file name", required = true)
    private String outputFileName;

    @Parameter(names = { "-f", "--formula" },
               description = "Formula to work with", required = true)
    private String formulaString;

    @Parameter(names = { "--queue-limit" },
               description = "Limit of the queue size")
    private Integer maxQueueSize = Integer.MAX_VALUE;

    @Parameter(names = { "--remainder-limit" },
               description = "Maximum number of elements in the computer kernel or remainder set")
    private Integer maxSetSize = Integer.MAX_VALUE;

    @Parameter(names = { "--core-retainment" },
            description = "Minimality postulate used for the kernel")
    private boolean coreRetainment = false;

    @Parameter(names = { "--relevance" },
            description = "Minimality postulate used for the partial meet")
    private boolean relevance = false;

    @Parameter(names = { "-c", "--contraction" },
               description = "KernelContraction operation")
    private boolean contraction = false;

    @Parameter(names = { "-r", "--revision" },
               description = "Revision operation")
    private boolean revision = false;

    @Parameter(names = { "-srw", "--srwPseudoContraction" },
               description = "SRW Pseudo-contraction operation")
    private boolean srwPseudoContraction = false;

    @Parameter(names = { "-h", "--help" }, help = true)
    private boolean help = false;

    @Parameter(names = { "-t", "--test" })
    private boolean test = false;

    private void run() {
        if (canPerformContraction()) {
            if (coreRetainment) {
                KernelContraction knc = new KernelContraction(inputFileName, outputFileName, formulaString,
                                                             maxQueueSize, maxSetSize);
                knc.run();
            }

            else if (relevance) {
                PartialMeetContraction pmc = new PartialMeetContraction(inputFileName, outputFileName, formulaString,
                                                                        maxQueueSize, maxSetSize);
                pmc.run();
            }
        }

        else if (revision) {
            Revision rev = new Revision(inputFileName, outputFileName, formulaString,
                                        maxQueueSize, maxSetSize);
            rev.run();
        }

        else if (srwPseudoContraction) {
            SRWPseudoContraction srw = new SRWPseudoContraction(inputFileName, outputFileName, formulaString,
                                                                maxQueueSize, maxSetSize);
            srw.run();
        }
    }

    private void runTests() {
        PerformanceTest pt = new PerformanceTest(inputFileName, outputFileName, formulaString, maxQueueSize, maxSetSize);
        pt.setOperations(contraction && coreRetainment ,contraction && relevance,
                          revision, srwPseudoContraction);
        pt.run();
    }

    private boolean isHelp() {
        return help;
    }

    private boolean isTest() {
        return test;
    }

    private boolean isOneOperation() {
        return contraction ^ revision ^ srwPseudoContraction;
    }

    private boolean canPerformContraction() {
        return contraction & isOneMinimalityPostulate();
    }

    private boolean isOneMinimalityPostulate() {
        return coreRetainment ^ relevance;
    }

    public static void main(String[] args) throws Exception {
        OntologyRepair or = new OntologyRepair();
        JCommander jc = new JCommander(or);
        jc.setProgramName("java -jar OntologyRepair-<version>.jar");

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            StringBuilder out = new StringBuilder();
            jc.usage(out);
            System.err.println(out);
            return;
        }

        if (!or.isOneOperation() || or.isHelp()) {
            jc.usage();
            return;
        }

        if (!or.isTest()) {
            or.run();
        }

        else {
            or.runTests();
        }

    }
}