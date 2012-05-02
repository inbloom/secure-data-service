package org.slc.sli.unit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.web.controller.ErrorController;

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
    
    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        errorController = new ErrorController();
    }
    
    @Test
    public void testHandleError() throws Exception {
        
        ModelMap model = new ModelMap();
        ErrorController errorController = PowerMockito.spy(new ErrorController());
        
        String exceptionLabel = "SLI Exception";
        String exceptionMessageType = "SLI Exception Message";
        String exceptionMessage = "SLI Exception Details";
        Throwable exceptionCause = null;
        ModelAndView modelAndView = errorController.handleError(exceptionLabel, exceptionMessageType, exceptionMessage,
                null, model, response);
        
        assertEquals(ErrorController.TEMPLATE_NAME, modelAndView.getViewName());
        Map exceptionMap = (Map) modelAndView.getModel().get(ErrorController.MODEL_ATTR_EXCEPTION);
        assertNotNull(exceptionMap);
        assertEquals(exceptionLabel, exceptionMap.get(ErrorController.MODEL_ATTR_EXCEPTION_LABEL));
        assertEquals(exceptionMessageType, exceptionMap.get(ErrorController.MODEL_ATTR_EXCEPTION_MESSAGE_TYPE));
        assertEquals(exceptionMessage, exceptionMap.get(ErrorController.MODEL_ATTR_EXCEPTION_MESSAGE));
        
    }
    
    @Test
    public void testHandleTest() throws Exception {
        
        ModelMap model = new ModelMap();
        ErrorController errorController = PowerMockito.spy(new ErrorController());
        
        try {
            ModelAndView modelAndView = errorController.handleTest(model);
            assertTrue("ErrorController handleTest() should generate an exception!", false);
        } catch (Exception exception) {
            int dummy = 0;
        }
        
    }
    
}
