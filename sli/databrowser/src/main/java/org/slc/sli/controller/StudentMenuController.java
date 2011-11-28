package org.slc.sli.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.slc.sli.client.Format;
import org.slc.sli.client.SliClient;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.enums.SexType;

/**
 * controller for going to the Student Menu page with error message
 */
@Controller
@RequestMapping("studentmenu.html")
public class StudentMenuController {
    
    private static Logger logger = LoggerFactory.getLogger(StudentMenuController.class);
    private static final String ERROR_MESSAGE = "Error: Jersey RESTful Web App (API) is not online!";
    
    @Autowired
    private SliClient client;
    
    /**
     * Selects the home page and populates the model with a message
     */
    @RequestMapping(method = RequestMethod.GET)
    public String displayMenu(Model model) {
        logger.info("Display Menu!");
        model.addAttribute("studentNumber", 0);
        return "studentmenu";
    }
    
    @RequestMapping(params = "action=list", method = RequestMethod.GET)
    public String listStudents(@RequestParam("format") String format, Model model) {
        logger.info("List Students Action!");
        // TODO need to fix format
        // if (format == null || format.equals(""))
        format = "json";
        try {
            client.setDefaultFormat(Format.JSON);
            List<Student> students = new ArrayList<Student>();
            Iterable<Student> it = client.list(Student.class);
            for (Student student : it)
                students.add(student);
            model.addAttribute("students", students);
            model.addAttribute("studentNumber", students.size());
        } catch (Exception e) {
            logger.info(ERROR_MESSAGE);
            model.addAttribute("errorMessage", ERROR_MESSAGE);
        }
        model.addAttribute("format", format);
        return "studentmenu";
    }
    
    @RequestMapping(params = "action=delete", method = RequestMethod.GET)
    public String deleteStudents(@RequestParam("format") String format, @RequestParam("id") String id, Model model) {
        logger.info("Delete Students Action!");
        // TODO need to fix format
        // if (format == null || format.equals(""))
        format = "json";
        client.setDefaultFormat(Format.JSON);
        try {
            client.deleteResource(Student.class, id);
        } catch (Exception e) {
            logger.info(ERROR_MESSAGE);
            model.addAttribute("errorMessage", ERROR_MESSAGE);
        }
        return "redirect:/spring/studentmenu.html?action=list&format=" + format;
    }
    
    @RequestMapping(params = "action=add", method = RequestMethod.GET)
    public String addStudents(@RequestParam("format") String format, @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName, Model model) {
        logger.info("Add Students Action!");
        if (!(firstName.equals("") || lastName.equals(""))) {
            Student student = new Student();
            student.setFirstName(firstName);
            student.setLastSurname(lastName);
            student.setStudentSchoolId(String.valueOf(new Random().nextInt(1000000000)));
            student.setStudentId(new Random().nextInt(1000000));
            student.setBirthDate(new Date());
            student.setSex(SexType.Male);
            // TODO need to fix format
            // if (format == null || format.equals(""))
            format = "json";
            try {
                client.setDefaultFormat(Format.JSON);
                client.addNewResource(student);
            } catch (Exception e) {
                logger.info(ERROR_MESSAGE);
                model.addAttribute("errorMessage", ERROR_MESSAGE);
            }
        }
        return "redirect:/spring/studentmenu.html?action=list&format=" + format;
    }
    
    @RequestMapping(params = "action=update", method = RequestMethod.GET)
    public String update(@RequestParam("format") String format, @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName, @RequestParam("id") String id, Model model) {
        logger.info("Update Student Action!");
        
        if (!(firstName.equals("") || lastName.equals("") || id.equals(""))) {
            Student student = new Student();
            student.setFirstName(firstName);
            student.setLastSurname(lastName);
            student.setStudentId(Integer.parseInt(id));
            student.setStudentSchoolId(String.valueOf(new Random().nextInt(1000000000)));
            student.setBirthDate(new Date());
            student.setSex(SexType.Male);
            // TODO need to fix format
            // if (format == null || format.equals(""))
            format = "json";
            try {
                client.setDefaultFormat(Format.JSON);
                client.updateResource(student);
            } catch (Exception e) {
                logger.info(ERROR_MESSAGE);
                model.addAttribute("errorMessage", ERROR_MESSAGE);
            }
        }
        return "redirect:/spring/studentmenu.html?action=list&format=" + format;
    }
}
