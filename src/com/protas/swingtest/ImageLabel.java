package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageLabel extends JLabel implements MouseListener, MouseMotionListener {
    public static BufferedImage bfImage;
    private final int imageWidth = 300;
    private final int imageHeight = 300;
    private boolean isImagePressed;
    private Point cordsClicked = null;
    private Point cordsReleased = null;
    private HashSet<ImagePixel> setOfPoints;
    private HashMap<Integer, Integer> visited;

    public ImageLabel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        bfImage = null;

        isImagePressed = false;

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

        setOfPoints = new HashSet<>();

        visited =  new HashMap<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // use MagicWand type of selection
        if(MainWindow.type == ImageSelectingType.MAGICWAND) {
            Point point = e.getPoint();
            // use MAGIC WAND
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isImagePressed = true;
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


        System.out.println(colorDistance(color, new Color(254, 254, 18)));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isImagePressed = false;
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
                // make crop of RECTANGLE
                new CutFrame(bfImage.getWidth(), bfImage.getHeight(), cutRectangle());
                // draw RECTANGLE
                drawRectangle();
                break;
            case OVAL:
                // make crop of OVAL
                new CutFrame(bfImage.getWidth(), bfImage.getHeight(), cutOval(bfImage));
                // draw OVAL
                drawOval();
                break;
            case MAGICWAND:
                setOfPoints.clear();
                floodFill(bfImage, cordsReleased.x, cordsReleased.y);
                BufferedImage temp = bufferedImageFromArray();
                new CutFrame(temp.getWidth(), temp.getHeight(),temp);
                System.out.println(setOfPoints.size());
                break;
            default:
                System.out.println("Correct shape is not selected");
        }
    }


    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        cordsReleased = e.getPoint();
        // create copy of bfImage
        BufferedImage temp = copyImage(bfImage);

        // draw shape
        switch(MainWindow.type) {
            case RECTANGLE:
                drawRectangle();
                break;
        }

        bfImage = copyImage(temp);
        this.setIcon(new ImageIcon(bfImage));
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        Point temp1 = e.getPoint();
        if(isImagePressed) {
            bfImage.createGraphics().drawRect(cordsClicked.x, cordsClicked.y,
                    Math.abs(temp1.x - cordsClicked.x),
                    Math.abs(temp1.y - cordsClicked.y));
        }
        this.repaint();
    }

    private void drawRectangle() {
        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.x > cordsClicked.x) ? cordsClicked.y : cordsReleased.y;

        bfImage.createGraphics().drawRect(x, y,
                Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));

        this.repaint();
    }

    private BufferedImage cutRectangle() {
        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.x > cordsClicked.x) ? cordsClicked.y : cordsReleased.y;

        BufferedImage output = bfImage.getSubimage(x, y,
                Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));

        return output;
    }

    private void cutOval(BufferedImage image, Point clicked, Point released) {
        int x = clicked.x;
        int y = clicked.y;
        int radius = 0;
        int margin = 1;
        if(released.x > clicked.x) {
            radius = 0;
        }
        else {

        }

        BufferedImage bi = new BufferedImage(2 * radius + (2 * margin), 2 * radius + (2 * margin), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.translate(bi.getWidth() / 2, bi.getHeight() / 2);
        //Arc2D myArea = new Arc2D.Float(0 - radius, 0 - radius, 2 * radius, 2 * radius, 0, -360, Arc2D.OPEN);
        Ellipse2D myEllipse = new Ellipse2D.Float(cordsReleased.x, cordsReleased.y, Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));

        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
        g2.setComposite(alphaComposite);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        g2.setClip(myEllipse);
        g2.drawImage(image.getSubimage(x - radius, y - radius, x + radius, y + radius), -radius, -radius, this);

        new CutFrame(bi.getWidth(), bi.getHeight(), bi);
    }

    private void drawOval() {
        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.x > cordsClicked.x) ? cordsClicked.y : cordsReleased.y;

        bfImage.createGraphics().drawOval(x, y,
                Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));

        this.repaint();
    }
    public BufferedImage cutOval(BufferedImage input) {
        int width = Math.abs(cordsReleased.x - cordsClicked.x);
        int height = Math.abs(cordsReleased.y - cordsClicked.y);

        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.x > cordsClicked.x) ? cordsClicked.y : cordsReleased.y;

        BufferedImage output = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Float(0, 0, width, height));

        g.setComposite(AlphaComposite.SrcAtop);
        g.drawImage(input, -x, -y, this);

        g.dispose();

        return output;
    }

    private void floodFillUtil(BufferedImage image, int x, int y, int color)
    {
        int threshold = 5;

        ImagePixel pixel = new ImagePixel(new Point(x, y),
                image.getRGB(x, y));

        // Base cases
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return;

        //if(image.getRGB(x, y) != color) return;

        if(setOfPoints.contains(pixel)) return;

        if(visited.containsKey(x) && visited.containsValue(y)) return;

        if((colorDistance(new Color(image.getRGB(x, y), true), new Color(color, true)) >= threshold))
            return;

        setOfPoints.add(pixel);
        System.out.println("Added point: " + pixel.getCords() + " to the list with color: "
                + new Color(pixel.getRgbValue(), true) + " hashCode: "
                + pixel.hashCode());
        System.out.println("Size: " + setOfPoints.size());

        // Recur for north, east, south and west
        floodFillUtil(bfImage, x+1, y, color);
        floodFillUtil(bfImage, x-1, y, color);
        floodFillUtil(bfImage, x, y+1, color);
        floodFillUtil(bfImage, x, y-1, color);

        visited.put(x, y);
    }

    // It mainly finds the previous color on (x, y) and
    // calls floodFillUtil()
    private  void floodFill(BufferedImage image, int x, int y)
    {
        int prevC = image.getRGB(x, y);

        //if(prevC != newC) return;

        floodFillUtil(image, x, y, prevC);
    }

    // method to copy image
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    // method to convert Image img to BufferedImage
    public static BufferedImage toBufferedImage(Image img) {
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

    // method to convert list of ImagePixel points to bufferedImage
    private BufferedImage bufferedImageFromArray() {
        BufferedImage output = new BufferedImage(bfImage.getWidth(), bfImage.getHeight(), bfImage.getType());
        for(ImagePixel pixel : setOfPoints) {
            output.setRGB(pixel.getCords().x, pixel.getCords().y, pixel.getRgbValue());
        }
        return output;
    }

    public static double colorDistance(Color color1, Color color2) {
        return Math.sqrt(
            Math.pow(color1.getRed() - color2.getRed(), 2) +
                    Math.pow(color1.getGreen() - color2.getGreen(), 2) +
                    Math.pow(color1.getBlue() - color2.getBlue(), 2)
        );
    }
}
