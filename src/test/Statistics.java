package test;

import java.util.ArrayList;

public class Statistics {

    public static void getConfidenceInterval(ArrayList<Long> executionTimes) {
        double mean = getMean(executionTimes);
        double stdd = getStandardDeviation(executionTimes);

        double lower = mean - 1.96 * stdd;
        double higher = mean + 1.96 * stdd;

        // print results
        System.out.println("MÉDIA DE TEMPO = " + mean);
        System.out.print("INTERVALO DE CONFIANÇA APROXIMADO: ");
        System.out.println("[ " + lower + ", " + higher + " ]");
    }

    private static double getMean(ArrayList<Long> executionTimes) {
        double sum = 0.0;
        int size = executionTimes.size();

        for (long time : executionTimes) {
            sum += time;
        }

        double mean = sum / size;
        return mean;
    }

    private static double getVariance(ArrayList<Long> executionTimes) {
        double mean = getMean(executionTimes);
        double sum = 0;
        int size = executionTimes.size();

        for(long time : executionTimes)
            sum += (time-mean) * (time-mean);

        double stdd = sum / (size - 1);
        return stdd;
    }

    private static double getStandardDeviation(ArrayList<Long> executionTimes) {
        return Math.sqrt(getVariance(executionTimes));
    }
}
