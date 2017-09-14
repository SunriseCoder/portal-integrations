package app.integrations.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.wav.WaveInputStream;
import app.integrations.audio.wav.WaveOutputStream;
import app.integrations.utils.FileHelper;

public class FileScanner {
    private static final int NORMAL_MEANING = 5000;
    private static final int MAX_FACTOR = 20;
    private static final int MIN_VALUE = -32768;
    private static final int MAX_VALUE = 32767;
    private static final int NEIGHBOUR_CHUNK_NUMBER = 3;

    private static final int DOTS_PER_LINE = 100;

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

    public List<Integer> collectStatistics() throws Exception {
        int frameRate = (int) format.getFrameRate();
        int[] frameBuffer = new int[frameRate / 10];
        List<Integer> fileMeanings = new ArrayList<>();
        int counter = 0;
        int framesPerPoint = 1000;
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            Statistics frameStatistics = new Statistics();
            frameStatistics.add(frameBuffer, read);
            fileMeanings.add(frameStatistics.getMathMeaning());
            counter++;
            if (counter % framesPerPoint == 0) {
                System.out.print(".");
            }
            if (counter % (framesPerPoint * DOTS_PER_LINE) == 0) {
                System.out.println();
            }
        }
        return fileMeanings;
    }

    public void calculateMeans(int chunkSize) throws IOException {
        int[] frameBuffer = new int[chunkSize];
        int minMean = Integer.MAX_VALUE;
        int maxMean = Integer.MIN_VALUE;
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            Statistics frameStatistics = new Statistics();
            frameStatistics.add(frameBuffer, read);
            int meaning = frameStatistics.getMathMeaning();
            if (meaning > maxMean) {
                maxMean = meaning;
            }
            if (meaning < minMean) {
                minMean = meaning;
            }
        }
        System.out.println("ChunkSize: " + chunkSize + ", minMean: " + minMean + ", maxMean: " + maxMean);
    }

    public void copy() throws IOException, UnsupportedAudioFileException {
        int[] frameBuffer = new int[1024];
        while(inputStream.available() > 0) {
            int read = inputStream.readFrames(frameBuffer);
            outputStream.write(frameBuffer, 0, read);
        }
    }

    public void adjust() throws IOException, UnsupportedAudioFileException {
        int[] frameBuffer = new int[4096];
        int framesPerPoint = 100;
        int counter = 0;
        int position = 0;
        Map<Integer, Object> buffers = new HashMap<>();
        // Big loop
        while(inputStream.available() > 0 || !buffers.isEmpty()) {
            // If there is something to read, reading and putting to map
            int available = inputStream.available();
            if (available > 0) {
                int read = inputStream.readFrames(frameBuffer);
                int[] buffer = new int[read];
                System.arraycopy(frameBuffer, 0, buffer, 0, read);
                buffers.put(counter++, buffer);
            }

            // If there are not enough data for smooth analysis, skipping
            if (available > 0 && counter <= position + NEIGHBOUR_CHUNK_NUMBER) {
                continue;
            }

            // Calculating smooth meaning and factor
            Statistics statistics = new Statistics();
            for (int i = position - NEIGHBOUR_CHUNK_NUMBER; i <= position + NEIGHBOUR_CHUNK_NUMBER; i++) {
                int[] buffer = (int[]) buffers.get(i);
                if (buffer != null) {
                    statistics.add(buffer);
                }
            }
            int smoothMean = statistics.getMathMeaning();

            int[] buffer = (int[]) buffers.get(position);
            if (smoothMean != 0) {
                // Adjusting current chunk
                double factor = (double) NORMAL_MEANING / smoothMean;
                if (factor > MAX_FACTOR) {
                    factor = MAX_FACTOR;
                }
                buffer = adjustFrameBuffer(buffer, factor);
            }
            outputStream.write(buffer);

            // if processing done, clearing the chunk map and exiting
            if (available == 0 && position == counter - 1) {
                buffers.clear();
                break;
            }

            // Removing last old chunk
            buffers.remove(position - NEIGHBOUR_CHUNK_NUMBER);
            position++;

            if (counter % framesPerPoint == 0) {
                System.out.print(".");
            }
            if (counter % (framesPerPoint * DOTS_PER_LINE) == 0) {
                System.out.println();
            }
        }
    }

    private int[] adjustFrameBuffer(int[] sourceBuffer, double factor) {
        int[] adjustedBuffer = new int[sourceBuffer.length];
        for (int i = 0; i < sourceBuffer.length; i++) {
            int value = sourceBuffer[i];
            value = adjustValue(factor, value);
            adjustedBuffer[i] = value;
        }
        return adjustedBuffer;
    }

    private int adjustValue(double factor, int value) {
        value = (int) Math.round(value * factor);
        if (value < MIN_VALUE) {
            value = MIN_VALUE;
        }
        if (value > MAX_VALUE) {
            value = MAX_VALUE;
        }
        return value;
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
