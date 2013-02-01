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


package org.slc.sli.dashboard.unit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slc.sli.dashboard.manager.PortalWSManager;
import org.slc.sli.dashboard.manager.impl.PortalWSManagerImpl;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.web.controller.ErrorController;
import org.slc.sli.dashboard.web.controller.ErrorController.ErrorDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * ErrorController JUnit test.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ErrorController.class })
public class ErrorControllerTest {
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ErrorController errorController;
    private PortalWSManager portalWSManager;
    
    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        errorController = PowerMockito.spy(new ErrorController());
        portalWSManager = PowerMockito.spy(new PortalWSManagerImpl());
        errorController.setPortalWSManager(portalWSManager);
    }
    
    @Test
    public void testHandleError() throws Exception {
        
        ModelMap model = new ModelMap();
        
        String errorType = "default";
        ModelAndView modelAndView = errorController.handleError(errorType, model, request, response);
        
        assertEquals(Constants.OVERALL_CONTAINER_PAGE, modelAndView.getViewName());
        String errorHeading = (String) modelAndView.getModel().get(Constants.ATTR_ERROR_HEADING);
        String errorContent = (String) modelAndView.getModel().get(Constants.ATTR_ERROR_CONTENT);
        String errorPage = (String) modelAndView.getModel().get(Constants.PAGE_TO_INCLUDE);
        assertEquals(errorPage, ErrorController.TEMPLATE_FILE);
        assertNotNull(errorHeading);
        assertEquals(ErrorDescriptor.DEFAULT.getHeading(), errorHeading);
        assertNotNull(errorContent);
        assertEquals(ErrorDescriptor.DEFAULT.getContent(), errorContent);
        
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testHandleTest() throws Exception {
        errorController.handleTest(new ModelMap());
    }
    
}
