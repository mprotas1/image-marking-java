package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.protas.swingtest.ImageLabel.bfImage;
import static com.protas.swingtest.ImageLabel.toBufferedImage;

public class MainWindow extends JFrame{
    private Toolkit toolkit;
    private JPanel mainPanel;
    private JButton confirmButton;
    private JPanel imagePanel = new GPanel();
    private JLabel imageLabel = new ImageLabel();
    private JPanel optionPanel;
    private JRadioButton modeButtonRect;
    private JRadioButton modeButtonOval;
    private JRadioButton modeButtonWand;
    private JLabel typeLabel;
    private JRadioButton modeButtonIrregular;
    private ButtonGroup modeButtonGroup;
    public static ImageSelectingType type;

    public MainWindow() {
        type = ImageSelectingType.RECTANGLE;
        modeButtonRect.setSelected(true);
        setSize(500, 400);
        toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        setTitle("Swing App");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.add(imagePanel);
        imagePanel.add(imageLabel);

        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(modeButtonRect);
        modeButtonGroup.add(modeButtonOval);
        modeButtonGroup.add(modeButtonWand);
        modeButtonGroup.add(modeButtonIrregular);

        System.out.println(type);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This button should load image to the imageLabel");
            }
        });
        modeButtonRect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = ImageSelectingType.RECTANGLE;
            }
        });
        modeButtonOval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = ImageSelectingType.OVAL;
                System.out.println(type);
            }
        });
        modeButtonWand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = ImageSelectingType.MAGICWAND;
                System.out.println(type);
            }
        });
        modeButtonIrregular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = ImageSelectingType.IRREGULAR;
                System.out.println(type);
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
    }

    private void loadImage() {

        // limit JFileChooser to select only from image type files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");

        JFileChooser chooser = new JFileChooser("D:\\Projects\\Java Projects\\swingTest\\resources",
                FileSystemView.getFileSystemView());

        chooser.setFileFilter(filter);

        int r = chooser.showSaveDialog(null);
        if(r == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            File file = new File(path);
            System.out.println(file.getPath());
            try {

                Image temp = ImageIO.read(new File(path)).getScaledInstance(300, 300,
                        Image.SCALE_SMOOTH);
                ImageLabel.bfImage = toBufferedImage(temp);
                ImageLabel.bfImageCopy = toBufferedImage(bfImage);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            imageLabel.setIcon(new ImageIcon(bfImage));
        }
    }
}

