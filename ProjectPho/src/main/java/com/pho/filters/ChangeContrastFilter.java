package com.pho.filters;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Change contrast filter.
 */
public class ChangeContrastFilter extends Filter {

    private Map<String, Double> params;
    /**
     * Constructor for the change contrast filter class.
     * @param params dictionary of filter parameters
     */
    public ChangeContrastFilter(Map<String, Double> params){
        // TODO: Implement
        this.params = params;
    }

    @Override
    public void applyToRectangle(int x1, int x2, int y1, int y2){
        // apply filtering to offscreen first
        BufferedImage offscreen = deepCopy(image);

        Double contrast = params.get("value");
        if (contrast >= 0) {
            double averageLuminance = 0;
            int count = 0;
            for (int i = x1; i < x2; i++) {
                for (int j = y1; j < y2; j++) {
                    count++;
                    int rgb = image.getRGB(i, j);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb >> 0) & 0xFF;
                    averageLuminance += 0.30 * r + 0.59 * g + 0.11 * b;
                }
            }

            averageLuminance /= count;

            for (int y = y1; y < y2; y++) {
                for (int x = x1; x < x2; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb >> 0) & 0xFF;
                    int newR = (int)((r - averageLuminance) * contrast + averageLuminance);
                    int newG = (int)((g - averageLuminance) * contrast + averageLuminance);
                    int newB = (int)((b - averageLuminance) * contrast + averageLuminance);
                    newR = (newR > 255) ? 255 : newR;
                    newG = (newG > 255) ? 255 : newG;
                    newB = (newB > 255) ? 255 : newB;
                    newR = (newR < 0) ? 0 : newR;
                    newG = (newG < 0) ? 0 : newG;
                    newB = (newB < 0) ? 0 : newB;
                    int newColor = ((newR & 0xFF) << 16) | ((newG & 0xFF) << 8) | newB & 0xFF;
                    offscreen.setRGB(x, y, newColor);
                }
            }
            // update the image with the offscreen
            image = offscreen;
        }
    }

    @Override
    public void applyToCircle(int x, int y, int r) {
        // TODO: Implement
    }
}
