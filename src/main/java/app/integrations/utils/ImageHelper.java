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

    public static void createImage(List<List<Integer>> statistics, String folderName, String fileName) throws IOException {
        BufferedImage image = createStatisticImage(statistics);
        saveImage(folderName, fileName, image);
    }

    public static BufferedImage createStatisticImage(List<List<Integer>> allStatistics) {
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
        return resultImage;
    }

    public static void combineImageFiles(String folder, String fileName1, String fileName2, String outputFileName) throws IOException {
        BufferedImage image1 = loadImage(folder, fileName1);
        BufferedImage image2 = loadImage(folder, fileName2);
        BufferedImage resultImage = combineImages(image1, image2);

        saveImage(folder, outputFileName, resultImage);
    }

    public static BufferedImage loadImage(String folder, String fileName) throws IOException {
        File file = FileHelper.checkAndGetFile(folder, fileName);
        BufferedImage image = ImageIO.read(file);
        return image;
    }

    public static BufferedImage combineImages(BufferedImage image1, BufferedImage image2) {
        if (image1 == null) {
            return image2;
        }

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

    public static void saveImage(String folderName, String fileName, BufferedImage image) throws IOException {
        File file = FileHelper.createFile(folderName, fileName, true);
        saveImage(image, file);
    }

    private static void saveImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file);
    }
}
