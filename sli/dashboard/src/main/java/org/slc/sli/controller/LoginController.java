package org.slc.sli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/login")
public class LoginController {

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Enter Username or password");
        return "login";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String checkLogin(String username, String pwd, ModelMap model) {
        if (!(username.equals("sivan") || username.equals("david") || username.equals("sravan"))) {
            model.addAttribute("message", "Enter valid Username");
            return "login";
        }
        return "redirect:appselector";
        
    }
    
}
