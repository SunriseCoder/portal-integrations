package app.integrations;

import app.integrations.audio.FileScanner;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        String folder = "data";
        String filename = "sample.wav";
        //String filename = "norm.wav";
        //String filename = "complex.wav";
        String outputname = "out.wav";

        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        scanner.setOutput(folder, outputname);
        //scanner.decode();
        scanner.copy();
        scanner.close();
    }
}
