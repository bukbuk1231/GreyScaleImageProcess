package processor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class GreyScaleUtil {

    public static BufferedImage readImage(String filePath) {
        try {
            File imageFile = new File(filePath);
            BufferedImage image = ImageIO.read(imageFile);
            return image;
        } catch(IOException e) {
            System.out.println("Error: "+ e);
        }
        return null;
    }

    public static BufferedImage generateImage(int[][] image) {
        BufferedImage img = new BufferedImage(image.length, image[0].length, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                raster.setSample(i, j, 0, image[i][j]);
            }
        }
        return img;
    }

    public static void writeImage(BufferedImage image, String filePath) {
        try {
            File output = new File(filePath);
            ImageIO.write(image, "jpg", output);
        } catch (IOException e) {
            System.out.println("Error: "+ e);
        }
    }

    public static int[][] get2DImageArray(BufferedImage image) {
        int[][] data = new int[image.getWidth()][image.getHeight()];
        Raster raster = image.getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++)
                data[i][j] = raster.getSample(i, j, 0);
        }
        return data;
    }

    public static void print2DImageArray(int[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                System.out.print(image[i][j] + ", ");
            }
            System.out.println();
        }
    }
}
