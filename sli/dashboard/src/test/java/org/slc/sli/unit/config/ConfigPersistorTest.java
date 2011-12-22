package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.DataPoint;
import org.slc.sli.config.DataSet;
import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;
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
        assertEquals(4, config.getDisplaySet().get(1).getField().size());
        assertEquals(2, config.getDisplaySet().get(2).getField().size());
    }
    
    @Test
    public void testSaveConfigSet() {
        
        ViewConfigSet configs = new ViewConfigSet();
        ViewConfig view = new ViewConfig();
        view.setName("listOfStudents");
        configs.getViewConfig().add(view);
        
        DataSet dataSet = new DataSet();
        dataSet.setType("studentInfo");
        view.getDataSet().add(dataSet);
       
        DataPoint dataPoint = new DataPoint();
        dataPoint.setId("name");
        dataSet.getDataPoint().add(dataPoint);
        
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
