package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
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
@Path("/api/rest/apps")
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ApplicationService implements ClientDetailsService {

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
	@Path("/")
	public List<EntityBody> getApplications() {
		List<EntityBody> apps = new ArrayList<EntityBody>();
		// get all applications from mongo
		// add the applications to the HashSet
		return apps;
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
