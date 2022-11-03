package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
    }
}

