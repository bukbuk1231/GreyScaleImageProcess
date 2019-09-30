package gui;

import processor.GreyScaleUtil;
import processor.HistogramEqualization;
import processor.ImageScaling;
import processor.ImageScalingAlgorithm;

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

public class CS555 {
    private JFrame frame;
    private String originalImagePath, generatePath;
    private JTextField textFieldX, textFieldY, textFieldHBCoeff, textFieldMask;
    private int w, h;
    private int maskSize, HBCoeff;
    private String algorithm;
    private int bit;
    private ImageScaling processor;
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
        HBCoeff = 3;
        maskSize = 3;
        algorithm = "Nearest Neigbor";
        bit = 0;
        processor = new ImageScaling();
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
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif", "png");
                file.setFileFilter(filter);
                int result = file.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = file.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    originalImagePath = path;
                    generatePath = path.substring(0, path.length() - 4);
                    originalLabel.setIcon(new ImageIcon(path));
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
                // Feed to processor and do some algorithm
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

                processor.setPath(originalImagePath);
                int[][] newImg = processor.scaleImage(w, h, alg, bit);
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_scaled.jpg");
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
        lblMask.setBounds(57, 286, 64, 14);
        optionsPanel.add(lblMask);

        textFieldMask = new JTextField();
        textFieldMask.setText("3");
        textFieldMask.setColumns(10);
        textFieldMask.setBounds(143, 286, 130, 28);
        optionsPanel.add(textFieldMask);

        JButton btnLocalHe = new JButton("Local HE");
        btnLocalHe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

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
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath + "_equalized.jpg");
                ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath + "_equalized.jpg"));
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
        optionsPanel.add(filterBox);

        JButton btnSpatialFiltering = new JButton("Spatial Filtering");
        btnSpatialFiltering.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnSpatialFiltering.setBackground(Color.GREEN);
        btnSpatialFiltering.setBounds(143, 414, 144, 33);
        optionsPanel.add(btnSpatialFiltering);

        JLabel lblA = new JLabel("A (HBoost only)");
        lblA.setBounds(256, 369, 75, 14);
        optionsPanel.add(lblA);

        textFieldHBCoeff = new JTextField();
        textFieldHBCoeff.setText("3");
        textFieldHBCoeff.setColumns(10);
        textFieldHBCoeff.setBounds(341, 369, 55, 28);
        optionsPanel.add(textFieldHBCoeff);
    }
}