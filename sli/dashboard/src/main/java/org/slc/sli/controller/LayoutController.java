package org.slc.sli.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.util.StudentProgramUtil;
import org.slc.sli.manager.PortalWSManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;

/**
 * Layout controller for all types of requests.
 * NOTE: This controller was introduced after the student list and app selector controllers.
 * 
 * @author dwu
 */
@Controller
//@RequestMapping(value = "/service/layout/")
public class LayoutController extends GenericLayoutController {
    private static final String TABBED_ONE_COL = "tabbed_one_col";
    
    @Autowired
    PortalWSManager portalWSManager;
    
    public void setPortalWSManager(PortalWSManager portalWSManager) {
        this.portalWSManager = portalWSManager;
    }
    
    /**
     * Controller for student profile
     * 
     * @param panelIds
     * @return
     */
    @RequestMapping(value = "/service/layout/student", method = RequestMethod.GET)
    public ModelAndView handleStudentProfile(@RequestParam String id, HttpServletRequest request) {
        ModelMap model = getPopulatedModel("studentProfile", id, request);
        // TODO: get rid of StudentProgramUtil - instead enrich student entity with relevant
        // programs
        model.addAttribute("programUtil", new StudentProgramUtil());
        model.addAttribute(Constants.ATTR_HEADER_STRING, portalWSManager.getHeader(SecurityUtil.getToken()));
        
        // Currently portal returns overlapping div names
        // TODO: Remove hack to override div_main
        model.addAttribute(Constants.ATTR_FOOTER_STRING, portalWSManager.getFooter(SecurityUtil.getToken())
                .replaceFirst("div_main", "div_footer"));
        return getModelView(TABBED_ONE_COL, model);
    }
    
    /**
     * Generic layout handler
     * 
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/layout/{componentId}", method = RequestMethod.GET)
    public ModelAndView handleListOfStudents(@PathVariable String componentId,
            @RequestParam(required = false) String id, HttpServletRequest request) {
        ModelMap model = getPopulatedModel(componentId, id, request);
        model.addAttribute(Constants.ATTR_HEADER_STRING, portalWSManager.getHeader(SecurityUtil.getToken()));
        model.addAttribute(Constants.ATTR_FOOTER_STRING, portalWSManager.getFooter(SecurityUtil.getToken())
                .replaceFirst("div_main", "div_footer"));
        return getModelView(TABBED_ONE_COL, model);
    }
    
    /**
     * Handles the "/" url by redirecting to list of students
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String handleHome(HttpServletRequest request) {
        return "redirect:/service/layout/listOfStudentsPage";
    }
    
}
