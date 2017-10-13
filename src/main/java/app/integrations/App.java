package app.integrations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.integrations.audio.ChannelOperation;
import app.integrations.audio.FileScanner;
import app.integrations.utils.ImageHelper;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            printUsage();
            System.exit(0);
        }

        String inputFileName = args[0];
        String outputFileName = args[1];
        String optionsString = args[2];

        List<ChannelOperation> operations = parseOperations(optionsString);

        long startTime = System.currentTimeMillis();

        process(inputFileName, outputFileName, operations);

        System.out.println();
        System.out.println("Done, took: " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    private static List<ChannelOperation> parseOperations(String optionsString) {
        List<ChannelOperation> operations = new ArrayList<>();
        String[] options = optionsString.split(",");
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            String[] params = option.split("-");

            if (params.length != 2) {
                System.out.println("Invalid option: " + option);
                printUsage();
                System.exit(-1);
            }

            String operationCode = params[0];
            boolean isAdjustOperation = false;
            if ("c".equals(operationCode)) {
                isAdjustOperation = false;
            } else if ("a".equals(operationCode)) {
                isAdjustOperation = true;
            } else {
                System.out.println("Invalid operation: " + operationCode);
                printUsage();
                System.exit(-1);
            }

            String sourceChannelNumber = params[1];
            int inputChannelNumber = Integer.parseInt(sourceChannelNumber);

            ChannelOperation operation = new ChannelOperation(inputChannelNumber, i, isAdjustOperation);

            operations.add(operation);
        }
        return operations;
    }

    private static void process(String inputFileName, String outputFileName, List<ChannelOperation> operations) throws Exception {
        FileScanner scanner = new FileScanner();
        scanner.open(inputFileName);
        scanner.setOutput(outputFileName, operations.size());
        scanner.process(operations, 1000);
        scanner.close();
    }

    private static void dumpImage(String folder, String inputName, String imageFile) throws Exception {
        FileScanner scanner = new FileScanner();
        scanner.open(folder, inputName);
        List<List<Integer>> statistics = scanner.calculateStatistics(1000);
        scanner.close();
        ImageHelper.createImage(statistics, folder, imageFile);
    }

    private static void combineImages(String folder, String file1, String file2, String outputFile) throws IOException {
        ImageHelper.combineImageFiles(folder, file1, file2, outputFile);
    }

    private static void printUsage() {
        System.out.println("Usage: " + App.class.getSimpleName() + " <input file> <output file> <options>");
        System.out.println("options: c-1,a-0 means: track 1 just copy, track 0 adjust volume and change their order");
    }
}
