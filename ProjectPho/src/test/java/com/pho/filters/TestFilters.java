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

    // Anonymous class for testing filters
    private interface Fixture {
        Filter init();
    }

    @Before
    public void setup() {
        // setup for filters
    }

    @After
    public void teardown() {
        // Does nothing for now
    }

    @DataPoint
    public static final Fixture blurFilter = new Fixture() {
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new BlurFilter(params);
        }
    };

    @DataPoint
    public static final Fixture changeContrastFilter = new Fixture() {
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new ChangeContrastFilter(params);
        }
    };

    @DataPoint
    public static final Fixture EdgeDetection = new Fixture() {
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new EdgeDetectionFilter(params);
        }
    };

    @Theory
    public void testFilterRectangle(Fixture fix) {
        Filter f = fix.init();
        f.loadImage("test.jpg");
        BufferedImage p1 = f.getImage();
        f.applyToRectangle(50, 100, 50, 100);
        BufferedImage p2 = f.getImage();

        assertTrue(isDifferent(p1, p2));
    }

    @Theory
    public void testFilterCircle(Fixture fix) {
        Filter f = fix.init();
        f.loadImage("test.jpg");
        BufferedImage p1 = f.getImage();
        f.applyToCircle(50, 50, 100);
        BufferedImage p2 = f.getImage();

        assertFalse(this.isDifferent(p1, p2));
    }

    @Theory
    public void testFilterIO(Fixture fix) throws IOException {
        String imageName = "test.jpg";
        Filter f = fix.init();
        BufferedImage p1 = ImageIO.read(new File(imageName));
        f.loadImage(imageName);
        BufferedImage p2 = f.getImage();

        assertFalse(this.isDifferent(p1, p2));
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
