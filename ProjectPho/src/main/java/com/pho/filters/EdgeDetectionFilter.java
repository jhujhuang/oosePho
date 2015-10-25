package com.pho.filters;

import java.util.Map;

/**
 * Edge detection filter.
 */
public class EdgeDetectionFilter extends Filter {

    /**
     * Constructor for the edge detection filter class.
     * @param params dictionary of filter parameters
     */
    public EdgeDetectionFilter(Map<String, Double> params){
        // TODO: Implement
    }

    @Override
    public void applyToRectangle(int x1, int x2, int y1, int y2){
        // TODO: Implement
        int mask[][] = new int[][]{
                {-1, -1, -1},
                {-1, 8, -1},
                {-1, -1, -1}
        };

        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {

                int rgb = image.getRGB(x, y);

                int newR = (rgb >> 16) & 0xFF * mask[1][1];
                int newG = (rgb >> 8) & 0xFF * mask[1][1];
                int newB = (rgb >> 0) & 0xFF * mask[1][1];

                if (x > 0 && y > 0) {
                    newR += (image.getRGB(x - 1, y - 1) >> 16) & 0xFF * mask[0][0];
                    newG += (image.getRGB(x - 1, y - 1) >> 8) & 0xFF * mask[0][0];
                    newB += (image.getRGB(x - 1, y - 1) >> 0) & 0xFF * mask[0][0];
                }

                if (y > 0) {
                    newR += (image.getRGB(x, y - 1) >> 16) & 0xFF * mask[1][0];
                    newG += (image.getRGB(x, y - 1) >> 8) & 0xFF * mask[1][0];
                    newB += (image.getRGB(x, y - 1) >> 0) & 0xFF * mask[1][0];
                }

                if (x < x2 - 1 && y > 0) {
                    newR += (image.getRGB(x + 1, y - 1) >> 16) & 0xFF * mask[2][0];
                    newG += (image.getRGB(x + 1, y - 1) >> 8) & 0xFF * mask[2][0];
                    newB += (image.getRGB(x + 1, y - 1) >> 0) & 0xFF * mask[2][0];
                }

                if (x > 0) {
                    newR += (image.getRGB(x - 1, y) >> 16) & 0xFF * mask[0][1];
                    newG += (image.getRGB(x - 1, y) >> 8) & 0xFF * mask[0][1];
                    newB += (image.getRGB(x - 1, y) >> 0) & 0xFF * mask[0][1];
                }

                if (x < x2 - 1) {
                    newR += (image.getRGB(x + 1, y) >> 16) & 0xFF * mask[2][1];
                    newG += (image.getRGB(x + 1, y) >> 8) & 0xFF * mask[2][1];
                    newB += (image.getRGB(x + 1, y) >> 0) & 0xFF  * mask[2][1];
                }

                if (x > 0 && y < y2 - 1) {
                    newR += (image.getRGB(x - 1, y + 1) >> 16) & 0xFF * mask[0][2];
                    newG += (image.getRGB(x - 1, y + 1) >> 8) & 0xFF * mask[0][2];
                    newB += (image.getRGB(x - 1, y + 1) >> 0) & 0xFF * mask[0][2];
                }

                if (y < y2 - 1) {
                    newR += (image.getRGB(x, y + 1) >> 16) & 0xFF * mask[1][2];
                    newG += (image.getRGB(x, y + 1) >> 8) & 0xFF * mask[1][2];
                    newB += (image.getRGB(x, y + 1) >> 0) & 0xFF * mask[1][2];
                }

                if (x < x2 -1 && y < y2 - 1) {
                    newR += (image.getRGB(x + 1, y + 1) >> 16) & 0xFF * mask[2][2];
                    newG += (image.getRGB(x + 1, y + 1) >> 8) & 0xFF * mask[2][2];
                    newB += (image.getRGB(x + 1, y + 1) >> 0) & 0xFF * mask[0][2];
                }

                newR = newR > 255 ? 255 : newR;
                newR = newR < 0 ? 0 : newR;

                newG = newG > 255 ? 255 : newG;
                newG = newG < 0 ? 0 : newG;

                newB = newB > 255 ? 255 : newB;
                newB = newB < 0 ? 0 : newB;

                int newColor = ((newR & 0x0ff) << 16) | (( newG & 0x0ff) << 8) | (newB & 0x0ff);
                image.setRGB(x, y, newColor);
            }
        }
    }

   @Override
    public void applyToCircle(int x, int y, int r) {
       // TODO: Implement
    }
}
