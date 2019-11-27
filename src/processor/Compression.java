package processor;

import utils.Cell;
import utils.GreyScaleUtil;
import utils.HuffmanCode;

import java.util.*;

public class Compression {

    private int[][] image;
    private int h, w;

    public Compression(String filePath) {
        image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(filePath));
        h = image.length;
        w = image[0].length;
    }

    public String rlePixel() {
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
            for (int i = 0; i < 32; i += 8)
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
        System.out.println("Compression Ratio: " + len + " to " + (h * w) + " = " + (len * 1.0 / (h * w)));
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");
        return res.toString();
    }

    public String[] rleBitplane() {
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
        System.out.println("Compression Ratio: " + size + " to " + (h * w) + " = " + (size * 1.0 / (h * w)));
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");

        String[] planes = new String[8];
        for (int i = 0; i < 8; i++)
            planes[i] = res[i].toString();
        return planes;
    }

    public HuffmanCode huffman() {
        int[] freq = new int[256];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                freq[image[i][j]]++;
            }
        }
        StringBuilder[] codes = new StringBuilder[256];
        PriorityQueue<Cell> heap = new PriorityQueue<>((a, b) -> {
            if (a.prob == b.prob)
                return 0;
            return a.prob < b.prob ? -1 : 1;
        });
        for (int i = 0; i < 256; i++) {
            codes[i] = new StringBuilder();
            if (freq[i] > 0)
                heap.offer(new Cell(new HashSet<>(Arrays.asList(i)), freq[i] * 1.0 / (h * w)));
        }

        long start = System.nanoTime();
        while (heap.size() >= 2) {
            Cell top1 = heap.poll();
            Cell top2 = heap.poll();
            for (int original : top1.set) {
                codes[original].append('0');
            }
            for (int original : top2.set) {
                codes[original].append('1');
            }
            Cell newCell = new Cell(new HashSet<>(), top1.prob + top2.prob);
            newCell.set.addAll(top1.set);
            newCell.set.addAll(top2.set);
            heap.offer(newCell);
        }
        long end = System.nanoTime();

        int size = 0;
        StringBuilder out = new StringBuilder();
        System.out.println("Encoded Image: ");
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                size += codes[image[i][j]].length();
                out.append(codes[image[i][j]]);
                System.out.print(codes[image[i][j]]);
            }
        }
        Map<Integer, String> table = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            if (codes[i].length() > 0)
                table.put(i, codes[i].toString());
        }

        System.out.println("\nSize: " + (size / 8) + " bytes");
        System.out.println("Compression Ratio: " + (size / 8) + " to " + (h * w) + " = " + ((size / 8) * 1.0 / (h * w)));
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");

        return new HuffmanCode(out.toString(), table);
    }

    public void lzw() {
        int[] img = GreyScaleUtil.flatten(image);
        int s = 0, f = 0, compCode = 256;
        StringBuilder encoded = new StringBuilder();
        Map<String, Integer> map = new HashMap<>();

        long start = System.nanoTime();
        while (f < img.length) {
            if (f + 1 >= img.length) {
                System.out.print(img[s]);
                break;
            }

            StringBuilder tmp = new StringBuilder(img[s]).append(img[++f]);
            String out = String.valueOf(img[s]), cur = tmp.toString();
            while (map.containsKey(cur)) {
                out = cur;
                if (f + 1 >= img.length) {
                    break;
                } else {
                    tmp.append(img[++f]);
                    cur = tmp.toString();
                }
            }
            map.put(cur, compCode++);
            encoded.append(out);
            s = f;
        }
        long end = System.nanoTime();

        System.out.println("Encoded Image: ");
        System.out.println(encoded);
        System.out.println("\nSize: " + (encoded.length() / 8) + " bytes");
        System.out.println("Compression Ratio: " + (encoded.length() / 8) + " to " + (h * w) + " = " + ((encoded.length() / 8) * 1.0 / (h * w)));
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");
    }

    // we need to store dimension information in encoded string, but for simplicity, I assume I know the dimension
    // There is some Java internal issues dealing with converting between Byte String and byte[]
    public int[][] rlePixelDecode(String code) {
        int[] flatten = new int[h * w];
        int index = 0;
        byte[] bcode = code.getBytes();
        for (int i = 0; i < bcode.length; i += 5) {
            int cnt = 0;
            for (int j = 0; j < 4; j++) {
                cnt |= (bcode[i + j] << (j << 3));
            }
            for (int j = 0; j < cnt; j++) {
                flatten[index++] = bcode[i + 4];
            }
        }
        return GreyScaleUtil.unflatten(flatten, h, w);
    }

    public int[][] rleBitplaneDecode(String[] code) {
        int[] flatten = new int[h * w];

        for (int i = 0; i < 8; i++) {
            int index = 0;
            String[] split = code[i].split("\\s+");
            int bit = Integer.valueOf(split[0]);
            for (int j = 1; j < split.length; j++) {
                for (int k = 0; k < Integer.valueOf(split[j]); k++) {
                    flatten[index++] |= bit << i;
                }
                bit = 1 - bit;
            }
        }
        return GreyScaleUtil.unflatten(flatten, h, w);
    }

    public int[][] huffmanDecode(HuffmanCode huffmanCode) {
        return null;
    }
}
