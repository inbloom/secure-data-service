package org.slc.sli.sandbox.idp.controller;

import java.util.List;

import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.LoginService.SamlResponse;
import org.slc.sli.sandbox.idp.service.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Prepares data needed to display the login form.
 */
@Controller
public class SelectUser {
    
    @Autowired
    Users userService;
    
    @Autowired
    LoginService service;
    
    @Autowired
    AuthRequests authRequestService;
    
    @RequestMapping(value = "/selectUser", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("userId") String userId, @RequestParam("selected_roles") List<String> roles,
            @RequestParam(value = "SAMLRequest", required = false) String encodedSamlRequest,
            @RequestParam("tenant") String tenant) {
        
        AuthRequests.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, tenant);
        
        SamlResponse samlResponse = service.login(userId, roles, requestInfo);
        ModelAndView mav = new ModelAndView("post");
        mav.addObject("samlResponse", samlResponse);
        return mav;

    }
}
