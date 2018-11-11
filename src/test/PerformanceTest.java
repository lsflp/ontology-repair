package test;

import main.operations.SRWPseudoContraction;

import java.util.ArrayList;

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

    public PerformanceTest(String inputFileName, String outputFileName, String formulaString,
                           Integer maxQueueSize, Integer maxSetSize) {

        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.formulaString = formulaString;
        this.maxQueueSize = maxQueueSize;
        this.maxSetSize = maxSetSize;
    }

    public void setOperations(boolean kernelContraction, boolean partialMeetContraction, boolean revision,
                              boolean srwPseudoContraction) {

        this.kernelContraction = kernelContraction;
        this.partialMeetContraction = partialMeetContraction;
        this.revision = revision;
        this.srwPseudoContraction = srwPseudoContraction;
    }

    public void run() {
        if (srwPseudoContraction) {
            runSRWPseudoContractionTest();
        }
    }

    private void runSRWPseudoContractionTest() {
        int numberOfTests = 100;
        ArrayList<Long> executionTimes = new ArrayList();
        SRWPseudoContraction srw = new SRWPseudoContraction(inputFileName, outputFileName, formulaString,
                                                            maxQueueSize, maxSetSize);
        for (int i = 0; i < numberOfTests; i++) {
            long start = System.currentTimeMillis();
            srw.run();
            long end = System.currentTimeMillis();
            executionTimes.add(end-start);
        }

        Statistics.getConfidenceInterval(executionTimes);
    }
}
