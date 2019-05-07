package app.integrations.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageHelper {
    private static final int SPEECH_MAX_DELTA = 1600;
    private static final int CHANNEL_IMAGE_HEIGHT = 400;

    public static void createImage(List<List<Integer>> statistics, String folderName, String fileName, String caption)
            throws IOException {
        BufferedImage image = createStatisticImage(statistics, caption);
        saveImage(folderName, fileName, image);
    }

    public static BufferedImage createStatisticImage(List<List<Integer>> allStatistics, String caption) {
        BufferedImage resultImage = createStatisticHeader(caption);
        for (int i = 0; i < allStatistics.size(); i++) {
            List<Integer> statistic = allStatistics.get(i);
            BufferedImage image = createImage(statistic, i % 2 == 1);
            resultImage = combineImages(resultImage, image);
        }
        return resultImage;
    }

    private static BufferedImage createStatisticHeader(String caption) {
        // Calculation text caption size
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        Font font = new Font("Verdana", Font.BOLD, 16);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(caption);
        int height = fm.getHeight();
        g2d.dispose();

        // Creating new image for it
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Rendering text caption
        g2d.setFont(font);
        g2d.setColor(Color.BLUE);
        fm = g2d.getFontMetrics();
        g2d.drawString(caption, 0, fm.getAscent());
        g2d.dispose();

        return image;
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
        Graphics graphics = resultImage.getGraphics();
        graphics.fillRect(0, 0, width, height);

        graphics.drawImage(image1, 0, 0, null);
        graphics.drawImage(image2, 0, image1.getHeight() + 1, null);
        return resultImage;
    }

    private static BufferedImage createImage(List<Integer> statistics, boolean colors) {
        int imageWidth = statistics.size();
        int imageHeight = CHANNEL_IMAGE_HEIGHT;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        // Filling background
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        // Drawing rectangle border
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, imageWidth - 1, imageHeight - 1);

        graphics.setColor(Color.BLUE);
        for (int i = 0; i < statistics.size(); i++) {
            int value = statistics.get(i);
            if (colors && value < SPEECH_MAX_DELTA) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.BLUE);
            }
            Limits limits = calculateLimits(value);
            graphics.drawLine(i, limits.neg, i, limits.pos);
            if (i % 1 == 0) {
                graphics.setColor(Color.MAGENTA);
                limits = calculateLimits(4000);
                graphics.drawLine(i, limits.neg, i, limits.neg);
                graphics.drawLine(i, limits.pos, i, limits.pos);
            }
        }

        return image;
    }

    private static Limits calculateLimits(int value) {
        int scaledValue = CHANNEL_IMAGE_HEIGHT / 2 * value / 8192;
        Limits limits = new Limits();
        limits.neg = CHANNEL_IMAGE_HEIGHT / 2 - scaledValue;
        limits.pos = limits.neg + 2 * scaledValue;
        return limits;
    }

    public static void saveImage(String folderName, String fileName, BufferedImage image) throws IOException {
        File file = FileHelper.createFile(folderName, fileName, true);
        saveImage(image, file);
    }

    private static void saveImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file);
    }

    private static class Limits {
        private int pos;
        private int neg;
    }
}
