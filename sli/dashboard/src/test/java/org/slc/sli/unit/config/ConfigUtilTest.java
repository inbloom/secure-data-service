package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ConfigUtil;
import org.slc.sli.config.View;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.DataSet;
import org.slc.sli.config.Field;

/**
 * Unit tests for the ConfigUtil helper class.
 * 
 */
public class ConfigUtilTest {

    @Before
    public void setup() {
        
    }

    @Test
    public void testFromXMLString1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><viewConfig><view name=\"listOfStudents\"><dataSet type=\"studentInfo\" displayName=\"Student Information\"><field id=\"programs\" visual=\"programGrid\" /></dataSet></view></viewConfig>";
        ViewConfig config = null;
        try {
            config = ConfigUtil.fromXMLString(xmlString);
        } catch (Exception e) {
            System.out.println(e);
        }
        assertEquals(1, config.getView().get(0).getDataSet().size());
        assertEquals(1, config.getView().get(0).getDataSet().get(0).getField().size());
    }
    
    @Test
    public void testToXMLString1() {
        
        ViewConfig config = new ViewConfig();
        View view = new View();
        view.setName("listOfStudents");
        config.getView().add(view);
        
        DataSet dataSet = new DataSet();
        dataSet.setType("studentInfo");
        dataSet.setDisplayName("Student Information");
        view.getDataSet().add(dataSet);
       
        Field field = new Field();
        field.setId("programs");
        field.setVisual("programGrid");
        dataSet.getField().add(field);
        
        String xmlString = null;
        try {
            xmlString = ConfigUtil.toXMLString(config);
        } catch (Exception e) {
            System.out.println(e);
        }
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                   + "<viewConfig>\n"
                   + "    <view name=\"listOfStudents\">\n"
                   + "        <dataSet type=\"studentInfo\" displayName=\"Student Information\">\n"
                   + "            <field visual=\"programGrid\" id=\"programs\"/>\n"
                   + "        </dataSet>\n"
                   + "    </view>\n"
                   + "</viewConfig>\n", 
                     xmlString);
        
    }
}
