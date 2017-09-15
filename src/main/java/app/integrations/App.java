package app.integrations;

import java.io.IOException;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.FileScanner;
import app.integrations.utils.ImageHelper;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        long t = System.currentTimeMillis();
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

        String name = "20170914";
        String suffix = "out44k__";
        //dumpImage(folder, name + ".wav", name + ".png");
        adjust(folder, name + ".wav", name + "_" + suffix + ".wav");
        //dumpImage(folder, name + "_" + suffix + ".wav", name + "_" + suffix + ".png");
        //combineImages(folder, name + ".png", name + "_" + suffix + ".png", name + "_and_" + suffix + ".png");

        //calculateMeans(folder, "norm.wav");

        System.out.println("Done");
        System.out.println("Took ms: " + (System.currentTimeMillis() - t));
    }

    private static void adjust(String folder, String filename, String outputname) throws IOException, UnsupportedAudioFileException {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        scanner.setOutput(folder, outputname);
        scanner.adjust(1000);
        scanner.close();
    }

    private static void dumpImage(String folder, String filename, String imageFile) throws Exception {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        List<Integer> meanings = scanner.calculateMeanings(1000);
        scanner.close();
        ImageHelper.createImage(meanings, folder, imageFile);
    }

    private static void combineImages(String folder, String file1, String file2, String outputFile) throws IOException {
        ImageHelper.combineImages(folder, file1, file2, outputFile);
    }
}
