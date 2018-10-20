package main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import main.operations.KernelContraction;
import main.operations.PartialMeetContraction;
import main.operations.SRWPseudoContraction;

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
               description = "Maximum number of elements in the computer remainder set")
    private Integer maxRemainderSize = Integer.MAX_VALUE;

    @Parameter(names = { "--core-retainment" },
            description = "Minimality postulate used for the kernel contraction")
    private boolean coreRetainment = false;

    @Parameter(names = { "--relevance" },
            description = "Minimality postulate used for the partial meet contraction")
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

    private void run() {
        if (canPerformContraction()) {
            if (coreRetainment) {
                KernelContraction kc = new KernelContraction(inputFileName, outputFileName, formulaString,
                                                             maxQueueSize, maxRemainderSize);
                kc.run();
            }

            else if (relevance) {
                PartialMeetContraction pmc = new PartialMeetContraction(inputFileName, outputFileName, formulaString,
                                                                        maxQueueSize, maxRemainderSize);
                pmc.run();
            }
        }

        else if (revision) {
            System.out.println("Calling the Revision");
        }

        else if (srwPseudoContraction) {
            SRWPseudoContraction srw = new SRWPseudoContraction(inputFileName, outputFileName, formulaString,
                                                                maxQueueSize, maxRemainderSize);
            srw.run();
        }
    }

    private boolean isHelp() {
        return help;
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
        or.run();

    }
}