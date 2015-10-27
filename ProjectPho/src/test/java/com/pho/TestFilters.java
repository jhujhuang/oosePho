package com.pho;

import com.pho.filters.BlurFilter;
import com.pho.filters.Filter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(Theories.class)
public class TestFilters {

    // Anonymous class
    private interface Fixture {
        Filter init();
    }

    @Before
    public void setup() {
        // Nothing to setup for filters
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

  /*  @DataPoint
    public static final Fixture changeContrastFilter = new Fixture() {
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new BlurFilter(params);
        }
    };

    @DataPoint
    public static final Fixture EdgeDetection = new Fixture() {
        public Filter init() {
            Map<String, Double> params = new HashMap<String, Double>();
            params.put("value", 0.8);
            return new BlurFilter(params);
        }
    };
*/
    @Theory
    public void testFilterRectangle(Fixture fix) throws Exception {
        Filter f = fix.init();
        f.loadImage("test.jpg");
        BufferedImage p1 = f.getImage();
        f.applyToRectangle(50, 100, 50, 100);
        BufferedImage p2 = f.getImage();

        assertEquals(p1.getHeight(), p2.getHeight());
        assertEquals(p1.getWidth(), p2.getWidth());

        boolean difference = false;

        for (int x = 0; x < p1.getWidth(); x++) {
            for (int y = 0; y < p1.getHeight(); y++) {
                if (p1.getRGB(x, y) != p2.getRGB(x, y))
                    difference = true;
            }
        }
        assertTrue(difference);
    }

    @Theory
    public void testFilterCircle(Fixture fix) throws Exception {
        Filter f = fix.init();
        f.loadImage("test.jpg");
        BufferedImage p1 = f.getImage();
        f.applyToCircle(50, 50, 100);
        BufferedImage p2 = f.getImage();

        assertEquals(p1.getHeight(), p2.getHeight());
        assertEquals(p1.getWidth(), p2.getWidth());

        boolean difference = false;

        for (int x = 0; x < p1.getWidth(); x++) {
            for (int y = 0; y < p2.getHeight(); y++) {
                if (p1.getRGB(x, y) != p2.getRGB(x, y))
                    difference = true;
            }
        }
        assertFalse(difference);
    }


}
