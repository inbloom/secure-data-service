/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.dashboard.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.dashboard.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Error controller to handle SLI exception scenarios and provide developer information.
 * 
 * @author rbloh
 */
@Controller
public class ErrorController extends GenericLayoutController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);
    
    public static final String EXCEPTION_URL = "/exception";
    public static final String TEST_EXCEPTION_URL = "/testException";
    public static final String TEMPLATE_NAME = "error";
    public static final String TEMPLATE_FILE = "error.ftl";
    public static final String URL_PARAM_ERROR_TYPE = "errorType";
    
    /**
     * Controller for SLI Exceptions
     * 
     * @param errorType
     *            - SLI Error type (enumerated type used to lookup error message specifics)
     * @param model
     *            - Freemarker model map
     * @param response
     *            - HttpServletResponse for testability purposes
     * @return ModelAndView
     */
    @RequestMapping(value = EXCEPTION_URL, method = RequestMethod.GET)
    public ModelAndView handleError(@RequestParam(value = URL_PARAM_ERROR_TYPE, required = false) String errorType,
            ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        
        Map<String, String> exceptionMap = new HashMap<String, String>();
        
        ErrorDescriptor error = ErrorDescriptor.DEFAULT;
        if (errorType != null) {
            ErrorDescriptor specificError = ErrorDescriptor.findByType(errorType);
            if (specificError != null) {
                error = specificError;
            }
        }
        
        model.addAttribute(Constants.ATTR_ERROR_HEADING, error.getHeading());
        model.addAttribute(Constants.ATTR_ERROR_CONTENT, error.getContent());
        
        addCommonData(model, request);
        
        model.addAttribute(Constants.PAGE_TO_INCLUDE, TEMPLATE_FILE);
        return new ModelAndView(Constants.OVERALL_CONTAINER_PAGE, model);
    }
    
    @RequestMapping(value = TEST_EXCEPTION_URL, method = RequestMethod.GET)
    public ModelAndView handleTest(ModelMap model) throws IllegalArgumentException {
        throw new IllegalArgumentException("Test Exception");
    }
    
    /**
     * SLI Error Descriptors
     * 
     */
    public static enum ErrorDescriptor {
        DEFAULT("default", "ERROR", "We're sorry, the page that you were looking for could not be found."), HTTP_403(
                "403", "Page Not Accessible", "The page you are requesting is not available."), HTTP_404("404",
                "Page Not Found", "The page you are requesting is no longer available."), HTTP_500("500",
                "Down for Maintenance",
                "We're sorry, access to this site is temporarily unavailable as we upgrade its features."), EXCEPTION(
                "exception", "Exception occurred",
                "We're sorry, an exception has occurred.  Please inspect the developer logs for a detailed explanation.");
        
        private final String type;
        private final String heading;
        private final String content;
        
        public static ErrorDescriptor findByType(String type) {
            for (ErrorDescriptor v : values()) {
                if (v.getType().equals(type)) {
                    return v;
                }
            }
            return null;
        }
        
        ErrorDescriptor(String type, String heading, String content) {
            this.type = type;
            this.heading = heading;
            this.content = content;
        }
        
        public String getType() {
            return type;
        }
        
        public String getHeading() {
            return heading;
        }
        
        public String getContent() {
            return content;
        }
        
    }
    
}
