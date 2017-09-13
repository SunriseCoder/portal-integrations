package app.integrations.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.wav.WaveInputStream;
import app.integrations.audio.wav.WaveOutputStream;

public class FileScanner {
    private AudioFormat format;
    private FrameInputStream inputStream;
    private FrameOutputStream outputStream;

    public void open(String foldername, String filename) throws IOException, UnsupportedAudioFileException {
        File folder = new File(foldername);
        File file = new File(folder, filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("'" + filename + "' in '" + foldername + "'");
        }

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        this.format = audioInputStream.getFormat();
        this.inputStream = new WaveInputStream(audioInputStream);
    }

    public void setOutput(String foldername, String outputname) throws IOException, UnsupportedAudioFileException {
        File folder = new File(foldername);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) {
            throw new FileNotFoundException("'" + folder.getAbsolutePath() + "' is not a directory");
        }
        File file = new File(folder, outputname);
        if (file.exists()) {
            file.delete();
        }
        this.outputStream = new WaveOutputStream(file, format);
        this.outputStream.writeHeader();
    }

    public void collectStatistics() throws Exception {
        int[] frameBuffer = new int[1024];
        Statistics statistics = new Statistics();
        long t = System.currentTimeMillis();
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            statistics.add(frameBuffer, read);
        }
        statistics.dump();
        System.out.println("time: " + (System.currentTimeMillis() - t));
    }

    public void copy() throws IOException, UnsupportedAudioFileException {
        int[] frameBuffer = new int[1024];
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            outputStream.write(frameBuffer, 0, read);
        }
    }

    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }

        if (outputStream != null) {
            outputStream.close();
        }
    }
}
