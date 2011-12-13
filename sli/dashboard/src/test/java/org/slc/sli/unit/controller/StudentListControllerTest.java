package org.slc.sli.unit.controller;

import static org.junit.Assert.assertFalse;
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

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import static org.powermock.api.easymock.PowerMock.createNicePartialMockAndInvokeDefaultConstructor;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;





@RunWith(PowerMockRunner.class)
public class StudentListControllerTest {

    private StudentListController studentListController;
    private String testUser;
    
    @Before
    public void setup() {
        studentListController = new StudentListController();
        testUser = "sravan";
    }
    /*
    @PrepareForTest(StudentListController.class)
    @Test
    public void testStudentListNotEmpty() throws Exception {
        
        MockAPIClient mockClient = new MockAPIClient();
        School[] schools = mockClient.getSchools("common");
        ModelMap model = new ModelMap();
        
        StudentListController partiallyMocked = createNicePartialMockAndInvokeDefaultConstructor(StudentListController.class, "retrieveSchools");
        expectPrivate(partiallyMocked, "retrieveSchools", "common").andReturn(schools);
        ModelAndView result;
        
        try {
            replay(StudentListController.class);
            result = studentListController.retrieveStudentList("common", model);
            assertEquals(result.getViewName(), "studentList");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            verify(StudentListController.class);
        }
        Boolean temp = model.containsAttribute("schoolList");
        System.err.println(temp);
        String schoolListJson = (String) model.get("schoolList");
        Gson gson = new Gson();
        School[] schoolList = gson.fromJson(schoolListJson, School[].class); 
        assertTrue(schoolList.length > 0);
    }
    
    
    @PrepareForTest(StudentListController.class)
    @Test
    public void testStudentListNullReturn() throws Exception {
        StudentListController partiallyMocked = createNicePartialMockAndInvokeDefaultConstructor(StudentListController.class, "retrieveStudents");
        expectPrivate(partiallyMocked, "retrieveStudents", "").andReturn(null);
        ModelMap model = new ModelMap();
        replay(StudentListController.class);
        String result = partiallyMocked.retrieveStudentList(model);
        assertFalse(model.containsKey("listOfStudents"));
        verify(StudentListController.class);
    }*/
    
}
