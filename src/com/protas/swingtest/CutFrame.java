package com.protas.swingtest;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class CutFrame extends JFrame {

    private JPanel panel;
    private JLabel imageLabel;

    public CutFrame(int width, int height, BufferedImage cutIcon) {
        panel = new JPanel();
        imageLabel = new JLabel(new ImageIcon(cutIcon));

        setContentPane(panel);
        panel.add(imageLabel);
        setTitle("Clip of image");

        setSize(width + 100, height + 100);
        setVisible(true);
    }

}
