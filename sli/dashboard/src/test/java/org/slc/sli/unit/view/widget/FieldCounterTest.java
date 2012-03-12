package org.slc.sli.unit.view.widget;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.config.Field;
import org.slc.sli.view.AggregateResolver;
import org.slc.sli.view.widget.FieldCounter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Basic test for Field Counter.
 */
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
        assert (counter.getText().equals("30"));
    }

    @Test
    public void testGetColor() throws Exception {
        when(mockResolver.getCountForPath(mockField)).thenReturn(30);
        assert (counter.getColorIndex() == 3);
    }
    
    @Test
    public void testFirstLevel() {
        when(mockResolver.getCountForPath(mockField)).thenReturn(0);
        assert (counter.getColorIndex() == 1);
    }
}
