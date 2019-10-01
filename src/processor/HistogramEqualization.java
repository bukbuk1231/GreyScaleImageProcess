package processor;

public class HistogramEqualization {

    private String path;
    private int[][] image;

    public void setPath(String path) {
        this.path = path;
        image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(this.path));
    }

    public int[][] globalEqualization() {
        return equalization(image);
    }

    public int[][] localEqualization(int maskSize) {
        int h = image.length, w = image[0].length;
        int[][] res = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int[][] maskRegion = GreyScaleUtil.createMaskRegion(image, i, j, maskSize);
                int[][] equalizedMask = equalization(maskRegion);
                res[i][j] = equalizedMask[maskSize / 2][maskSize / 2];
            }
        }
        return res;
    }

    public int[][] equalization(int[][] image) {
        int h = image.length, w = image[0].length;
        int[][] res = new int[h][w];

        int[] numPixels = new int[256];
        int totalPixels = h * w;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < h; j++) {
                numPixels[image[i][j]]++;
            }
        }

        double[] probability = new double[256];
        for (int i = 0; i < 256; i++)
            probability[i] = numPixels[i] * 1.0 / totalPixels;
        for (int i = 1; i < 256; i++)
            probability[i] = probability[i - 1] + probability[i];
        for (int i = 0; i < 256; i++)
            probability[i] = Math.floor(probability[i] * 256);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < h; j++) {
                res[i][j] = (int)probability[image[i][j]];
            }
        }
        return res;
    }
}
