package org.slc.sli.api.security.oauth;

import java.util.Map;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeAuthenticationTokenHolder;
import org.springframework.stereotype.Component;

/**
 * Extends the RandomValueAuthorizationCodeServices class. Used for storing and removing
 * authorization codes as part of OAuth 2.0 implementation.
 * 
 * @author shalka
 * 
 */
@Component
public class MongoAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    
    private static final String OAUTH_AUTHORIZATION_CODE = "oauth_code";
    
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
        final EntityBody verificationCode = new EntityBody();
        verificationCode.put("code", code);

        verificationCode.put("authorizationBlob", OAuthTokenUtil.serialize(authentication));
        verificationCode.put("expiration", System.currentTimeMillis() + (5 * 60 * 1000));
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
        Iterable<Entity> results = repo.findByQuery(OAUTH_AUTHORIZATION_CODE,
                new Query(Criteria.where("body.code").is(code)), 0, 1);
        UnconfirmedAuthorizationCodeAuthenticationTokenHolder toReturn = null;
        if (results != null) {
            for (Entity verifyCode : results) {
                Map<String, Object> body = verifyCode.getBody();
                long authCodeExpiration = Long.parseLong(body.get("expiration").toString());
                
                if (!OAuthTokenUtil.isTokenExpired(authCodeExpiration)) {
                  byte[] objectData = (byte[]) body.get("authorizationBlob");
                  toReturn = (UnconfirmedAuthorizationCodeAuthenticationTokenHolder) OAuthTokenUtil.deserialize(objectData);
                }
            }
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
