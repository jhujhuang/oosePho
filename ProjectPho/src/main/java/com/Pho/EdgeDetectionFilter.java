package com.Pho;

import java.util.Map;

/**
 * This is the edge detection filter class.
 */
public class EdgeDetectionFilter extends Filter {

    /**
     * Constructor for the edge detection filter class.
     * @param params filter parameters
     */
    public EdgeDetectionFilter(Map<String, Double> params){

    }

    /**
     * Apply filter to a rectangular area.
     * @param x1 x coordinate of the top left point of the rectangular area.
     * @param x2 x coordinate of the bot right point of the rectangular area.
     * @param y1 y coordinate of the top left point of the rectangular area.
     * @param y2 y coordinate of the bot right point of the rectangular area.
     */
    public void applyToRectangle(int x1, int x2, int y1, int y2){

    }

    /**
     * Apply filter to a circled area.
     * @param x x coordinate of the center of the circle.
     * @param y y coordinate of the center of the circle.
     * @param r radius of the circle.
     */
    public void applyToCircle(int x, int y, int r) {

    }
}
