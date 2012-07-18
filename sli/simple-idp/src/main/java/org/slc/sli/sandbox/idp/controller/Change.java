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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;


@Controller
public class Change {
	
    private static final Logger LOG = LoggerFactory.getLogger(Login.class);

    @Autowired
    SamlAssertionService samlService;
    
    @Autowired
    UserService userService;

    @Autowired
    AuthRequestService authRequestService;

    /**
     * render change password form
     * @param encodedSamlRequest
     * @param realm
     * @param userId
     * @param httpSession
     * @param request
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public void changePassword(
    		@RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String realm,
            @RequestParam(value = "userId", required = false) String userId) {
    }
    
    /**
     * save changes from user form to update password
     * @param userId
     * @param oldPass
     * @param newPass
     * @param newConfirm
     * @param encodedSamlRequest
     * @param realm
     * @return ModelAndview
     */
    @RequestMapping(value = "/saveChanges", method = RequestMethod.POST)
    public ModelAndView saveChanges(
    		@RequestParam("userId") String userId, 
    		@RequestParam("old_pass") String oldPass,
    		@RequestParam("new_pass") String newPass,
    		@RequestParam("new_confirm") String newConfirm,
    		@RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam(value = "realm", required = false) String realm){
    	
    	ModelAndView mav;
    	User user;
    	String password = oldPass;
    	
    	try{
    		user = userService.authenticate(realm, userId, oldPass);
    	}
    	catch (AuthenticationException e){
    		mav = new ModelAndView("changePassword");
    		mav.addObject("msg", "Incorrect Old Password.");
            mav.addObject("SAMLRequest", encodedSamlRequest);
            mav.addObject("realm", realm);
            mav.addObject("userId", userId);
            return mav;
    	}
    	
    	try{    	
    		if(!(newPass.trim().equals(newConfirm.trim()))){
        		mav = new ModelAndView("changePassword");
        		mav.addObject("msg", "New Password does not match the confirmation");
                mav.addObject("SAMLRequest", encodedSamlRequest);
                mav.addObject("realm", realm);
                mav.addObject("userId", userId);
                return mav;
    		} else if(newPass.trim().equals(oldPass.trim())){
        		mav = new ModelAndView("changePassword");
        		mav.addObject("msg", "New password is identical to old password");
                mav.addObject("SAMLRequest", encodedSamlRequest);
                mav.addObject("realm", realm);
                mav.addObject("userId", userId);
                return mav;
    		}
    		
        	user.getAttributes().put("userPassword", newPass);
        	userService.updateUser(realm, user);
        	password = newPass;
    	}
    	catch(Exception e){
    		LOG.error("Failed to update user with new password.", e);
    	}
    	
    	mav = new ModelAndView("update");
        mav.addObject("SAMLRequest", encodedSamlRequest);
        mav.addObject("realm", realm);
        mav.addObject("userId", userId);
        mav.addObject("password", password);
    	return mav;
    }
}
