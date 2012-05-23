    package org.slc.sli.sample.oauth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        if (req.getParameter("byPassToken") != null && !req.getParameter("byPassToken").equals("")) {
            byPassAuthenticate(request, response);
            chain.doFilter(request, response);
        } else if (req.getRequestURI().equals("/sample/callback")) {
            handleCallback(request, response);
            ((HttpServletResponse) response).sendRedirect(afterCallbackRedirect);
            return;
        } else if (req.getSession().getAttribute("client") == null) {
            authenticate(request, response);
        } else {
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                // Redirect to login on any errors
                // TODO - we should handle responses correctly here.  If the session is invalidated, we need
                // to handle this properly.  Same with the other HTTP response codes.a
                authenticate(request, response);
            }
        }
    }

    private void handleCallback(ServletRequest request, ServletResponse response) {
        BasicClient client = (BasicClient) ((HttpServletRequest) request).getSession().getAttribute("client");
        String code = ((HttpServletRequest) request).getParameter("code");
//        DE260 - commenting out possibly sensitive data
//        LOG.debug("Got authoriation code: {}", code);

        if (client != null) {
//            String accessToken =
                    client.connect(code);
//            DE260 - commenting out possibly sensitive data
//            LOG.debug("Got access token: {}", accessToken);
        }
    }

    private void byPassAuthenticate(ServletRequest req, ServletResponse res) {
        BasicClient client = null;
        if (((HttpServletRequest) req).getSession().getAttribute("client") == null) {
            client = new BasicClient(apiUrl, clientId, clientSecret, callbackUrl);
        } else {
            client = (BasicClient) ((HttpServletRequest) req).getSession().getAttribute("client");
        }
        String sessionToken = req.getParameter("byPassToken");
        client.setToken(sessionToken);
        ((HttpServletRequest) req).getSession().removeAttribute("client");
        ((HttpServletRequest) req).getSession().setAttribute("client", client);
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

        InputStream propStream;

        String externalProps = System.getProperty("sli.conf");
        if (externalProps != null) {
            try {
                propStream = new FileInputStream(externalProps);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Unable to load properties file: " + externalProps);
            }
        } else {
            String env = System.getProperty("sli.env");
            if (env == null) {
                throw new RuntimeException("sli.env system property is not set!");
            }
            propStream = this.getClass().getResourceAsStream("/config/" + env + ".properties");
            if (propStream == null) {
                throw new RuntimeException("no properties file found for sli.env: " + env);
            }
        }
        Properties props = new Properties();
        try {
            props.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientId = props.getProperty("sli.sample.clientId");
        clientSecret = props.getProperty("sli.sample.clientSecret");
        String apiUrlString = props.getProperty("sli.sample.apiUrl");
        String callbackUrlString = props.getProperty("sli.sample.callbackUrl");
        if (clientId == null || clientSecret == null || apiUrlString == null || callbackUrlString == null) {
            throw new RuntimeException(
                    "Missing property.  All of the following properties must be available: clientId, clientSecret, apiUrl, callbackUrl");
        }
        try {
            apiUrl = new URL(apiUrlString);
            callbackUrl = new URL(callbackUrlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
