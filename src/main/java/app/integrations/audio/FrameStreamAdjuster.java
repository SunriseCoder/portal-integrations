package app.integrations.audio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

public class FrameStreamAdjuster {
    private static final int NORMAL_MEANING = 4000;
    private static final int MAX_FACTOR = 10;
    private static final int MIN_VALUE = -32768;
    private static final int MAX_VALUE = 32767;
    private static final int NEIGHBOUR_CHUNK_NUMBER = 3;

    private FrameInputStream inputStream;
    private FrameOutputStream outputStream;

    private Map<Integer, Object> chunkMap;
    private int[] buffer;

    public void setChunkSize(int chunkSize) {
        buffer = new int[chunkSize];
    }

    public void setInputStream(FrameInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(FrameOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void adjust() throws IOException, UnsupportedAudioFileException {
        int counter = 0;
        int position = 0;
        chunkMap = new HashMap<>();
        int minFactor = Integer.MAX_VALUE;
        int maxFactor = Integer.MIN_VALUE;
        // Big loop
        while(inputStream.available() > 0 || !chunkMap.isEmpty()) {
            // If there is something to read, reading and putting to map
            int available = inputStream.available();
            if (available > 0) {
                int read = inputStream.readFrames(buffer);
                int[] buffer = new int[read];
                System.arraycopy(buffer, 0, buffer, 0, read);
                chunkMap.put(counter++, buffer);
            }

            // If there are not enough data for smooth analysis, skipping
            if (available > 0 && counter <= position + NEIGHBOUR_CHUNK_NUMBER) {
                continue;
            }

            // Calculating smooth meaning and factor
            Statistics statistics = new Statistics();
            for (int i = position - NEIGHBOUR_CHUNK_NUMBER; i <= position + NEIGHBOUR_CHUNK_NUMBER; i++) {
                int[] buffer = (int[]) chunkMap.get(i);
                if (buffer != null) {
                    statistics.add(buffer);
                }
            }
            int smoothMean = statistics.getMathMeaning();

            int[] buffer = (int[]) chunkMap.get(position);
            if (smoothMean != 0) {
                // Adjusting current chunk
                double factor = (double) NORMAL_MEANING / smoothMean;
                //System.out.println("Position: " + position + ", factor: " + factor);
                if (factor > maxFactor) {
                    maxFactor = (int) factor;
                }
                if (factor < minFactor) {
                    minFactor = (int) factor;
                }
                if (factor > MAX_FACTOR) {
                    factor = MAX_FACTOR;
                }
                buffer = adjustFrameBuffer(buffer, factor);
            }
            outputStream.write(buffer);

            // if processing done, clearing the chunk map and exiting
            if (available == 0 && position == counter - 1) {
                chunkMap.clear();
                break;
            }

            // Removing last old chunk
            chunkMap.remove(position - NEIGHBOUR_CHUNK_NUMBER);
            position++;
        }
        System.out.println("minFactor: " + minFactor);
        System.out.println("maxFactor: " + maxFactor);
        System.out.println("chunkMap: " + chunkMap.size());
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

}
