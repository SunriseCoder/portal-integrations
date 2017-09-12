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

        FileScanner scanner = new FileScanner();
        scanner.open(folder, filename);
        scanner.decode();
    }
}
