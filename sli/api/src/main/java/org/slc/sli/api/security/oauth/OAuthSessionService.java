package org.slc.sli.api.security.oauth;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.ext.Provider;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.oauth2.provider.ClientToken;
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
@Provider
public class OAuthSessionService extends RandomValueTokenServices {

    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = OAuthTokenUtil.getOAuthAccessTokenCollectionName();
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = OAuthTokenUtil.getAccessTokenValidity();
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthSessionService.class);

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private TokenStore mongoTokenStore;

    @Autowired
    private OAuthTokenUtil util;
    
    @Autowired
    private ContextResolverStore contextResolverStore;
    
    @PostConstruct
    public void init() {
        setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        setSupportRefreshToken(false);
        setTokenStore(mongoTokenStore);
    }

    /**
     * This is where we verify the user's district has access to the client
     */
    @Override
    public OAuth2AccessToken createAccessToken(
            OAuth2Authentication authentication) throws AuthenticationException {
        
        validateAppAuthorization(authentication);
        return super.createAccessToken(authentication);
    }
    
    protected void validateAppAuthorization(OAuth2Authentication authentication) throws UnauthorizedClientException {
        Entity district = findUsersDistrict((SLIPrincipal) authentication.getUserAuthentication().getPrincipal());
        if (district != null) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("authId", "=", district.getEntityId()));
            query.addCriteria(new NeutralCriteria("authType", "=", "EDUCATION_ORGANIZATION"));
            Entity authorizedApps = repo.findOne("applicationAuthorization", query);
            
            query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("client_id", "=", authentication.getClientAuthentication().getClientId()));
            Entity appEntity = repo.findOne("application", query);
            assert appEntity != null;
            
            if (authorizedApps != null) {
                List<String> appIds = (List<String>) authorizedApps.getBody().get("appIds");
                if (!appIds.contains(appEntity.getEntityId())) {
                    throw new UnauthorizedClientException("District " + district.getEntityId() + " has not enabled application " + appEntity.getEntityId());
                }
            } else {
                LOGGER.warn("No authorized applications found for LEA {}", district.getEntityId());
            }
        }
    }

    private Entity findUsersDistrict(SLIPrincipal principal) {
        
        if (principal.getEntity() != null) {
            try {
                List<String> edOrgs = contextResolverStore.findResolver(EntityNames.TEACHER, EntityNames.EDUCATION_ORGANIZATION).findAccessible(principal.getEntity());
                for (String id : edOrgs) {
                    Entity entity = repo.findById(EntityNames.EDUCATION_ORGANIZATION, id);
                    List<String> category = (List<String>) entity.getBody().get("organizationCategories");
                    if (category.contains("Local Education Agency")) {
                        return entity;
                    }
                }
            } catch (IllegalArgumentException ex) {
                //this is what the resolver throws if it doesn't find any edorg data
                LOGGER.warn("Could not find an associated ed-org for {}.", principal.getExternalId());
            }
        } else {
            LOGGER.warn("Skipping LEA lookup for {} because no entity data was found.", principal.getExternalId());
        }
        LOGGER.warn("Could not find an associated LEA for {}.", principal.getExternalId());
        return null;
    }


    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue)
            throws AuthenticationException {

        Entity tokenEntity = findEntityForAccessToken(accessTokenValue);
        OAuth2Authentication toReturn = tokenEntity != null ? getOAuth2AuthenticationFromEntity(tokenEntity) : null;
        OAuth2AccessToken token = tokenEntity != null ? getOAuth2AccessTokenFromEntity(tokenEntity) : null;

        String time = Long.toString(System.currentTimeMillis());

        if (toReturn == null || token.getExpiration().before(new Date())) {
            LOGGER.debug("Invalid or expired access token {}", accessTokenValue);

            //Ideally we would throw an exception.  However, since this method is called
            //in a filter, it's too early in a chain for the Jersey ExceptionMapper to handle
            //properly.
            return new OAuth2Authentication(new ClientToken("UNKNOWN", "UNKNOWN", new HashSet<String>()), 
                    new AnonymousAuthenticationToken(time, time, Arrays.<GrantedAuthority>asList(Right.ANONYMOUS_ACCESS)));
        }
        return toReturn;
    }

    private OAuth2Authentication getOAuth2AuthenticationFromEntity(Entity entity) {
        Map authData = (Map) entity.getBody().get("authentication");
        return util.createOAuth2Authentication(authData);
    }

    private OAuth2AccessToken getOAuth2AccessTokenFromEntity(Entity entity) {
        Map accessTokenData = (Map) entity.getBody().get("accessToken");
        return util.deserializeAccessToken(accessTokenData);
    }

    private Entity findEntityForAccessToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        util.removeExpiredTokens();
        neutralQuery.addCriteria(new NeutralCriteria("token", "=", token));
        return repo.findOne(OAUTH_ACCESS_TOKEN_COLLECTION, neutralQuery);
    }

}
