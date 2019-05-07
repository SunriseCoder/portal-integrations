package app.integrations.audio;

public class StatsCalculator {
    private long[] statistics;
    private long counter;
    private int lastValue;
    private long sumOfValues;
    private long sumOfDeltas;

    public StatsCalculator() {
        statistics = new long[32768];
    }

    public void add(int[] frameBuffer) {
        addData(frameBuffer, frameBuffer.length);
    }

    public void addData(int[] frameBuffer, int read) {
        for (int i = 0; i < read; i++) {
            int value = frameBuffer[i];
            if (value < 0) {
                value = Math.abs(value) - 1;
            }
            statistics[value]++;
            counter++;
            sumOfValues += value;
            int delta = Math.abs(value - lastValue);
            sumOfDeltas += delta;
            lastValue = value;
        }
    }

    public int getMathMeaning() {
        int mathMeaning = (int) (sumOfValues / counter);
        return mathMeaning;
    }

    public int getAverageDelta() {
        int averageDelta = (int) (sumOfDeltas / counter);
        return averageDelta;
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
