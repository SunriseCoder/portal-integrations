package app.integrations.audio;

import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class FileScanner {
    private FrameInputStream inputStream;

    public void open(String foldername, String filename) throws Exception {
        File folder = new File(foldername);
        File file = new File(folder, filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("'" + filename + "' in '" + foldername + "'");
        }

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        this.inputStream = new FrameInputStream(audioInputStream);
    }

    public void decode() throws Exception {
        int[] frames = new int[10];
        int read = inputStream.nextFrames(frames);
        System.out.println(read);
    }
}
