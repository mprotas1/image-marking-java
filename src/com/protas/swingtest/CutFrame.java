package com.protas.swingtest;

import javax.swing.*;
import java.awt.image.BufferedImage;

/*
    Frame designed to display the selected part of image
 */
public class CutFrame extends JFrame {

    private JPanel panel;
    private JLabel imageLabel;

    public CutFrame(int width, int height, BufferedImage cutIcon) {
        super("Clip of image");
        panel = new JPanel();
        imageLabel = new JLabel(new ImageIcon(cutIcon));

        setContentPane(panel);
        panel.add(imageLabel);

        setSize(width + 100, height + 100);
        setVisible(true);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

}
