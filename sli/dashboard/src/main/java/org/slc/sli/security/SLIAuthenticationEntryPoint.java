package org.slc.sli.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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
        
        String urlProtocol =  "http";
        String serverAddress = "testapi1.slidev.org";
        int serverPort = 8080;
        String callBackPageUrlPrefix =  "?RelayState=";
        String apiCallPrefix =  "/api";        

        URL url = new URL(urlProtocol, serverAddress, serverPort, apiCallPrefix + "/rest/system/session/check");
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        
        String inputLine;
        StringBuffer responseString = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            responseString.append(inputLine);
        }
        in.close();
        
        Gson gson = new Gson();
        System.out.println(responseString);
        SecurityResponse resp = gson.fromJson(responseString.toString(), SecurityResponse.class);
        String redirectUrl = resp.getRedirect_user();
        
        String realmUrl = redirectUrl + callBackPageUrlPrefix + URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
        response.sendRedirect(realmUrl);

    }
    
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }    
}