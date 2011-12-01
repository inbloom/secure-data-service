package org.slc.sli.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slc.sli.client.MockAPIClient;
import org.slc.sli.entity.Student;



@Controller
@RequestMapping("/studentlist")
public class StudentListController {

    private MockAPIClient apiClient;
    
    public StudentListController() {
        apiClient = new MockAPIClient();
        
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String retrieveStudentList(ModelMap model) throws IOException {

        //TODO: Make call to actual client instead of mock client, and use a token instead of empty string
        Student[] studentList = retrieveStudents("");
        if (studentList != null)
            model.addAttribute("listOfStudents", studentList);
        return "studentList";
    }
    
    private Student[] retrieveStudents(String token) throws IOException {
        return apiClient.getStudents(token);
    }
    
    
}
