package org.slc.sli.sandbox.idp.controller;

import java.io.IOException;

import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
import org.slc.sli.sandbox.idp.service.RoleService;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    SamlAssertionService samlService;
    
    @Autowired
    RoleService roleService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    AuthRequestService authRequestService;
    
    @Value("${sli.simple-idp.sandboxImpersonationEnabled}")
    private boolean isSandboxImpersonationEnabled;
    
    void setSandboxImpersonationEnabled(boolean isSandboxImpersonationEnabled) {
        this.isSandboxImpersonationEnabled = isSandboxImpersonationEnabled;
    }
    
    /**
     * Loads required data and redirects to the login page view.
     * 
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView form(@RequestParam("SAMLRequest") String encodedSamlRequest, @RequestParam("realm") String realm) {
        ModelAndView mav = new ModelAndView("login");
        authRequestService.processRequest(encodedSamlRequest, realm);
        mav.addObject("SAMLRequest", encodedSamlRequest);
        mav.addObject("realm", realm);
        return mav;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestParam("userId") String userId, @RequestParam("password") String password,
            @RequestParam("SAMLRequest") String encodedSamlRequest, @RequestParam("realm") String realm) {
        
        AuthRequestService.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, realm);
        User user;
        try {
            user = userService.authenticate(realm, userId, password);
        } catch (AuthenticationException e) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("msg", "Invalid userId or password");
            mav.addObject("SAMLRequest", encodedSamlRequest);
            return mav;
        }
        
        if (realm.equals("SLIAdmin") || !isSandboxImpersonationEnabled) {
            SamlAssertion samlAssertion = samlService.buildAssertion(userId, user.getRoles(), user.getAttributes(), requestInfo);
            ModelAndView mav = new ModelAndView("post");
            mav.addObject("samlAssertion", samlAssertion);
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("selectUser");
            mav.addObject("SAMLRequest", encodedSamlRequest);
            mav.addObject("roles", roleService.getAvailableRoles());
            mav.addObject("realm", realm);
            return mav;
        }
        
    }
}
