package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Resources available to administrative apps during the onboarding and provisioning process.
 */
@Component
@Scope("request")
@Path("/onboarding")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class OnboardingResource {

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    Repository<Entity> repo;

	private static final String STATE_EDORG_ID = "stateOrganizationId";
	private static final String EDORG_INSTITUTION_NAME = "nameOfInstitution";
	private static final String ADDRESSES = "address";
	private static final String ADDRESS_STREET = "streetNumberName";
	private static final String ADDRESS_CITY = "city";
	private static final String ADDRESS_STATE_ABRV = "stateAbbreviation";
	private static final String ADDRESS_POSTAL_CODE = "postalCode";
	private static final String CATEGORIES = "organizationCategories";  // 'State Education Agency'

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(EntityNames.EDUCATION_ORGANIZATION);
    }

    /**
     * Provision a landing zone for the provide educational organization.
     *
     * @QueryParameter stateOrganizationId -- the unique identifier for this ed org
     * @QueryParameter tenantId -- the tenant ID for this edorg.
     */
    @POST
    @Path("Provision")
    public Response provision(
    	@QueryParam(STATE_EDORG_ID) String orgId,
    	@QueryParam(ResourceConstants.ENTITY_METADATA_TENANT_ID) String tenantId,
    	@Context final UriInfo uriInfo) {

    	// TODO -- do all provisioning here.
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }

	/**
	 * Create an EdOrg if it does not exists.
	 */
    // @TODO -- this will become a method and not a resource.  The top level 'Provision' resource will call
    //    	into this.
    @POST
	@Path("EdOrg/{" + STATE_EDORG_ID + "}")
    public Response createEdOrg(@PathParam(STATE_EDORG_ID) String orgId, @Context final UriInfo uriInfo) {

    	// Ensure the user is an admin.
        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to provision a landing zone.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

	    EntityBody body = new EntityBody();
		body.put(STATE_EDORG_ID, orgId);
		body.put(EDORG_INSTITUTION_NAME, orgId);

		List<String> categories = new ArrayList<String>();
		categories.add("State Education Agency");
		body.put(CATEGORIES, categories);

		List<Map<String, String>> addresses = new ArrayList<Map<String, String>>();
		Map<String, String> address = new HashMap<String, String>();
		address.put(ADDRESS_STREET, "unknown");
		address.put(ADDRESS_CITY, "unknown");
		address.put(ADDRESS_STATE_ABRV, "NC");
		address.put(ADDRESS_POSTAL_CODE, "27713");
		addresses.add(address);

		body.put(ADDRESSES, addresses);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(STATE_EDORG_ID, "=", orgId));

        // Look up the user's tenantID
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> meta = new HashMap<String, Object>();
        meta.put(ResourceConstants.ENTITY_METADATA_TENANT_ID, principal.getTenantId());


		if (repo.findOne(EntityNames.EDUCATION_ORGANIZATION, query) != null) {
	        return Response.status(Status.CONFLICT).entity(orgId).build();
		}

		Entity e = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);
        return Response.status(Status.CREATED).entity(e).build();
    }
}
