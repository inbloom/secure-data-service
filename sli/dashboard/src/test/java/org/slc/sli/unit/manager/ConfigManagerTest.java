package org.slc.sli.unit.manager;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.manager.ConfigManager;


/**
 * Unit tests for the StudentManager class.
 * 
 */
public class ConfigManagerTest {

    @Before
    public void setup() {
        
    }

    @Test
    public void testGetConfigSet() {
        
        ViewConfigSet configSet = ConfigManager.getInstance().getConfigSet("lkim");
        assertEquals(1, configSet.getViewConfig().size());
        assertEquals("IL_3-8_ELA", configSet.getViewConfig().get(0).getName());
    }
    
    @Test
    public void testGetConfigSetMissing() {
    
        // look for a config set that doesn't exist
        ViewConfigSet configSet = ConfigManager.getInstance().getConfigSet("not_there");
        assertNull(configSet);
    }
    
    @Test
    public void testGetConfig() {
    
        ViewConfig config = ConfigManager.getInstance().getConfig("lkim", "IL_3-8_ELA");
        assertEquals(3, config.getDisplaySet().size());
    }
    
    @Test
    public void testGetConfigMissing() {
        
        ViewConfig config = ConfigManager.getInstance().getConfig("not_there", "IL_3-8_ELA");
        assertNull(config);
        
        ViewConfig config2 = ConfigManager.getInstance().getConfig("lkim", "not_there");
        assertNull(config2);
    }
    
}
