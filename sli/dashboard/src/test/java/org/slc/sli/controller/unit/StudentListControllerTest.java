package org.slc.sli.controller.unit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.controller.StudentListController;
import org.slc.sli.entity.Student;
import org.springframework.ui.ModelMap;

public class StudentListControllerTest {

    private StudentListController studentListController;
    
    
    @Before
    public void setup() {
        studentListController = new StudentListController();
    }
    
    
    @Test
    public void testStudentListNotEmpty() {
        
        ModelMap model = new ModelMap();
        String result;
        try {
            result = studentListController.retrieveStudentList(model);
            assertEquals(result, "studentList");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Student[] studentList = (Student[]) model.get("listOfStudents");
        assertTrue(studentList.length > 0);
    }
    
}
