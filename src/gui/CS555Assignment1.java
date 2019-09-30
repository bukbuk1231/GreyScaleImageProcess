package gui;

import processor.GreyScaleUtil;
import processor.ImageProcess;
import processor.ImageProcessingAlgorithm;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
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
import java.net.URL;

public class CS555Assignment1 {
    private JFrame frame;
    private String originalImagePath, generatePath;
    private JTextField textFieldX, textFieldY;
    private int w, h;
    private String algorithm;
    private int bit;
    private ImageProcess processor;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CS555Assignment1 window = new CS555Assignment1();
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
    public CS555Assignment1() {
        originalImagePath = generatePath = null;
        w = h = 512;
        algorithm = "Nearest Neigbor";
        bit = 0;
        processor = new ImageProcess();
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 1447, 545);
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
        optionsPanel.setBounds(1024, 0, 406, 512);
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
                    generatePath = path.substring(0, path.length() - 4) + "_processed.jpg";
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
        textFieldX.setBounds(143, 115, 130, 42);
        optionsPanel.add(textFieldX);
        textFieldX.setColumns(10);

        textFieldY = new JTextField();
        textFieldY.setText("512");
        textFieldY.setBounds(143, 150, 130, 42);
        optionsPanel.add(textFieldY);
        textFieldY.setColumns(10);

        JLabel lblResolution = new JLabel("Scale(m*n)");
        lblResolution.setBounds(57, 122, 75, 28);
        optionsPanel.add(lblResolution);

        JComboBox bitBox = new JComboBox();
        bitBox.setBounds(143, 191, 144, 42);
        for (int i = 8; i >= 1; i--) {
            bitBox.addItem("" + i);
        }
        optionsPanel.add(bitBox);

        JLabel lblNewLabel = new JLabel("Bit");
        lblNewLabel.setBounds(57, 198, 64, 28);
        optionsPanel.add(lblNewLabel);

        JComboBox algBox = new JComboBox();
        algBox.setBounds(143, 283, 144, 42);
        algBox.addItem("Nearest Neighbor");
        algBox.addItem("Linear X");
        algBox.addItem("Linear Y");
        algBox.addItem("Bilinear");
        optionsPanel.add(algBox);

        JLabel lblNewLabel_1 = new JLabel("Algorithm");
        lblNewLabel_1.setBounds(57, 297, 46, 14);
        optionsPanel.add(lblNewLabel_1);

        JButton btnNewButton = new JButton("GO");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // Feed to processor and do some algorithm
                // create util function to resize the image and convert to ImageIcon
                w = Integer.valueOf(textFieldX.getText());
                h = Integer.valueOf(textFieldY.getText());
                algorithm = (String)algBox.getSelectedItem();
                bit = Integer.valueOf((String) bitBox.getSelectedItem());

                ImageProcessingAlgorithm alg = null;
                switch (algorithm) {
                    case "Nearest Neighbor":
                        alg = ImageProcessingAlgorithm.NEAREST_NEIGHBOR;
                        break;
                    case "Linear X":
                        alg = ImageProcessingAlgorithm.LINEAR_X;
                        break;
                    case "Linear Y":
                        alg = ImageProcessingAlgorithm.LINEAR_Y;
                        break;
                    case "Bilinear":
                        alg = ImageProcessingAlgorithm.BILINEAR_INTERPOLATION;
                        break;
                }

                processor.setPath(originalImagePath);
                int[][] newImg = processor.processImage(w, h, alg, bit);
                GreyScaleUtil.writeImage(GreyScaleUtil.generateImage(newImg), generatePath);
                ImageIcon icon = new ImageIcon(GreyScaleUtil.readImage(generatePath));
                processedLabel.setIcon(icon);

                System.out.println("File: " + originalImagePath);
                System.out.println("Resolution: " + w + " * " + h);
                System.out.println("Algorithm: " + algorithm);
                System.out.println("Bit: " + bit);
            }
        });
        btnNewButton.setBackground(Color.GREEN);
        btnNewButton.setBounds(143, 381, 144, 42);
        optionsPanel.add(btnNewButton);
    }
}
