package org.slc.sli.controller;

import java.io.IOException;

import com.google.gson.Gson;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Student;



@Controller
@RequestMapping("/studentlist")
public class StudentListController {

    private MockAPIClient apiClient;
    
    public StudentListController() {
        apiClient = new MockAPIClient();
        
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView retrieveStudentList(String username, ModelMap model) throws IOException {


        Gson gson = new Gson();
        //TODO: Make call to actual client instead of mock client, and use a token instead of empty string
        School[] schoolList = retrieveSchools(username);
        model.addAttribute("schoolList", gson.toJson(schoolList));
        
        return new ModelAndView("studentList");
    }
    
    private Student[] retrieveStudents(String token) throws IOException {
        return apiClient.getStudents(token);
    }
    
    private School[] retrieveSchools(String token) throws IOException {
        return apiClient.getSchools(token);
    }
    
}
