package app.integrations.audio;

import java.io.IOException;

public interface FrameInputStream {
    int available() throws IOException;
    int readFrames(int[] frames) throws IOException;
    void close() throws IOException;
}
