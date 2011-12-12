package org.slc.sli.controller.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.gson.Gson;
import org.slc.sli.controller.StudentListController;
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
    
    
    @Before
    public void setup() {
        studentListController = new StudentListController();
    }
    
    
    @Test
    public void testStudentListNotEmpty() {
        
        ModelMap model = new ModelMap();
        ModelAndView result;
        try {
            result = studentListController.retrieveStudentList("", model);
            assertEquals(result.getViewName(), "studentList");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String studentListJson = (String) model.get("schoolList");
        Gson gson = new Gson();
        Student[] studentList = gson.fromJson(studentListJson, Student[].class); 
        assertTrue(studentList.length > 0);
    }
    
    /*
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
