/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.unit.entity;

import junit.framework.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Type;

/**
 *
 * @author dwu
 *
 */
public class ConfigTest {

    private static final String DEFAULT_LAYOUT_JSON = "{id : 'studentProfile', type: 'LAYOUT', "
            + " data :{entity: 'mock', cacheKey: 'mock' }, "
            + " items: [{id : 'csi', name: 'Student Info', type: 'PANEL'},"
            + "         {id: 'tab2', name: 'Attendance and Discipline', type : 'TAB', "
            + "          condition: {field: 'limitedEnglishProficiency', value: ['Limited']}, "
            + "          items: [{id : 'csi', type: 'PANEL'}]}]}";

    private static final String GRID_JSON = "{id : 'enrollmentHist', type : 'GRID', "
            + " name : 'Enrollment History', data :{ entity: 'student', cacheKey: 'student'}, "
            + " items : [ "
            + "  {id: 'col1', name: 'School Year', type:'FIELD', datatype: 'string', field: 'studentSchool.schoolYear', width: 100}, "
            + "  {id: 'col2', name: 'School', type:'FIELD', datatype: 'string', field: 'studentSchool.nameOfInstitution', width: 220} ]}";

    private static final String STUDENT_PROFILE_DRIVER = "{id : 'studentProfile',name: 'Name', type: 'LAYOUT',data :{"
            + "entity: 'student',cacheKey: 'student'}, items: [" + "{id : 'csi', name: 'Student Info', type: 'PANEL'}]}";

    private static final String STUDENT_PROFILE_DISTRICT = "{id : 'studentProfileDistrict',"
            + "type: 'FIELD', name: 'District Name', data :{entity: 'studentDistrct',cacheKey: 'studentDistrct'}, "
            + "items: [{id : 'csiDistrict', name: 'Student Info District', type: 'PANEL'},"
            + "{id: 'tabDistrict', name: 'Daybreak district tab',  type : 'TAB', items: []}]}";

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
        Assert.assertEquals("Condition [field=limitedEnglishProficiency, value=[Limited]]", c.getItems()[1]
                .getCondition().toString());
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
        Assert.assertEquals("ViewItem [width=100, type=string, color=null, style=null, formatter=null, params=null]",
                c.getItems()[0].toString());
    }

    @Test
    public void testMerge() {
        Gson gson = new GsonBuilder().create();
        Config driver = gson.fromJson(STUDENT_PROFILE_DRIVER, Config.class);
        Config district = gson.fromJson(STUDENT_PROFILE_DISTRICT, Config.class);
        Config merged = driver.overWrite(district);

        Assert.assertEquals(merged.getId(), driver.getId());
        Assert.assertEquals(merged.getName(), "District Name");
        Assert.assertEquals(driver.getName(), "Name");
        Assert.assertEquals(district.getType(), Type.FIELD);
        Assert.assertEquals(driver.getType(), Type.LAYOUT);
        Assert.assertEquals(merged.getType(), Type.LAYOUT);

        Config.Data data = merged.getData();
        Assert.assertEquals("studentDistrct", data.getCacheKey());
        Assert.assertEquals(data.getEntityRef(), "student");
        Assert.assertNull(data.getParams());

        Config.Item[] items = merged.getItems();
        Assert.assertEquals(items.length, 2);
        Assert.assertEquals(items[0].getId(), "csiDistrict");
        Assert.assertEquals(items[0].getType(), Config.Type.PANEL);
        Assert.assertEquals(items[0].getName(), "Student Info District");

        Assert.assertEquals(items[1].getId(), "tabDistrict");
        Assert.assertEquals(items[1].getType(), Config.Type.TAB);
        Assert.assertEquals(items[1].getName(), "Daybreak district tab");
    }
}
