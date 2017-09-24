package app.integrations;

import java.io.IOException;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.ChannelOperation;
import app.integrations.audio.FileScanner;
import app.integrations.utils.ImageHelper;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: App <file name without extension>");
            return;
        }

        String name = args[0];
        long t = System.currentTimeMillis();
        String folder = ".";
        //String folder = "data";
        //String filename = "sample.wav";
        //String filename = "norm.wav";
        //String filename = "complex.wav";
        //String outputname = "out.wav";
        //String imageFile = "image.png";

        //dumpImage(folder, "norm.wav", "norm.png");
        //dumpImage(folder, "norm-short.wav", "norm-short.png");
        //adjust(folder, "norm-short.wav", "norm_out2.wav");
        //dumpImage(folder, "norm_out.wav", "norm_out.png");

//        adjust(folder, "norm.wav", "norm_out5.wav");
//        dumpImage(folder, "norm_out5.wav", "norm_out5.png");
//        combineImages(folder, "norm.png", "norm_out5.png", "norm_and_out5.png");

        //String name = "20170920";

        //String suffix = "copy";
        //copy(folder, name + ".wav", name + "_" + suffix + ".wav");

        String suffix = "out";
        //ChannelOperation[] operations = {new ChannelOperation(0, 0, false)}; // Copy first track to Mono
        ChannelOperation[] operations = {new ChannelOperation(0, 0, true)}; // Adjust first track to Mono
        // Copy Stereo
        //ChannelOperation[] operations = {new ChannelOperation(0, 0, false), new ChannelOperation(1, 1, false)};

        //ChannelOperation[] operations = {new ChannelOperation(0, 0, true)}; // Adjust first track to Mono

        // Copy first start to first and second to stereo file
        //ChannelOperation[] operations = {new ChannelOperation(0, 0, false), new ChannelOperation(0, 1, false)};

        // Adjust mono. Keep first track as-is and adjust it as second one

        //ChannelOperation[] operations = {new ChannelOperation(0, 0, false), new ChannelOperation(0, 1, true)};
        process(folder, name + ".wav", name + "_" + suffix + ".wav", operations);

        //dumpImage(folder, name + ".wav", name + ".png");
        //dumpImage(folder, name + "_" + suffix + ".wav", name + "_" + suffix + ".png");
        //combineImages(folder, name + ".png", name + "_" + suffix + ".png", name + "_and_" + suffix + ".png");


        //calculateMeans(folder, "norm.wav");

        System.out.println();
        System.out.println("Done");
        System.out.println("Took " + (System.currentTimeMillis() - t) + " ms");
    }

    private static void process(String folder, String inputName, String outputName, ChannelOperation[] operations) throws IOException, UnsupportedAudioFileException {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, inputName);
        scanner.setOutput(folder, outputName, operations.length);
        scanner.process(operations, 1000);
        scanner.close();
    }

    private static void dumpImage(String folder, String inputName, String imageFile) throws Exception {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, inputName);
        List<List<Integer>> statistics = scanner.calculateStatistics(1000);
        scanner.close();
        ImageHelper.createImage(statistics, folder, imageFile);
    }

    private static void combineImages(String folder, String file1, String file2, String outputFile) throws IOException {
        ImageHelper.combineImageFiles(folder, file1, file2, outputFile);
    }
}
