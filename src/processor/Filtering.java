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
                pixel += image[i][j];
                if (pixel > 255)
                    pixel = 255;
                else if (pixel < 0)
                    pixel = 0;
                newImg[i][j] = pixel;
            }
        }


        return newImg;
    }

    public int[][] highboost(int maskSize, double a) {
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
                newImg[i][j] = (int)(image[i][j] + a * mask[i][j]);
                if (newImg[i][j] > 255)
                    newImg[i][j] = 255;
                else if (newImg[i][j] < 0)
                    newImg[i][j] = 0;
            }
        }
        return newImg;
    }

    public int[][] createLaplacianMask(int maskSize) {
        int[][] mask = new int[maskSize][maskSize];
        int center = -(maskSize * maskSize - 1);
        for (int i = 0; i < maskSize; i++)
            Arrays.fill(mask[i], 1);
        mask[maskSize / 2][maskSize / 2] = center;
        return mask;
    }

    public int[][] arithmeticMean(int maskSize) {
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

    public int[][] geometricMean(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                double prod = 1.0;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++)
                        prod *= maskRegion[m][n];
                }
                newImg[i][j] = (int)Math.pow(prod, 1.0 / (maskSize * maskSize));
                if (newImg[i][j] > 255)
                    newImg[i][j] = 255;
                else if (newImg[i][j] < 0)
                    newImg[i][j] = 0;
            }
        }
        return newImg;
    }  // debug --> change getMask() for padding

    public int[][] harmonicMean(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                double reverseSum = 0;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++)
                        reverseSum += 1.0 / maskRegion[m][n];
                }
                newImg[i][j] = (int)((maskSize * maskSize * 1.0) / reverseSum);
            }
        }
        return newImg;
    }

    public int[][] contraHarmonicMean(int maskSize, double Q) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                double sum = 0, expSum = 0;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++) {
                        sum += Math.pow(maskRegion[m][n], Q);
                        expSum += Math.pow(maskRegion[m][n], Q + 1);
                    }
                }
                newImg[i][j] = (int)(expSum / sum);
            }
        }
        return newImg;
    }

    public int[][] max(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int max = 0;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++)
                        max = Math.max(max, maskRegion[m][n]);
                }
                newImg[i][j] = max;
            }
        }
        return newImg;
    }

    public int[][] min(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int min = 256;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++)
                        min = Math.min(min, maskRegion[m][n]);
                }
                newImg[i][j] = min;
            }
        }
        return newImg;
    }

    public int[][] midpoint(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int max = 0, min = 256;
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++) {
                        max = Math.max(max, maskRegion[m][n]);
                        min = Math.min(min, maskRegion[m][n]);
                    }
                }
                newImg[i][j] = (int)((max + min) * 1.0 / 2);
            }
        }
        return newImg;
    }

    public int[][] alphaTrimmedMean(int maskSize, double d) {
        int h = image.length, w = image[0].length;
        int[][] newImg = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int sum = 0, index = 0;
                int[] flatten = new int[maskSize * maskSize];
                for (int m = 0; m < maskSize; m++) {
                    for (int n = 0; n < maskSize; n++) {
                        flatten[index++] = maskRegion[m][n];
                    }
                }
                Arrays.sort(flatten);
                for (int m = (int)d - (int)(d / 2); m < flatten.length - (int)(d / 2); m++)
                    sum += flatten[m];

                newImg[i][j] = (int)(1.0 / ((maskSize * maskSize) - d) * sum);
            }
        }
        return newImg;
    }
}