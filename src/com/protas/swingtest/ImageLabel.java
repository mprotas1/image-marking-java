package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLabel extends JLabel implements MouseListener {
    BufferedImage bfImage;
    public ImageLabel() {
        this.addMouseListener(this);
        bfImage = null;
        try {
            bfImage = ImageIO.read(new File("D:\\Projects\\Java Projects\\swingTest\\resources\\images\\leMinion.jpg"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        setSize(200, 200);

        bfImage.getScaledInstance(this.getWidth(), this.getHeight(),
                Image.SCALE_SMOOTH);

        this.setIcon(new ImageIcon(bfImage));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point cords = e.getPoint();
        System.out.println("x: " + cords.x + " y: " + cords.y);

        //Getting the pixel value
        int pixel = bfImage.getRGB(cords.x, cords.y);

        //Creating a Color object from pixel value
        Color color = new Color(pixel, true);

        //Retrieving the R G B values
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        System.out.println(pixel);
        System.out.println("R: " + red + " G: " + green + " B: " + blue);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
