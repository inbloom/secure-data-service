package org.slc.sli.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Layout controller for all types of requests.
 * NOTE: This controller was introduced after the student list and app selector controllers.
 *
 * @author dwu
 */
@Controller
public class LayoutController extends GenericLayoutController {
    private static final String TABBED_ONE_COL = "tabbed_one_col";
    private static final String LOS_ID = "listOfStudentsPage";
    /**
     * Generic layout handler
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/layout/{componentId}/{id}", method = RequestMethod.GET)
    public ModelAndView handleWithId(@PathVariable String componentId, @PathVariable String id, HttpServletRequest request) {
        return handle(componentId, id, request);
    }
    
    
    /**
     * Controller for student search 
    */ 
    @RequestMapping(value = "/service/layout/studentSearchPage", method = RequestMethod.GET)
    public ModelAndView searchForStudent(String firstName, String lastName, HttpServletRequest request) {
       ModelMap model = getPopulatedModel("studentSearchPage", new String[]{firstName, lastName}, request);
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
    public ModelAndView handle(@PathVariable String componentId, @RequestParam(required = false) String id, HttpServletRequest request) {
        return getModelView(TABBED_ONE_COL, getPopulatedModel(componentId, id, request));
    }

    /**
     * Handles the "/" url by redirecting to list of students
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView handleLos(HttpServletRequest request) {
        return handle(LOS_ID, null, request);
    }
}
