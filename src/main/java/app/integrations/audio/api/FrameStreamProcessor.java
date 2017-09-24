package app.integrations.audio.api;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface FrameStreamProcessor {
    void setChunkSize(int chunkSize);

    void prepareOperation() throws IOException, UnsupportedAudioFileException;
    /**
     * @return <b>true</b> if processed some data, otherwise <b>false</b>.
     */
    long processPortion() throws IOException, UnsupportedAudioFileException;
    void close();
}
