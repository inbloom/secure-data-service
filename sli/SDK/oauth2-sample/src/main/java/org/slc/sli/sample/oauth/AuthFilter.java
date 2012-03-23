package org.slc.sli.sample.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.impl.BasicClient;

/**
 * Basic authentication example using the SLI SDK.
 */
public class AuthFilter implements Filter {
    
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);
    private String clientId;
    private String clientSecret;
    private URL apiUrl;
    private URL callbackUrl;
    private String afterCallbackRedirect;
    
    @Override
    public void destroy() {
        LOG.info("Destroy auth filter");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getRequestURI().equals("/oauth2-sample/callback")) {
            handleCallback(request, response);
            ((HttpServletResponse) response).sendRedirect(afterCallbackRedirect);
            return;
        } else if (req.getSession().getAttribute("client") == null) {
            authenticate(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
    
    private void handleCallback(ServletRequest request, ServletResponse response) {
        BasicClient client = (BasicClient) ((HttpServletRequest) request).getSession().getAttribute("client");
        String code = ((HttpServletRequest) request).getParameter("code");
        LOG.debug("Got authoriation code: {}", code);
        String accessToken = client.connect(code);
        LOG.debug("Got access token: {}", accessToken);
    }
    
    private void authenticate(ServletRequest req, ServletResponse res) {
        
        BasicClient client = new BasicClient(apiUrl, clientId, clientSecret, callbackUrl);
        try {
            ((HttpServletResponse) res).sendRedirect(client.getLoginURL().toExternalForm());
        } catch (IOException e) {
            LOG.error("Bad redirect", e);
        }
        ((HttpServletRequest) req).getSession().setAttribute("client", client);
    }
    
    @Override
    public void init(FilterConfig conf) throws ServletException {
        afterCallbackRedirect = conf.getInitParameter("afterCallbackRedirect");
        
        String env = System.getProperty("sli.env");
        if (env == null) {
            System.out.println("sli.env not set!");
            throw new RuntimeException("sli.env system property is not set!");
        }
        InputStream propStream = this.getClass().getResourceAsStream("/config/" + env + ".properties");
        if (propStream == null) {
            System.out.println("no properties file found for sli.env: " + env);
            throw new RuntimeException("no properties file found for sli.env: " + env);
        }
        Properties props = new Properties();
        try {
            props.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clientId = props.getProperty("clientId");
        clientSecret = props.getProperty("clientSecret");
        try {
            apiUrl = new URL(props.getProperty("apiUrl"));
            callbackUrl = new URL(props.getProperty("callbackUrl"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
