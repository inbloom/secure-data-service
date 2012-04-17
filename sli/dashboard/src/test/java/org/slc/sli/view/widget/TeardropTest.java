package org.slc.sli.view.widget;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;


/**
 * Teardrop JUnit test.
 * 
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class TeardropTest {
    
    @Before
    public void setup() {
    }
    
    @Test
    public void testInit() throws Exception {
        
        String testValue = "testValue";
        String testPreviousValue = "testPreviousValue";
        String testStyleName = "testStyleName";
        
        Teardrop teardrop = new Teardrop(testValue, testPreviousValue);
        
        assertEquals("Teardrop initialization of value failed!", testValue, teardrop.getValue());
        assertEquals("Teardrop initialization of previous value failed!", testPreviousValue,
                teardrop.getPreviousValue());
        
        teardrop = new Teardrop();
        teardrop.setValue(testValue);
        teardrop.setPreviousValue(testPreviousValue);
        teardrop.setStyleName(testStyleName);
        
        assertEquals("Teardrop initialization of value failed!", testValue, teardrop.getValue());
        assertEquals("Teardrop initialization of previous value failed!", testPreviousValue,
                teardrop.getPreviousValue());
        assertEquals("Teardrop initialization of style name failed!", testStyleName, teardrop.getStyleName());
        
    }
    
    @Test
    public void testGradeColors() throws Exception {
        
        Teardrop teardrop;
        
        teardrop = new Teardrop("A+", null);
        assertEquals("Teardrop for A+ failed!", "teardrop-darkgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("A", null);
        assertEquals("Teardrop for A failed!", "teardrop-darkgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("A-", null);
        assertEquals("Teardrop for A- failed!", "teardrop-darkgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("B+", null);
        assertEquals("Teardrop for B+ failed!", "teardrop-lightgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("B", null);
        assertEquals("Teardrop for B failed!", "teardrop-lightgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("B-", null);
        assertEquals("Teardrop for B- failed!", "teardrop-lightgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("C+", null);
        assertEquals("Teardrop for C+ failed!", "teardrop-yellow-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("C", null);
        assertEquals("Teardrop for C failed!", "teardrop-yellow-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("C-", null);
        assertEquals("Teardrop for C- failed!", "teardrop-yellow-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("D+", null);
        assertEquals("Teardrop for D+ failed!", "teardrop-orange-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("D", null);
        assertEquals("Teardrop for D failed!", "teardrop-orange-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("D-", null);
        assertEquals("Teardrop for D- failed!", "teardrop-orange-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("F+", null);
        assertEquals("Teardrop for F+ failed!", "teardrop-red-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("F", null);
        assertEquals("Teardrop for F failed!", "teardrop-red-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("F-", null);
        assertEquals("Teardrop for F- failed!", "teardrop-red-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("1", null);
        assertEquals("Teardrop for 1 failed!", "teardrop-darkgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("2", null);
        assertEquals("Teardrop for 2 failed!", "teardrop-lightgreen-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("3", null);
        assertEquals("Teardrop for 3 failed!", "teardrop-yellow-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("4", null);
        assertEquals("Teardrop for 4 failed!", "teardrop-orange-notrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("5", null);
        assertEquals("Teardrop for 5 failed!", "teardrop-red-notrend", teardrop.getStyleName());
        
    }
    
    @Test
    public void testGradeTrends() throws Exception {
        
        Teardrop teardrop;
        
        teardrop = new Teardrop("A+", "A+");
        assertEquals("Teardrop for A+ failed!", "teardrop-darkgreen-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("A", "A+");
        assertEquals("Teardrop for A failed!", "teardrop-darkgreen-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("A-", "B+");
        assertEquals("Teardrop for A- failed!", "teardrop-darkgreen-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("B+", "B+");
        assertEquals("Teardrop for B+ failed!", "teardrop-lightgreen-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("B", "B+");
        assertEquals("Teardrop for B failed!", "teardrop-lightgreen-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("B-", "C+");
        assertEquals("Teardrop for B- failed!", "teardrop-lightgreen-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("C+", "C+");
        assertEquals("Teardrop for C+ failed!", "teardrop-yellow-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("C", "C+");
        assertEquals("Teardrop for C failed!", "teardrop-yellow-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("C-", "D+");
        assertEquals("Teardrop for C- failed!", "teardrop-yellow-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("D+", "D+");
        assertEquals("Teardrop for D+ failed!", "teardrop-orange-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("D", "D+");
        assertEquals("Teardrop for D failed!", "teardrop-orange-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("D-", "F+");
        assertEquals("Teardrop for D- failed!", "teardrop-orange-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("F+", "F+");
        assertEquals("Teardrop for F+ failed!", "teardrop-red-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("F", "F+");
        assertEquals("Teardrop for F failed!", "teardrop-red-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("F", "F-");
        assertEquals("Teardrop for F failed!", "teardrop-red-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("1", "1");
        assertEquals("Teardrop for 1 failed!", "teardrop-darkgreen-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("1", "2");
        assertEquals("Teardrop for 1 failed!", "teardrop-darkgreen-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("2", "2");
        assertEquals("Teardrop for 2 failed!", "teardrop-lightgreen-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("2", "1");
        assertEquals("Teardrop for 2 failed!", "teardrop-lightgreen-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("2", "3");
        assertEquals("Teardrop for 2 failed!", "teardrop-lightgreen-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("3", "3");
        assertEquals("Teardrop for 3 failed!", "teardrop-yellow-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("3", "2");
        assertEquals("Teardrop for 3 failed!", "teardrop-yellow-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("3", "4");
        assertEquals("Teardrop for 3 failed!", "teardrop-yellow-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("4", "4");
        assertEquals("Teardrop for 4 failed!", "teardrop-orange-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("4", "3");
        assertEquals("Teardrop for 4 failed!", "teardrop-orange-downtrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("4", "5");
        assertEquals("Teardrop for 4 failed!", "teardrop-orange-uptrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("5", "5");
        assertEquals("Teardrop for 5 failed!", "teardrop-red-flattrend", teardrop.getStyleName());
        
        teardrop = new Teardrop("5", "4");
        assertEquals("Teardrop for 5 failed!", "teardrop-red-downtrend", teardrop.getStyleName());
        
    }
    
}
