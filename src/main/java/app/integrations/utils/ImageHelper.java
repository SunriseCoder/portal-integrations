package app.integrations.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageHelper {
    private static final int SPEECH_MAX_DELTA = 400;
    private static final int CHANNEL_IMAGE_HEIGHT = 400;

    public static void createImage(List<List<Integer>> allStatistics, String foldername, String filename) throws IOException {
        File file = FileHelper.createFile(foldername, filename, true);
        BufferedImage resultImage = null;
        for (int i = 0; i < allStatistics.size(); i++) {
            List<Integer> statistic = allStatistics.get(i);
            BufferedImage image = createImage(statistic, i % 2 == 1);
            if (resultImage == null) {
                resultImage = image;
            } else {
                resultImage = combineImages(resultImage, image);
            }
        }
        saveImage(resultImage, file);
    }

    public static void combineImageFiles(String folder, String filename1, String filename2, String outputFilename) throws IOException {
        File file1 = FileHelper.checkAndGetFile(folder, filename1);
        BufferedImage image1 = ImageIO.read(file1);
        File file2 = FileHelper.checkAndGetFile(folder, filename2);
        BufferedImage image2 = ImageIO.read(file2);

        BufferedImage resultImage = combineImages(image1, image2);

        File outputFile = FileHelper.createFile(folder, outputFilename, true);
        saveImage(resultImage, outputFile);
    }

    private static BufferedImage combineImages(BufferedImage image1, BufferedImage image2) {
        int width = Math.max(image1.getWidth(), image2.getWidth());
        int height = image1.getHeight() + image2.getHeight() + 1;
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        resultImage.getGraphics().drawImage(image1, 0, 0, null);
        resultImage.getGraphics().drawImage(image2, 0, image1.getHeight() + 1, null);
        return resultImage;
    }

    private static BufferedImage createImage(List<Integer> statistics, boolean colors) {
        int width = statistics.size();
        BufferedImage image = new BufferedImage(width, CHANNEL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, CHANNEL_IMAGE_HEIGHT);

        graphics.setColor(Color.BLUE);
        for (int i = 0; i < statistics.size(); i++) {
            int value = statistics.get(i);
            if (colors && value < SPEECH_MAX_DELTA) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.BLUE);
            }
            value = CHANNEL_IMAGE_HEIGHT / 2 * value / 32767;
            int offsetY = CHANNEL_IMAGE_HEIGHT / 2 - value;
            graphics.drawLine(i, offsetY, i, offsetY + 2 * value);
        }

        return image;
    }

    private static void saveImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file);
    }
}
