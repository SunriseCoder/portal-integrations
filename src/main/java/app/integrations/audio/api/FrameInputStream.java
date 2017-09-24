package app.integrations.audio.api;

import java.io.IOException;

public interface FrameInputStream {
    boolean available() throws IOException;
    boolean available(int channel) throws IOException;
    int readFrames(int[] frames) throws IOException;
    int readFrames(int channel, int[] frames) throws IOException;
    long getFramesCount();
    void close() throws IOException;
}
