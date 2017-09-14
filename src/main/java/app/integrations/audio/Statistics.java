package app.integrations.audio;

import java.math.BigInteger;

public class Statistics {
    private long[] statistics;
    private long counter;
    BigInteger sum;

    public Statistics() {
        statistics = new long[32768];
        sum = new BigInteger("0");
    }

    public void add(int[] frameBuffer) {
        add(frameBuffer, frameBuffer.length);
    }

    public void add(int[] frameBuffer, int read) {
        for (int i = 0; i < read; i++) {
            int value = frameBuffer[i];
            if (value < 0) {
                value = Math.abs(value) - 1;
            }
            statistics[value]++;
            counter++;
            sum = sum.add(new BigInteger(String.valueOf(value)));
        }
    }

    public int getMathMeaning() {
        int mathMeaning = sum.divide(new BigInteger(String.valueOf(counter))).intValue();
        return mathMeaning;
    }

    public void dump() {
        long cumulativeTotal = 0;
        for (int i = 0; i < statistics.length; i++) {
            long value = statistics[i];
            long deltaBefore = cumulativeTotal * 100 / counter;
            long deltaAfter = (cumulativeTotal + value) * 100 / counter;
            if (deltaBefore != deltaAfter) {
                System.out.println(deltaAfter + "%:\t" + i + "\t" + i * 100 / statistics.length + "%");
            }
            cumulativeTotal += value;
        }
        System.out.println("total hits: " + counter);
        System.out.println("cumulative total: " + cumulativeTotal);
        System.out.println("mean: " + getMathMeaning());
    }
}
