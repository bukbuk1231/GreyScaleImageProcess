package processor;

import java.util.Arrays;

public class Filtering {

    private String path;
    private int[][] image;

    public void setPath(String path) {
        this.path = path;
        image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(this.path));
    }

    public int[][] smoothing(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int sum = 0;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++)
                        sum += maskRegion[m][n];
                }
                newImg[i][j] = sum / (maskSize * maskSize);
            }
        }
        return newImg;
    }

    public int[][] median(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int[] flatten = new int[maskSize * maskSize];
                for (int k = 0; k < maskSize * maskSize; k++)
                    flatten[k] = maskRegion[k / maskSize][k % maskSize];
                Arrays.sort(flatten);
                newImg[i][j] = flatten[maskSize * maskSize / 2];
            }
        }
        return newImg;
    }
}
