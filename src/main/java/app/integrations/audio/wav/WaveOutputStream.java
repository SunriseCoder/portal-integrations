package app.integrations.audio.wav;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.FrameOutputStream;
import app.integrations.utils.Primitives;

public class WaveOutputStream implements FrameOutputStream {
    private RandomAccessFile outputFile;
    private AudioFormat format;
    private WaveHeaderWriter headerWriter;
    private int writtenBytes;

    public WaveOutputStream(File file, AudioFormat format) throws IOException {
        this.outputFile = new RandomAccessFile(file, "rw");
        this.format = format;
        headerWriter = new WaveHeaderWriter(this.outputFile, format);
    }

    public void writeHeader() throws IOException, UnsupportedAudioFileException {
        headerWriter.writerHeader();
    }

    public void write(int[] frameBuffer, int offset, int length) throws IOException, UnsupportedAudioFileException {
        if (offset != 0 || length != frameBuffer.length) {
            int[] oldArray = frameBuffer;
            frameBuffer = new int[length];
            System.arraycopy(oldArray, offset, frameBuffer, 0, length);
        }

        byte[] buffer = framesToByteArrays(frameBuffer);
        outputFile.seek(outputFile.length());
        outputFile.write(buffer);
        writtenBytes += buffer.length;
        headerWriter.updateHeader(writtenBytes);
    }

    private byte[] framesToByteArrays(int[] frameBuffer) throws UnsupportedAudioFileException {
        int frameSize = format.getFrameSize();
        byte[] byteArray = null;
        switch (frameSize) {
        case 1:
            byteArray = decode8bit(frameBuffer);
            break;
        case 2:
            byteArray = decode16bit(frameBuffer);
            break;
        default:
            throw new UnsupportedAudioFileException(frameSize + " bytes per frame is not supported");
        }
        return byteArray;
    }

    private byte[] decode8bit(int[] frameBuffer) {
        byte[] byteArray = new byte[frameBuffer.length];
        for (int i = 0; i < frameBuffer.length; i++) {
            int value = frameBuffer[i];
            byteArray[i] = (byte) value;
        }
        return byteArray;
    }

    private byte[] decode16bit(int[] frameBuffer) {
        byte[] byteArray = new byte[frameBuffer.length * 2];
        for (int i = 0; i < frameBuffer.length; i++) {
            int value = frameBuffer[i];
            byte[] arr = format.isBigEndian() ? Primitives.intToBigEndianByteArray2(value) : Primitives.intToLittleEndianByteArray2(value);
            System.arraycopy(arr, 0, byteArray, i * 2, arr.length);
        }
        return byteArray;
    }

    public void close() throws IOException {
        outputFile.close();
    }
}
