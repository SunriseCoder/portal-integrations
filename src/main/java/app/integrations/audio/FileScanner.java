package app.integrations.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.wav.WaveInputStream;
import app.integrations.audio.wav.WaveOutputStream;
import app.integrations.utils.FileHelper;

public class FileScanner {
    private AudioFormat format;
    private FrameInputStream inputStream;
    private FrameOutputStream outputStream;

    public void open(String foldername, String filename) throws IOException, UnsupportedAudioFileException {
        File file = FileHelper.checkAndGetFile(foldername, filename);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        this.format = audioInputStream.getFormat();
        this.inputStream = new WaveInputStream(audioInputStream);
    }

    public void setOutput(String foldername, String outputname) throws IOException, UnsupportedAudioFileException {
        File file = FileHelper.createFile(foldername, outputname, true);
        this.outputStream = new WaveOutputStream(file, format);
        this.outputStream.writeHeader();
    }

    public List<Integer> calculateMeanings(int chunkSizeMs) throws Exception {
        int frameRate = (int) format.getFrameRate();
        int chunkSize = frameRate * chunkSizeMs / 1000;
        int[] frameBuffer = new int[chunkSize];
        List<Integer> fileMeanings = new ArrayList<>();
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            Statistics frameStatistics = new Statistics();
            frameStatistics.add(frameBuffer, read);
            fileMeanings.add(frameStatistics.getMathMeaning());
        }
        return fileMeanings;
    }

    public void copy() throws IOException, UnsupportedAudioFileException {
        int[] frameBuffer = new int[1024];
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            outputStream.write(frameBuffer, 0, read);
        }
    }

    public void adjust(int chunkSizeMs) throws IOException, UnsupportedAudioFileException {
        int frameRate = (int) format.getFrameRate();
        int chunkSize = frameRate * chunkSizeMs / 1000;

        FrameStreamAdjuster adjuster = new FrameStreamAdjuster();
        adjuster.setChunkSize(chunkSize);
        adjuster.setInputStream(inputStream);
        adjuster.setOutputStream(outputStream);

        adjuster.adjust();
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
