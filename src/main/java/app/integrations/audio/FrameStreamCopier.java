package app.integrations.audio;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

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
    public boolean processPortion() throws IOException, UnsupportedAudioFileException {
        int read = inputStream.readFrames(frameBuffer);
        boolean copiedSomething = false;
        if (read > 0) {
            outputStream.write(outputChannel, frameBuffer, 0, read);
            copiedSomething = true;
        }
        return copiedSomething;
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
