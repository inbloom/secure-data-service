package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeAuthenticationTokenHolder;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeClientToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Extends the RandomValueAuthorizationCodeServices class. Used for storing and removing
 * authorization codes as part of OAuth 2.0 implementation.
 * 
 * @author shalka
 * 
 */
@Component
public class MongoAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    
    /** Entity identifier **/
    private static final String OAUTH_AUTHORIZATION_CODE = "oauthAuthorizationCode";
    
    /**
     * Lifetime (duration of validity) of an Authorization Code in seconds.
     */
    private static final int AUTHORIZATION_CODE_VALIDITY = 300;
    
    @Autowired
    private UserLocator userLocator;
    
    @Autowired
    RolesToRightsResolver roleResolver;
    
    @Autowired
    private Repository<Entity> repo;
    
    @Autowired
    private EntityDefinitionStore store;
    
    @Autowired
    private SliClientDetailService clientDetailService;

    @Override
    protected void store(String code, UnconfirmedAuthorizationCodeAuthenticationTokenHolder authentication) {
        assert false;   //this shouldn't be used because we bypass the normal Spring oauth authorize call
    }
    
    protected void create(String clientId, String state, String samlId) {
        final EntityBody authorizationCode = new EntityBody();
        String redirectUri = clientDetailService.loadClientByClientId(clientId).getWebServerRedirectUri();
        long expiration = AUTHORIZATION_CODE_VALIDITY * 1000L;
        authorizationCode.put("expiration", new Date().getTime() + expiration);
        authorizationCode.put("redirectUri", redirectUri);
        authorizationCode.put("clientId", clientId);
        authorizationCode.put("samlId", samlId);
        authorizationCode.put("state", state);
        SecurityUtil.sudoRun(new SecurityTask<Boolean>() {
            @Override
            public Boolean execute() {
                getService().create(authorizationCode);
                return true;
            }
        });
    }
    
    public String createAuthorizationCodeForMessageId(String samlId, final SLIPrincipal principal) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("samlId", "=", samlId));
        
        
        Iterable<Entity> results = repo.findAll(OAUTH_AUTHORIZATION_CODE, neutralQuery);
        Entity e = results.iterator().next();
        
        final String id = e.getEntityId();
        EntityBody authorizationCode = SecurityUtil.sudoRun(new SecurityTask<EntityBody>() {
            @Override
            public EntityBody execute() {
                EntityBody authorizationCode = getService().get(id);
                String authCode = createAuthorizationCode();
                authorizationCode.put("value", authCode);
                authorizationCode.put("userId", principal.getExternalId());
                authorizationCode.put("userRoles", StringUtils.collectionToCommaDelimitedString(principal.getRoles()));
                authorizationCode.put("userRealm", principal.getRealm());
                authorizationCode.put("userName", principal.getName());
                authorizationCode.put("adminRealm", principal.getAdminRealm());

                getService().update(id, authorizationCode);
                return authorizationCode;
            }
        });
        
        UriBuilder uri = UriBuilder.fromUri(authorizationCode.get("redirectUri").toString());
        uri.queryParam("code", authorizationCode.get("value"));
        if (authorizationCode.get("state") != null) {
            uri.queryParam("state", authorizationCode.get("state"));
        }
        return uri.build().toString();
    }
    
    /**
     * Performs a lookup based on the specified authorization code, and invalidates the
     * authorization code by expiring the authorization code. No deletion operation is performed.
     */
    @Override
    protected UnconfirmedAuthorizationCodeAuthenticationTokenHolder remove(String code) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("value", "=", code));
        
        Iterable<Entity> results = repo.findAll(OAUTH_AUTHORIZATION_CODE, neutralQuery);
        final Map<String, Object> body = results.iterator().next().getBody();
        UnconfirmedAuthorizationCodeAuthenticationTokenHolder toReturn = null;
    
        long authCodeExpiration = Long.parseLong(body.get("expiration").toString());
        
        if (!OAuthTokenUtil.isTokenExpired(authCodeExpiration)) {
            ClientDetails client = clientDetailService.loadClientByClientId(body.get("clientId").toString());
            String state = "";
            Set<String> scope = new HashSet<String>();
            scope.addAll(client.getScope());
            UnconfirmedAuthorizationCodeClientToken clientToken = new UnconfirmedAuthorizationCodeClientToken(client.getClientId(), client.getClientSecret(), scope, state, body.get("redirectUri").toString());
            NeutralQuery neutralQuery2 = new NeutralQuery();
            neutralQuery2.addCriteria(new NeutralCriteria("_id", "=", body.get("userRealm").toString()));
            Entity realm = repo.findOne("realm", neutralQuery2);
            SLIPrincipal user = userLocator.locate((String) realm.getBody().get("regionId"), body.get("userId").toString());
            
            Set<String> roleNamesSet = StringUtils.commaDelimitedListToSet(body.get("userRoles").toString());
            user.setRoles(new ArrayList<String>(roleNamesSet));
            user.setName((String) body.get("userName"));
            user.setRealm(realm.getEntityId());
            user.setAdminRealm((String) body.get("adminRealm"));

            final List<String> roleNames = new ArrayList<String>();
            roleNames.addAll(roleNamesSet);
            Set<GrantedAuthority> authoritiesSet = SecurityUtil.sudoRun(new SecurityTask<Set<GrantedAuthority>>() {
                @Override
                public Set<GrantedAuthority> execute() {
                    return roleResolver.resolveRoles(body.get("userRealm").toString(), roleNames);
                }
            });
            ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.addAll(authoritiesSet);
           // Authentication authentication = new AnonymousAuthenticationToken(user.getId(), user, authorities);
            Authentication authentication =  new PreAuthenticatedAuthenticationToken(user, clientToken, authorities);
            toReturn = new UnconfirmedAuthorizationCodeAuthenticationTokenHolder(clientToken, authentication);
        }
        return toReturn;
    }
    
    /**
     * Gets the EntityService associated with the OAuth 2.0 session collection.
     * 
     * @return Instance of EntityService for performing collection operations.
     */
    public EntityService getService() {
        EntityDefinition defn = store.lookupByResourceName(OAUTH_AUTHORIZATION_CODE);
        return defn.getService();
    }
}
