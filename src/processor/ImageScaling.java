package processor;

public class ImageScaling {

    private String path;
    private int[][] image;

    public ImageScaling() {
        path = null;
    }

    public void setPath(String path) {
        this.path = path;
        image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(this.path));
    }

    public int[][] scaleImage(int w2, int h2, ImageScalingAlgorithm method, int bitSize) {
        int[][] newImg = new int[h2][w2];
        switch (method) {
            case NEAREST_NEIGHBOR:
                newImg = nearestNeighbor(w2, h2);
                break;
            case LINEAR_X:
                newImg = linearX(w2, h2);
                break;
            case LINEAR_Y:
                newImg = linearY(w2, h2);
                break;
            case BILINEAR_INTERPOLATION:
                newImg = bilinear(w2, h2);
                break;
        }
        int ratio = 256 / (1 << bitSize);
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                newImg[i][j] /= ratio;
                newImg[i][j] *= ratio;
            }
        }
        return newImg;
    }

    public int[][] linearX(int w2, int h2) {
        int[][] newImg = new int[h2][w2];
        int h1 = image.length, w1 = image[0].length;
        double xr = (w1 - 1) * 1.0 / w2;
        double yr = h1 * 1.0 / h2;
        int x, y;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int)(j * xr);
                y = (int)(i * yr);
                double diffX = (j * xr) - x;
                int a = image[y][x], b = image[y][x + 1];
                newImg[i][j] = (int)(a + diffX * (b - a));
            }
        }
        return newImg;
    }

    public int[][] linearY(int w2, int h2) {
        int[][] newImg = new int[h2][w2];
        int h1 = image.length, w1 = image[0].length;
        double xr = w1 * 1.0 / w2;
        double yr = (h1 - 1) * 1.0 / h2;
        int x, y;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int)(j * xr);
                y = (int)(i * yr);
                double diffX = (j * xr) - x;
                int c = image[y][x], d = image[y + 1][x];
                newImg[i][j] = (int)(c + diffX * (d - c));
            }
        }
        return newImg;
    }

    private int[][] bilinear(int w2, int h2) {
        int[][] newImg = new int[h2][w2];
        int h1 = image.length, w1 = image[0].length;

        // to avoid index out of bound
        double xr = (w1 - 1) * 1.0 / w2;
        double yr = (h1 - 1) * 1.0 / h2;
        int x, y;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int)(j * xr);
                y = (int)(i * yr);
                double diffX = (j * xr) - x, diffY = (i * yr) - y;
                int a = image[y][x];
                int b = image[y][x + 1];
                int c = image[y + 1][x];
                int d = image[y + 1][x + 1];
                newImg[i][j] = (int)(a * (1 - diffX) * (1 - diffY) + b * diffX * (1 - diffY) + c * (1 - diffX) * diffY + d * diffX * diffY);
            }
        }
        return newImg;
    }

    private int[][] nearestNeighbor(int w2, int h2) {
        int h1 = image.length, w1 = image[0].length;
        int[][] newImg = new int[h2][w2];
        double xr = w1 * 1.0 / w2;
        double yr = h1 * 1.0 / h2;
        int x, y;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int)(j * xr);
                y = (int)(i * yr);
                newImg[i][j] = image[y][x];
            }
        }
        return newImg;
    }
}
