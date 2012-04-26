package org.slc.sli.sandbox.idp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.Roles;
import org.slc.sli.sandbox.idp.service.Users;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Handles login form submissions.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Controller
public class Login {
    
    protected static final String REQUEST_INFO = "request_info";
    @Autowired
    LoginService service;
 
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
        ModelAndView mav = new ModelAndView("login");
        AuthRequests.Request requestInfo = tenantService.processRequest(encodedSamlRequest, tenantId);
        if (requestInfo == null) {
            throw new RuntimeException("No valid SAMLRequest found.");
        }
        String tenant = requestInfo.getTenant();
        session.setAttribute(REQUEST_INFO, requestInfo);
        
        mav.addObject("tenant", tenant);
        return mav;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestParam("userId") String userId, @RequestParam("password") String password,
            HttpSession session, HttpServletResponse response) {
        
        AuthRequests.Request reqInfo = (AuthRequests.Request) session.getAttribute(REQUEST_INFO);
        String tenant = reqInfo.getTenant();
        User user = userService.authenticate(tenant, userId, password);
        
        if(user==null){
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("msg", "Invalid userId or password");
            return mav;
        }else{
            if(tenant.equals("SLI")){
                return new RedirectView(service.login(user, user.getRoles(), reqInfo).toString());
            }else{
                ModelAndView mav = new ModelAndView("selectUser");
                mav.addObject("roles", roleService.getAvailableRoles());
                mav.addObject("tenant", tenant);
                return mav;
            }
        }
        
    }
}
