package app.integrations;

import java.awt.image.BufferedImage;
import java.util.List;

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

        long startTime = System.currentTimeMillis();

        List<List<Integer>> statistics = collectStatistics(".", inputFileName);
        BufferedImage image = ImageHelper.createStatisticImage(statistics);

        if (args.length > 2 && "a".equals(args[2])) { // Append
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

    private static List<List<Integer>> collectStatistics(String folder, String inputName) throws Exception {
        FileScanner scanner = new FileScanner();
        try {
            scanner.open(folder, inputName);
            List<List<Integer>> statistics = scanner.calculateStatistics(1000);
            return statistics;
        } finally {
            scanner.close();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: " + AudioDump.class.getSimpleName() + " <input file> <output file> [options]");
        System.out.println("Options:");
        System.out.println("    a - append to an existing image");
    }
}
