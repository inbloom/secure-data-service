package org.slc.sli.controller;

import java.io.IOException;

import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * TODO: Write Javadoc
 *
 */
public class LandingPageController {

    private String url; // url to redirect to

    // accessors
    public void setUrl(String s) { url = s; } 
    public String getUrl() { return url; } 
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView retrieveStudentList(String username, ModelMap model) throws IOException {
        return new ModelAndView(new RedirectView(getUrl()));
    }
    
}
