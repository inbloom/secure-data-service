package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ConfigPersistor;
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
    public void testGetConfig() {
     
        ViewConfig config = null;
        try {
            config = ConfigPersistor.getConfig("lkim");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(3, config.getView().get(0).getDataSet().size());
        assertEquals(3, config.getView().get(0).getDataSet().get(0).getField().size());
        assertEquals(3, config.getView().get(0).getDataSet().get(1).getField().size());
        assertEquals(2, config.getView().get(0).getDataSet().get(2).getField().size());
    }
    
}
