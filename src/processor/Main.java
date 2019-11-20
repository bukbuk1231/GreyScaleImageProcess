package processor;

import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        Compression cmp = new Compression("C:\\Users\\louda\\Pictures\\Saved Pictures\\lena.jpg");
        // cmp.rlePixel();
        cmp.rleBitplane();
    }
}
