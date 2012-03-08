package org.slc.sli.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.util.StudentProgramUtil;
import org.slc.sli.util.Constants;


/**
 * Layout controller for all types of requests. 
 * NOTE: This controller was introduced after the student list and app selector controllers. 
 * 
 * @author dwu
 */
@Controller
@RequestMapping(value = "/service/layout/")
public class LayoutController extends GenericLayoutController {
    private static final String TABBED_ONE_COL = "tabbed_one_col";

    /**
     * Controller for student profile
     * @param panelIds
     * @return
     */
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public ModelAndView handleStudentProfile(@RequestParam String id) {
        ModelMap model = getPopulatedModel(Constants.VIEW_TYPE_STUDENT_PROFILE_PAGE, id);
        // TODO: get rid of StudentProgramUtil - instead enrich student entity with relevant programs 
        model.addAttribute("programUtil", new StudentProgramUtil());
        return getModelView(TABBED_ONE_COL, model);
    }
}
