package org.slc.sli.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Simple Mapping Exception Resolver to log the exception and display userfriendly message.
 * @author vummalaneni
 *
 */
public class DashboardExceptionResolver extends SimpleMappingExceptionResolver {
    
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public DashboardExceptionResolver() {
        super();
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        
        String stackTrace = getStackTrace(ex);
        logger.error(stackTrace);
        ModelAndView mv = super.resolveException(request, response, handler, ex);
        response.setStatus(500);
        if (logger.isDebugEnabled()) {
            stackTrace = stackTrace.replaceAll("\n", "<br>");
            stackTrace = stackTrace.replaceAll("\t", "&nbsp; &nbsp; &nbsp; &nbsp;");
            if (mv == null) {
                mv = new ModelAndView();
            }
            mv.addObject("debugEnabled", true);
            mv.addObject("errorMessage", ex.getMessage());
            mv.addObject("stackTrace", stackTrace);
        } else {
            mv.addObject("debugEnabled", false);
        }
        
        return mv;
    }
    
    /**
     * This method is converts the stack trace to a string
     * @param t, Throwable
     * @return returns a String of exception stack trace
     */
    public String getStackTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        t.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }
}