package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

@Component
@Scope("request")
@Path("/custome")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class CustomRoleResource {
    
    /*
     * TODO:
     * -Ensure that there can only be one of these per realm/tenant
     * -Ensure that default roles can't be changed
     * -Should we change realmId to be the realm mongo ID?
     */
    
    @Autowired
    private EntityDefinitionStore store;

    private EntityService service;
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("customRole");
        service = def.getService();
    }

    @GET
    public Response readAll() {
        if (!SecurityUtil.hasRight(Right.CRUD_ROLE)) {
            return SecurityUtil.forbiddenResponse();
        }
        
        NeutralQuery realmQuery = new NeutralQuery();
        realmQuery.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getTenantId()));
        realmQuery.addCriteria(new NeutralCriteria("edOrg", NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getEdOrg()));
        Entity realm = repo.findOne("realm", realmQuery);
        
        
        List<Map> results = new ArrayList<Map>();
        NeutralQuery customRoleQuery = new NeutralQuery();
        customRoleQuery.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getTenantId()));
        customRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.OPERATOR_EQUAL, realm.getEntityId()));
        Entity customRole = repo.findOne("customRole", customRoleQuery);
        results.add(customRole.getBody());
        return Response.ok(results).build();
    }
    
    @GET
    @Path("{id}")
    public Response read(@PathParam("id") String id) {
        if (!SecurityUtil.hasRight(Right.CRUD_ROLE)) {
            return SecurityUtil.forbiddenResponse();
        }
        EntityBody customRole = service.get(id);
        return Response.ok(customRole).build();
    }
    
    @POST
    public Response createCustomeRole(EntityBody newCustomRole, @Context final UriInfo uriInfo) {
        SecurityUtil.ensureAuthenticated();
        if (!SecurityUtil.hasRight(Right.CRUD_ROLE)) {
            return SecurityUtil.forbiddenResponse();
        }
        
        String id = service.create(newCustomRole);
        String uri = uriToString(uriInfo) + "/" + id;
        return Response.status(Status.CREATED).header("Location", uri).build();
    }
    
    @PUT
    @Path("{id}")
    public Response updateCustomRole(@PathParam("id") String id, EntityBody updated) {
        if (!SecurityUtil.hasRight(Right.CRUD_ROLE)) {
            return SecurityUtil.forbiddenResponse();
        }

        if(service.update(id, updated)) {
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }
    
    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }

    @DELETE
    @Path("{id}")
    public Response deleteCustomeRole(@PathParam("id") String id) {
        if (!SecurityUtil.hasRight(Right.CRUD_ROLE)) {
            return SecurityUtil.forbiddenResponse();
        }
        service.delete(id);
        return Response.status(Status.NO_CONTENT).build();
    }
    
}
