package org.slc.sli.sandbox.idp.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.Users;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Handles login form submissions.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Controller
public class Login {
    
    @Autowired
    LoginService service;
    
    @Autowired
    Users users;
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public View login(@RequestParam("selected_user") String userId, @RequestParam("selected_roles") List<String> roles,
            HttpSession session) {
        
        AuthRequests.Request reqInfo = (AuthRequests.Request) session.getAttribute(Form.REQUEST_INFO);
        User user = users.getUser(reqInfo.getTenant(), userId);
        
        return new RedirectView(service.login(user, roles, reqInfo).toString());
    }
}
