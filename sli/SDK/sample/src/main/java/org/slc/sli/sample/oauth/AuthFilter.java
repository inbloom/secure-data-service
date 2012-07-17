/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import javax.ws.rs.core.Response;

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
        LOG.info("URI:" + req.getRequestURI());
        if (req.getRequestURI().equals("/sample/callback")) {
            if (handleCallback(request, response)) {
                ((HttpServletResponse) response).sendRedirect(afterCallbackRedirect);
            }
            LOG.info("callback");
            return;
        } else if (req.getSession().getAttribute("client") == null) {
            LOG.info("authenticate");
            authenticate(request, response);
        } else {
            try {
                LOG.info("chain");
                chain.doFilter(request, response);
            } catch (Exception e) {
                // Redirect to login on any errors
                // TODO - we should handle responses correctly here. If the session is invalidated,
                // we need to handle this properly. Same with the other HTTP response codes.
                authenticate(request, response);
            }
        }
    }

    private boolean handleCallback(ServletRequest request, ServletResponse response) throws IOException {
        BasicClient client = (BasicClient) ((HttpServletRequest) request).getSession().getAttribute("client");
        String code = ((HttpServletRequest) request).getParameter("code");

        if (client != null) {
            String token = null;
            Response rval = client.connect(code, token);

            switch (rval.getStatus()) {
                case 200:  // OK
                    return true;
                case 403:
                    ((HttpServletResponse) response).sendRedirect("403.html");
                    return false;
                case 404:
                    ((HttpServletResponse) response).sendRedirect("404.html");
                    return false;
                case 422:
                    ((HttpServletResponse) response).sendRedirect("422.html");
                    return false;
                case 400:
                case 500:
                    ((HttpServletResponse) response).sendRedirect("500.html");
                    return false;
            }
        }

        return true;
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
        LOG.info("Loading properties from: {}", externalProps);
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
