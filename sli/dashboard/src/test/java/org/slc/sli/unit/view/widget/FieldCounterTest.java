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

    
    @Before
    public void setUp() throws Exception {
        AggregateResolver mockResolver = mock(AggregateResolver.class);
        Field mockField = new Field();
        mockField.setValue("path.path");
        when(mockResolver.getCountForPath(mockField)).thenReturn(30);

        counter = new FieldCounter(mockField, null, mockResolver, new int[]{0, 5, 10});
    }

    @After
    public void tearDown() throws Exception {
        counter = null;
    }

    @Test
    public void testGetText() throws Exception {
        assert (counter.getText().equals("30"));
    }

    @Test
    public void testGetColor() throws Exception {
        assert (counter.getColorIndex() == 3);
    }
    
    @Test
    public void testFirstLevel() {
        AggregateResolver mockResolver = mock(AggregateResolver.class);
        Field mockField = new Field();
        when(mockResolver.getCountForPath(mockField)).thenReturn(0);
        assert (counter.getColorIndex() == 1);
    }
}
