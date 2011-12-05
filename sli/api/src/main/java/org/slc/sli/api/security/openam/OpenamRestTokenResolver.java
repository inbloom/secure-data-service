package org.slc.sli.api.security.openam;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.api.security.SLIAuthenticationEntryPoint;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityTokenResolver;

public class OpenamRestTokenResolver implements SecurityTokenResolver {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    private String              tokenServiceUrl;
    private RestTemplate        rest;
    
    @Override
    public Authentication resolve(String token) throws AuthenticationException {
        
        Authentication auth = null;
        
        // Validate Session
        ResponseEntity<String> entity = rest.getForEntity(tokenServiceUrl + "/identity/isTokenValid?tokenid=" + token, String.class, Collections.<String, Object> emptyMap());
        
        if (entity.getStatusCode() == HttpStatus.OK && entity.getBody().contains("boolean=true")) {
            
            // Get session attributes
            entity = rest.getForEntity(tokenServiceUrl + "/identity/attributes?subjectid=" + token, String.class, Collections.<String, Object> emptyMap());
            LOG.debug("-------------------------------------");
            LOG.debug(entity.getBody());
            LOG.debug("-------------------------------------");
            
            // Create Authentication object and cram it into SCH
            auth = buildAuthentication(entity.getBody());
        } else if (entity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            LOG.warn("Received 'UNAUTHORIZED' response from the token service");
        }
        
        return auth;
    }
    
    public void setTokenServiceUrl(String tokenServiceUrl) {
        this.tokenServiceUrl = tokenServiceUrl;
    }
    
    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }
    
    private Authentication buildAuthentication(String payload) {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setId(extractValue("uid", payload));
        principal.setName(extractValue("cn", payload));
        return new PreAuthenticatedAuthenticationToken(principal, null, Collections.singletonList(new GrantedAuthorityImpl("ROLE_USER")));
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
