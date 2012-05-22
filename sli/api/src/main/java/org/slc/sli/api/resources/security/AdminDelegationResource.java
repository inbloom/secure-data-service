package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.context.resolver.EdOrgToChildEdOrgNodeFilter;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;


/**
 * Endpoints to access admin delegation data.
 */
@Component
@Scope("request")
@Path("/adminDelegation")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class AdminDelegationResource {

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    Repository<Entity> repo;

    @Autowired
    EdOrgToChildEdOrgNodeFilter edOrgNodeFilter;

    private EntityService service;

    public static final String RESOURCE_NAME = "adminDelegation";
    public static final String LEA_ID = "localEdOrgId";

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
        this.service = def.getService();
    }

    /**
     * Get admin delegation records for principals Education Organization.
     * If SEA admin method returns a list of delegation records for child LEAs.
     * If LEA admin method returns a list containing only the delegation record for the LEA.
     *
     * @return A list of admin delegation records.
     */
    @GET
    public Response getDelegations() {

        if (SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {

            String edOrg = SecurityUtil.getEdOrg();
            if (edOrg == null) {
                throw new InsufficientAuthenticationException("No edorg exists on principal.");
            }

            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(LEA_ID, NeutralCriteria.CRITERIA_IN,
                    edOrgNodeFilter.getChildEducationOrganizations(edOrg)));
            for (Entity entity : repo.findAll(RESOURCE_NAME, query)) {
                entity.getBody().put("id", entity.getEntityId());
                results.add(entity.getBody());
            }

            return Response.ok(results).build();

        } else if (SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ)) {
            Entity entity = getEntity();
            if (entity == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.status(Status.OK).entity(Arrays.asList(entity.getBody())).build();

        }

        return SecurityUtil.forbiddenResponse();

    }


    /**
     * Set the admin delegation record for an LEA admin user's EdOrg.
     *
     * @param body Admin delegation record to be written.
     * @return NO_CONTENT on success. BAD_REQUEST or FORBIDDEN on failure.
     */
    @PUT
    @Path("myEdOrg")
    public Response setLocalDelegation(EntityBody body) {
        if (!SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ)) {
            return SecurityUtil.forbiddenResponse();
        }

        //verifyBodyEdOrgMatchesPrincipalEdOrg
        if (body == null || !body.containsKey(LEA_ID) || !body.get(LEA_ID).equals(SecurityUtil.getEdOrg())) {
            EntityBody response = new EntityBody();
            response.put("message", "Entity EdOrg must match principal's EdOrg when writing delegation record.");
            return Response.status(Status.BAD_REQUEST).entity(response).build();
        }

        Entity entity = getDelegationRecordForPrincipal();
        if (entity == null) {

            if (service.create(body).isEmpty()) {
                return Response.status(Status.BAD_REQUEST).build();
            } else {
                return Response.status(Status.CREATED).build();
            }

        } else {

            if (!service.update(entity.getEntityId(), body)) {
                return Response.status(Status.BAD_REQUEST).build();
            }

        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @POST
    public Response create(EntityBody body) {
        return setLocalDelegation(body);
    }

    @GET
    @Path("myEdOrg")
    public Response getSingleDelegation() {
        Entity entity = getEntity();
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(entity).build();
    }


    private Entity getDelegationRecordForPrincipal() {
        String edOrg = SecurityUtil.getEdOrg();
        if (edOrg == null) {
            throw new InsufficientAuthenticationException("No edorg exists on principal.");
        }

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(LEA_ID, "=", edOrg));
        return repo.findOne(RESOURCE_NAME, query);
    }


    private Entity getEntity() {
        if (SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ)) {

            Entity entity = getDelegationRecordForPrincipal();
            if (entity != null) {
                entity.getBody().put("id", entity.getEntityId());
                return entity;
            }

        }
        return null;
    }
}
