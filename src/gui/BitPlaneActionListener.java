package gui;

import processor.BitPlane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BitPlaneActionListener implements ActionListener {

    private int bit;
    private String path;
    private int[][] image;

    public BitPlaneActionListener(String bit) {
        this.bit = Integer.valueOf(bit);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        BitPlane bitPlane = new BitPlane();
        bitPlane.setPath(path);

    }
}
