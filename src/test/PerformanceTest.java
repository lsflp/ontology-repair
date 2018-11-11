package test;

import main.operations.KernelContraction;
import main.operations.PartialMeetContraction;
import main.operations.Revision;
import main.operations.SRWPseudoContraction;

import java.util.ArrayList;

/**
 * Main class of the tests.
 *
 * @author Luís F. de M. C. Silva
 *
 */
public class PerformanceTest {

    private String inputFileName;
    private String outputFileName;
    private String formulaString;
    private Integer maxQueueSize;
    private Integer maxSetSize;
    private boolean kernelContraction;
    private boolean partialMeetContraction;
    private boolean revision;
    private boolean srwPseudoContraction;
    private int numberOfTests = 100;

    /**
     * The constructor of the class.
     *
     * @param inputFileName
     *             the input file
     * @param outputFileName
     *             the output file
     * @param formulaString
     *             the formula to work with
     * @param maxQueueSize
     *             maximum size of the queue
     * @param maxSetSize
     *             maximum size of the kernel or remainder size
     */
    public PerformanceTest(String inputFileName, String outputFileName, String formulaString,
                           Integer maxQueueSize, Integer maxSetSize) {

        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.formulaString = formulaString;
        this.maxQueueSize = maxQueueSize;
        this.maxSetSize = maxSetSize;
    }

    /**
     * Used to set the operation to be tested.
     *
     * @param kernelContraction
     *             true for the Kernel Contraction
     * @param partialMeetContraction
     *             true for the Partial Meet Contraction
     * @param revision
     *             true for the Revision
     * @param srwPseudoContraction
     *             true for the SRW Pseudo-contraction
     *
     * There can not be more than one true parameter.
     */
    public void setOperations(boolean kernelContraction, boolean partialMeetContraction,
                              boolean revision, boolean srwPseudoContraction) {

        this.kernelContraction = kernelContraction;
        this.partialMeetContraction = partialMeetContraction;
        this.revision = revision;
        this.srwPseudoContraction = srwPseudoContraction;
    }


    /**
     * Main method of the class
     */
    public void run() {
        if (kernelContraction) {
            runKernelContractionTest();
            return;
        }

        if (partialMeetContraction) {
            runPartialMeetContractionTest();
            return;
        }

        if (revision) {
            runRevisionTest();
            return;
        }

        if (srwPseudoContraction) {
            runSRWPseudoContractionTest();
            return;
        }
    }

    /**
     * Run the tests for the Kernel Contraction.
     */
    private void runKernelContractionTest() {
        ArrayList<Long> executionTimes = new ArrayList();

        for (int i = 0; i < numberOfTests; i++) {
            long start = System.currentTimeMillis();
            KernelContraction kc = new KernelContraction(inputFileName, outputFileName, formulaString,
                                                         maxQueueSize, maxSetSize);
            kc.run();
            long end = System.currentTimeMillis();
            executionTimes.add(end-start);
        }

        Statistics.getConfidenceInterval(executionTimes);
    }

    /**
     * Run the tests for the Partial Meet Contraction.
     */
    private void runPartialMeetContractionTest() {
        ArrayList<Long> executionTimes = new ArrayList();

        for (int i = 0; i < numberOfTests; i++) {
            long start = System.currentTimeMillis();
            PartialMeetContraction pmc = new PartialMeetContraction(inputFileName, outputFileName,
                                                                    formulaString, maxQueueSize, maxSetSize);
            pmc.run();
            long end = System.currentTimeMillis();
            executionTimes.add(end-start);
        }

        Statistics.getConfidenceInterval(executionTimes);
    }

    /**
     * Run the tests for the Revision.
     */
    private void runRevisionTest() {
        ArrayList<Long> executionTimes = new ArrayList();

        for (int i = 0; i < numberOfTests; i++) {
            long start = System.currentTimeMillis();
            Revision rv = new Revision(inputFileName, outputFileName, formulaString, maxQueueSize, maxSetSize);
            rv.run();
            long end = System.currentTimeMillis();
            executionTimes.add(end-start);
        }

        Statistics.getConfidenceInterval(executionTimes);
    }

    /**
     * Run the tests for the SRW Pseudo-contraction.
     */
    private void runSRWPseudoContractionTest() {
        ArrayList<Long> executionTimes = new ArrayList();

        for (int i = 0; i < numberOfTests; i++) {
            long start = System.currentTimeMillis();
            SRWPseudoContraction srw = new SRWPseudoContraction(inputFileName, outputFileName,
                                                                formulaString, maxQueueSize, maxSetSize);
            srw.run();
            long end = System.currentTimeMillis();
            executionTimes.add(end-start);
        }

        Statistics.getConfidenceInterval(executionTimes);
    }
}
