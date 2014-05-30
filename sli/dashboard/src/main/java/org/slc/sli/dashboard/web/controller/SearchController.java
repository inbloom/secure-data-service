package org.slc.sli.dashboard.web.controller;

import org.slc.sli.dashboard.web.entity.StudentSearch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Layout controller for search.
 * NOTE: This controller was introduced after the student list and app selector controllers.
 *
 * @author dwu
 */
@Controller
@RequestMapping(value = {"/s/l"})
public class SearchController extends LayoutController {

    private static final String SEARCH_RESULTS = "search_results";

    /**
     * Controller for student search
     */
    @RequestMapping(value = "/studentSearch", method = RequestMethod.GET)
    public ModelAndView handle(@Valid StudentSearch studentSearch, HttpServletRequest request) {
        return getModelView(SEARCH_RESULTS, getPopulatedModel("studentSearch", studentSearch.get(), request));
    }

}
