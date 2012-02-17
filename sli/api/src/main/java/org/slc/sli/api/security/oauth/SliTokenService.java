package org.slc.sli.api.security.oauth;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.provider.token.RandomValueTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Handles Authorization and Resource Server token services by extending
 * RandomValueTokenServices.
 * 
 * @author shalka
 * 
 */
@Component
public class SliTokenService extends RandomValueTokenServices {
    private static final int REFRESH_TOKEN_VALIDITY_SECONDS = 3600; // 1 hour
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 900;   // 15 minutes
    private static final String OAUTH_SESSION_COLLECTION = OAuthTokenUtil.getOAuthCollectionName();
    
    @Autowired
    private EntityRepository repo;
    
    @Autowired
    private EntityDefinitionStore store;
    
    @Autowired
    private TokenStore sliTokenStore;
    
    private EntityService service;
    
    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(OAUTH_SESSION_COLLECTION);
        setService(def.getService());
        setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        setSupportRefreshToken(true);
        setTokenStore(sliTokenStore);
    }
    
    public void setService(EntityService service) {
        this.service = service;
    }
    
    /**
     * Method called by SAML consumer. Performs a lookup in Mongo to find
     * OAuth2Authentication object corresponding to the original SAML message
     * ID, and stores information about the authenticated user into that object.
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
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.samlMessageId").is(originalMsgId)), 0, 1);
        if (results != null) {
            for (Entity oauthSession : results) {
                StringBuilder url = new StringBuilder();
                Map<String, Object> body = oauthSession.getBody();
                
                EntityBody sliPrincipal = OAuthTokenUtil.mapSliPrincipal(principal);
                body.put("userAuthn", sliPrincipal);
                
                service.update(oauthSession.getEntityId(), (EntityBody) body);
                
                // it might be pertinent to do more in terms of checking the
                // client and user authentication objects for
                // consistency/validity
                @SuppressWarnings("unchecked")
                Map<String, Object> clientAuthentication = (Map<String, Object>) body.get("clientAuthn");
                
                String clientRedirectUri = (String) clientAuthentication.get("redirectUri");
                
                String requestToken = (String) body.get("requestToken");
                String verificationCode = (String) body.get("verificationCode");
                
                url.append(clientRedirectUri);
                url.append("?requestToken=" + requestToken);
                url.append("&verificationCode=" + verificationCode);
                return url.toString();
            }
        }
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
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.requestToken").is(requestToken)), 0, 1);
        if (results != null && messageId.length() > 0) {
            for (Entity oauthSession : results) {
                Map<String, Object> body = oauthSession.getBody();
                body.put("samlMessageId", messageId);
                service.update(oauthSession.getEntityId(), (EntityBody) body);
            }
        }
    }
}
