package org.slc.sli.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.web.entity.SafeUUID;

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
     * Generic layout component that takes component id and uid of the data entity
     * @param componentId
     * @param id
     * @param request
     * @return
     */
    // The path variable validation for id is simplified since spring doesn't seem to support exact length regex
    @RequestMapping(value = "/service/layout/{componentId:[a-zA-Z0-9]+}/{id:[a-f0-9-]+}", method = RequestMethod.GET)
    public ModelAndView handleWithId(
            @PathVariable String componentId, @PathVariable SafeUUID id, HttpServletRequest request
    ) {
        return getModelAndView(componentId, id, request);
    }

    /**
     * Generic layout handler
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/layout/{componentId:[a-zA-Z0-9]+}", method = RequestMethod.GET)
    public ModelAndView handle(
            @PathVariable String componentId, @Valid SafeUUID id, HttpServletRequest request
    ) {
        return getModelAndView(componentId, id, request);
    }

    /**
     * Handles the "/" url by redirecting to list of students
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView handleLos(HttpServletRequest request) {
        return getModelAndView(LOS_ID, null, request);
    }

    /**
     * Handles the short los url by redirecting to list of students
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/list/{id:[a-f0-9-]+}", method = RequestMethod.GET)
    public ModelAndView handleLos(@PathVariable SafeUUID id, HttpServletRequest request) {
        return getModelAndView(LOS_ID, id, request);
    }

    private ModelAndView getModelAndView(String componentId, SafeUUID id, HttpServletRequest request) {
        return getModelView(TABBED_ONE_COL, getPopulatedModel(componentId, id == null ? null : id.getId(), request));
    }
}
