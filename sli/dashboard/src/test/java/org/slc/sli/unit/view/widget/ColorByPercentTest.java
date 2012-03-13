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
        assertEquals(5, percent.getColorIndex());
    }

    @Test
    public void testGetYellowColor() throws Exception {
        percent.setActual(75);
        assertTrue(percent.getText() + " should be 75", percent.getText().equals("75"));
        assertEquals(4, percent.getColorIndex());
    }

    @Test
    public void testGetRedColor() throws Exception {
        percent.setActual(40);
        assertTrue(percent.getText() + " should be 40", percent.getText().equals("40"));
        assertEquals(2, percent.getColorIndex());
    }

    @Test
    public void testFailWithBadData() throws Exception {
        percent.setActual(-3);
        assertTrue(percent.getText() + " should be 0", percent.getText().equals("0"));
        assertEquals(1, percent.getColorIndex());
    }

    @Test
    public void testInvertedPercent() throws Exception {
        percent.setActual(3);
        percent.setIsInverted(true);
        assertTrue(percent.getText() + " should be 97", percent.getText().equals("97"));
        assertEquals(5, percent.getColorIndex());
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
        assertTrue("An invalid number should be none", percent.getColorIndex() == 0);
    }
    
    /* Color levels used
     * private static int[][] perfToColor = {{0, 0, 0, 0, 0}, // 1 level
                                          {1, 5, 0, 0, 0}, // 2 levels
                                          {1, 2, 5, 0, 0}, // 3 levels
                                          {1, 2, 4, 5, 0}, // 4 levels
                                          {1, 2, 3, 4, 5}};  // 5 levels
     */
    
    @Test
    public void testCustomBoundaries0() {
        percent.setBoundaries(new int[] {});
        percent.setTotal(100);
        percent.setActual(49);
        assertEquals(0, percent.getColorIndex());
    }
    
    @Test
    public void testCustomBoundaries1() {
        percent.setBoundaries(new int[] {50});
        percent.setTotal(100);
        percent.setActual(49);
        assertEquals(1, percent.getColorIndex());
        
        percent.setActual(51);
        assertEquals(5, percent.getColorIndex());
    }
    
    @Test
    public void testCustomBoundaries2() {
        percent.setBoundaries(new int[] {30, 60});
        percent.setTotal(100);
        percent.setActual(29);
        assertEquals(1, percent.getColorIndex());
        
        percent.setActual(31);
        assertEquals(2, percent.getColorIndex());
        
        percent.setActual(61);
        assertEquals(5, percent.getColorIndex());
    }
    
    @Test
    public void testCustomBoundaries3() {
        percent.setBoundaries(new int[] {10, 20, 30});
        percent.setTotal(100);
        percent.setActual(5);
        assertEquals(1, percent.getColorIndex());
        
        percent.setActual(15);
        assertEquals(2, percent.getColorIndex());
        
        percent.setActual(25);
        assertEquals(4, percent.getColorIndex());
        
        percent.setActual(35);
        assertEquals(5, percent.getColorIndex());
    }
    
    @Test
    public void testCustomBoundaries4() {
        percent.setBoundaries(new int[] {10, 20, 30, 40});
        percent.setTotal(100);
        percent.setActual(9);
        assertEquals(1, percent.getColorIndex());
        
        percent.setActual(19);
        assertEquals(2, percent.getColorIndex());
        
        percent.setActual(29);
        assertEquals(3, percent.getColorIndex());
        
        percent.setActual(39);
        assertEquals(4, percent.getColorIndex());
        
        percent.setActual(41);
        assertEquals(5, percent.getColorIndex());
    }
    
    @Test
    public void testCustomBoundariesReverse() {
        percent.setBoundaries(new int[] {50, 25, 5});
        percent.setTotal(100);
        
        percent.setActual(75);
        assertEquals(1, percent.getColorIndex());
        
        percent.setActual(30);
        assertEquals(2, percent.getColorIndex());
        
        percent.setActual(20);
        assertEquals(4, percent.getColorIndex());
        
        percent.setActual(2);
        assertEquals(5, percent.getColorIndex());
    }
    
    @Test
    //place a point on boundary on the lower section
    public void testPointOnBoundary() {
        percent.setBoundaries(new int[] {50});
        percent.setTotal(50);
        assertEquals(1, percent.getColorIndex());
    }
}
