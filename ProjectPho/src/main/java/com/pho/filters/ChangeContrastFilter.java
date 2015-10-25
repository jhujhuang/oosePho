package com.pho.filters;

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
        // TODO: Implement
        Double contrast = params.get("contrast");
        if (contrast >= 0) {
            if (contrast == 1)
                return;
            double averageLuminance = 0;
            int count = 0;
            for (int i = 0; i < x1; i++) {
                for (int j = 0; j < x2; j++) {
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
                    int newR = (int)((r - averageLuminance) * contrast + (255 - averageLuminance));
                    int newG = (int)((g - averageLuminance) * contrast + (255 - averageLuminance));
                    int newB = (int)((b - averageLuminance) * contrast + (255 - averageLuminance));
                    int newColor = ((newR & 0x0ff) << 16) | (( newG & 0x0ff) << 8) | (newB & 0x0ff);
                    image.setRGB(x, y, newColor);
                }
            }
        }
    }

    @Override
    public void applyToCircle(int x, int y, int r) {
        // TODO: Implement
    }
}
