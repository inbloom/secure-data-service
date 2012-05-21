package org.slc.sli.sandbox.idp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.LoggingUtils;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.RoleService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(Login.class);
    
    private static final String USER_SESSION_KEY = "user_session_key";
    
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
    
    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String sliAdminRealmName;
    
    void setSandboxImpersonationEnabled(boolean isSandboxImpersonationEnabled) {
        this.isSandboxImpersonationEnabled = isSandboxImpersonationEnabled;
    }
    
    void setSliAdminRealmName(String name) {
        this.sliAdminRealmName = name;
    }
    
    /**
     * Loads required data and redirects to the login page view.
     * 
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView form(@RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String realm, HttpSession httpSession) {
        
        AuthRequestService.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, realm);
        
        User user = (User) httpSession.getAttribute(USER_SESSION_KEY);
        
        if (user != null && !requestInfo.isForceAuthn()) {
            LOG.debug("Login request with existing session, skipping authentication");
            SamlAssertion samlAssertion = samlService.buildAssertion(user.getUserId(), user.getRoles(),
                    user.getAttributes(), requestInfo);
            ModelAndView mav = new ModelAndView("post");
            mav.addObject("samlAssertion", samlAssertion);
            return mav;
        }
        
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("SAMLRequest", encodedSamlRequest);
        if (isSandboxImpersonationEnabled && (realm == null || realm.length() == 0)) {
            realm = null;
            mav.addObject("is_sandbox", true);
            mav.addObject("roles", roleService.getAvailableRoles());
        } else {
            mav.addObject("is_sandbox", false);
        }
        mav.addObject("realm", realm);
        return mav;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("user_id") String userId, @RequestParam("password") String password,
            @RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String incomingRealm,
            @RequestParam(value = "impersonate_user", required = false) String impersonateUser,
            @RequestParam(value = "selected_roles", required = false) List<String> roles, HttpSession httpSession,
            HttpServletRequest request) {
        
        String realm = incomingRealm;
        boolean doImpersonation = false;
        if (isSandboxImpersonationEnabled && (incomingRealm == null || incomingRealm.length() == 0)) {
            doImpersonation = true;
            realm = sliAdminRealmName;
        }
        
        AuthRequestService.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, incomingRealm);
        
        User user;
        try {
            user = userService.authenticate(realm, userId, password);
        } catch (AuthenticationException e) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("msg", "Invalid User Name or password");
            mav.addObject("SAMLRequest", encodedSamlRequest);
            mav.addObject("realm", incomingRealm);
            if (doImpersonation) {
                mav.addObject("is_sandbox", true);
                mav.addObject("impersonate_user", impersonateUser);
                mav.addObject("roles", roleService.getAvailableRoles());
            } else {
                mav.addObject("is_sandbox", false);
            }
            writeLoginSecurityEvent(false, userId, realm, request);
            return mav;
        }
        
        if (doImpersonation) {
            user.setUserId(impersonateUser);
            user.setRoles(roles);
            // only send the tenant - no other values since this is impersonatation
            String tenant = user.getAttributes().get("tenant");
            user.getAttributes().clear();
            user.getAttributes().put("tenant", tenant);
        }
        SamlAssertion samlAssertion = samlService.buildAssertion(user.getUserId(), user.getRoles(),
                user.getAttributes(), requestInfo);
        
        writeLoginSecurityEvent(true, userId, realm, request);
        
        httpSession.setAttribute(USER_SESSION_KEY, user);
        
        ModelAndView mav = new ModelAndView("post");
        mav.addObject("samlAssertion", samlAssertion);
        return mav;
        
    }
    
    private void writeLoginSecurityEvent(boolean successful, String userId, String realm, HttpServletRequest request) {
        SecurityEvent event = new SecurityEvent();
        
        event.setUser(userId);
        event.setTargetEdOrg(realm);
        
        try {
            event.setExecutedOn(LoggingUtils.getCanonicalHostName());
        } catch (RuntimeException e) {
        }
        
        if (request != null) {
            event.setActionUri(request.getRequestURI());
            event.setUserOrigin(request.getRemoteHost());
        }
        
        if (successful) {
            event.setLogLevel(LogLevelType.TYPE_INFO);
            event.setLogMessage("Successful login to " + realm + " by " + userId + ".");
        } else {
            event.setLogLevel(LogLevelType.TYPE_ERROR);
            event.setLogMessage("Failed login to " + realm + " by " + userId + ".");
        }
        
        audit(event);
    }
}
