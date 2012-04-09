package org.slc.sli.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.util.StudentProgramUtil;


/**
 * Layout controller for all types of requests. 
 * NOTE: This controller was introduced after the student list and app selector controllers. 
 * 
 * @author dwu
 */
@Controller
@RequestMapping(value = "/service/data/")
public class DataController extends GenericLayoutController {
    private static final String TABBED_ONE_COL = "tabbed_one_col";

    /**
     * Controller for list of students
     * 
     */
    @RequestMapping(value = "/studentlist", method = RequestMethod.GET)
    public ModelAndView handleStudentList(@RequestParam String sectionId, HttpServletRequest request) {
        ModelMap model = getPopulatedModel("listOfStudents", sectionId, request);
        // TODO: get rid of StudentProgramUtil - instead enrich student entity with relevant programs 
        model.addAttribute("programUtil", new StudentProgramUtil());
        return getModelView("data_dump", model);
    }
    
}
