package org.slc.sli.api.security.openam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityTokenResolver;
import org.slc.sli.api.security.SliEntryPoint;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;

/**
 * Creates Spring Authentication object by calling openAM restful API
 * To validate and fetch attributes for provided token
 * 
 * @author dkornishev
 */
@Component
@Primary
public class OpenamRestTokenResolver implements SecurityTokenResolver {
    
    private static final Logger   LOG  = LoggerFactory.getLogger(SliEntryPoint.class);
    
    private RestTemplate          rest = new RestTemplate();
    
    @Value("${sli.security.tokenService.url}")
    private String                tokenServiceUrl;
    
    @Autowired
    private RolesToRightsResolver resolver;
    
    @Autowired
    private UserLocator           locator;
    
    /**
     * Populates Authentication object by calling openAM with given token id
     * 
     * @param token
     *            sessionId to use in lookups
     * @return populated Authentication or null if sessionId isn't valid
     */
    @Override
    public Authentication resolve(String token) {
        
        Authentication auth = null;
        
        if (token != null && !"".equals(token)) {
            
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
        }
        return auth;
    }
    
    private Authentication buildAuthentication(String token, String payload) {
        String externalUserId = extractValue("uid", payload);
        SLIPrincipal principal = this.locator.locate(extractRealm(payload), externalUserId);
        principal.setName(extractValue("cn", payload));
        principal.setRoles(extractRoles(payload));
        principal.setRealm(extractRealm(payload));

        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(null, null, Arrays.asList(Right.READ_GENERAL)));
        Set<GrantedAuthority> grantedAuthorities = this.resolver.resolveRoles(principal.getRealm(), principal.getRoles());
        SecurityContextHolder.clearContext();
        return new PreAuthenticatedAuthenticationToken(principal, token, grantedAuthorities);
        
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
    
    /**
     * Extracts the realm that the user belongs to. Current implementation of the SLIPrincipal
     * allows for exactly one realm per user. This function will change if users are allowed to
     * belong to multiple realms.
     * 
     * @param payload
     *            OpenAM user attributes.
     * @return String containing realm information.
     */
    private String extractRealm(String payload) {
        String value = extractValue("dn", payload);
        String myRealm = "";
        Pattern p = Pattern.compile("ou=\\w+,(.+)");
        Matcher m = p.matcher(value);
        
        if (m.find()) {
            myRealm = m.group(1);
        }
        
        return myRealm;
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
    
    public void setTokenServiceUrl(String tokenServiceUrl) {
        this.tokenServiceUrl = tokenServiceUrl;
    }
    
    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }
    
    public void setResolver(RolesToRightsResolver resolver) {
        this.resolver = resolver;
    }
    
    public void setLocator(UserLocator locator) {
        this.locator = locator;
    }
    
}
