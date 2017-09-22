package app.integrations.audio;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface FrameStreamProcessor {
    void setChunkSize(int chunkSize);

    void prepareOperation() throws IOException, UnsupportedAudioFileException;
    /**
     * @return <b>true</b> if processed some data, otherwise <b>false</b>.
     */
    boolean processPortion() throws IOException, UnsupportedAudioFileException;
    void close();
}
