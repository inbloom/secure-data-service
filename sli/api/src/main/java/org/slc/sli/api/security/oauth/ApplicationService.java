package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

/**
 * 
 * Implements the ClientDetailsService interface provided by the Spring OAuth
 * 2.0 implementation.
 * 
 * @author shalka
 */
@Component
@Scope("request")
@Path("/apps")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ApplicationService implements ClientDetailsService {
    
    @Autowired
    private EntityDefinitionStore store;
    
    private EntityService service;
    
    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("application");
        setService(def.getService());
    }
    
    // Injector
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }
    
    // Injector
    public void setService(EntityService service) {
        this.service = service;
    }
    
    @POST
    public Response createApplication(EntityBody newApp) {
        newApp.put("client_id", IdGenerator.generateId(10));
        newApp.put("client_secret", IdGenerator.generateId(48));
        String id = service.create(newApp);
        EntityBody resObj = new EntityBody();
        resObj.put("id", id);
        
        return Response.status(Status.CREATED).entity(resObj).build();
    }
    
    @GET
    public List<EntityBody> getApplications() {
        List<EntityBody> results = new ArrayList<EntityBody>();
        Iterable<String> realmList = service.list(0, 1000);
        for (String id : realmList) {
            EntityBody result = service.get(id);
            results.add(result);
        }
        return results;
    }

	@Override
	public ClientDetails loadClientByClientId(String clientId)
			throws OAuth2Exception {
		ClientDetails details = new ApplicationDetails();
		// get the set of applications in mongo matching the specified client_id
		// field
		// check to make sure only 1 application was returned
		// if 1 application was returned --> populate 'details' with the
		// information retrieved from mongo
		// if more than 1 application was returned --> throw
		// InvalidClientException
		return details;
	}


	@GET
	@Path("{client_id}")
	public EntityBody getApplication(@PathParam("client_id") String clientId) {
		EntityBody body = null;
		// get application from mongo
		// store application into body
		return body;
	}

	@DELETE
	@Path("{client_id}")
	public void deleteApplication(@PathParam("client_id") String clientId) {
		// delete application from mongo
	}
}
