package app.integrations.audio.wav;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.FrameInputStream;

public class WaveInputStream implements FrameInputStream {
    private AudioFormat format;
    private AudioInputStream inputStream;
    private int frameSize;

    public WaveInputStream(AudioInputStream audioInputStream) throws UnsupportedAudioFileException {
        this.format = audioInputStream.getFormat();

        if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
            throw new UnsupportedAudioFileException("Only PCM-Signed Wav-files are supported");
        }
        if (format.isBigEndian()) {
            throw new UnsupportedAudioFileException("Big-Endian Wav-files are not supported");
        }

        this.frameSize = format.getFrameSize();

        if (frameSize > 4) {
            throw new UnsupportedAudioFileException("Only 8 to 32-bit Wav-files are supported");
        }

        this.inputStream = audioInputStream;
    }

    @Override
    public int readFrames(int[] frames) throws IOException {
        byte[] buffer = new byte[frameSize * frames.length];
        int read = inputStream.read(buffer);
        if (read % frameSize != 0) {
            throw new IllegalStateException("Incomplete data in source stream");
        }

        convertValues(buffer, frames);
        read = read / frameSize;
        return read;
    }

    private void convertValues(byte[] buffer, int[] frames) {
        int framesNum = buffer.length / frameSize;
        for (int i = 0; i < framesNum; i++) {
            int value = parseValue(buffer, i);
            frames[i] = value;
        }
    }

    private int parseValue(byte[] buffer, int i) {
        int value = 0;
        for (int j = 0; j < frameSize; j++) {
            int b = buffer[i * frameSize + j];
            if (j != frameSize - 1) {
                b &= 0xFF;
            }
            value += b << j * 8;
        }
        return value;
    }

    @Override
    public int available() throws IOException {
        int available = inputStream.available();
        return available;
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
