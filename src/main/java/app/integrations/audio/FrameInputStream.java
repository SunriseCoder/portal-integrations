package app.integrations.audio;

import java.io.IOException;

public interface FrameInputStream {
    boolean available() throws IOException;
    boolean available(int channel) throws IOException;
    int readFrames(int[] frames) throws IOException;
    int readFrames(int channel, int[] frames) throws IOException;
    void close() throws IOException;
}
