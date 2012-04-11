package org.slc.sli.sandbox.idp.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.Roles;
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
public class Form {
    
    protected static final String REQUEST_INFO = "request_info";
    
    @Autowired
    Roles roleService;
    
    @Autowired
    Users userService;
    
    @Autowired
    AuthRequests tenantService;
    
    /**
     * Loads required data and redirects to the login page view.
     * 
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView form(@RequestParam(value = "SAMLRequest", required = false) String encodedSamlRequest,
            @RequestParam("tenant") String tenantId, HttpSession session) throws IOException {
        ModelAndView mav = new ModelAndView("form");
        AuthRequests.Request requestInfo = tenantService.processRequest(encodedSamlRequest, tenantId);
        if (requestInfo == null) {
            throw new RuntimeException("No valid SAMLRequest found.");
        }
        String tenant = requestInfo.getTenant();
        session.setAttribute(REQUEST_INFO, requestInfo);
        
        mav.addObject("tenant", tenant);
        mav.addObject("users", userService.getAvailableUsers(tenant));
        mav.addObject("roles", roleService.getAvailableRoles());
        return mav;
    }
}
