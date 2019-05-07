package app.integrations.audio.wav;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.api.FrameOutputStream;
import app.integrations.utils.ByteBuffer;
import app.integrations.utils.FrameBuffer;
import app.integrations.utils.Primitives;

public class WaveOutputStream implements FrameOutputStream {
    private static final int WRITE_BUFFER_SIZE = 1048576;

    private RandomAccessFile outputFile;
    private AudioFormat format;
    private FrameBuffer[] frameBuffers;
    private ByteBuffer outputStreamBuffer;

    private WaveHeaderWriter headerWriter;
    private int writtenBytes;
    private int sampleSize;

    public WaveOutputStream(File file, AudioFormat format) throws IOException {
        this.outputFile = new RandomAccessFile(file, "rw");
        this.format = format;
        this.sampleSize = format.getSampleSizeInBits() / 8;
        headerWriter = new WaveHeaderWriter(this.outputFile, format);

        int channelCount = format.getChannels();
        this.frameBuffers = new FrameBuffer[channelCount];
        for (int i = 0; i < channelCount; i++) {
            this.frameBuffers[i] = new FrameBuffer();
        }

        this.outputStreamBuffer = new ByteBuffer();
    }

    @Override
    public void writeHeader() throws IOException, UnsupportedAudioFileException {
        headerWriter.writerHeader();
    }

    @Override
    public void write(int channel, int[] buffer, int offset, int length) throws IOException, UnsupportedAudioFileException {
        if (offset != 0 || length != buffer.length) {
            int[] oldArray = buffer;
            buffer = new int[length];
            System.arraycopy(oldArray, offset, buffer, 0, length);
        }
        write(channel, buffer);
    }

    @Override
    public void write(int channel, int[] buffer) throws IOException, UnsupportedAudioFileException {
        FrameBuffer frameBuffer = frameBuffers[channel];
        frameBuffer.push(buffer);
        checkAndWrite();
    }

    private void checkAndWrite() throws IOException {
        int available = Integer.MAX_VALUE;

        for (int i = 0; i < frameBuffers.length; i++) {
            FrameBuffer frameBuffer = frameBuffers[i];
            available = Math.min(available, frameBuffer.available());
        }

        if (available > 0) {
            writeFrameBuffers(available);
        }
    }

    private void writeFrameBuffers(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < frameBuffers.length; j++) {
                FrameBuffer frameBuffer = frameBuffers[j];
                int value = frameBuffer.read();
                byte[] buffer = encodeValue(value);
                outputStreamBuffer.push(buffer);
            }
        }
        checkAndWriteToStream(false);
    }

    private void checkAndWriteToStream(boolean force) throws IOException {
        int available = outputStreamBuffer.available();
        if ((force && available > 0) || available >= WRITE_BUFFER_SIZE) {
            byte[] cachedBuffer = new byte[available];
            int read = outputStreamBuffer.read(cachedBuffer);
            if (read != cachedBuffer.length) {
                throw new IllegalStateException();
            }
            writeToStream(cachedBuffer);
        }
    }

    private void writeToStream(byte[] buffer) throws IOException {
        outputFile.seek(outputFile.length());
        outputFile.write(buffer);
        writtenBytes += buffer.length;
        headerWriter.updateHeader(writtenBytes);
    }

    private byte[] encodeValue(int value) {
        byte[] buffer;
        boolean isBigEndian = format.isBigEndian();
        if (isBigEndian) {
            buffer = Primitives.intToBigEndianByteArray(value, sampleSize);
        } else {
            buffer = Primitives.intToLittleEndianByteArray(value, sampleSize);
        }
        return buffer;
    }

    @Override
    public void close() throws IOException {
        checkAndWriteToStream(true);
        outputFile.close();
    }
}
