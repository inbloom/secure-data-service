package org.slc.sli.unit.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.gson.Gson;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.controller.StudentListController;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Student;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


@RunWith(PowerMockRunner.class)
@PrepareForTest(StudentListController.class)
public class StudentListControllerTest {

    private StudentListController studentListController;
    private String testUser;
    
    @Before
    public void setup() {
        studentListController = new StudentListController();
        testUser = "sravan";
    }
    

    
    

    @Test
    public void testStudentListNotEmpty() throws Exception {
        
        MockAPIClient mockClient = new MockAPIClient();
        School[] schools = mockClient.getSchools("common");
        ModelMap model = new ModelMap();
        StudentListController partiallyMocked = PowerMockito.spy(new StudentListController());
        PowerMockito.doReturn(schools).when(partiallyMocked, "retrieveSchools", "");

        ModelAndView result;   
        result = partiallyMocked.retrieveStudentList("", model);
        assertEquals(result.getViewName(), "studentList");
        String schoolListJson = (String) model.get("schoolList");
        Gson gson = new Gson();
        School[] schoolList = gson.fromJson(schoolListJson, School[].class); 
        assertTrue(schoolList.length > 0);

        
    }
    
    
    @Test
    public void testStudentListNullReturn() throws Exception {

        StudentListController mocked = PowerMockito.spy(new StudentListController());
        PowerMockito.doReturn(null).when(mocked, "retrieveSchools", "");
        ModelMap model = new ModelMap();
        mocked.retrieveStudentList("", model);
        assert(model.get("schoolList") == null);

    }
    
}
