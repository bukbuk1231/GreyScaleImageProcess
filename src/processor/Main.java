package processor;

import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        int[][] img = {{1, 3, 2}, {5, 7, 4}, {8, 9, 0}};
        int[][] mask = GreyScaleUtil.createMaskRegion(img, 0, 2, 3);
        GreyScaleUtil.print2DImageArray(mask);
    }
}
