package org.slc.sli.sandbox.idp.controller;

import java.io.IOException;

import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.LoginService.SamlResponse;
import org.slc.sli.sandbox.idp.service.Roles;
import org.slc.sli.sandbox.idp.service.Users;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
    Roles roleService;
    
    @Autowired
    Users userService;
    
    @Autowired
    AuthRequests authRequestService;
    
    /**
     * Loads required data and redirects to the login page view.
     * 
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView form(@RequestParam(value = "SAMLRequest", required = false) String encodedSamlRequest,
            @RequestParam("tenant") String tenant) throws IOException {
        ModelAndView mav = new ModelAndView("login");
        authRequestService.processRequest(encodedSamlRequest, tenant);
        mav.addObject("SAMLRequest", encodedSamlRequest);
        mav.addObject("tenant", tenant);
        return mav;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestParam("userId") String userId, @RequestParam("password") String password,
            @RequestParam(value = "SAMLRequest", required = false) String encodedSamlRequest,
            @RequestParam("tenant") String tenant) {
        
        AuthRequests.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, tenant);
        User user;
        try {
            user = userService.authenticate(tenant, userId, password);
        } catch (AuthenticationException e) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("msg", "Invalid userId or password");
            mav.addObject("SAMLRequest", encodedSamlRequest);
            return mav;
        }
        
        if (tenant.equals("SLI")) {
            SamlResponse samlResponse = service.login(user, user.getRoles(), requestInfo);
            ModelAndView mav = new ModelAndView("post");
            mav.addObject("samlResponse", samlResponse);
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("selectUser");
            mav.addObject("SAMLRequest", encodedSamlRequest);
            mav.addObject("roles", roleService.getAvailableRoles());
            mav.addObject("tenant", tenant);
            return mav;
        }
        
    }
}
