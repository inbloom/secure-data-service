package org.slc.sli.unit.manager;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.manager.ConfigManager;


/**
 * Unit tests for the StudentManager class.
 * 
 */
public class ConfigManagerTest {

    ConfigManager configManager;
    MockAPIClient mockClient;
    
    @Before
    public void setup() {
        mockClient = new MockAPIClient();
        configManager = new ConfigManager();
        configManager.setApiClient(mockClient);
    }

    @Test
    public void testGetConfigSet() {
        
        ViewConfigSet configSet = configManager.getConfigSet("lkim");
        assertEquals(1, configSet.getViewConfig().size());
        assertEquals("IL_3-8_ELA", configSet.getViewConfig().get(0).getName());
    }
    
    @Test
    public void testGetConfigSetMissing() {
    
        // look for a config set that doesn't exist
        ViewConfigSet configSet = configManager.getConfigSet("not_there");
        assertNull(configSet);
    }
    
    @Test
    public void testGetConfig() {
    
        ViewConfig config = configManager.getConfig("lkim", "IL_3-8_ELA");
        assertEquals(3, config.getDisplaySet().size());
    }
    
    @Test
    public void testGetConfigMissing() {
        
        ViewConfig config = configManager.getConfig("not_there", "IL_3-8_ELA");
        assertNull(config);
        
        ViewConfig config2 = configManager.getConfig("lkim", "not_there");
        assertNull(config2);
    }
    
}
