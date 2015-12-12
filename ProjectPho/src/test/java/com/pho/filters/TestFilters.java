package com.pho.filters;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class TestFilters {

    private BufferedImage testImage;

    // Anonymous class for testing filters
    private interface Fixture {
        Filter init();
        String filterType();
    }

    @Before
    public void setup() throws IOException {
        this.testImage = ImageIO.read(new File("test.jpg"));
        // setup for filters
    }

    @After
    public void teardown() {
        // Does nothing for now
    }

    @DataPoint
    public static final Fixture blurFilter = new Fixture() {
        public String filterType() {
            return "BlurFilter";
        }
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new BlurFilter(params);
        }
    };

    @DataPoint
    public static final Fixture changeContrastFilter = new Fixture() {
        public String filterType() {
            return "ChangeContrastFilter";
        }
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new ChangeContrastFilter(params);
        }
    };

    @DataPoint
    public static final Fixture edgeDetection = new Fixture() {
        public String filterType() {
            return "EdgeDetectionFilter";
        }
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new EdgeDetectionFilter(params);
        }
    };

    @Theory
    public void testFilterRectangle(Fixture fix) throws IOException {
        Filter f = fix.init();
        f.loadImage(testImage);
        BufferedImage p1 = f.getImage();
        f.applyToRectangle(50, 100, 50, 100);
        BufferedImage p2 = f.getImage();

        assertTrue(this.isDifferent(p1, p2));

        // Output result to file, to check with human eyes
        File output = new File("tmp/" + fix.filterType() + ".jpg");
        ImageIO.write(p2, "jpg", output);
        output = new File("tmp/" + fix.filterType() + ".png");
        ImageIO.write(p2, "png", output);
    }

    @Theory
    public void testFilterCircle(Fixture fix) {
        Filter f = fix.init();
        f.loadImage(testImage);
        BufferedImage p1 = f.getImage();
        f.applyToCircle(50, 50, 100);
        BufferedImage p2 = f.getImage();

        assertFalse(this.isDifferent(p1, p2));
    }

    @Theory
    public void testFilterIOSuccess(Fixture fix) throws IOException {
        Filter f = fix.init();
        f.loadImage(testImage);
        BufferedImage p2 = f.getImage();

        assertFalse(this.isDifferent(testImage, p2));
    }

    @Theory
    public void testFilterIOFail(Fixture fix) throws IOException {
        Filter f = fix.init();
        // TODO: When we actually handle it
    }


    // Helper method for checking if two images are equal
    private boolean isDifferent(BufferedImage p1, BufferedImage p2) {
        if (p1.getHeight() != p2.getHeight() || p1.getWidth() != p2.getWidth())
            return true;
        for (int x = 0; x < p1.getWidth(); x++) {
            for (int y = 0; y < p2.getHeight(); y++) {
                if (p1.getRGB(x, y) != p2.getRGB(x, y))
                    return true;
            }
        }
        return false;
    }
}
