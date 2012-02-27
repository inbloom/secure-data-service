package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.security.ApplicationResource;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.enums.Right;

/**
 * 
 * @author pwolf
 * 
 */
@Component
public class SliClientDetailService implements ClientDetailsService {
    
    @Autowired
    private EntityDefinitionStore store;
    
    private EntityService service;
    
    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("application");
        service = def.getService();
    }
    
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
        final String uuid = lookupIdFromClientId(clientId);
        
        if (uuid != null) {
            //EntityBody entity = service.get(uuid);
            
            EntityBody entity = SecurityUtil.sudoRun(new SecurityTask<EntityBody>() {
                @Override
                public EntityBody execute() {
                    return service.get(uuid);
                }
            });
            
            ApplicationDetails details = new ApplicationDetails();
            details.setClientId((String) entity.get("client_id"));
            details.setClientSecret((String) entity.get("client_secret"));
            details.setWebServerRedirectUri((String) entity.get("redirect_uri"));
            details.setIsScoped(false);
            details.setIsSecretRequired(true);
            
            String scope = (String) entity.get("scope");
            List<String> scopes = new ArrayList<String>();
            scopes.add(scope);
            details.setScope(scopes);
            
            // TODO: set authorities and grant types
            List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
            auths.add(Right.FULL_ACCESS);
            details.setAuthorities(auths);
            return details;
        } else {
            throw new OAuth2Exception("Could not find client with ID " + clientId);
        }
    }
    
    private String lookupIdFromClientId(final String clientId) {

        Iterable<String> results = SecurityUtil.sudoRun(new SecurityTask<Iterable<String>>() {
            @Override
            public Iterable<String> execute() {
                return service.list(0, 1, ApplicationResource.CLIENT_ID + "=" + clientId);
            }
        });
        if (results.iterator().hasNext()) {
            return results.iterator().next();
        }
        return null;
    }
    
}
