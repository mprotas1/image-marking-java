package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
    Frame designed to display the selected part of image
 */
public class CutFrame extends JFrame {

    private JPanel panel;
    private JLabel imageLabel;
    private BufferedImage image;
    private JButton saveButton;
    public CutFrame(int width, int height, BufferedImage cutIcon) {
        super("Clip of image");
        panel = new JPanel();
        saveButton = new JButton("SAVE");
        saveButton.setSize(20, 10);
        saveButton.setBounds(5, 5, 20, 10);
        image = cutIcon;
        imageLabel = new JLabel(new ImageIcon(image));

        setContentPane(panel);
        panel.add(imageLabel);
        this.add(saveButton);

        setSize(width + 100, height + 100);
        setVisible(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageToFile();
            }
        });

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    private void saveImageToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    ImageIO.write(image, "png", file);
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
        }

    }
}
