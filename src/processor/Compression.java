package processor;

public class Compression {

    private int[][] image;
    private int h, w;

    public Compression(String filePath) {
        image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(filePath));
        h = image.length;
        w = image[0].length;
    }

    public void rlePixel() {
        StringBuilder res = new StringBuilder();
        int[] img = GreyScaleUtil.flatten(image);
        int s = 0, f = 0, len = 0;

        long start = System.nanoTime();
        while (f < img.length) {
            int cnt = 0;
            while (f < img.length && img[f] == img[s]) {
                cnt++;
                f++;
            }
            byte[] bytes = new byte[4];
            for (int i = 0; i < 32; i++)
                bytes[i >> 3] |= (byte) (cnt >> i);
            res.append(new String(bytes));

            res.append(new String(new byte[] { (byte)img[s] }));

            len += 5;
            s = f;
        }
        long end = System.nanoTime();

        System.out.print("Encoded Byte String: ");
        System.out.println(res);
        System.out.println("Size: " + len + " bytes");
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");
    }

    public void rleBitplane() {
        StringBuilder[] res = new StringBuilder[8];
        int[] img = GreyScaleUtil.flatten(image);
        long start = System.nanoTime();

        for (int i = 0; i < 8; i++) {
            StringBuilder plane = new StringBuilder();
            int s = 0, f = 0;
            plane.append(img[0] >> i & 1).append(' ');
            while (f < img.length) {
                int cnt = 0;
                while (f < img.length && (img[f] >> i & 1) == (img[s] >> i & 1)) {
                    cnt++;
                    f++;
                }
                plane.append(cnt).append(' ');
                s = f;
            }
            res[i] = plane;
        }
        long end = System.nanoTime();

        int size = 0;
        System.out.println("Encoded Bit Planes:");
        for (int i = 0; i < 8; i++) {
            System.out.println("Plane " + i + ": " + res[i]);
            size += res[i].length() * 2;
        }
        System.out.println("Size: " + size + " bytes");
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");
    }
}
