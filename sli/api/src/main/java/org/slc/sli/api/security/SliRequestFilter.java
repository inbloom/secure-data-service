package org.slc.sli.api.security;

import org.slc.sli.api.security.roles.AnonymousPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A security filter responsible for checking SLI session
 * 
 * @author dkornishev
 */
@Component
public class SliRequestFilter extends GenericFilterBean {
    
    private static final Logger   LOG                 = LoggerFactory.getLogger(SliRequestFilter.class);
    private static final String   PARAM_SESSION       = "sessionId";
    private static final String   HEADER_SESSION_NAME = "sessionId";
    
    @Resource(name = "openamRestTokenResolver")
    private SecurityTokenResolver resolver;
    
    @Value("${sli.security.noSession.landing.url}")
    private String                realmSelectionUrl;
    
    @Autowired
    private AnonymousPrincipal    anonymousPrincipal;
    
    /**
     * Intercepter method called by spring
     * Checks cookies to see if SLI session id exists
     * If session does exist, resolution will be attempted
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        String sessionId = getSessionIdFromRequest((HttpServletRequest) request);
        
        Authentication auth = resolver.resolve(sessionId);
        
        if (auth == null && isPublicResource((HttpServletRequest) request)) {
            auth = new PreAuthenticatedAuthenticationToken(anonymousPrincipal, null);
        }
        
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);
        } else {
            LOG.warn("Unauthorized access");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).setHeader("WWW-Authenticate", this.realmSelectionUrl);
            return;
        }
    }
    
    private boolean isPublicResource(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/rest/system/session/check") || request.getRequestURI().startsWith("/api/rest/pub/");
    }
    
    private String getSessionIdFromRequest(HttpServletRequest req) {
        
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
    
    public void setRealmSelectionUrl(String realmSelectionUrl) {
        this.realmSelectionUrl = realmSelectionUrl;
    }
    
}
