package org.slc.sli.admin.client;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import org.slc.sli.admin.util.URLHelper;
import org.slc.sli.admin.util.UrlBuilder;

/**
 * 
 * @author scole
 * 
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    private static final String SESSION_ID_KEY = "ADMIN_SESSION_ID";
    private static final String OPENAM_COOKIE_NAME = "iPlanetDirectoryPro";
    
    private static Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    
    @Autowired
    private RESTClient restClient;
    
    public void setRESTClient(RESTClient rest) {
        this.restClient = rest;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object controller,
            Exception exception) throws Exception {
        
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object sessionId = request.getSession().getAttribute(SESSION_ID_KEY);
        if (sessionId == null) {
            Cookie openAmCookie = WebUtils.getCookie(request, OPENAM_COOKIE_NAME);
            if (openAmCookie != null) {
                request.getSession().setAttribute(SESSION_ID_KEY, openAmCookie.getValue());
            } else {
                JsonObject jsonSession = this.restClient.sessionCheck(null);
                if (!jsonSession.get("authenticated").getAsBoolean()) {
                    String baseUrl = jsonSession.get("redirect_user").getAsString();
                    String requestedURL = URLHelper.getUrl(request);
                    logger.debug("Using return URL of: " + requestedURL);
                    UrlBuilder url = new UrlBuilder(baseUrl);
                    url.addQueryParam("RelayState", requestedURL);
                    response.sendRedirect(url.toString());
                }
                return false;
            }
        }
        return true;
    }
}
