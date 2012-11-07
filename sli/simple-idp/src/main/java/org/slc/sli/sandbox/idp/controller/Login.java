/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.sandbox.idp.controller;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.LoggingUtils;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.DefaultUsersService;
import org.slc.sli.sandbox.idp.service.DefaultUsersService.Dataset;
import org.slc.sli.sandbox.idp.service.DefaultUsersService.DefaultUser;
import org.slc.sli.sandbox.idp.service.RoleService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
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

    @Autowired
    DefaultUsersService defaultUsersService;
    
    @Value("${sli.simple-idp.sandboxImpersonationEnabled}")
    private boolean isSandboxImpersonationEnabled;

    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String sliAdminRealmName;


    @Value("${bootstrap.app.admin.url}")
    private String adminUrl;

    void setSandboxImpersonationEnabled(boolean isSandboxImpersonationEnabled) {
        this.isSandboxImpersonationEnabled = isSandboxImpersonationEnabled;
    }

    void setSliAdminRealmName(String name) {
        sliAdminRealmName = name;
    }


    /**
     * Loads required data and redirects to the login page view.
     *
     */
    @RequestMapping(value = "/logout")
    public ModelAndView logout(@RequestParam(value="SAMLRequest", required=false) String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String realm, HttpSession httpSession) {
        httpSession.setAttribute(USER_SESSION_KEY, null);
        if(encodedSamlRequest!=null){
            ModelAndView mav = form(encodedSamlRequest, realm, httpSession);
            mav.addObject("msg", "You are now logged out");
            return mav;
        }else{
            return new ModelAndView("loggedOut");
        }
            
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

        if (user != null){
            if(isSandboxImpersonationEnabled){
                if(isAdminRealm(realm)){
                    LOG.debug("Sandbox Admin Login request with existing session (" + user.getUserId() + ") skipping authentication");
                    return handleNoAuthRequired(user, requestInfo);
                }else if(requestInfo.isForceAuthn() || user.getImpersonationUser()==null){
                    LOG.debug("Sandbox Impersonation Login request with existing session (" + user.getUserId() + ") skipping authentication, going to impersonation");
                    return buildImpersonationModelAndView(realm, encodedSamlRequest, "");
                }else{
                    LOG.debug("Sandbox impersonation Login request with existing session (" + user.getUserId() + ") skipping authentication and impersonation and using: " + user.getImpersonationUser().getUserId());
                    return handleNoAuthRequired(user.getImpersonationUser(), requestInfo);
                }
            }else if (!requestInfo.isForceAuthn()) {
                LOG.debug("Login request with existing session, skipping authentication");
                return handleNoAuthRequired(user, requestInfo);
            }else{
                httpSession.setAttribute(USER_SESSION_KEY, null);
            }
        }

        boolean isForgotPasswordVisible = false;
        if (user == null && (isAdminRealm(realm) || (realm == null||realm.length()==0)) ) {
            isForgotPasswordVisible = true;
        }

        ModelAndView mav = new ModelAndView("login");
        mav.addObject("subTitle", buildSubTitle(realm));
        mav.addObject("isSandbox", isSandboxImpersonationEnabled && (realm == null || realm.length() == 0));
        mav.addObject("SAMLRequest", encodedSamlRequest);
        mav.addObject("adminUrl", adminUrl);
        mav.addObject("realm", realm);
        mav.addObject("isForgotPasswordVisible", isForgotPasswordVisible);
        return mav;
    }
    
    private ModelAndView handleNoAuthRequired(User user, AuthRequestService.Request requestInfo){
        SamlAssertion samlAssertion = samlService.buildAssertion(user.getUserId(), user.getRoles(),
                user.getAttributes(), requestInfo);
        ModelAndView mav = new ModelAndView("post");
        mav.addObject("samlAssertion", samlAssertion);
        return mav;
    }
    
    private String buildSubTitle(String realm) {
        if(sliAdminRealmName.equals(realm)){
            return "";
        }else if(isSandboxImpersonationEnabled){
            return "Application Developer Sandbox";
        }else{
            return "Mock IDP for "+ realm;
        }
    }

    private boolean isAdminRealm(String realm) {
        return realm != null && sliAdminRealmName != null && realm.equals(sliAdminRealmName);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(
            @RequestParam("user_id") String userId,
            @RequestParam("password") String password,
            @RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String incomingRealm,
            @RequestParam(value = "isForgotPasswordVisible", required = false) boolean isForgotPasswordVisible,
            HttpSession httpSession,
            HttpServletRequest request) {

        String realm = incomingRealm;
        boolean doImpersonation = false;
        if (isSandboxImpersonationEnabled && (incomingRealm == null || incomingRealm.length() == 0)) {
            doImpersonation = true;
            realm = sliAdminRealmName;
        }

        AuthRequestService.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, incomingRealm);

        User user = (User) httpSession.getAttribute(USER_SESSION_KEY);

        if(user == null){
            try {
                user = userService.authenticate(realm, userId, password);
                if (shouldForcePasswordChange(user, incomingRealm)) {
    
                    //create timestamp as part of resetKey for user
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
    
                    SecureRandom sRandom = new SecureRandom();
                    byte[] bytes = new byte[20];
                    sRandom.nextBytes(bytes);
    
                    StringBuilder sb = new StringBuilder();
                    for (byte bt : bytes) { sb.append((char) bt); }
    
                    String token = sb.toString() + user.getAttributes().get("mail");
    
                    PasswordEncoder pe = new Md5PasswordEncoder();
                    String hashedToken = pe.encodePassword(token, null);
    
                    String resetKey = hashedToken + "@" + (ts.getTime() / 1000);
    
                    userService.updateUser(realm, user, resetKey, password);
                    ModelAndView mav = new ModelAndView("forcePasswordChange");
                    String resetUri = adminUrl + "/resetPassword";
                    mav.addObject("resetUri", resetUri);
                    mav.addObject("key", hashedToken);
                    return mav;
                }
            } catch (AuthenticationException e) {
                ModelAndView mav = new ModelAndView("login");
                mav.addObject("subTitle", buildSubTitle(realm));
                mav.addObject("errorMsg", "Invalid User Name or password");
                mav.addObject("SAMLRequest", encodedSamlRequest);
                mav.addObject("realm", incomingRealm);
                mav.addObject("isForgotPasswordVisible", isForgotPasswordVisible);
                mav.addObject("adminUrl", adminUrl);
    
                // if a user with this userId exists, get his info and roles/groups and
                // log that information as a failed login attempt.
                String edOrg = "UnknownEdOrg";
                String tenant = "UnknownTenant";
                List<String> userRoles = Collections.emptyList();
                try {
                    User unauthenticatedUser = userService.getUser(realm, userId);
                    if (unauthenticatedUser != null) {
                        Map<String, String> attributes = unauthenticatedUser.getAttributes();
                        if (attributes != null) {
                            edOrg = attributes.get("edOrg");
                            tenant = attributes.get("tenant");
                        }
                    }
                    userRoles = userService.getUserGroups(realm, userId);
                } catch (EmptyResultDataAccessException noMatchesException) {
                    LOG.info(userId + " failed to login into realm [" + realm + "]. User does not exist.");
                } catch (Exception exception) {
                    LOG.info(userId + " failed to login into realm [" + realm + "]. " + exception.getMessage());
                }
                writeLoginSecurityEvent(false, userId, userRoles, edOrg, tenant, request);
                return mav;
            }
        }
        
        httpSession.setAttribute(USER_SESSION_KEY, user);
        writeLoginSecurityEvent(true, user.getUserId(), user.getRoles(), user.getAttributes().get("edOrg"), user.getAttributes().get("tenant"), request);
        
        if (doImpersonation) {
            ModelAndView mav =  buildImpersonationModelAndView(incomingRealm, encodedSamlRequest, "");
            return mav;
        } else {
            SamlAssertion samlAssertion = samlService.buildAssertion(user.getUserId(), user.getRoles(),
                    user.getAttributes(), requestInfo);
            
            ModelAndView mav = new ModelAndView("post");
            mav.addObject("samlAssertion", samlAssertion);
            return mav;
            
        }
    }

    private ModelAndView buildImpersonationModelAndView(String realm, String saml, String impersonateUserName) {
        ModelAndView mav = new ModelAndView("impersonate");
        mav.addObject("SAMLRequest", saml);
        mav.addObject("realm", realm);
        mav.addObject("impersonate_user", impersonateUserName);
        mav.addObject("roles", roleService.getAvailableRoles());
        List<Dataset> datasets = defaultUsersService.getAvailableDatasets();
        mav.addObject("datasets", datasets);
        for(Dataset dataset : datasets){
            mav.addObject(dataset.getKey(), defaultUsersService.getUsers(dataset.getKey()));
       }
        return mav;
    }

    @RequestMapping(value = "/impersonate", method = RequestMethod.POST)
    public ModelAndView impersonate(
            @RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String realm,
            @RequestParam(value = "impersonate_user", required = false) String impersonateUser,
            @RequestParam(value = "selected_roles", required = false) List<String> roles,
            @RequestParam(value = "customRoles", required = false) String customRoles,
            @RequestParam(value = "datasets", required = false) String dataset,
            @RequestParam(value = "userList", required = false) String datasetUser,
            @RequestParam(value = "manualConfig", required = false) boolean manualConfig,
            HttpSession httpSession,
            HttpServletRequest request) {
        
        if (manualConfig && customRoles != null) {
            List customRolesList = Arrays.asList(customRoles.trim().split("\\s*,\\s*"));
            if (roles != null) {
                roles.addAll(customRolesList);
            } else {
                roles = customRolesList;
            }
        }
        
        User impersonationUser = new User();
        
        if(!manualConfig){
            DefaultUser defaultUser = defaultUsersService.getUser(dataset, datasetUser);
            if(defaultUser!=null){
                impersonationUser.setUserId(defaultUser.getUserId());
                impersonationUser.setRoles(Arrays.asList(defaultUser.getRole()));
            }
        }
        
        if(impersonationUser.getUserId()==null){
            if (roles == null || roles.size() == 0) {
                ModelAndView mav = buildImpersonationModelAndView(realm, encodedSamlRequest, impersonateUser);
                mav.addObject("errorMsg", "Please select or enter one role to impersonate.");
                return mav;
            } 
            impersonationUser.setUserId(impersonateUser);
            impersonationUser.setRoles(roles);
        }
        
        User user = (User) httpSession.getAttribute(USER_SESSION_KEY);
        
        String tenant = user.getAttributes().get("tenant");
        if (tenant == null || tenant.length() == 0) {
            ModelAndView mav = buildImpersonationModelAndView(realm, encodedSamlRequest, impersonateUser);
            mav.addObject("errorMsg", "User account not properly configured for impersonation.");
            return mav;
        }
        impersonationUser.getAttributes().put("tenant", tenant);

        user.setImpersonationUser(impersonationUser);
        
        AuthRequestService.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, realm);
        SamlAssertion samlAssertion = samlService.buildAssertion(impersonationUser.getUserId(), impersonationUser.getRoles(),
                impersonationUser.getAttributes(), requestInfo);

        writeLoginSecurityEvent(true, user.getUserId(), user.getRoles(), user.getAttributes().get("edOrg"), user.getAttributes().get("tenant"), request);

        ModelAndView mav = new ModelAndView("post");
        mav.addObject("samlAssertion", samlAssertion);
        return mav;
    }
    
    private void writeLoginSecurityEvent(boolean successful, String userId, List<String> roles, String edOrg,
            String tenant, HttpServletRequest request) {
        SecurityEvent event = new SecurityEvent();

        event.setUser(userId);
        event.setTargetEdOrg(edOrg);
        event.setRoles(roles);
        event.setTenantId(tenant);

        try {
            event.setExecutedOn(LoggingUtils.getCanonicalHostName());
        } catch (RuntimeException e) {
            event.setLogLevel(LogLevelType.TYPE_TRACE);
            event.setLogMessage("Runtime exception: " + e.getLocalizedMessage() + " " + edOrg + " by " + userId + ".");
        }

        if (request != null) {
            event.setActionUri(request.getRequestURL().toString());
            event.setUserOrigin(request.getRemoteHost());
        }

        if (successful) {
            event.setLogLevel(LogLevelType.TYPE_INFO);
            event.setLogMessage("Successful login to " + edOrg + " by " + userId + ".");
        } else {
            event.setLogLevel(LogLevelType.TYPE_ERROR);
            event.setLogMessage("Failed login to " + edOrg + " by " + userId + ".");
        }

        audit(event);
    }

    private boolean shouldForcePasswordChange(User user, String incomingRealm) {
        if (incomingRealm == null || !incomingRealm.equals(sliAdminRealmName) || user == null) {
            return false;
        }

        if (user.getAttributes().get("emailToken").trim().length() == 0) {
            return true;
        }
        return false;
    }
}
