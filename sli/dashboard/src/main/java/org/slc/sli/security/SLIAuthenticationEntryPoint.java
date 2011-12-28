package org.slc.sli.security;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Spring interceptor for calls that don't have a session
 * This implementation simply redirects to the login URL
 * 
 * @author dkornishev
 *
 */
public class SLIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    private String   authUrl, loginPageUrl, callBackPageUrlPrefix, dashboardLandingPage;
    
   
    public String getCallBackPageUrlPrefix() {
        return callBackPageUrlPrefix;
    }

    public void setCallBackPageUrlPrefix(String callBackPageUrlPrefix) {
        this.callBackPageUrlPrefix = callBackPageUrlPrefix;
    }

    public String getDashboardLandingPage() {
        return dashboardLandingPage;
    }

    public void setDashboardLandingPage(String dashboardLandingPage) {
        this.dashboardLandingPage = dashboardLandingPage;
    }

    public String getLoginPageUrl() {
        return loginPageUrl;
    }

    public void setLoginPageUrl(String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }

    /**
     * Redirects user to login URL
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        
        String callBackURL = request.getHeader("referer");
        String securityLoginUrl = loginPageUrl + callBackPageUrlPrefix + ((callBackURL == null) ? dashboardLandingPage : callBackURL);
        String realmURL = loginPageUrl + authUrl + "?return=" + URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
        LOG.info("Redirecting user to realm " + realmURL);
        
        response.sendRedirect(realmURL);

    }
    
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }    
}