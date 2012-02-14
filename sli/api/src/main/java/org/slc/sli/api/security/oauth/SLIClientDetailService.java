package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.representation.EntityBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * 
 * @author pwolf
 *
 */
public class SLIClientDetailService implements ClientDetailsService {

    @Autowired
    private ApplicationService appService;
    
    //used by unit test
    protected void setApplicationServer(ApplicationService service) {
        this.appService = service;
    }
    
    @Override
    public ClientDetails loadClientByClientId(String clientId)
            throws OAuth2Exception {
        Response resp = appService.getApplication(clientId);
        
        if (resp.getStatus() == Status.OK.getStatusCode()) {
            EntityBody result = (EntityBody) resp.getEntity();
            ApplicationDetails details = new ApplicationDetails();
            details.setClientId((String) result.get("client_id"));
            details.setClientSecret((String) result.get("client_secret"));
            details.setWebServerRedirectUri((String) result.get("redirect_uri"));
            details.setIsScoped(true);
            details.setIsSecretRequired(true);
            
            String scope = (String) result.get("scope");
            List<String> scopes = new ArrayList<String>();
            scopes.add(scope);
            details.setScope(scopes);
            
            //TODO: set authorities and grant types
            return details;
        } else {
            throw new OAuth2Exception("Could not find client with ID " + clientId);
        }
    }

}
