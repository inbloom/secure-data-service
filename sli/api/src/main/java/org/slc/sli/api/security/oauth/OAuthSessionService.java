package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RandomValueTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 * Handles Authorization and Resource Server token services by extending
 * RandomValueTokenServices.
 * 
 * @author shalka
 * 
 */
@Component
public class OAuthSessionService extends RandomValueTokenServices {

    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = OAuthTokenUtil.getOAuthAccessTokenCollectionName();
    private static final int REFRESH_TOKEN_VALIDITY_SECONDS = OAuthTokenUtil.getRefreshTokenValidity(); 
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = OAuthTokenUtil.getAccessTokenValidity();
    
    @Autowired
    private EntityRepository repo;
    
    @Autowired
    private EntityDefinitionStore store;
    
    @Autowired
    private TokenStore mongoTokenStore;

    @Autowired
    private RolesToRightsResolver resolver;
    
    @PostConstruct
    public void init() {
        setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        setSupportRefreshToken(true);
        setTokenStore(mongoTokenStore);
    }
    
    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue)
            throws AuthenticationException {
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION, new Query(Criteria.where("body.token").is(accessTokenValue)), 0, 1);
        for (Entity oauth2Session : results) {
            
            //Since our granted authorities come from the securityContext.xml, they're not getting 
            //transformed into actual SLI Rights.  Here we recreate a UsernamePasswordAuthenticationToken with the SLI Rights
            OAuth2Authentication auth = (OAuth2Authentication) OAuthTokenUtil.deserialize((byte[]) oauth2Session.getBody().get("authenticationBlob"));
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            for (GrantedAuthority ga : auth.getUserAuthentication().getAuthorities()) {
                if (Right.valueOf(ga.getAuthority()) != null) {
                    authorities.add(Right.valueOf(ga.getAuthority()));
                }
            }
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth.getUserAuthentication();
            SLIPrincipal principal = new SLIPrincipal();
            principal.setName(token.getName());
            principal.setRealm("dc=slidev,dc=org");
            principal.setEntity(new MongoEntity("system_entity", new HashMap<String, Object>()));
            UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken(principal,
                    token.getCredentials(),
                    authorities);
            auth = new OAuth2Authentication(auth.getClientAuthentication(), newToken);
            
            return auth;
        }
        return null;
    }
    
    /**
     * Method called by SAML consumer. Performs a lookup in Mongo to find
     * OAuth2Authentication object corresponding to the original SAML message
     * ID, and stores information about the authenticated user into that object.
     * The returned String is NOT url encoded.
     * 
     * @param originalMsgId
     *            Unique identifier of SAML message sent to disco.
     * @param credential
     *            String representing Identity Provider issuer.
     * @param principal
     *            SLIPrincipal representing the authenticated user.
     * @return String representing the composed redirect URI with verification
     *         code and request token as parameters in link.
     */
    public String userAuthenticated(String originalMsgId, String issuer, SLIPrincipal principal) {
    /*    Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.samlMessageId").is(originalMsgId)), 0, 1);
        if (results != null) {
            for (Entity oauthSession : results) {
                // StringBuilder url = new StringBuilder();
                
                Map<String, Object> body = oauthSession.getBody();
                
                EntityBody sliPrincipal = OAuthTokenUtil.mapSliPrincipal(principal);
                body.put("userAuthn", sliPrincipal);
                
                getService().update(oauthSession.getEntityId(), (EntityBody) body);
                
                // it might be pertinent to do more in terms of checking the
                // client and user authentication objects for
                // consistency/validity
                @SuppressWarnings("unchecked")
                Map<String, Object> clientAuthentication = (Map<String, Object>) body.get("clientAuthn");
                
                String clientRedirectUri = (String) clientAuthentication.get("redirectUri");
                Map<String, Object> parameterMap = new HashMap<String, Object>();
                parameterMap.put("state", (String) body.get("requestToken"));
                parameterMap.put("code", (String) body.get("verificationCode"));
                // String requestToken = (String) body.get("requestToken");
                // String verificationCode = (String) body.get("verificationCode");
                
                // URI url = UriBuilder.fromUri(clientRedirectUri).buildFromMap(parameterMap);
                // url.append(clientRedirectUri);
                // url.append("?requestToken=" + requestToken);
                // url.append("&verificationCode=" + verificationCode);
                // return url.toString();
                return UriBuilder.fromUri(clientRedirectUri).buildFromMap(parameterMap).toString();
            }
        }*/
        return null;
    }
    
    /**
     * Method called by SAML consumer. Performs a lookup in Mongo to find
     * oauthSession correponding to the requestToken, and stores the SAML
     * message ID into that object.
     * 
     * @param requestToken
     *            Value of token granted to the application (signifying request
     *            for access token).
     * @param messageId
     *            SAML message unique identifier.
     */
    public void storeSamlMessageId(String requestToken, String messageId) {
       /* Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.requestToken").is(requestToken)), 0, 1);
        if (results != null && messageId.length() > 0) {
            for (Entity oauthSession : results) {
                Map<String, Object> body = oauthSession.getBody();
                body.put("samlMessageId", messageId);
                getService().update(oauthSession.getEntityId(), (EntityBody) body);
            }
        }*/
    }
    
    /**
     * Gets the EntityService associated with the OAuth 2.0 session collection.
     * 
     * @return Instance of EntityService for performing collection operations.
     */
    private EntityService getService() {
        EntityDefinition defn = store.lookupByResourceName(OAUTH_ACCESS_TOKEN_COLLECTION);
        return defn.getService();
    }
}
