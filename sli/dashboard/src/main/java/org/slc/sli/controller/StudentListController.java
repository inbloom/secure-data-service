package org.slc.sli.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slc.sli.client.MockAPIClient;



@Controller
@RequestMapping("/studentlist")
public class StudentListController {

    @RequestMapping(method = RequestMethod.GET)
    public String retrieveStudentList(ModelMap model) throws IOException {

        //TODO: Make call to actual client instead of mock client
        MockAPIClient client = new MockAPIClient();
        model.addAttribute("listOfStudents", client.getStudents("something"));
        return "studentList";
    }
}
