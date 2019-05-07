package app.integrations;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.integrations.audio.FileScanner;
import app.integrations.utils.ImageHelper;

public class AudioDump {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            printUsage();
            System.exit(-1);
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        // Parse options
        Set<String> options = new HashSet<>();
        if (args.length > 2) {
            options.addAll(Arrays.asList(args[2].split(",")));
        }

        long startTime = System.currentTimeMillis();

        List<List<Integer>> statistics = collectStatistics(".", inputFileName, options);
        BufferedImage image = ImageHelper.createStatisticImage(statistics, inputFileName);

        if (options.contains("a")) { // Append to an existing image
            BufferedImage existingImage = null;
            try {
                existingImage = ImageHelper.loadImage(".", outputFileName);
            } catch (Exception e) {
                // Just swallowing
            }
            image = ImageHelper.combineImages(existingImage, image);
        }

        ImageHelper.saveImage(".", outputFileName, image);

        System.out.println();
        System.out.println("Done, took: " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    private static List<List<Integer>> collectStatistics(String folder, String inputName, Set<String> options) throws Exception {
        FileScanner scanner = new FileScanner();
        try {
            scanner.open(folder, inputName);
            List<List<Integer>> statistics = scanner.calculateStatistics(1000, options);
            return statistics;
        } finally {
            scanner.close();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: " + AudioDump.class.getSimpleName() + " <input file> <output file> [options]");
        System.out.println("Options (comma-separated):");
        System.out.println("    m - Calculate Math Meanings");
        System.out.println("    d - Calculate Deltas");
        System.out.println("    a - Append this image to an existing one");
    }
}
