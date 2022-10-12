package com.protas.swingtest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame{
    private Toolkit toolkit;
    private JPanel mainPanel;
    private JButton confirmButton;
    private JPanel imagePanel = new GPanel();
    private JLabel imageLabel = new ImageLabel();
    public MainWindow() {
        setSize(500, 400);
        toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        setTitle("Swing App");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.add(imagePanel);
        imagePanel.add(imageLabel);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button is working:)");
            }
        });
    }
}

