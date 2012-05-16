package org.slc.sli.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.web.entity.StudentSearch;

/**
 * Layout controller for search.
 * NOTE: This controller was introduced after the student list and app selector controllers.
 *
 * @author dwu
 */
@Controller
public class SearchController extends GenericLayoutController {
    private static final String SEARCH_RESULTS = "search_results";

    /**
     * Controller for student search
    */
    @RequestMapping(value = "/service/layout/studentSearchPage", method = RequestMethod.GET)
    public ModelAndView handle(
            @Valid StudentSearch studentSearch,
            HttpServletRequest request) {
        return getModelView(SEARCH_RESULTS, getPopulatedModel("studentSearchPage", studentSearch.get(), request));
    }
}
