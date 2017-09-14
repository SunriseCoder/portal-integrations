package app.integrations;

import java.io.IOException;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.FileScanner;
import app.integrations.utils.StatisticHelper;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        String folder = "data";
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

//        dumpImage(folder, "complex.wav", "complex.png");
        adjust(folder, "complex.wav", "complex_out4k.wav");
        dumpImage(folder, "complex_out4k.wav", "complex_out4k.png");
        combineImages(folder, "complex.png", "complex_out4k.png", "complex_and_out4k.png");

        //calculateMeans(folder, "norm.wav");

        System.out.println("Done");
    }

    private static void adjust(String folder, String filename, String outputname) throws IOException, UnsupportedAudioFileException {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        scanner.setOutput(folder, outputname);
        scanner.adjust();
        scanner.close();
    }

    private static void dumpImage(String folder, String filename, String imageFile) throws Exception {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        List<Integer> meanings = scanner.collectStatistics();
        scanner.close();
        StatisticHelper.createImage(meanings, folder, imageFile);
    }

    private static void calculateMeans(String folder, String filename) throws IOException, UnsupportedAudioFileException {
        for (int i = (int) Math.pow(2, 18); i > 1; i /= 2) {
            meansSeek(folder, filename, i);
        }
    }

    private static void meansSeek(String folder, String filename, int chunkSize) throws IOException, UnsupportedAudioFileException {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        scanner.calculateMeans(chunkSize);
        scanner.close();
    }

    private static void combineImages(String folder, String file1, String file2, String outputFile) throws IOException {
        StatisticHelper.combineImages(folder, file1, file2, outputFile);
    }
}
