package com.Pho;

/**
 * An abstract class for filters.
 */
public abstract class Filter {

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
     * Loads the img of the photo in the filter instance.
     * @param pId The pId of the photo to load with this filter.
     */
    protected void loadImage (String pId) {
        // TODO: implement
    }
}
