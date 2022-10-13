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
    int imageWidth = 300;
    int imageHeight = 300;
    public ImageLabel() {
        this.addMouseListener(this);

        bfImage = null;
        try {
            bfImage = ImageIO.read(new File("D:\\Projects\\Java Projects\\swingTest\\resources\\images\\leMinion.jpg"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        setSize(imageWidth, imageHeight);

        Image tempImage = bfImage.getScaledInstance(this.getWidth(), this.getHeight(),
                        Image.SCALE_SMOOTH);

        bfImage = toBufferedImage(tempImage);

        this.setIcon(new ImageIcon(bfImage));
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        bfImage.flush();
        g = bfImage.getGraphics();
        g.setColor(new Color(15,15,15));
        g.fillRect(10, 10, 20, 20);
        g.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point cords = e.getPoint();
        System.out.println("x: " + cords.x + " y: " + cords.y);
        bfImage.flush();
        //Getting the pixel value
        int pixel = bfImage.getRGB(cords.x, cords.y);

        //Creating a Color object from pixel value
        Color color = new Color(pixel, true);

        //Retrieving the R G B values
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        System.out.println("Value of pixel: " + pixel);
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
