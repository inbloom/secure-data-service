package org.slc.sli.unit.view.widget;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.view.widget.ColorByPerf;

/**
 * Unit tests for the ColorByPerf class.
 *
 */
public class ColorByPerfTest {

    @Before
    public void setup() {
    }

    @Test
    public void testGetColorIndex() {

        ColorByPerf c = new ColorByPerf(null, null, null);

        assertEquals(1, c.getColorIndex(1, 5));
        assertEquals(2, c.getColorIndex(2, 5));
        assertEquals(3, c.getColorIndex(3, 5));
        assertEquals(4, c.getColorIndex(4, 5));
        assertEquals(5, c.getColorIndex(5, 5));
        assertEquals(0, c.getColorIndex(6, 5));
        assertEquals(0, c.getColorIndex(-1, 5));

        assertEquals(1, c.getColorIndex(1, 4));
        assertEquals(2, c.getColorIndex(2, 4));
        assertEquals(4, c.getColorIndex(3, 4));
        assertEquals(5, c.getColorIndex(4, 4));

        assertEquals(1, c.getColorIndex(1, 3));
        assertEquals(2, c.getColorIndex(2, 3));
        assertEquals(5, c.getColorIndex(3, 3));

        assertEquals(1, c.getColorIndex(1, 2));
        assertEquals(5, c.getColorIndex(2, 2));
    }

}
