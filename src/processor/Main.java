package processor;

import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        // BufferedImage image = GreyScaleUtil.readImage("C:\\Users\\louda\\Pictures\\Saved Pictures\\lena_gray.gif");
        ImageScaling ip = new ImageScaling();

        BufferedImage image = GreyScaleUtil.readImage("/Users/jundalou/Downloads/lena_gray.jpg");
        int[][] data = GreyScaleUtil.get2DImageArray(image);
        ip.setPath("/Users/jundalou/Downloads/lena_gray.jpg");
        ip.setPath("/Users/jundalou/Downloads/lena_gray_generated.jpg");
        data = ip.scaleImage(420, 420, ImageScalingAlgorithm.BILINEAR_INTERPOLATION, 8);

        BufferedImage img = GreyScaleUtil.generateImage(data);
        GreyScaleUtil.writeImage(img, "/Users/jundalou/Downloads/lena_gray_generated.jpg");

        // GreyScaleUtil.print2DImageArray(data);
    }
}
