package org.slc.sli.unit.entity;

import junit.framework.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import org.slc.sli.entity.Config;

/**
 * 
 * @author dwu
 *
 */
public class ConfigTest {

    private static final String DEFAULT_LAYOUT_JSON = 
            "{id : 'studentProfile', type: 'LAYOUT', " 
          + " data :{entity: 'mock', alias: 'mock' }, " 
          + " items: [{id : 'csi', name: 'Student Info', type: 'PANEL'},"
          + "         {id: 'tab2', name: 'Attendance and Discipline', type : 'TAB', " 
          + "          condition: {field: 'limitedEnglishProficiency', value: ['Limited']}, "
          + "          items: [{id : 'csi', type: 'PANEL'}]}]}";
    
    
    private static final String GRID_JSON = 
            "{id : 'enrollmentHist', type : 'GRID', "
          + " name : 'Enrollment History', data :{ entity: 'student', alias: 'student'}, "
          + " items : [ " 
          + "  {id: 'col1', name: 'School Year', type:'FIELD', datatype: 'string', field: 'studentSchool.schoolYear', width: 100}, "
          + "  {id: 'col2', name: 'School', type:'FIELD', datatype: 'string', field: 'studentSchool.nameOfInstitution', width: 220} ]}";
    
    /**
     * Test conversion from layout json string to Config object
     */
    @Test
    public void testConvertLayout() {
        
        Gson gson = new GsonBuilder().create();
        Config c = gson.fromJson(DEFAULT_LAYOUT_JSON, Config.class);
        Assert.assertEquals(2, c.getItems().length);
        Assert.assertEquals("csi", c.getItems()[0].getId());
        Assert.assertEquals("Student Info", c.getItems()[0].getName());
        Assert.assertEquals(Config.Type.PANEL, c.getItems()[0].getType());
        Assert.assertEquals("Data [entityRef=mock, entityAlias=mock, params=null]", c.getData().toString());
        Assert.assertEquals("Condition [field=limitedEnglishProficiency, value=[Limited]]", c.getItems()[1].getCondition().toString());
    }

    /**
     * Test conversion from grid json string to Config object
     */
    @Test
    public void testConvertGrid() {
        
        Gson gson = new GsonBuilder().create();
        Config c = gson.fromJson(GRID_JSON, Config.class);
        Assert.assertEquals(2, c.getItems().length);
        Assert.assertEquals("col1", c.getItems()[0].getId());
        Assert.assertEquals(Config.Type.FIELD, c.getItems()[0].getType());
        Assert.assertEquals("studentSchool.schoolYear", c.getItems()[0].getField());
        Assert.assertEquals("100", c.getItems()[0].getWidth());
        Assert.assertEquals("string", c.getItems()[0].getDatatype());
        Assert.assertEquals("ViewItem [width=100, type=string, color=null, style=null, formatter=null, params=null]", c.getItems()[0].toString());
    }
    
}
