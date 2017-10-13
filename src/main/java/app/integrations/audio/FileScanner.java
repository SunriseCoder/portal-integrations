package app.integrations.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import app.integrations.audio.api.FrameInputStream;
import app.integrations.audio.api.FrameOutputStream;
import app.integrations.audio.api.FrameStreamProcessor;
import app.integrations.audio.wav.WaveInputStream;
import app.integrations.audio.wav.WaveOutputStream;
import app.integrations.utils.AudioFormatHelper;
import app.integrations.utils.FileHelper;
import app.integrations.utils.ProgressPrinter;

public class FileScanner {
    private File inputFile;
    private File outputFile;
    private AudioFormat inputFormat;
    private FrameOutputStream outputStream;

    public void open(String inputFileName) throws IOException, UnsupportedAudioFileException {
        File file = FileHelper.checkAndGetFile(inputFileName);
        setInputFile(file);
    }

    public void open(String foldername, String filename) throws IOException, UnsupportedAudioFileException {
        File file = FileHelper.checkAndGetFile(foldername, filename);
        setInputFile(file);
    }

    private void setInputFile(File file) throws UnsupportedAudioFileException, IOException {
        this.inputFile = file;

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        this.inputFormat = audioInputStream.getFormat();
    }

    public void setOutput(String outputFileName, int channelsCount) throws IOException, UnsupportedAudioFileException {
        AudioFormat outputFormat = AudioFormatHelper.copyFormat(inputFormat, channelsCount);
        setOutput(outputFileName, outputFormat);
    }

    public void setOutput(String outputFileName, AudioFormat outputFormat) throws IOException, UnsupportedAudioFileException {
        this.outputFile = FileHelper.createFile(outputFileName, true);
        this.outputStream = new WaveOutputStream(outputFile, outputFormat);
        this.outputStream.writeHeader();
    }

    public void setOutput(String foldername, String outputname, int channelsCount) throws IOException, UnsupportedAudioFileException {
        AudioFormat outputFormat = AudioFormatHelper.copyFormat(inputFormat, channelsCount);
        setOutput(foldername, outputname, outputFormat);
    }

    public void setOutput(String foldername, String outputname, AudioFormat outputFormat) throws IOException, UnsupportedAudioFileException {
        this.outputFile = FileHelper.createFile(foldername, outputname, true);
        this.outputStream = new WaveOutputStream(outputFile, outputFormat);
        this.outputStream.writeHeader();
    }

    public List<List<Integer>> calculateStatistics(int chunkSizeMs) throws Exception {
        int frameRate = (int) inputFormat.getFrameRate();
        int chunkSize = frameRate * chunkSizeMs / 1000;
        int[] frameBuffer = new int[chunkSize];

        int channelCount = inputFormat.getChannels();
        List<List<Integer>> fileStatistics = new ArrayList<>();

        for (int channel = 0; channel < channelCount; channel++) {
            fileStatistics.add(new ArrayList<>());
            fileStatistics.add(new ArrayList<>());
        }

        FrameInputStream inputStream = WaveInputStream.create(inputFile);
        long framesCount = inputStream.getFramesCount();

        ProgressPrinter progressPrinter = new ProgressPrinter();
        progressPrinter.setTotal(framesCount);

        long processedFramesTotal = 0;
        do {
            for (int channel = 0; channel < channelCount; channel++) {
                int read = inputStream.readFrames(channel, frameBuffer);
                if (read > 0) {
                    StatsCalculator frameStatistics = new StatsCalculator();
                    frameStatistics.addData(frameBuffer, read);

                    List<Integer> channelMeanings = fileStatistics.get(channel);
                    channelMeanings.add(frameStatistics.getMathMeaning());

                    List<Integer> channelDeltas = fileStatistics.get(channel + 1);
                    channelDeltas.add(frameStatistics.getAverageDelta());
                }

                processedFramesTotal += read;
                progressPrinter.updateProgress(processedFramesTotal);
            }
        } while (processedFramesTotal < framesCount);
        close(inputStream);
        return fileStatistics;
    }

    public void process(List<ChannelOperation> operations, int chunkSizeMs) throws IOException, UnsupportedAudioFileException {
        int frameRate = (int) inputFormat.getFrameRate();
        int chunkSize = frameRate * chunkSizeMs / 1000;

        fileInfoPrint(operations);

        List<FrameStreamProcessor> processors = new ArrayList<>();
        long framesCount = 0;
        for (ChannelOperation operation : operations) {

            int inputChannel = operation.getInputChannel();
            int outputChannel = operation.getOutputChannel();

            FrameInputStream inputStream = WaveInputStream.create(inputFile, inputChannel);
            framesCount = inputStream.getFramesCount();

            FrameStreamProcessor processor;
            if (operation.isAdjust()) { // Need to adjust channel
                processor = new FrameStreamAdjuster(inputStream, outputStream, outputChannel);
            } else {
                processor = new FrameStreamCopier(inputStream, outputStream, outputChannel);
            }
            processor.setChunkSize(chunkSize);
            processor.prepareOperation();
            processors.add(processor);
        }

        ProgressPrinter progressPrinter = new ProgressPrinter();
        progressPrinter.setTotal(framesCount);
        long processedFramesTotal = 0;
        long processedFrames = 0;
        do {
            for (FrameStreamProcessor processor : processors) {
                processedFrames = processor.processPortion();
            }

            processedFramesTotal += processedFrames;
            progressPrinter.updateProgress(processedFramesTotal);
        } while (processedFramesTotal < framesCount);
        close(processors);
    }

    private void fileInfoPrint(List<ChannelOperation> operations) {
        System.out.println("Input file: " + inputFile.getAbsolutePath());
        System.out.println("Output file: " + outputFile.getAbsolutePath());

        for (ChannelOperation operation : operations) {
            String operationText = operation.getInputChannel() + "-> " + operation.getOutputChannel();
            operationText += " (" + (operation.isAdjust() ? "adjust" : "copy") + ")";
            System.out.println(operationText);
        }
    }

    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void close(List<FrameStreamProcessor> processors) {
        for (FrameStreamProcessor processor : processors) {
            processor.close();
        }
    }

    private void close(FrameInputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Just swallowing
            }
        }
    }
}
