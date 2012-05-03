package org.slc.sli.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
public class ErrorController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);

    public static final String TEMPLATE_NAME = "exceptionPage";
    public static final String URL_PARAM_EXCEPTION_LABEL = "exception.label";
    public static final String URL_PARAM_EXCEPTION_MESSAGE_TYPE = "exception.messageType";
    public static final String URL_PARAM_EXCEPTION_MESSAGE = "exception.message";
    public static final String URL_PARAM_EXCEPTION_CAUSE = "exception.cause";
    public static final String MODEL_ATTR_EXCEPTION = "exception";
    public static final String MODEL_ATTR_EXCEPTION_LABEL = "label";
    public static final String MODEL_ATTR_EXCEPTION_MESSAGE_TYPE = "messageType";
    public static final String MODEL_ATTR_EXCEPTION_MESSAGE = "message";
    
    /**
     * Controller for SLI Exceptions
     * 
     * @param exceptionLabel
     *            - SLI Exception panel label
     * @param exceptionMessageType
     *            - SLI Exception panel message type
     * @param exceptionMessage
     *            - SLI Exception panel message details
     * @param exceptionCause
     *            - SLI Exception panel cause or stacktrace details
     * @param model
     *            - Freemarker model map
     * @param response
     *            - HttpServletResponse for testability purposes
     * @return ModelAndView
     */
    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public ModelAndView handleError(
            @RequestParam(value = URL_PARAM_EXCEPTION_LABEL, required = false) String exceptionLabel,
            @RequestParam(value = URL_PARAM_EXCEPTION_MESSAGE_TYPE, required = false) String exceptionMessageType,
            @RequestParam(value = URL_PARAM_EXCEPTION_MESSAGE, required = false) String exceptionMessage,
            @RequestParam(value = URL_PARAM_EXCEPTION_CAUSE, required = false) Throwable exceptionCause,
            ModelMap model, HttpServletResponse response) {
        
        Map<String, String> exceptionMap = new HashMap<String, String>();
        
        if (exceptionLabel != null) {
            exceptionMap.put(MODEL_ATTR_EXCEPTION_LABEL, exceptionLabel);
        }
        if (exceptionMessageType != null) {
            exceptionMap.put(MODEL_ATTR_EXCEPTION_MESSAGE_TYPE, exceptionMessageType);
        }
        if (exceptionMessage != null) {
            exceptionMap.put(MODEL_ATTR_EXCEPTION_MESSAGE, exceptionMessage);
        }
        if (exceptionCause != null) {
            exceptionMap.put(MODEL_ATTR_EXCEPTION_MESSAGE, exceptionCause.getMessage());
        }
        
        model.addAttribute(MODEL_ATTR_EXCEPTION, exceptionMap);
        
        return new ModelAndView(TEMPLATE_NAME, model);
    }
    
    @RequestMapping(value = "/testException", method = RequestMethod.GET)
    public ModelAndView handleTest(ModelMap model) throws Exception {        
        throw new IllegalArgumentException("Test Exception");
    }
    
}
