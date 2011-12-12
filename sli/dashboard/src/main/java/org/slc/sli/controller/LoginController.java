package org.slc.sli.controller;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String checkLogin(String username, String pwd, ModelMap model) {
        if (!(username.equals("sivan") || username.equals("david") || username.equals("sravan"))) {
            model.addAttribute("message", "Enter Username and password");
            model.addAttribute("errorMessage", "Invalid Username or password, please try again");
            model.addAttribute("displayError", "block");
            return "login";
        }
         
        return "redirect:appselector";
    }


    public SpringPropertiesTest getTestClass() {
        return testClass;
    }

    public void setTestClass(SpringPropertiesTest testClass) {
        this.testClass = testClass;
    }
    
}
