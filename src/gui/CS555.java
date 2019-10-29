package gui;

import processor.*;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Color;
import java.util.Arrays;
import javax.swing.JCheckBox;

public class CS555 {
    private JFrame frame;
    private String originalImagePath, generatePath;
    private JTextField textFieldX, textFieldY, textFieldHBCoeff, textFieldMask, textFieldAlphaCoeff, textFieldContraHarmonicCoeff;
    private int w, h;
    private int maskSize;
    private double HBCoeff, alphaCoeff, contraHarmonicCoeff;
    private String algorithm, filter;
    private int bit;
    private ImageScaling scaler;
    private int[][] image;
    protected boolean[] plane;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CS555 window = new CS555();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public CS555() {
        originalImagePath = generatePath = null;
        w = h = 512;
        HBCoeff = 3.0;
        alphaCoeff = 5.0;
        contraHarmonicCoeff = 2.0;
        maskSize = 3;
        algorithm = "Nearest Neigbor";
        filter = "Smoothing";
        bit = 0;
        scaler = new ImageScaling();
        plane = new boolean[8];
        Arrays.fill(plane, true);
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 1447, 618);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel originalPanel = new JPanel();
        originalPanel.setBounds(0, 0, 512, 512);
        frame.getContentPane().add(originalPanel);
        originalPanel.setLayout(new BoxLayout(originalPanel, BoxLayout.X_AXIS));

        JLabel originalLabel = new JLabel("Original Image");
        originalLabel.setIcon(new ImageIcon());
        originalPanel.add(originalLabel);

        JPanel processedPanel = new JPanel();
        processedPanel.setBounds(513, 0, 512, 512);
        processedPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        frame.getContentPane().add(processedPanel);
        processedPanel.setLayout(null);

        JLabel processedLabel = new JLabel("Processed Image");
        processedLabel.setBounds(0, 0, 512, 512);
        processedPanel.add(processedLabel);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setBounds(1024, 0, 406, 579);
        frame.getContentPane().add(optionsPanel);

        JButton selectImgButton = new JButton("Choose a image");
        selectImgButton.setBounds(143, 36, 125, 50);
        selectImgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser file = new JFileChooser();
                file.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif", "png", "tif");
                file.setFileFilter(filter);
                int result = file.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = file.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    originalImagePath = path;
                    generatePath = path.substring(0, path.length() - 4);
                    if (path.substring(path.length() - 3).equals("tif")) {
                        GreyScaleUtil.writeImage(GreyScaleUtil.readImage(path), generatePath + "_converted_for_displaying.jpg", "jpg");
                        originalLabel.setIcon(new ImageIcon(generatePath + "_converted_for_displaying.jpg"));
                    } else {
                        originalLabel.setIcon(new ImageIcon(path));
                    }
                    image = GreyScaleUtil.get2DImageArray(GreyScaleUtil.readImage(originalImagePath));
                    h = image.length;
                    w = image[0].length;
                } else if (result == JFileChooser.CANCEL_OPTION) {
                    System.out.println("No Image Choosen");
                }
            }
        });
        optionsPanel.setLayout(null);
        optionsPanel.add(selectImgButton);

        textFieldX = new JTextField();
        textFieldX.setText("512");
        textFieldX.setBounds(143, 115, 64, 33);
        optionsPanel.add(textFieldX);
        textFieldX.setColumns(10);

        textFieldY = new JTextField();
        textFieldY.setText("512");
        textFieldY.setBounds(225, 115, 64, 33);
        optionsPanel.add(textFieldY);
        textFieldY.setColumns(10);

        JLabel lblResolution = new JLabel("Scale(m*n)");
        lblResolution.setBounds(57, 122, 75, 28);
        optionsPanel.add(lblResolution);

        JComboBox bitBox = new JComboBox();
        bitBox.setBounds(143, 159, 144, 28);
        for (int i = 8; i >= 1; i--) {
            bitBox.addItem("" + i);
        }
        optionsPanel.add(bitBox);

        JLabel lblNewLabel = new JLabel("Bit");
        lblNewLabel.setBounds(57, 161, 64, 19);
        optionsPanel.add(lblNewLabel);

        JComboBox algBox = new JComboBox();
        algBox.setBounds(143, 198, 144, 33);
        algBox.addItem("Nearest Neighbor");
        algBox.addItem("Linear X");
        algBox.addItem("Linear Y");
        algBox.addItem("Bilinear");
        optionsPanel.add(algBox);

        JLabel lblNewLabel_1 = new JLabel("Algorithm");
        lblNewLabel_1.setBounds(57, 207, 46, 14);
        optionsPanel.add(lblNewLabel_1);

        JButton btnNewButton = new JButton("Scale");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // Feed to scaler and do some algorithm
                // create util function to resize the image and convert to ImageIcon
                w = Integer.valueOf(textFieldX.getText());
                h = Integer.valueOf(textFieldY.getText());
                algorithm = (String)algBox.getSelectedItem();
                bit = Integer.valueOf((String) bitBox.getSelectedItem());

                ImageScalingAlgorithm alg = null;
                switch (algorithm) {
                    case "Nearest Neighbor":
                        alg = ImageScalingAlgorithm.NEAREST_NEIGHBOR;
                        break;
                    case "Linear X":
                        alg = ImageScalingAlgorithm.LINEAR_X;
                        break;
                    case "Linear Y":
                        alg = ImageScalingAlgorithm.LINEAR_Y;
                        break;
                    case "Bilinear":
                        alg = ImageScalingAlgorithm.BILINEAR_INTERPOLATION;
                        break;
                }

                scaler.setPath(originalImagePath);
                int[][] newImg = scaler.scaleImage(w, h, alg, bit);
                image = newImg;
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_scaled.jpg", "jpg");
                ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath + "_scaled.jpg"));
                processedLabel.setIcon(icon);

                System.out.println("File: " + originalImagePath);
                System.out.println("Resolution: " + w + " * " + h);
                System.out.println("Algorithm: " + algorithm);
                System.out.println("Bit: " + bit);
            }
        });
        btnNewButton.setBackground(Color.GREEN);
        btnNewButton.setBounds(143, 242, 144, 33);
        optionsPanel.add(btnNewButton);

        JLabel lblMask = new JLabel("Mask (n * n)");
        lblMask.setBounds(57, 286, 87, 14);
        optionsPanel.add(lblMask);

        textFieldMask = new JTextField();
        textFieldMask.setText("3");
        textFieldMask.setColumns(10);
        textFieldMask.setBounds(191, 286, 51, 28);
        optionsPanel.add(textFieldMask);

        JButton btnLocalHe = new JButton("Local HE");
        btnLocalHe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                maskSize = Integer.valueOf(textFieldMask.getText());
                HistogramEqualization histogramEqualization = new HistogramEqualization();
                histogramEqualization.setPath(originalImagePath);
                int[][] newImg = histogramEqualization.localEqualization(maskSize);
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_local_equalized.jpg", "jpg");
                ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath + "_local_equalized.jpg"));
                processedLabel.setIcon(icon);
            }
        });
        btnLocalHe.setBackground(Color.GREEN);
        btnLocalHe.setBounds(57, 325, 119, 33);
        optionsPanel.add(btnLocalHe);

        JButton btnGlobalHe = new JButton("Global HE");
        btnGlobalHe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HistogramEqualization histogramEqualization = new HistogramEqualization();
                histogramEqualization.setPath(originalImagePath);
                int[][] newImg = histogramEqualization.globalEqualization();
                image = newImg;
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_global_equalized.jpg", "jpg");
                ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath + "_global_equalized.jpg"));
                processedLabel.setIcon(icon);
            }
        });
        btnGlobalHe.setBackground(Color.GREEN);
        btnGlobalHe.setBounds(225, 325, 125, 33);
        optionsPanel.add(btnGlobalHe);

        JLabel lblFilter = new JLabel("Filter");
        lblFilter.setBounds(57, 369, 46, 14);
        optionsPanel.add(lblFilter);

        JComboBox filterBox = new JComboBox();
        filterBox.setBounds(98, 369, 144, 33);
        filterBox.addItem("Smoothing");
        filterBox.addItem("Median");
        filterBox.addItem("Sharpening Laplacian");
        filterBox.addItem("High-boosting");
        filterBox.addItem("Arithmetic Mean");
        filterBox.addItem("Geometric Mean");
        filterBox.addItem("Harmonic Mean");
        filterBox.addItem("Contraharmonic Mean");
        filterBox.addItem("Max");
        filterBox.addItem("Min");
        filterBox.addItem("Midpoint");
        filterBox.addItem("Alpha-trimmed Mean");
        optionsPanel.add(filterBox);

        JButton btnSpatialFiltering = new JButton("Spatial Filtering");
        btnSpatialFiltering.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filter = (String)filterBox.getSelectedItem();
                maskSize = Integer.valueOf(textFieldMask.getText());
                HBCoeff = Double.valueOf(textFieldHBCoeff.getText());
                alphaCoeff = Double.valueOf(textFieldAlphaCoeff.getText());
                contraHarmonicCoeff = Double.valueOf(textFieldContraHarmonicCoeff.getText());

                int[][] newImg = null;
                Filtering filtering = new Filtering();
                filtering.setPath(originalImagePath);
                switch (filter) {
                    case "Smoothing":
                        newImg = filtering.smoothing(maskSize);
                        break;
                    case "Median":
                        newImg = filtering.median(maskSize);
                        break;
                    case "Sharpening Laplacian":
                        newImg = filtering.laplacian(maskSize);
                        break;
                    case "High-boosting":
                        newImg = filtering.highboost(maskSize, HBCoeff);
                        break;
                    case "Arithmetic Mean":
                        newImg = filtering.arithmeticMean(maskSize);
                        break;
                    case "Geometric Mean":
                        newImg = filtering.geometricMean(maskSize);
                        break;
                    case "Harmonic Mean":
                        newImg = filtering.harmonicMean(maskSize);
                        break;
                    case "Contraharmonic Mean":
                        newImg = filtering.contraHarmonicMean(maskSize, contraHarmonicCoeff);
                        break;
                    case "Max":
                        newImg = filtering.max(maskSize);
                        break;
                    case "Min":
                        newImg = filtering.min(maskSize);
                        break;
                    case "Midpoint":
                        newImg = filtering.midpoint(maskSize);
                        break;
                    case "Alpha-trimmed Mean":
                        newImg = filtering.alphaTrimmedMean(maskSize, alphaCoeff);
                        break;
                }
                image = newImg;
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_filtered.jpg", "jpg");
                ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath + "_filtered.jpg"));
                processedLabel.setIcon(icon);
            }
        });
        btnSpatialFiltering.setBackground(Color.GREEN);
        btnSpatialFiltering.setBounds(100, 414, 144, 33);
        optionsPanel.add(btnSpatialFiltering);

        JLabel lblA = new JLabel("A (HBoost)");
        lblA.setBounds(256, 369, 94, 14);
        optionsPanel.add(lblA);

        textFieldHBCoeff = new JTextField();
        textFieldHBCoeff.setText("3");
        textFieldHBCoeff.setColumns(10);
        textFieldHBCoeff.setBounds(365, 369, 31, 28);
        optionsPanel.add(textFieldHBCoeff);

        JLabel lblD = new JLabel("D (Alpha)");
        lblD.setBounds(256, 400, 94, 14);
        optionsPanel.add(lblD);

        textFieldAlphaCoeff = new JTextField();
        textFieldAlphaCoeff.setText("5");
        textFieldAlphaCoeff.setColumns(10);
        textFieldAlphaCoeff.setBounds(365, 400, 31, 28);
        optionsPanel.add(textFieldAlphaCoeff);

        JLabel lblQ = new JLabel("Q (ContraH)");
        lblQ.setBounds(256, 435, 94, 14);
        optionsPanel.add(lblQ);

        textFieldContraHarmonicCoeff = new JTextField();
        textFieldContraHarmonicCoeff.setText("2");
        textFieldContraHarmonicCoeff.setColumns(10);
        textFieldContraHarmonicCoeff.setBounds(365, 430, 31, 28);
        optionsPanel.add(textFieldContraHarmonicCoeff);

        JLabel lblBitPlanes = new JLabel("Bit planes");
        lblBitPlanes.setBounds(57, 455, 75, 14);
        optionsPanel.add(lblBitPlanes);

        final int x = 138, y1 = 454, y2 = 480, dx = 60;
        for (int i = 0; i < 8; i++) {
            JCheckBox bitCheckBox = new JCheckBox(String.valueOf(i), true);
            bitCheckBox.addActionListener(new BitPlaneActionListener(i, processedLabel));
            if (i >= 4)
                bitCheckBox.setBounds(x + (i - 4) * dx, y2, 43, 23);
            else
                bitCheckBox.setBounds(x + i * dx, y1, 43, 23);
            optionsPanel.add(bitCheckBox);
        }
    }

    class BitPlaneActionListener implements ActionListener {
        private int bit;
        private JLabel processedLabel;
        BitPlaneActionListener(int bit, JLabel processedLabel) {
            this.bit = bit;
            this.processedLabel = processedLabel;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            plane[bit] = !plane[bit];
            int[][] newImg = getImageUnderBitPlane();
            GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_bitPlane.jpg", "jpg");
            ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath + "_bitPlane.jpg"));
            processedLabel.setIcon(icon);
        }

        private int[][] getImageUnderBitPlane() {
            int[][] newImg = new int[h][w];
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int pixel = image[i][j];
                    for (int k = 0; k < 8; k++) {
                        if (plane[k] == false) {
                            pixel = pixel & (~(1 << k));
                        }
                    }
                    newImg[i][j] = pixel;
                }
            }
            return newImg;
        }
    }
}