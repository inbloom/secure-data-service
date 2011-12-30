package org.slc.sli.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Logout handler for SLI sessions.
 * Calls logout on openAM restful token service.
 */
@Component
public class SliLogoutHandler implements LogoutHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SliLogoutHandler.class);

    @Value("http://devdanil.slidev.org:8080/idp")
    private String tokenServiceUrl;

    @Autowired
    private RestTemplate rest;

    @Autowired
    private SliRequestFilter requestFilter;

    /**
     * Requires the request to be passed in.
     *
     * @param request        from which to obtain a HTTP session (cannot be null)
     * @param response       not used (can be <code>null</code>)
     * @param authentication not used (can be <code>null</code>)
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Assert.notNull(request, "HttpServletRequest required");

        String tokenId = requestFilter.getSessionIdFromRequest(request);

        if (tokenId != null) {
            invalidateSessionAtTokenService(tokenId);
        }

        invalidateHttpSession(request);
        SecurityContextHolder.clearContext();
    }

    private void invalidateSessionAtTokenService(String sessionId) {
        try {
            // OpenAM Logout resource http://<OpenAM_Host>:<Port>/<deploy_uri>/identity/logout?subjectid=<sessionId>
            String tokenValidUrl = tokenServiceUrl + "/identity/logout?subjectid=" + sessionId;
            ResponseEntity<String> entity = rest.getForEntity(tokenValidUrl, String.class);
            if (entity.getStatusCode() == HttpStatus.OK) {
                LOG.debug("Logout success for {}", sessionId);
            } else {
                LOG.warn("OpenAM logout request returned status {}", entity.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Logout error calling openAM Restful Service", e);
        }
    }

    private void invalidateHttpSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public void setTokenServiceUrl(String tokenServiceUrl) {
        this.tokenServiceUrl = tokenServiceUrl;
    }

    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }

}
