package org.slc.sli.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeClientToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.enums.Right;

/**
 * Utilities for the OAuth 2.0 implementations of SliTokenService and
 * SliTokenStore.
 * 
 * @author shalka
 * 
 */
@Component
public class OAuthTokenUtil {
    
    @Autowired
    private UserLocator locator;
    
    @Autowired
    private EntityRepository repo;
    
    @Autowired
    private IdConverter converter;
    
    @Autowired
    private RolesToRightsResolver resolver;
    
    /**
     * Name of the collection in Mongo that stores OAuth 2.0 session
     * information.
     */
    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = "oauth_access_token";
    private static final String OAUTH_REFRESH_TOKEN_COLLECTION = "oauth_refresh_token";
    /**
     * Lifetime (duration of validity) of an Access Token in seconds.
     */
    private static final int ACCESS_TOKEN_VALIDITY = 1800;
    
    /**
     * Lifetime (duration of validity) of a Refresh Token in seconds.
     */
    private static final int REFRESH_TOKEN_VALIDITY = 3600;
    
    
    /**
     * Get the name of the collection in Mongo that stores OAuth 2.0 session
     * information.
     * 
     * @return String representing Mongo collection name.
     */
    public static String getOAuthAccessTokenCollectionName() {
        return OAUTH_ACCESS_TOKEN_COLLECTION;
    }
    
    public static String getOAuthRefreshTokenCollectionName() {
        return OAUTH_REFRESH_TOKEN_COLLECTION;
    }
    
    /**
     * Returns true if the current time (in ms) is greater than the specified
     * expiration date (indicating that expiration is true).
     * 
     * @param expiration
     *            Date to be checked (represented by number of milliseconds since last epoch).
     * @return 'true' if expired, 'false' if not expired.
     */
    public static boolean isTokenExpired(long expiration) {
        return System.currentTimeMillis() > expiration;
    }
    
    
    /**
     * Returns the validity of a refresh token in seconds.
     * 
     * @return Integer representing the number of seconds that the refresh token is valid for.
     */
    public static int getRefreshTokenValidity() {
        return REFRESH_TOKEN_VALIDITY;
    }
    
    /**
     * Returns the validity of an access token in seconds.
     * 
     * @return Integer representing the number of seconds that the access token is valid for.
     */
    public static int getAccessTokenValidity() {
        return ACCESS_TOKEN_VALIDITY;
    }
    
    @SuppressWarnings("unchecked")
    public OAuth2Authentication createOAuth2Authentication(Map data) {
        String realm = (String) data.get("realm");
        String externalId = (String) data.get("externalId");
        
        Entity realmEntity = repo.findOne("realm", new Query(Criteria.where("_id").is(converter.toDatabaseId(realm))));
        SLIPrincipal principal = locator.locate((String) realmEntity.getBody().get("regionId"), externalId);
        principal.setName((String) data.get("name"));
        principal.setRoles((List<String>) data.get("roles"));
        principal.setRealm(realm);
        return reconstituteAuth(principal, data);
    }
    
    public OAuth2Authentication reconstituteAuth(final SLIPrincipal principal,
            Map data) {
        Set<String> scope = listToSet((List) data.get("scope"));
        Set<String> resourceIds = listToSet((List) data.get("resourceIds"));
        Collection<GrantedAuthority> clientAuthorities = deserializeAuthorities(listToSet((List) data.get("clientAuthorities")));
        Collection<GrantedAuthority> userAuthorities = SecurityUtil.sudoRun(new SecurityTask<Collection<GrantedAuthority>>() {
            @Override
            public Collection<GrantedAuthority> execute() {
                return resolver.resolveRoles(principal.getRealm(), principal.getRoles());
            }
        });

        ClientToken client = new ClientToken((String) data.get("clientId"), 
                resourceIds, 
                (String) data.get("clientSecret"), 
                scope, 
                clientAuthorities);
        UnconfirmedAuthorizationCodeClientToken token = new UnconfirmedAuthorizationCodeClientToken(client.getClientId(), 
                client.getClientSecret(), 
                scope, 
                (String) data.get("state"), 
                (String) data.get("requestedRedirect"));
        PreAuthenticatedAuthenticationToken user = new PreAuthenticatedAuthenticationToken(principal, token, userAuthorities);
        return new OAuth2Authentication(client, user);
    }
    
    public static EntityBody serializeOauth2Auth(OAuth2Authentication auth) {
        EntityBody body = new EntityBody();
        SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
        body.put("realm", principal.getRealm());
        body.put("externalId", principal.getExternalId());
        body.put("name", principal.getName());
        body.put("roles", principal.getRoles());
        body.put("clientId", auth.getClientAuthentication().getClientId());
        body.put("clientSecret", auth.getClientAuthentication().getClientSecret());
        body.put("scope", auth.getClientAuthentication().getScope());
        body.put("userAuthorities", serializeAuthorities(auth.getUserAuthentication().getAuthorities()));
        body.put("clientAuthorities", serializeAuthorities(auth.getClientAuthentication().getAuthorities()));
        body.put("resourceIds", auth.getClientAuthentication().getResourceIds());
        UnconfirmedAuthorizationCodeClientToken token = (UnconfirmedAuthorizationCodeClientToken) auth.getUserAuthentication().getCredentials();
        body.put("state", token.getState());
        body.put("requestedRedirect", token.getRequestedRedirect());
        return body;
    }
    
    public static EntityBody serializeAccessToken(OAuth2AccessToken token) {
        EntityBody body = new EntityBody();
        body.put("type", token.getTokenType());
        body.put("expiration", token.getExpiration());
        body.put("value", token.getValue());
        body.put("scope", token.getScope());
        body.put("refreshToken", serializeRefreshToken(token.getRefreshToken()));
        return body;
    }
    
    public static OAuth2AccessToken deserializeAccessToken(Map data) {
        OAuth2AccessToken token = new OAuth2AccessToken((String) data.get("value"));
        token.setExpiration((Date) data.get("expiration"));
        token.setTokenType((String) data.get("type"));
        token.setScope(listToSet((List) data.get("scope")));
        token.setRefreshToken(deserializeRefreshToken((Map) data.get("refreshToken")));
        return token;
    }
    
    public static EntityBody serializeRefreshToken(OAuth2RefreshToken token) {
        EntityBody body = new EntityBody();
        body.put("value", token.getValue());
        if (token instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken exp = (ExpiringOAuth2RefreshToken) token;
            body.put("expiration", exp.getExpiration());
        }
        return body;
    }
    
    public static OAuth2RefreshToken deserializeRefreshToken(Map data) {
        OAuth2RefreshToken toReturn;
        if (data.containsKey("expiration")) {
            toReturn = new ExpiringOAuth2RefreshToken((String) data.get("value"), 
                    (Date) data.get("expiration"));
        } else {
            toReturn = new OAuth2RefreshToken((String) data.get("value"));
        }
        return toReturn;
    }

    private static Set<String> listToSet(List list) {
        HashSet<String> set = new HashSet<String>();
        for (Object o : list) {
            set.add((String) o);
        }
        return set;
    }

    private static Collection<GrantedAuthority> deserializeAuthorities(Set<String> auths) {
        List<GrantedAuthority> toReturn = new ArrayList<GrantedAuthority>();
        for (String auth : auths) {
            toReturn.add(Right.valueOf(auth));
        }
        return toReturn;
    }
    
    private static Set<String> serializeAuthorities(Collection<GrantedAuthority> auths) {
        Set<String> toReturn = new HashSet<String>();
        for (GrantedAuthority auth : auths) {
            toReturn.add(auth.toString());
        }
        return toReturn;
    }

 
    
}
