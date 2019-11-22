package processor;

import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        Compression cmp = new Compression("C:\\Users\\louda\\Pictures\\Saved Pictures\\lena.jpg");
        // Compression cmp = new Compression("C:\\Users\\louda\\Pictures\\Saved Pictures\\All Black Backgrounds 0.jpg");

//        String x = cmp.rlePixel();
//        cmp.rlePixelDecode(x);

//        String[] y = cmp.rleBitplane();
//        int[][] img = cmp.rleBitplaneDecode(y);
//        GreyScaleUtil.print2DImageArray(img);

        // cmp.huffman();


        // cmp.lzw();
    }
}
