package org.slc.sli.api.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Provides functionality to deal with Oauth data
 * 
 * @author dkornishev
 * 
 */
@Component
public class Oauth2Helper {
    
    public static final String SESSION_COLLECTION = "oauth_access_token";
    
    private static final Pattern USER_AUTH = Pattern.compile("Bearer (.+)", Pattern.CASE_INSENSITIVE);
    
    @Autowired
    private Repository<Entity> repo;
    
    @Autowired
    private OAuthTokenUtil util;
    
    /**
     * Loads session referenced by the headers
     * 
     * @param headers
     * @return
     */
    @SuppressWarnings("unchecked")
    public OAuth2Authentication getAuthentication(String authz) {
        OAuth2Authentication auth = createAnonymousAuth();
        
        if (authz != null && !authz.equals("")) {
            try {
                Matcher user = USER_AUTH.matcher(authz);
                if (user.find()) {
                    String accessToken = user.group(1);
                    
                    Entity sessionEntity = findEntityForAccessToken(accessToken);
                    auth = util.createOAuth2Authentication((Map<String, Object>) sessionEntity.getBody().get("authentication"));
                } else {
                    info("User is anonymous");
                }
            } catch (Exception e) {
                warn("Error processing authentication.  Anonymous context will be returned...\n {}", e);
            }
        }
        return auth;
    }
    
    private OAuth2Authentication createAnonymousAuth() {
        String time = Long.toString(System.currentTimeMillis());
        return new OAuth2Authentication(new ClientToken("UNKNOWN", "UNKNOWN", new HashSet<String>()), new AnonymousAuthenticationToken(time, time, Arrays.<GrantedAuthority>asList(Right.ANONYMOUS_ACCESS)));
    }
    
    private Entity findEntityForAccessToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("token", "=", token));
        return repo.findOne(SESSION_COLLECTION, neutralQuery);
    }
}