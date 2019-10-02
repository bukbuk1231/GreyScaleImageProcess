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

    public int[][] laplacian(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        int[][] mask = createLaplacianMask(maskSize);
        // GreyScaleUtil.print2DImageArray(mask);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                // GreyScaleUtil.print2DImageArray(maskRegion);
                int pixel = 0;
                for (int m = 0; m < maskSize; m++)
                    for (int n = 0; n < maskSize; n++)
                        pixel = pixel + mask[m][n] * maskRegion[m][n];
                if (pixel > 255)
                    pixel = 255;
                else if (pixel < 0)
                    pixel = 0;
                newImg[i][j] = pixel;
            }
        }
        return newImg;
    }

    public int[][] highboost(int maskSize, int a) {
        int h = image.length, w = image[0].length;
        int[][] blurred = smoothing(maskSize);
        int[][] mask = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                mask[i][j] = image[i][j] - blurred[i][j];
            }
        }

        int[][] newImg = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                newImg[i][j] = image[i][j] + a * mask[i][j];
                if (newImg[i][j] > 255)
                    newImg[i][j] = 255;
                else if (newImg[i][j] < 0)
                    newImg[i][j] = 0;
            }
        }
        return newImg;
    }

    private int[][] createLaplacianMask(int maskSize) {
        int[][] mask = new int[maskSize][maskSize];
        int center = -(maskSize * maskSize - 1);
        for (int i = 0; i < maskSize; i++)
            Arrays.fill(mask[i], 1);
        mask[maskSize / 2][maskSize / 2] = center;
        return mask;
    }
}
