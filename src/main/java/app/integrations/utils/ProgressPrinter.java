package app.integrations.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProgressPrinter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");

    private long total;

    // Service variables
    private long startTime;
    private int lastPercentage;

    public void setTotal(long total) {
        this.total = total;
        this.startTime = System.currentTimeMillis();
    }

    public boolean updateProgress(long currentValue) {
        int percentage = (int) (currentValue * 100 / total);
        if (percentage <= lastPercentage) {
            return false;
        }

        System.out.print("\r[");
        for (int i = 0; i < percentage - 1; i++) {
            System.out.print("=");
        }
        System.out.print(">");
        for (int i = percentage; i < 100; i++) {
            System.out.print(" ");
        }
        System.out.print("] " + percentage + "% ");
        if (percentage > 0) {
            long elapsed = System.currentTimeMillis() - startTime;
            long elapsedPercentCost = elapsed / 10 / percentage;
            long estimation = (100 - percentage) * elapsedPercentCost / 100;
            LocalTime time = LocalTime.ofSecondOfDay(estimation);
            String timeStr = time.format(formatter);
            System.out.print("ETA: " + timeStr);
        }

        lastPercentage = percentage;
        return true;
    }
}
