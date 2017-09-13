package app.integrations.audio.wav;

import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.utils.Primitives;

public class WaveHeaderWriter {
    private static final int RIFF_TEXT_OFFSET = 0;
    private static final int FILE_SIZE_OFFSET = 4;
    private static final int WAVE_FMT_TEXT_OFFSET = 8;
    private static final int FRAME_SIZE_OFFSET = 16;
    private static final int FORMAT_TYPE_OFFSET = 20;
    private static final int NUMBER_OF_CHANNELS_OFFSET = 22;
    private static final int SAMPLE_RATE_OFFSET = 24;
    private static final int BIT_RATE_OFFSET = 28;
    private static final int BYTES_PER_SAMPLE_OFFSET = 32;
    private static final int BITS_PER_SAMPLE_OFFSET = 34;
    private static final int DATA_TEXT_OFFSET = 36;
    private static final int DATA_SIZE_OFFSET = 40;

    private static final int HEADER_SIZE = 44;
    private static final int FORMAT_TYPE = 1;

    private RandomAccessFile outputFile;
    private AudioFormat format;

    public WaveHeaderWriter(RandomAccessFile outputFile, AudioFormat format) {
        this.outputFile = outputFile;
        this.format = format;
    }

    public void writerHeader() throws IOException, UnsupportedAudioFileException {
        validateFormat();
        writeRiffText();
        writeFileSize(0);
        writeWaveFmtText();
        writeFrameSize();
        writeFormatType();
        writeNumberOfChannels();
        writeSampleRate();
        writeBitRate();
        writeBytesPerFrame();
        writeBitsPerSample();
        writeDataText();
        writeDataSize(0);
    }

    private void validateFormat() throws UnsupportedAudioFileException {
        if (format == null) {
            throw new UnsupportedAudioFileException("Output format is null");
        }
    }

    private void writeRiffText() throws IOException {
        byte[] buffer = "RIFF".getBytes();
        outputFile.seek(RIFF_TEXT_OFFSET);
        outputFile.write(buffer);
    }

    private void writeFileSize(int dataSize) throws IOException {
        int fileSize = HEADER_SIZE + dataSize - 8;
        byte[] buffer = Primitives.intToLittleEndianByteArray4(fileSize);
        outputFile.seek(FILE_SIZE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeWaveFmtText() throws IOException {
        byte[] buffer = "WAVEfmt ".getBytes();
        outputFile.seek(WAVE_FMT_TEXT_OFFSET);
        outputFile.write(buffer);
    }

    private void writeFrameSize() throws IOException {
        int frameSize = format.getSampleSizeInBits();
        byte[] buffer = Primitives.intToLittleEndianByteArray4(frameSize);
        outputFile.seek(FRAME_SIZE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeFormatType() throws IOException {
        byte[] buffer = Primitives.intToLittleEndianByteArray2(FORMAT_TYPE);
        outputFile.seek(FORMAT_TYPE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeNumberOfChannels() throws IOException {
        int numberOfChannels = format.getChannels();
        byte[] buffer = Primitives.intToLittleEndianByteArray2(numberOfChannels);
        outputFile.seek(NUMBER_OF_CHANNELS_OFFSET);
        outputFile.write(buffer);
    }

    private void writeSampleRate() throws IOException {
        int sampleRate = Math.round(format.getFrameRate());
        byte[] buffer = Primitives.intToLittleEndianByteArray4(sampleRate);
        outputFile.seek(SAMPLE_RATE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeBitRate() throws IOException {
        int sampleRate = Math.round(format.getFrameRate());
        int sampleSize = format.getFrameSize();
        int numberOfChannels = format.getChannels();
        int bitRate = sampleRate * sampleSize * numberOfChannels;
        byte[] buffer = Primitives.intToLittleEndianByteArray4(bitRate);
        outputFile.seek(BIT_RATE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeBytesPerFrame() throws IOException {
        int bitsPerSample = format.getSampleSizeInBits();
        int numberOfChannels = format.getChannels();
        int bytesPerSampleTotal = bitsPerSample * numberOfChannels / 8;
        byte[] buffer = Primitives.intToLittleEndianByteArray2(bytesPerSampleTotal);
        outputFile.seek(BYTES_PER_SAMPLE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeBitsPerSample() throws IOException {
        int bitsPerSample = format.getSampleSizeInBits();
        byte[] buffer = Primitives.intToLittleEndianByteArray2(bitsPerSample);
        outputFile.seek(BITS_PER_SAMPLE_OFFSET);
        outputFile.write(buffer);
    }

    private void writeDataText() throws IOException {
        byte[] buffer = "data".getBytes();
        outputFile.seek(DATA_TEXT_OFFSET);
        outputFile.write(buffer);
    }

    private void writeDataSize(int dataSize) throws IOException {
        byte[] buffer = Primitives.intToLittleEndianByteArray4(dataSize);
        outputFile.seek(DATA_SIZE_OFFSET);
        outputFile.write(buffer);
    }

    public void updateHeader(int dataSize) throws IOException {
        writeFileSize(dataSize);
        writeDataSize(dataSize);
    }

    public void close() throws IOException {
        outputFile.close();
    }
}
