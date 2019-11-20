package processor;

import java.util.*;

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

    public void huffman() {
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
        System.out.println("Encoded Image: ");
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                size += codes[image[i][j]].length();
                System.out.print(codes[image[i][j]]);
            }
        }
        System.out.println("\nSize: " + (size / 8) + " bytes");
        System.out.println("Encode time: " + ((end - start) / 1e9) + " seconds");
    }

    class Cell {
        Set<Integer> set;
        double prob;
        Cell(Set<Integer> set, double prob) {
            this.set = set;
            this.prob = prob;
        }
    }
}
