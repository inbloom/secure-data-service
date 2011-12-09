package org.slc.sli.api.security.openam;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slc.sli.api.security.SLIAuthenticationEntryPoint;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityTokenResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Creates Spring Authentication object by calling openAM restful API
 * To validate and fetch attributes for provided token
 * 
 * @author dkornishev
 * 
 */
public class OpenamRestTokenResolver implements SecurityTokenResolver {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
<<<<<<< HEAD
    private String              tokenServiceUrl;
    private RestTemplate        rest;
=======
    private String tokenServiceUrl;
    private RestTemplate rest;
>>>>>>> 3bd1c345402a304d47c6039d71289bc3b2aee5ea
    
    /**
     * Populates Authentication object by calling openAM with given token id
     * 
     * @param token
     *            sessionId to use in lookups
     * @return populated Authentication or null if sessionId isn't valid
     * @throws AuthenticationException
     */
    @Override
    public Authentication resolve(String token) {
        
        Authentication auth = null;
        
        try {
            
            // Validate Session
<<<<<<< HEAD
            ResponseEntity<String> entity = rest.getForEntity(tokenServiceUrl + "/identity/isTokenValid?tokenid=" + token, String.class);
=======
            ResponseEntity<String> entity = rest.getForEntity(tokenServiceUrl + "/identity/isTokenValid?tokenid="
                    + token, String.class, Collections.emptyMap());
>>>>>>> 3bd1c345402a304d47c6039d71289bc3b2aee5ea
            
            if (entity.getStatusCode() == HttpStatus.OK && entity.getBody().contains("boolean=true")) {
                
                // Get session attributes
<<<<<<< HEAD
                entity = rest.getForEntity(tokenServiceUrl + "/identity/attributes?subjectid=" + token, String.class);
=======
                entity = rest.getForEntity(tokenServiceUrl + "/identity/attributes?subjectid=" + token, String.class,
                        Collections.emptyMap());
>>>>>>> 3bd1c345402a304d47c6039d71289bc3b2aee5ea
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
        
        return new PreAuthenticatedAuthenticationToken(principal, token, extractAuthorities());
    }
    
    private List<GrantedAuthorityImpl> extractAuthorities() {
        return Collections.singletonList(new GrantedAuthorityImpl("ROLE_USER"));    // TODO look at
                                                                                 // actual roles
    }
    
    private String extractValue(String valueName, String payload) {
        String result = "";
        
<<<<<<< HEAD
        Pattern p = Pattern.compile("userdetails\\.attribute\\.name=" + valueName + "\\s*userdetails\\.attribute\\.value=(.+)$", Pattern.MULTILINE);
=======
        Pattern p = Pattern.compile("userdetails\\.attribute\\.name=" + valueName
                + "\\s*userdetails\\.attribute\\.value=(.+)$", Pattern.MULTILINE);
>>>>>>> 3bd1c345402a304d47c6039d71289bc3b2aee5ea
        Matcher m = p.matcher(payload);
        
        if (m.find()) {
            result = m.group(1);
        }
        
        return result;
    }
}
