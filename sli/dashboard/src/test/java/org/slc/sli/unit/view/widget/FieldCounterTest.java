package org.slc.sli.unit.view.widget;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import org.slc.sli.config.Field;
import org.slc.sli.view.AggregateResolver;
import org.slc.sli.view.widget.FieldCounter;

/**
 * Basic test for Field Counter.
 */
@DirtiesContext
public class FieldCounterTest {
    private FieldCounter counter;

    private AggregateResolver mockResolver;
    private Field mockField;
    
    @Before
    public void setUp() throws Exception {
        mockResolver = mock(AggregateResolver.class);
        mockField = new Field();
        mockField.setValue("path.path");

        counter = new FieldCounter(mockField, null, mockResolver, new int[]{0, 5, 10});
    }

    @After
    public void tearDown() throws Exception {
        counter = null;
    }

    @Test
    public void testGetText() throws Exception {
        when(mockResolver.getCountForPath(mockField)).thenReturn(30);
        assertEquals("30", counter.getText());
    }
    
    @Test
    public void testGetColorFirstLevel() {
        when(mockResolver.getCountForPath(mockField)).thenReturn(0);
        assertEquals(1, counter.getColorIndex());
    }

    @Test
    public void testGetColorSecondLevel() {
        when(mockResolver.getCountForPath(mockField)).thenReturn(4);
        assertEquals(2, counter.getColorIndex());
    }
    
    @Test
    public void testColorThirdLevel() {
        when(mockResolver.getCountForPath(mockField)).thenReturn(6);
        assertEquals(3, counter.getColorIndex());
    }
    
    @Test
    public void testGetColor() throws Exception {
        when(mockResolver.getCountForPath(mockField)).thenReturn(30);
        assertEquals(3, counter.getColorIndex());
    }
    
}
