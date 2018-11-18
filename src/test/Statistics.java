package test;

import java.util.ArrayList;

/**
 * Class with some statistics methods.
 *
 * @author Luís F. de M. C. Silva
 *
 */
public class Statistics {

    /**
     * Prints the confidence interval of a list of values.
     * @param values
     */
    public static void getConfidenceInterval(ArrayList<Long> values) {
        double mean = getMean(values);
        double stdd = getStandardDeviation(values);

        double lower = mean - 1.96 * stdd/Math.sqrt(values.size());
        double higher = mean + 1.96 * stdd/Math.sqrt(values.size());

        System.out.println("MÉDIA DE TEMPO = " + mean);
        System.out.print("INTERVALO DE CONFIANÇA APROXIMADO: ");
        System.out.println("[ " + lower + ", " + higher + " ]");
    }

    /**
     * Calculates the mean of a list of values.
     * @param values
     * @return the mean
     */
    private static double getMean(ArrayList<Long> values) {
        double sum = 0.0;
        int size = values.size();

        for (long time : values) {
            sum += time;
        }

        double mean = sum / size;
        return mean;
    }

    /**
     * Calculates the variance of a list of values.
     * @param values
     * @return the variation
     */
    private static double getVariance(ArrayList<Long> values) {
        double mean = getMean(values);
        double sum = 0;
        int size = values.size();

        for(long time : values)
            sum += (time-mean) * (time-mean);

        double stdd = sum / (size - 1);
        return stdd;
    }

    /**
     * Calculates the standard deviation of a list of values.
     *
     * @param values
     * @return the standard deviation
     */
    private static double getStandardDeviation(ArrayList<Long> values) {
        return Math.sqrt(getVariance(values));
    }
}
