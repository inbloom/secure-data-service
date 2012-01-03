package org.slc.sli.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A security filter responsible for checking SLI session
 *
 * @author dkornishev
 */
@Component
public class SliRequestFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(SliRequestFilter.class);
    private static final String PARAM_SESSION = "sessionId";
    private static final String HEADER_SESSION_NAME = "sessionId";

    @Resource(name = "openamRestTokenResolver")
    private SecurityTokenResolver resolver;

    /**
     * Intercepter method called by spring
     * Checks cookies to see if SLI session id exists
     * If session does exist, resolution will be attempted
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        @SuppressWarnings("unchecked")
        String sessionId = getSessionIdFromRequest((HttpServletRequest) request);

        if (sessionId != null) {
            SecurityContextHolder.getContext().setAuthentication(resolver.resolve(sessionId));
        }

        chain.doFilter(request, response);
    }

    protected String getSessionIdFromRequest(HttpServletRequest req) {

        String sessionId = "";

        if (requestContainsSessionParam(req)) {
            sessionId = req.getParameter(PARAM_SESSION);
        } else {
            sessionId = req.getHeader(HEADER_SESSION_NAME);
        }

        return sessionId;
    }

    private boolean requestContainsSessionParam(HttpServletRequest req) {
        return req.getParameter(PARAM_SESSION) != null;
    }

    public void setResolver(SecurityTokenResolver resolver) {
        this.resolver = resolver;
    }
}
