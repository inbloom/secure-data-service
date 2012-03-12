package org.slc.sli.unit.view.widget;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.view.widget.ColorByPercent;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
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
    
    @Test
    public void testRounding() {
        percent.setTotal(9);
        percent.setActual(6);
        assertTrue("2/3 should round up to 67%", percent.getText().equals("67"));
    }
    
    @Test
    public void testDivisionBy0() {
        percent.setTotal(0);
        percent.setActual(6);
        assertTrue("Not a number...should show up as -", percent.getText().equals("-"));
    }
    
    @Test
    public void testDivisionBy0Color() {
        percent.setTotal(0);
        percent.setActual(1);
        assertTrue("An invalid number should be none", percent.getColor().equals("none"));
    }
    
    @Test
    public void testCustomBoundaries() {
        percent.setBoundaries(new int[] {10, 20, 30});
        percent.setTotal(100);
        percent.setActual(5);
        assertEquals("0 - 10 is critical", "critical", percent.getColor());
        
        percent.setActual(15);
        assertEquals("10 - 20 is low", "low", percent.getColor());
        
        percent.setActual(25);
        assertEquals("20 - 30 is average", "average", percent.getColor());
        
        percent.setActual(35);
        assertEquals("30 - 40 is high", "high", percent.getColor());
    }
    
    @Test
    public void testCustomBoundariesReverse() {
        percent.setBoundaries(new int[] {50, 25, 5});
        percent.setTotal(100);
        
        percent.setActual(75);
        assertEquals("50 - 100 is critical", "critical", percent.getColor());
        
        percent.setActual(30);
        assertEquals("25 - 50 is low", "low", percent.getColor());
        
        percent.setActual(20);
        assertEquals("5 - 25 is average", "average", percent.getColor());
        
        percent.setActual(2);
        assertEquals("0 - 5 is high", "high", percent.getColor());
    }
}
