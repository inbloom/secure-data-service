package org.slc.sli.api.resources.security;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.enums.Right;

/**
 *
 * Provides CRUD operations on registered application through the /tenants path.
 *
 * @author
 */
@Component
@Scope("request")
@Path("tenants")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class TenantResource extends DefaultCrudEndpoint {

    @Autowired
    private EntityDefinitionStore store;

    @Value("${landingzone.inbounddir}")
    private String inbounddir;
    
    @Value("${sli.tenant.ingestionServers}")
    private String ingestionServers;

    private String[] ingestionServerList;

    private Random random = new Random(System.currentTimeMillis());

    @PostConstruct
    protected void init() {
        ingestionServerList = ingestionServers.split(",");
    }

    public static final String UUID = "uuid";
    public static final String RESOURCE_NAME = "tenant";
    public static final String TENANT_ID = "tenantId";
    public static final String LZ = "landingZone";
    public static final String LZ_EDUCATION_ORGANIZATION = "educationOrganization";
    public static final String LZ_INGESTION_SERVER = "ingestionServer";
    public static final String LZ_PATH = "path";
    public static final String LZ_USER_NAMES = "userNames";
    public static final String LZ_DESC = "desc";

    @Autowired
    public TenantResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        store = entityDefs;
    }

    @POST
    public Response create(EntityBody newTenant, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to provision tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }


        // look up the tenantId; if it exists, add new LZs here to the existing list; otherwise, create

        // Create the query
        if (!newTenant.containsKey(LZ) || !newTenant.containsKey(TENANT_ID)) {
            EntityBody body = new EntityBody();
            body.put("message", "Required attributes (" + LZ + "," + TENANT_ID + ") not specified in POST.  "
                    + "add attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        String existingTenantId = createLandingZone(newTenant);
        
        if (null != existingTenantId) {
            // Construct the response
            String uri = ResourceUtil.getURI(uriInfo, "tenants", existingTenantId).toString();
            return Response.status(Status.CREATED).header("Location", uri).build();
        } else {
            return null; //TODO
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected String createLandingZone(EntityBody newTenant) {
        EntityService tenantService = store.lookupByResourceName(RESOURCE_NAME).getService();

        String tenantId = (String) newTenant.get(TENANT_ID);
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(TENANT_ID, "=", tenantId));

        List<Map<String, Object>> newLzs = (List<Map<String, Object>>) newTenant.get(LZ);
        
        //NOTE: OnboardingResource may only send in one at a time
        if (1 != newLzs.size())
            return null; //TODO: return error
        
        Map<String, Object> newLz = newLzs.get(0);
        String newEdOrg = (String) newLz.get(LZ_EDUCATION_ORGANIZATION);

        newLz.put(LZ_INGESTION_SERVER, randomIngestionServer());
        newLz.put(LZ_PATH, inbounddir + File.pathSeparatorChar + tenantId + "-" + newEdOrg);

        // look up ids of existing tenant entries
        List<String> existingIds = new ArrayList<String>();
        for (String id : tenantService.listIds(query)) {
            existingIds.add(id);
        }
        // If no tenant already exist, create
        if (existingIds.size() == 0) {
            tenantService.create(newTenant);
            return tenantId;
        }
        // If more than exists, something is wrong
        if (existingIds.size() > 1) {
            throw new RuntimeException("Internal error: multiple tenant entry with identical IDs");
        }

        String existingTenantId = existingIds.get(0);
        // combine lzs from existing tenant and new tenant entry, overwriting with values of new tenant entry if there is conflict.
        TreeSet allLandingZones = new TreeSet(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Map<String, Object> lz1 = (Map<String, Object>) o1;
                Map<String, Object> lz2 = (Map<String, Object>) o2;
                if (!lz1.containsKey(LZ_EDUCATION_ORGANIZATION) || !(lz1.get(LZ_EDUCATION_ORGANIZATION) instanceof String)) {
                    throw new RuntimeException("Badly formed tenant entry: " + lz1.toString());
                }
                if (!lz2.containsKey(LZ_EDUCATION_ORGANIZATION) || !(lz2.get(LZ_EDUCATION_ORGANIZATION) instanceof String)) {
                    throw new RuntimeException("Badly formed tenant entry: " + lz2.toString());
                }
                return ((String) lz1.get(LZ_EDUCATION_ORGANIZATION)).compareTo((String) lz2.get(LZ_EDUCATION_ORGANIZATION));
            }
        });

        Set<Map<String, Object>> all = (Set<Map<String, Object>>) allLandingZones; 
        for (Map<String, Object> lz : all) {
            if (lz.get(LZ_EDUCATION_ORGANIZATION).equals(newEdOrg))
                return null; //TODO: return error
        }

        EntityBody existingBody = tenantService.get(existingTenantId);
        List existingLandingZones = (List) existingBody.get(LZ);
        allLandingZones.addAll(existingLandingZones);
        
        List newLandingZones = (List) newTenant.get(LZ);
        allLandingZones.addAll(newLandingZones);

        existingBody.put(LZ, new ArrayList(allLandingZones));
        tenantService.update(existingTenantId, existingBody);
        return tenantId;
    }

    private String randomIngestionServer() {
        return ingestionServerList[random.nextInt(ingestionServerList.length)];
    }

    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context
            HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Looks up a specific application based on client ID, ie.
     * /api/rest/tenants/<tenantId>
     *
     * @param tenantId
     *            the client ID, not the "id"
     * @return the JSON data of the application, otherwise 404 if not found
     */
    @GET
    @Path("{" + UUID + "}")
    public Response read(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.read(uuid, headers, uriInfo);
    }

    @DELETE
    @Path("{" + UUID + "}")
    public Response delete(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to delete tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.delete(uuid, headers, uriInfo);
    }

    @PUT
    @Path("{" + UUID + "}")
    public Response update(@PathParam(UUID) String uuid, EntityBody tenant,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to provision tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.update(uuid, tenant, headers, uriInfo);
    }

}
