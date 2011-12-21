package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.config.ViewConfig;

/**
 * Unit tests for the ConfigPersistor class.
 * 
 */
public class ConfigPersistorTest {

    @Before
    public void setup() {
        
    }

    @Test
    public void testGetConfigSet() {
     
        ViewConfigSet configSet = null;
        try {
            configSet = ConfigPersistor.getConfigSet("lkim");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewConfig config = configSet.getViewConfig().get(0);
        assertEquals(3, config.getDataSet().size());
        assertEquals(1, config.getDataSet().get(0).getDataPoint().size());
        assertEquals(4, config.getDataSet().get(1).getDataPoint().size());
        assertEquals(2, config.getDataSet().get(2).getDataPoint().size());
        assertEquals(3, config.getDisplaySet().size());
        assertEquals(1, config.getDisplaySet().get(0).getField().size());
        assertEquals(1, config.getDisplaySet().get(1).getDisplaySet().size());
        assertEquals(4, config.getDisplaySet().get(1).getDisplaySet().get(0).getField().size());
        assertEquals(1, config.getDisplaySet().get(2).getDisplaySet().size());
        assertEquals(2, config.getDisplaySet().get(2).getDisplaySet().get(0).getField().size());
    }
    
}
