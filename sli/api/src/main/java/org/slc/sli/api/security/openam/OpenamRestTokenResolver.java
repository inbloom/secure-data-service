package org.slc.sli.api.security.openam;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityTokenResolver;
import org.slc.sli.api.security.SliEntryPoint;
import org.slc.sli.api.security.roles.DefaultRoleMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates Spring Authentication object by calling openAM restful API
 * To validate and fetch attributes for provided token
 *
 * @author dkornishev
 */
@Component
public class OpenamRestTokenResolver implements SecurityTokenResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SliEntryPoint.class);

    private RestTemplate rest = new RestTemplate();

    @Value("${security.tokenService.url}")
    private String tokenServiceUrl;

    /**
     * Populates Authentication object by calling openAM with given token id
     *
     * @param token sessionId to use in lookups
     * @return populated Authentication or null if sessionId isn't valid
     */
    @Override
    public Authentication resolve(String token) {

        Authentication auth = null;

        try {

            String tokenValidUrl = tokenServiceUrl + "/identity/isTokenValid?tokenid=" + token;

            // Validate Session
            ResponseEntity<String> entity = rest.getForEntity(tokenValidUrl, String.class, Collections.<String, Object>emptyMap());

            if (entity.getStatusCode() == HttpStatus.OK && entity.getBody().contains("boolean=true")) {

                // Get session attributes
                entity = rest.getForEntity(tokenServiceUrl + "/identity/attributes?subjectid=" + token, String.class, Collections.<String, Object>emptyMap());
                LOG.debug("-------------------------------------");
                LOG.debug(entity.getBody());
                LOG.debug("-------------------------------------");

                // Create Authentication object and cram it into SCH
                auth = buildAuthentication(token, entity.getBody());
            }
        } catch (RestClientException e) {
            LOG.error("Error calling openAM Restful Service", e);
        }

        return auth;
    }

    public void setTokenServiceUrl(String tokenServiceUrl) {
        this.tokenServiceUrl = tokenServiceUrl;
    }

    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }

    private Authentication buildAuthentication(String token, String payload) {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setId(extractValue("uid", payload));
        principal.setName(extractValue("cn", payload));
        principal.setTheirRoles(extractRoles(payload));

        return new PreAuthenticatedAuthenticationToken(principal, token, new DefaultRoleMapperImpl(principal.getTheirRoles()).buildMappedRoles());
    }

    private List<String> extractRoles(String payload) {
        List<String> roles = new ArrayList<String>();
        Pattern p = Pattern.compile("userdetails\\.role=id=([^,]*)", Pattern.MULTILINE);
        Matcher m = p.matcher(payload);

        while (m.find()) {
            roles.add(m.group(1));
        }
        return roles;
    }

    private String extractValue(String valueName, String payload) {
        String result = "";

        Pattern p = Pattern.compile("userdetails\\.attribute\\.name=" + valueName + "\\s*userdetails\\.attribute\\.value=(.+)$", Pattern.MULTILINE);
        Matcher m = p.matcher(payload);

        if (m.find()) {
            result = m.group(1);
        }

        return result;
    }
}
