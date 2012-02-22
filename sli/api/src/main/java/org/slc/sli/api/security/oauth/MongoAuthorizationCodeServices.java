package org.slc.sli.api.security.oauth;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeAuthenticationTokenHolder;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeClientToken;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

@Component
public class MongoAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    
    private static final String OAUTH_VERIFICATION_CODE = "oauthVerificationCode";
        
    @Autowired
    private EntityRepository repo;
    
    @Autowired
    private EntityDefinitionStore store;
    
    /**
     * Performs a lookup based on user and client authentication (in unconfirmed authorization code
     * authentication token holder), and stores the authorization 'code' into Mongo.
     */
    @Override
    protected void store(String code, UnconfirmedAuthorizationCodeAuthenticationTokenHolder authentication) {
        final EntityBody verificationCode = OAuthTokenUtil.mapAuthorizationCode(code, authentication.getClientAuthentication().getRequestedRedirect(), authentication.getUserAuthentication().getName());
        
        SecurityUtil.sudoRun(new SecurityTask<Boolean>() {
            @Override
            public Boolean execute() {
                getService().create(verificationCode);
                return true;
            }
        });       
    }
    
    /**
     * Performs a lookup based on the specified authorization code, and invalidates the
     * authorization code by expiring the authorization code. No deletion operation is performed.
     */
    @Override
    protected UnconfirmedAuthorizationCodeAuthenticationTokenHolder remove(String code) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_VERIFICATION_CODE,
                new Query(Criteria.where("body.value").is(code)), 0, 1);
        UnconfirmedAuthorizationCodeClientToken clientAuthentication = null;
        Authentication userAuthentication = null;
        if (results != null) {
            for (Entity verifyCode : results) {
                Map<String, Object> body = verifyCode.getBody();
                long authCodeExpiration = Long.parseLong(body.get("expiration").toString());
                
                if (!OAuthTokenUtil.isTokenExpired(authCodeExpiration)) {
                    //String clientId = (String) body.get("clientAuthn.clientId");
                    //String clientSecret = (String) body.get("clientAuthn.clientSecret");
                    Set<String> clientScope = new HashSet<String>();
                    clientScope.add("ENABLED");
                    //String redirectUri = (String) body.get("clientAuthn.redirectUri");
                    
                    clientAuthentication = new UnconfirmedAuthorizationCodeClientToken("admin", "sb70uDUEYK1IkE5LB2xdBkTJRIQNhBnaOYu1ig5EZW3UwpP4",
                            clientScope, "", (String) body.get("redirectUri"));
                    userAuthentication = SecurityContextHolder.getContext().getAuthentication();
                    
                    //EntityBody verificationCode = OAuthTokenUtil.mapAuthorizationCode(code);
                    //body.put("verificationCode", verificationCode);
                    //getService().update(oauth2Session.getEntityId(), (EntityBody) body);
                }
            }
        }
        return new UnconfirmedAuthorizationCodeAuthenticationTokenHolder(clientAuthentication, userAuthentication);
    }
    
    /**
     * Gets the EntityService associated with the OAuth 2.0 session collection.
     * 
     * @return Instance of EntityService for performing collection operations.
     */
    public EntityService getService() {
        EntityDefinition defn = store.lookupByResourceName(OAUTH_VERIFICATION_CODE);
        return defn.getService();
    }
}
