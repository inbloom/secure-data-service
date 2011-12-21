package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.ViewConfigSet;

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
        assertEquals(3, configSet.getViewConfig().get(0).getDataSet().size());
        assertEquals(3, configSet.getViewConfig().get(0).getDataSet().get(0).getField().size());
        assertEquals(3, configSet.getViewConfig().get(0).getDataSet().get(1).getField().size());
        assertEquals(2, configSet.getViewConfig().get(0).getDataSet().get(2).getField().size());
    }
    
}
