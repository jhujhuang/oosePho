package com.pho.filters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * An abstract class for filters.
 */
public abstract class Filter {

    protected BufferedImage image = null;

    /**
     * Apply filter to a rectangular area.
     * @param x1 x coordinate of the top left point of the rectangular area.
     * @param x2 x coordinate of the bot right point of the rectangular area.
     * @param y1 y coordinate of the top left point of the rectangular area.
     * @param y2 y coordinate of the bot right point of the rectangular area.
     */
    public abstract void applyToRectangle(int x1, int x2, int y1, int y2);

    /**
     * Apply filter to a circled area.
     * @param x x coordinate of the center of the circle.
     * @param y y coordinate of the center of the circle.
     * @param r radius of the circle.
     */
    public abstract void applyToCircle (int x, int y, int r);

    /**
     * Load the img of the photo in the filter instance.
     * @param pId The pId of the photo to load with this filter.
     */
    public void loadImage (String pId) {
        try {
            image = ImageIO.read(new File(pId));
        } catch (IOException E) {
            System.out.println("Unable to read image");
        }
    }

    /**
     * Getter method for the image in this filter.
     * @return the image of the filter.
     */
    public BufferedImage getImage () {
        return image;
    }

    // For copying a BufferedImage before modifying the image
    protected BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
