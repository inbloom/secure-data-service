package org.slc.sli.sandbox.idp.controller;

import java.util.List;

import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
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
    SamlAssertionService samlService;
    
    @Autowired
    AuthRequestService authRequestService;
    
    @RequestMapping(value = "/selectUser", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("userId") String userId,
            @RequestParam("selected_roles") List<String> roles, @RequestParam("SAMLRequest") String encodedSamlRequest,
            @RequestParam("realm") String realm) {
        
        AuthRequestService.Request requestInfo = authRequestService.processRequest(encodedSamlRequest, realm);
        
        SamlAssertion samlAssertion = samlService.buildAssertion(userId, roles, null, requestInfo);
        ModelAndView mav = new ModelAndView("post");
        mav.addObject("samlAssertion", samlAssertion);
        return mav;
        
    }
}
