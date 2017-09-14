package app.integrations.audio;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface FrameOutputStream {
    void writeHeader() throws IOException, UnsupportedAudioFileException;
    void write(int[] buffer) throws IOException, UnsupportedAudioFileException;
    void write(int[] frameBuffer, int offset, int length) throws IOException, UnsupportedAudioFileException;
    void close() throws IOException;
}
