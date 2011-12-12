package org.slc.sli.controller;


import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.SpringPropertiesTest;


public class LoginController {


   
    private SpringPropertiesTest testClass;
    
    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Enter Username and password");
        model.addAttribute("errorMessage", "Invalid Username or password, please try again");
        model.addAttribute("displayError", "none");
        return "login";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView checkLogin(String username, String pwd, ModelMap model) {
        if (!(username.equals("sivan") || username.equals("david") || username.equals("sravan") || username.equals("cgray"))) {
            model.addAttribute("message", "Enter valid Username");
            model.addAttribute("errorMessage", "Invalid Username or password, please try again");
            model.addAttribute("displayError", "block");
            return new ModelAndView("login");
        }
        
        return new ModelAndView("redirect:appselector").addObject("username", username);
    }


    public SpringPropertiesTest getTestClass() {
        return testClass;
    }

    public void setTestClass(SpringPropertiesTest testClass) {
        this.testClass = testClass;
    }
    
}
