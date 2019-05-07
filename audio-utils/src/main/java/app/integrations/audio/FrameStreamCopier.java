package app.integrations.audio;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.api.FrameInputStream;
import app.integrations.audio.api.FrameOutputStream;
import app.integrations.audio.api.FrameStreamProcessor;

public class FrameStreamCopier implements FrameStreamProcessor {
    private FrameInputStream inputStream;
    private FrameOutputStream outputStream;
    private int outputChannel;

    // Staff variables
    private int[] frameBuffer;

    public FrameStreamCopier(FrameInputStream inputStream, FrameOutputStream outputStream, int outputChannel) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.outputChannel = outputChannel;
    }

    @Override
    public void setChunkSize(int chunkSize) {
        this.frameBuffer = new int[chunkSize];
    }

    @Override
    public void prepareOperation() {
        // Seems, nothing to do here
    }

    @Override
    public long processPortion() throws IOException, UnsupportedAudioFileException {
        int read = inputStream.readFrames(frameBuffer);
        if (read > 0) {
            outputStream.write(outputChannel, frameBuffer, 0, read);
        }
        return read;
    }

    @Override
    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            // Just swallowing
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            // Just swallowing
        }
    }
}
