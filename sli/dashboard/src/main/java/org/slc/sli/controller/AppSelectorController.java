package org.slc.sli.controller;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


/**
 * 
 * TODO: Write Javadoc
 *
 */
@Controller
@RequestMapping({"/", "/appselector" })
public class AppSelectorController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView returnApps(ModelMap model) {
        model.addAttribute("message", "Select an application");
        HashMap<String, String> appToUrlMap = new HashMap<String, String>();

        //TODO: Retrieve the applications from a service
        appToUrlMap.put("Dashboard", "studentlist");
        appToUrlMap.put("Learning Map", "/fakeapp");
        appToUrlMap.put("Administration Tools", "/fakeapp");
        model.addAttribute("appToUrl", appToUrlMap);
        return new ModelAndView("SelectApp");
    }
}
