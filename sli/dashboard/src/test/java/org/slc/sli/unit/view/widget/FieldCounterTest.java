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
        when(mockResolver.getCountForPath("path.path")).thenReturn(30);
        Field f = new Field();
        f.setValue("path.path");
        
        counter = new FieldCounter(f, null, mockResolver);
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
        assert (counter.getColor().equals("black"));

    }
}
