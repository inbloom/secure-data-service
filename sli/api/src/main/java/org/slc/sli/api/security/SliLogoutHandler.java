package org.slc.sli.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logout handler for SLI sessions.
 * Calls logout on openAM restful token service.
 * <p/>
 * OpenAM Logout resource:
 * http://<OpenAM_Host>:<Port>/<deploy_uri>/identity/logout?subjectid=<sessionId>
 */
@Component
public class SliLogoutHandler implements LogoutHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SliLogoutHandler.class);

    @Value("http://devdanil.slidev.org:8080/idp")
    private String tokenServiceUrl;
    @Value("/identity/logout?subjectid=")
    private String logoutPath;

    private RestTemplate rest = new RestTemplate();

    /**
     * Requires the request to be passed in.
     *
     * @param request
     *            from which to obtain a HTTP session (cannot be null)
     * @param response
     *            not used (can be <code>null</code>)
     * @param authentication
     *            not used (can be <code>null</code>)
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Assert.notNull(request, "HttpServletRequest required");

        if (authentication != null && authentication.getCredentials() != null) {

            String tokenId = (String) authentication.getCredentials();
            invalidateSessionAtTokenService(tokenId);
        }
    }

    private void invalidateSessionAtTokenService(String sessionId) {
        try {
            String logoutUrl = tokenServiceUrl + logoutPath + sessionId;
            ResponseEntity<String> entity = rest.getForEntity(logoutUrl, String.class);
            if (entity.getStatusCode() == HttpStatus.OK) {
                LOG.debug("Logout success for session {}", sessionId);
            } else {
                LOG.warn("Logout request returned status {}", entity.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Logout error calling openAM Restful Service at {}", e);
        }
    }

    public void setTokenServiceUrl(String tokenServiceUrl) {
        this.tokenServiceUrl = tokenServiceUrl;
    }

    public void setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
    }

    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }

}
