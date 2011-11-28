package org.slc.sli.controller;

import java.util.ArrayList;
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
import org.slc.sli.domain.School;

/**
 * controller for going to the School Menu page with error message
 */
@Controller
@RequestMapping("schoolmenu.html")
public class SchoolMenuController {
    
    private static final Logger LOG = LoggerFactory.getLogger(SchoolMenuController.class);
    private static final String ERROR_MESSAGE = "Error: Jersey RESTful Web App (API) is not online!";
    
    @Autowired
    private SliClient client;
    
    /**
     * Selects the home page and populates the model with a message
     */
    @RequestMapping(method = RequestMethod.GET)
    public String displayMenu(Model model) {
        LOG.info("Display School Menu!");
        model.addAttribute("schoolNumber", 0);
        return "schoolmenu";
    }
    
    @RequestMapping(params = "action=list", method = RequestMethod.GET)
    public String listSchools(@RequestParam("format") String format, Model model) {
        LOG.info("List Schools Action!");
        if (format == null || format.equals(""))
            format = "json";
        try {
            if (format == "json")
                client.setDefaultFormat(Format.JSON);
            else if (format == "xml")
                client.setDefaultFormat(Format.XML);
            ArrayList<School> schools = new ArrayList<School>();
            Iterable<School> it = client.list(School.class);
            for (School school : it)
                schools.add(school);
            model.addAttribute("schools", schools);
            model.addAttribute("schoolNumber", schools.size());
        } catch (Exception e) {
            LOG.info(ERROR_MESSAGE);
            model.addAttribute("errorMessage", ERROR_MESSAGE);
        }
        model.addAttribute("format", format);
        return "schoolmenu";
    }
    
    @RequestMapping(params = "action=delete", method = RequestMethod.GET)
    public String deleteSchools(@RequestParam("format") String format, @RequestParam("id") String id, Model model) {
        LOG.info("Delete Schools Action!");
        if (format == null || format.equals(""))
            format = "json";
        if (format == "json")
            client.setDefaultFormat(Format.JSON);
        else if (format == "xml")
            client.setDefaultFormat(Format.XML);
        LOG.info("delete school id is {}" + id);
        try {
            client.deleteResource(School.class, id);
        } catch (Exception e) {
            LOG.info(ERROR_MESSAGE);
            model.addAttribute("errorMessage", ERROR_MESSAGE);
        }
        return "redirect:/spring/schoolmenu.html?action=list&format=" + format;
    }
    
    @RequestMapping(params = "action=add", method = RequestMethod.GET)
    public String addSchools(@RequestParam("format") String format,
            @RequestParam("schoolfullname") String schoolFullName, Model model) {
        LOG.info("Add Schools Action!");
        if (!(schoolFullName.equals(""))) {
            School school = new School();
            school.setFullName(schoolFullName);
            school.setSchoolId(new Random().nextInt(1000000000));
            
            if (format == null || format.equals(""))
                format = "json";
            
            try {
                if (format == "json")
                    client.setDefaultFormat(Format.JSON);
                else if (format == "xml")
                    client.setDefaultFormat(Format.XML);
                client.addNewResource(school);
            } catch (Exception e) {
                LOG.info(ERROR_MESSAGE);
                model.addAttribute("errorMessage", ERROR_MESSAGE);
            }
        }
        return "redirect:/spring/schoolmenu.html?action=list&format=" + format;
    }
    
    @RequestMapping(params = "action=update", method = RequestMethod.GET)
    public String update(@RequestParam("format") String format, @RequestParam("schoolfullname") String schoolFullName,
            @RequestParam("id") String id, Model model) {
        LOG.info("Update School Action!");
        
        if (!(schoolFullName.equals("") || id.equals(""))) {
            School school = new School();
            school.setFullName(schoolFullName);
            school.setSchoolId(Integer.parseInt(id));
            if (format == null || format.equals(""))
                format = "json";
            try {
                if (format == "json")
                    client.setDefaultFormat(Format.JSON);
                else if (format == "xml")
                    client.setDefaultFormat(Format.XML);
                client.updateResource(school);
            } catch (Exception e) {
                LOG.info(ERROR_MESSAGE);
                model.addAttribute("errorMessage", ERROR_MESSAGE);
            }
        }
        return "redirect:/spring/schoolmenu.html?action=list&format=" + format;
    }
}
