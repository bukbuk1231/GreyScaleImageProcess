package processor;

import utils.GreyScaleUtil;
import utils.HuffmanCode;

import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        Compression cmp = new Compression("C:\\Users\\louda\\Pictures\\Saved Pictures\\lena.jpg");
        // Compression cmp = new Compression("C:\\Users\\louda\\Pictures\\Saved Pictures\\All Black Backgrounds 0.jpg");

//        String x = cmp.rlePixel();
//        cmp.rlePixelDecode(x);

//        String[] y = cmp.rleBitplane();
//        int[][] img = cmp.rleBitplaneDecode(y);
//        GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(img), "C:\\Users\\louda\\Pictures\\Saved Pictures\\lena_bitplane_decode.jpg", "jpg");

//        HuffmanCode hc = cmp.huffman();
//        int[][] img = cmp.huffmanDecode(hc);
//        GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(img), "C:\\Users\\louda\\Pictures\\Saved Pictures\\lena_bitplane_decode.jpg", "jpg");

         cmp.lzw();
    }
}
