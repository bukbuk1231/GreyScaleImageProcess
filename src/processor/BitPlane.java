package processor;

public class BitPlane {

    private String path;
    private int[][] image;

    public void setPath(String path) {
        this.path = path;
        image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(this.path));
    }

}
