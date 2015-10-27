package com.pho.filters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Blur filter.
 */
public class BlurFilter extends Filter {

    private Map<String, Double> params;
    /**
     * Constructor for the blur filter class.
     * @param params filter parameters
     */
    public BlurFilter(Map<String, Double> params) {
        // TODO: Implement
        this.params = params;
    }

    /**
     * Apply filter to a rectangular area.
     * @param x1 x coordinate of the top left point of the rectangular area.
     * @param x2 x coordinate of the bot right point of the rectangular area.
     * @param y1 y coordinate of the top left point of the rectangular area.
     * @param y2 y coordinate of the bot right point of the rectangular area.
     */
    public void applyToRectangle(int x1, int x2, int y1, int y2) {
        // TODO: Implement
        int mask[][] = new int[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        // apply filtering to offscreen first
        BufferedImage offscreen = deepCopy(image);

        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {

                int totalPixels = 4;
                int rgb = image.getRGB(x, y);

                int newR = (rgb >> 16) & 0xFF * mask[1][1];
                int newG = (rgb >> 8) & 0xFF * mask[1][1];
                int newB = (rgb >> 0) & 0xFF * mask[1][1];

                if (x > 0 && y > 0) {
                    newR += (image.getRGB(x - 1, y - 1) >> 16) & 0xFF * mask[0][0];
                    newG += (image.getRGB(x - 1, y - 1) >> 8) & 0xFF * mask[0][0];
                    newB += (image.getRGB(x - 1, y - 1) >> 0) & 0xFF * mask[0][0];
                    totalPixels+= 1;
                }

                if (y > 0) {
                    newR += (image.getRGB(x, y - 1) >> 16) & 0xFF * mask[1][0];
                    newG += (image.getRGB(x, y - 1) >> 8) & 0xFF * mask[1][0];
                    newB += (image.getRGB(x, y - 1) >> 0) & 0xFF * mask[1][0];
                    totalPixels+= 2;
                }

                if (x < x2 - 1 && y > 0) {
                    newR += (image.getRGB(x + 1, y - 1) >> 16) & 0xFF * mask[2][0];
                    newG += (image.getRGB(x + 1, y - 1) >> 8) & 0xFF * mask[2][0];
                    newB += (image.getRGB(x + 1, y - 1) >> 0) & 0xFF * mask[2][0];
                    totalPixels+= 1;
                }

                if (x > 0) {
                    newR += (image.getRGB(x - 1, y) >> 16) & 0xFF * mask[0][1];
                    newG += (image.getRGB(x - 1, y) >> 8) & 0xFF * mask[0][1];
                    newB += (image.getRGB(x - 1, y) >> 0) & 0xFF * mask[0][1];
                    totalPixels+= 2;
                }

                if (x < x2 - 1) {
                    newR += (image.getRGB(x + 1, y) >> 16) & 0xFF * mask[2][1];
                    newG += (image.getRGB(x + 1, y) >> 8) & 0xFF * mask[2][1];
                    newB += (image.getRGB(x + 1, y) >> 0) & 0xFF  * mask[2][1];
                    totalPixels+= 2;
                }

                if (x > 0 && y < y2 - 1) {
                    newR += (image.getRGB(x - 1, y + 1) >> 16) & 0xFF * mask[0][2];
                    newG += (image.getRGB(x - 1, y + 1) >> 8) & 0xFF * mask[0][2];
                    newB += (image.getRGB(x - 1, y + 1) >> 0) & 0xFF * mask[0][2];
                    totalPixels+= 1;
                }

                if (y < y2 - 1) {
                    newR += (image.getRGB(x, y + 1) >> 16) & 0xFF * mask[1][2];
                    newG += (image.getRGB(x, y + 1) >> 8) & 0xFF * mask[1][2];
                    newB += (image.getRGB(x, y + 1) >> 0) & 0xFF * mask[1][2];
                    totalPixels+= 2;
                }

                if (x < x2 -1 && y < y2 - 1) {
                    newR += (image.getRGB(x + 1, y + 1) >> 16) & 0xFF * mask[2][2];
                    newG += (image.getRGB(x + 1, y + 1) >> 8) & 0xFF * mask[2][2];
                    newB += (image.getRGB(x + 1, y + 1) >> 0) & 0xFF * mask[0][2];
                    totalPixels+= 1;
                }

                newR /= totalPixels;
                newG /= totalPixels;
                newB /= totalPixels;

                int newColor = ((newR & 0x0ff) << 16) | (( newG & 0x0ff) << 8) | (newB & 0x0ff);
                offscreen.setRGB(x, y, newColor);
            }
        }
        // update image with offscreen
        image = offscreen;
    }

    /**
     * Apply filter to a circled area.
     * @param x x coordinate of the center of the circle.
     * @param y y coordinate of the center of the circle.
     * @param r radius of the circle.
     */
    public void applyToCircle(int x, int y, int r) {
        // TODO: Implement
    }
}
