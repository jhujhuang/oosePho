package com.Pho;

public abstract class Filter {

    public abstract void applyToRectangle(int x1, int x2, int y1, int y2);

    public abstract void applyToCircle (int x, int y, int r);

    protected void loadImage (String pId) {

    }
}
