package com.protas.swingtest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageLabel extends JLabel implements MouseListener, MouseMotionListener {
    public static BufferedImage bfImage;
    public static BufferedImage bfImageCopy;
    private final int imageWidth = 300;
    private final int imageHeight = 300;
    private boolean isImagePressed;
    private Point cordsClicked = null;
    private Point cordsReleased = null;
    private HashSet<ImagePixel> setOfPoints;
    private HashMap<Integer, Integer> visited;
    public static double threshold = 0.2d;
    public ImageLabel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        bfImage = null;
        bfImageCopy = null;

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

        bfImageCopy = copyImage(bfImage);

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
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isImagePressed = true;
        cordsClicked = e.getPoint();
        System.out.println("Pressed - x: " + cordsClicked.x + " y: " + cordsClicked.y);

        //Getting the pixel value
        int pixel = bfImage.getRGB(cordsClicked.x, cordsClicked.y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isImagePressed = false;
        cordsReleased = e.getPoint();
        bfImage = copyImage(bfImageCopy);

        if(cordsReleased.x < bfImage.getWidth() &&
                cordsReleased.y < bfImage.getHeight() &&
                cordsReleased.x != 0 &&
                cordsReleased.y != 0) {
            System.out.println("Released - x: " + cordsReleased.x + " y: " + cordsReleased.y);

            //Getting the pixel value
            int pixel = bfImage.getRGB(cordsReleased.x, cordsReleased.y);

            this.setIcon(new ImageIcon(bfImage));
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
                    bfsFloodFill(bfImage.getWidth(), bfImage.getWidth(), bfImage, cordsReleased.x, cordsReleased.y, pixel);
                    break;
                default:
                    System.out.println("Correct shape is not selected");
            }
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

        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        // draw shape
        switch(MainWindow.type) {
            case RECTANGLE:
                drawRectangle();
                break;
            case OVAL:
                drawOval();
                break;
        }
        this.setIcon(new ImageIcon(bfImage));

        bfImage = copyImage(bfImageCopy);

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void drawRectangle() {
        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.y > cordsClicked.y) ? cordsClicked.y : cordsReleased.y;

        Graphics2D g = (Graphics2D) bfImage.getGraphics();

        Rectangle2D rect = new Rectangle2D.Float(x, y,
                Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));

        float[] dash = { 5F, 5F };

        g.setColor(Color.BLACK);

        Stroke dashedStroke = new BasicStroke( 2F, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 3F, dash,0F);

        g.fill(dashedStroke.createStrokedShape(rect));

        this.repaint();
    }

    private BufferedImage cutRectangle() {

        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.y > cordsClicked.y) ? cordsClicked.y : cordsReleased.y;

        if (x != 0 && y != 0) {
            BufferedImage output = bfImage.getSubimage(x, y,
                    Math.abs(cordsReleased.x - cordsClicked.x),
                    Math.abs(cordsReleased.y - cordsClicked.y));
            return output;
        }

        return null;
    }

    private void drawOval() {
        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.y > cordsClicked.y) ? cordsClicked.y : cordsReleased.y;

        Graphics2D g = (Graphics2D) bfImage.getGraphics();

        Ellipse2D ellipse =  new Ellipse2D.Float(x, y,
                Math.abs(cordsReleased.x - cordsClicked.x),
                Math.abs(cordsReleased.y - cordsClicked.y));

        float[] dash = { 5F, 5F };

        g.setColor(Color.BLACK);

        Stroke dashedStroke = new BasicStroke( 2F, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 3F, dash,0F);

        g.fill(dashedStroke.createStrokedShape(ellipse));

        this.repaint();
    }

    public BufferedImage cutOval(BufferedImage input) {
        int width = Math.abs(cordsReleased.x - cordsClicked.x);
        int height = Math.abs(cordsReleased.y - cordsClicked.y);

        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.y > cordsClicked.y) ? cordsClicked.y : cordsReleased.y;

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

    public void bfsFloodFill(int n, int m, BufferedImage image,int x, int y, int color)
    {

        // Visiting array
        int vis[][] = new int[bfImage.getWidth()][bfImage.getHeight()];

        // Initializing all as zero
        for(int i = 0; i <= bfImage.getWidth() - 1; i++){
            for(int j = 0; j <= bfImage.getHeight() - 1; j++){
                vis[i][j] = 0;
            }
        }

        // Creating queue for bfs
        Queue<Pair> obj = new LinkedList<>();

        // Pushing pair of {x, y}
        Pair pq=new Pair(x,y);
        obj.add(pq);

        // Marking {x, y} as visited
        vis[x][y] = 1;

        // Until queue is empty
        while (!obj.isEmpty())
        {
            // Extracting front pair
            Pair coord = obj.peek();
            int x1 = coord.first;
            int y1 = coord.second;
            int preColor = image.getRGB(x1, y1);

            //image.setRGB(x1, y1, color);
            setOfPoints.add(new ImagePixel(
                    new Point(x1, y1), image.getRGB(x1, y1)
            ));

            // Popping front pair of queue
            obj.remove();

            // For Upside Pixel or Cell
            if ((validCoord(x1 + 1, y1, n, m)==1) && vis[x1 + 1][y1] == 0 &&
                    distanceSquaredPct(new Color(image.getRGB(x1 + 1, y1)), new Color(preColor)) <= threshold)
            {
                Pair p = new Pair(x1 +1, y1);
                obj.add(p);
                vis[x1 + 1][y1] = 1;
                ImagePixel pixel = new ImagePixel(
                        new Point(x1 + 1, y1),
                        image.getRGB(x1 + 1, y1));
                setOfPoints.add(pixel);
            }

            // For Downside Pixel or Cell
            if ((validCoord(x1 - 1, y1, n, m)==1) && vis[x1 - 1][y1] == 0 &&
                    distanceSquaredPct(new Color(image.getRGB(x1 - 1, y1)), new Color(preColor)) <= threshold)
            {
                Pair p = new Pair(x1 - 1, y1);
                obj.add(p);
                vis[x1- 1][y1] = 1;
                ImagePixel pixel = new ImagePixel(
                        new Point(x1 - 1, y1),
                        image.getRGB(x1 - 1, y1));
                setOfPoints.add(pixel);
            }

            // For Right side Pixel or Cell
            if ((validCoord(x1, y1 + 1, n, m)==1) && vis[x1][y1 + 1] == 0 &&
                    distanceSquaredPct(new Color(image.getRGB(x1, y1 + 1)), new Color(preColor)) <= threshold)
            {
                Pair p = new Pair(x1,y1 +1);
                obj.add(p);
                vis[x1][y1 + 1] = 1;
                ImagePixel pixel = new ImagePixel(
                        new Point(x1, y1 + 1),
                        image.getRGB(x1, y1 + 1));
                setOfPoints.add(pixel);
            }

            // For Left side Pixel or Cell
            if ((validCoord(x1, y1 - 1, n, m)==1) && vis[x1][y1 - 1] == 0 &&
                    distanceSquaredPct(new Color(image.getRGB(x1, y1 - 1)), new Color(preColor)) <= threshold)
            {
                Pair p = new Pair(x1,y1 -1);
                obj.add(p);
                vis[x1][y1 - 1] = 1;
                ImagePixel pixel = new ImagePixel(
                        new Point(x1, y1 - 1),
                        image.getRGB(x1, y1 - 1));
                setOfPoints.add(pixel);
            }
        }

        // Printing The Changed Matrix Of Pixels
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < m; j++)
            {
                System.out.print(image.getRGB(x, y));
            }
            System.out.println();
        }
        System.out.println();

        new CutFrame(image.getWidth(), image.getHeight(), bufferedImageFromArray());
    }

    public static int validCoord(int x, int y, int n, int m)
    {
        if (x < 0 || y < 0) {
            return 0;
        }
        if (x >= n || y >= m) {
            return 0;
        }
        return 1;
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
            output.createGraphics().dispose();
        }
        setOfPoints.clear();
        return output;
    }

    public static double colorDistance(Color color1, Color color2) {
        return Math.sqrt(
            Math.pow(color1.getRed() - color2.getRed(), 2) +
                    Math.pow(color1.getGreen() - color2.getGreen(), 2) +
                    Math.pow(color1.getBlue() - color2.getBlue(), 2)
        );
    }

    public static double distanceSquared(Color a, Color b)
    {
        int deltaR = a.getRed() - b.getRed();
        int deltaG = a.getGreen() - b.getGreen();
        int deltaB = a.getBlue() - b.getBlue();
        int deltaAlpha = a.getAlpha() - b.getAlpha();
        double rgbDistanceSquared = (deltaR * deltaR + deltaG * deltaG + deltaB * deltaB) / 3.0;
        return deltaAlpha * deltaAlpha / 2.0 + rgbDistanceSquared * a.getAlpha() * b.getAlpha() / (255 * 255);
    }

    public static double distanceSquaredPct(Color a, Color b ) {
        return distanceSquared(a, b) / 100.0d;
    }
}

