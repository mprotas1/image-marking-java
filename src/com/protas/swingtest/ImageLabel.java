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
    private Point cordsClicked = null;
    private Point cordsReleased = null;
    private Point cordsDragged = null;
    private final Color markingColor = new Color(0, 0, 0, 255);
    private HashSet<ImagePixel> setOfPoints;
    public static double threshold =  8;//353.3384d; //0.3065d;
    private ArrayList<Point> listOfPoints;

    public ImageLabel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        bfImage = null;
        bfImageCopy = null;

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
        listOfPoints = new ArrayList<>();

        System.out.println(colorsDifference2(Color.BLACK, Color.WHITE));
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
        cordsClicked = e.getPoint();
        bfImage = copyImage(bfImageCopy);
        if(MainWindow.type.equals(ImageSelectingType.MAGICWAND)) {
            bfsFloodFill(bfImage.getWidth(), bfImage.getWidth(), bfImage, cordsClicked.x, cordsClicked.y);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        cordsReleased = e.getPoint();
        bfImage = copyImage(bfImageCopy);

        if(cordsReleased.x <= bfImage.getWidth() && // if x is inside image bounds
                cordsReleased.y <= bfImage.getHeight() && // if y is inside image bounds
                Math.abs(cordsReleased.x - cordsClicked.x) > 0 && // if width is higher than zero
                Math.abs(cordsReleased.y - cordsClicked.y) > 0) { // if height is higher than zero

            this.setIcon(new ImageIcon(bfImage));
            switch(MainWindow.type) {
                case RECTANGLE:
                    // make crop of RECTANGLE
                    new CutFrame(bfImage.getWidth(), bfImage.getHeight(), cutRectangle(bfImage));
                    break;
                case OVAL:
                    // make crop of OVAL
                    new CutFrame(bfImage.getWidth(), bfImage.getHeight(), cutOval(bfImage));
                    break;
                case IRREGULAR:
                    new CutFrame(bfImage.getWidth(), bfImage.getHeight(), cutIrregularShape(bfImage));
                    drawIrregularShape();

                    // clear list of points to create irregular (lasso) shape
                    listOfPoints.clear();
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
        cordsDragged = e.getPoint();

        // draw shape
        switch(MainWindow.type) {
            case RECTANGLE:
                // draw rectangle
                drawRectangle();
                break;
            case OVAL:
                // draw oval
                drawOval();
                break;
            case IRREGULAR:
                // collect points for selecting an irregular shape (lasso type)
                listOfPoints.add(cordsReleased);

                // drawing line for supporting user's choice
                drawLine();
                break;
        }

        this.setIcon(new ImageIcon(bfImage));

        bfImage = copyImage(bfImageCopy);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void drawRectangle() {

        int x = (cordsDragged.x > cordsClicked.x) ? cordsClicked.x : cordsDragged.x;
        int y = (cordsDragged.y > cordsClicked.y) ? cordsClicked.y : cordsDragged.y;

        Graphics2D g = (Graphics2D) bfImage.getGraphics();

        Rectangle2D rect = new Rectangle2D.Float(x, y,
                Math.abs(cordsDragged.x - cordsClicked.x),
                Math.abs(cordsDragged.y - cordsClicked.y));

        float[] dash = { 5F, 5F };

        g.setColor(markingColor);

        Stroke dashedStroke = new BasicStroke( 2F, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 3F, dash,0F);

        g.fill(dashedStroke.createStrokedShape(rect));

        this.repaint();
    }

    private BufferedImage cutRectangle(BufferedImage input) {

        int x = (cordsReleased.x > cordsClicked.x) ? cordsClicked.x : cordsReleased.x;
        int y = (cordsReleased.y > cordsClicked.y) ? cordsClicked.y : cordsReleased.y;

        int width = Math.abs(cordsReleased.x - cordsClicked.x);
        int height = Math.abs(cordsReleased.y - cordsClicked.y);

        try {

        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }

        BufferedImage output = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();

        Rectangle2D rect = new Rectangle2D.Float(0, 0, width, height);

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fill(rect);

        g.setComposite(AlphaComposite.SrcAtop);
        g.drawImage(input, -x, -y, this);

        g.dispose();

        return output;
    }

    private void drawOval() {
        int x = (cordsDragged.x > cordsClicked.x) ? cordsClicked.x : cordsDragged.x;
        int y = (cordsDragged.y > cordsClicked.y) ? cordsClicked.y : cordsDragged.y;

        Graphics2D g = (Graphics2D) bfImage.getGraphics();

        Ellipse2D ellipse =  new Ellipse2D.Float(x, y,
                Math.abs(cordsDragged.x - cordsClicked.x),
                Math.abs(cordsDragged.y - cordsClicked.y));

        float[] dash = { 5F, 5F };

        g.setColor(markingColor);

        Stroke dashedStroke = new BasicStroke( 1.25F, BasicStroke.CAP_SQUARE,
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

    private void drawIrregularShape() {

        Polygon poly = new Polygon();

        Graphics2D g = (Graphics2D) bfImage.getGraphics();

        int index = 0;
        for (Point listOfPoint : listOfPoints) {
            poly.addPoint(listOfPoint.x, listOfPoint.y);
            index++;
        }

        g.setColor(markingColor);

        g.drawPolygon(poly);
        System.out.println("Drawn the polygon with: " + index + " indices");
        this.repaint();
    }

    private BufferedImage cutIrregularShape(BufferedImage input) {
        Polygon poly = new Polygon();

        BufferedImage output = new BufferedImage(bfImage.getWidth(),
                bfImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) output.getGraphics();

        for (Point listOfPoint : listOfPoints) {
            poly.addPoint(listOfPoint.x, listOfPoint.y);
        }

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.fill(poly);

        g.setComposite(AlphaComposite.SrcAtop);
        g.drawImage(input, 0, 0, this);

        g.dispose();

        return output;
    }

    private void drawLine() {
        // getting reference to the bfImage's Graphics component
        Graphics2D g = (Graphics2D) bfImage.getGraphics();

        // setting paint for the Graphics component
        g.setPaint(markingColor);

        // initializing two one-dimensional arrays for x and y values
        int[] xArray = new int[listOfPoints.size()];
        int[] yArray = new int[listOfPoints.size()];

        // converting listOfPoints arrayList<>() to two separate arrays
        for(int i = 0; i < listOfPoints.size(); i++) {
            xArray[i] = listOfPoints.get(i).x;
            yArray[i] = listOfPoints.get(i).y;
        }

        // drawing polyLine on screen
        g.drawPolyline(xArray, yArray, listOfPoints.size());
    }

    public void bfsFloodFill(int n, int m, BufferedImage image,int x, int y)
    {
        // Visiting array
        int[][] vis = new int[bfImage.getWidth()][bfImage.getHeight()];

        // Initializing all as zero
        for(int i = 0; i <= bfImage.getWidth() - 1; i++) {
            for(int j = 0; j <= bfImage.getHeight() - 1; j++) {
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
            //colorDifference(new Color(image.getRGB(x1 + 1, y1)), new Color(preColor));
            // For Upside Pixel or Cell
            if ((validCoord(x1 + 1, y1, n, m)==1) && vis[x1 + 1][y1] == 0 &&
                    colorsDifference2(new Color(image.getRGB(x1 + 1, y1)), new Color(preColor)) <= threshold)
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
                    colorsDifference2(new Color(image.getRGB(x1 - 1, y1)), new Color(preColor)) <= threshold)
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
                    colorsDifference2(new Color(image.getRGB(x1, y1 + 1)), new Color(preColor)) <= threshold)
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
                    colorsDifference2(new Color(image.getRGB(x1, y1 - 1)), new Color(preColor)) <= threshold)
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
                System.out.print(image.getRGB(x, y) + " ");
            }
            System.out.println();
        }
        System.out.println();

        new CutFrame(image.getWidth(), image.getHeight(), bufferedImageFromArray());
    }

    // method to check if coord is valid
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
    public static BufferedImage copyImage(BufferedImage source) {
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

    public static double colorDifference(Color a, Color b) {
        double aRed = (a.getRed() / 255) * 100;
        double aGreen = (a.getGreen() / 255) * 100;
        double aBlue = (a.getBlue() / 255) * 100;
        double bRed = (b.getRed() / 255) * 100;
        double bGreen = (b.getGreen() / 255) * 100;
        double bBlue = (b.getBlue() / 255) * 100;
        double percent1 = Math.round((aRed + aGreen + aBlue) / 3);
        double percent2 = Math.round((bRed + bGreen + bBlue) / 3);

        return Math.abs(percent1 - percent2);
    }

    public static double colorsDifference2(Color a, Color b) {
        int deltaR = a.getRed() - b.getRed();
        int deltaG = a.getGreen() - b.getGreen();
        int deltaB = a.getBlue() - b.getBlue();

        return Math.sqrt(Math.pow(deltaR, 2) + Math.pow(deltaG, 2) + Math.pow(deltaB, 2));
    }
}

