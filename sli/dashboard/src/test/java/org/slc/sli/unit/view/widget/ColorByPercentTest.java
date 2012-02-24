package org.slc.sli.unit.view.widget;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.view.widget.ColorByPercent;

import static junit.framework.Assert.assertTrue;

/**
 * Tests for the color percentage
 */
public class ColorByPercentTest {

    private ColorByPercent percent;
    @Before
    public void setUp() throws Exception {
        percent = new ColorByPercent();
        percent.setTotal(100);
    }

    @After
    public void tearDown() throws Exception {
        percent = null;
    }

    @Test
    public void testGetGreenColor() throws Exception {
        percent.setActual(97);
        assertTrue(percent.getText() + " should be 97", percent.getText().equals("97"));
        assert (percent.getColor().equals("high"));
    }

    @Test
    public void testGetYellowColor() throws Exception {
        percent.setActual(75);
        assertTrue(percent.getText() + " should be 75", percent.getText().equals("75"));
        assert (percent.getColor().equals("average"));
    }

    @Test
    public void testGetRedColor() throws Exception {
        percent.setActual(40);
        assertTrue(percent.getText() + " should be 40", percent.getText().equals("40"));
        assert (percent.getColor().equals("low"));
    }

    @Test
    public void testFailWithBadData() throws Exception {
        percent.setActual(-3);
        assertTrue(percent.getText() + " should be 0", percent.getText().equals("0"));
        assert (percent.getColor().equals("critical"));
    }

    @Test
    public void testInvertedPercent() throws Exception {
        percent.setActual(3);
        percent.setIsInverted(true);
        assertTrue(percent.getText() + " should be 97", percent.getText().equals("97"));
        assert (percent.getColor().equals("high"));
    }
}
