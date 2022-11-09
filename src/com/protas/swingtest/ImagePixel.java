package com.protas.swingtest;

import java.awt.*;

public class ImagePixel {
    private Point cords;
    private int rgbValue;

    public ImagePixel(Point cords, int rgbValue) {
        this.cords = cords;
        this.rgbValue = rgbValue;
    }

    public Point getCords() {
        return cords;
    }

    public void setCords(Point cords) {
        this.cords = cords;
    }

    public int getRgbValue() {
        return rgbValue;
    }

    public void setRgbValue(int rgbValue) {
        this.rgbValue = rgbValue;
    }
}
