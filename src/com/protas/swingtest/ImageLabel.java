package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLabel extends JLabel implements MouseListener {
    BufferedImage bfImage;
    int imageWidth = 300;
    int imageHeight = 300;
    private Point cordsClicked = null;
    private Point cordsReleased = null;
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

    // method to convert Image img to BufferedImage
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
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // use MagicWand type of selection
        if(MainWindow.type == ImageSelectingType.MAGICWAND) {
            // use MAGIC WAND
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        cordsClicked = e.getPoint();
        System.out.println("Pressed - x: " + cordsClicked.x + " y: " + cordsClicked.y);

        //Getting the pixel value
        int pixel = bfImage.getRGB(cordsClicked.x, cordsClicked.y);

        //Creating a Color object from pixel value
        Color color = new Color(pixel, true);

        //Retrieving the R G B values
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        System.out.println("Pressed - value of pixel: " + pixel);
        System.out.println("Pressed - R: " + red + " G: " + green + " B: " + blue);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        cordsReleased = e.getPoint();
        System.out.println("Released - x: " + cordsReleased.x + " y: " + cordsReleased.y);

        //Getting the pixel value
        int pixel = bfImage.getRGB(cordsReleased.x, cordsReleased.y);

        //Creating a Color object from pixel value
        Color color = new Color(pixel, true);

        //Retrieving the R G B values
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        System.out.println("Released - value of pixel: " + pixel);
        System.out.println("Released - R: " + red + " G: " + green + " B: " + blue);

        switch(MainWindow.type) {
            case RECTANGLE:
                // draw RECTANGLE
                cutRectangle(bfImage, cordsClicked, cordsReleased);
                break;
            case OVAL:
                // draw OVAL
                break;
            case MAGICWAND:
                // use MAGIC WAND
                break;
        }

        /*
        bfImage.createGraphics().drawOval(cordsClicked.x, cordsClicked.y,
                Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));
        */
        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void cutRectangle(BufferedImage image, Point clicked, Point released) {
        if(cordsReleased.x > cordsClicked.x) { // drawing to the right side of BfImage
            new CutFrame(bfImage.getWidth(), bfImage.getHeight(),
                    (BufferedImage) bfImage.getSubimage(cordsClicked.x, cordsClicked.y,
                            Math.abs(cordsReleased.x - cordsClicked.x),
                            Math.abs(cordsReleased.y - cordsClicked.y)));

            bfImage.createGraphics().drawRect(cordsClicked.x, cordsClicked.y,
                    Math.abs(cordsReleased.x - cordsClicked.x),
                    Math.abs(cordsReleased.y - cordsClicked.y));
        }

        if(cordsReleased.x < cordsClicked.x) {
            new CutFrame(bfImage.getWidth(), bfImage.getHeight(),
                    (BufferedImage) bfImage.getSubimage(cordsReleased.x, cordsReleased.y,
                            Math.abs(cordsReleased.x - cordsClicked.x),
                            Math.abs(cordsReleased.y - cordsClicked.y)));

            bfImage.createGraphics().drawRect(cordsReleased.x, cordsReleased.y,
                    Math.abs(cordsReleased.x - cordsClicked.x),
                    Math.abs(cordsReleased.y - cordsClicked.y));
        }
    }
}
