package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.LozengeConfig;

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
            ConfigPersistor persistor = new ConfigPersistor();
            persistor.setApiClient(new MockAPIClient());
            configSet = persistor.getConfigSet("lkim");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewConfig config = configSet.getViewConfig().get(0);
        assertEquals(3, config.getDisplaySet().size());
        assertEquals(1, config.getDisplaySet().get(0).getField().size());
        assertEquals(4, config.getDisplaySet().get(1).getField().size());
        assertEquals(2, config.getDisplaySet().get(2).getField().size());
    }

    @Test
    public void testGetLozengeConfig() {
        LozengeConfig[] lozengeConfigs = null;
        try {
            ConfigPersistor persistor = new ConfigPersistor();
            persistor.setApiClient(new MockAPIClient());
            lozengeConfigs = persistor.getLozengeConfig("lkim");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(10, lozengeConfigs.length);
    }

    @Test
    public void testSaveConfigSet() {
        
        ViewConfigSet configs = new ViewConfigSet();
        ViewConfig view = new ViewConfig();
        view.setName("listOfStudents");
        configs.getViewConfig().add(view);
        
        DisplaySet displaySet = new DisplaySet();
        displaySet.setDisplayName("");
        view.getDisplaySet().add(displaySet);
        
        Field field = new Field();
        field.setValue("stud.studentInfo.name");
        field.setFormat("firstLast");
        field.setDisplayName("Student");
        displaySet.getField().add(field);
        
        /*
        try {
            ConfigPersistor.saveConfigSet("common", configs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        
    }
    
}
