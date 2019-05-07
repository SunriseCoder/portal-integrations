package app.integrations.audio.api;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface FrameOutputStream {
    void writeHeader() throws IOException, UnsupportedAudioFileException;
    void write(int channel, int[] buffer) throws IOException, UnsupportedAudioFileException;
    void write(int channel, int[] frameBuffer, int offset, int length) throws IOException, UnsupportedAudioFileException;
    void close() throws IOException;
}
