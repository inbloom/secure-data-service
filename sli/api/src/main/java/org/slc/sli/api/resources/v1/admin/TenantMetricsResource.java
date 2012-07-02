package org.slc.sli.api.resources.v1.admin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * TODO: add javadoc
 *
 */
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
@Path(PathConstants.V1 + "/" + "tenant_metrics")
public class TenantMetricsResource {

    @Autowired
    protected Repository<Entity> repo;

    @GET
    public Response getTenantMetrics(@Context final UriInfo uriInfo) {

        SecurityUtil.ensureAuthenticated();

        // Only an SLC Operator (with admin rights) has access to this information.
        if (!(SecurityUtil.hasRight(Right.ADMIN_ACCESS) && SecurityUtil.hasRole(RoleInitializer.SLC_OPERATOR))) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view tenant metrics.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        DBCollection tenants = repo.getCollection("tenant");

        Map<String, CollectionMetrics> metrics = new HashMap<String, CollectionMetrics>();
        DBCursor cursor = tenants.find();

        while (cursor.hasNext()) {

            DBObject obj = cursor.next();
            if (obj.containsField("body") && ((DBObject) obj.get("body")).containsField("tenantId")) {

                String id = (String) ((DBObject) obj.get("body")).get("tenantId");
                CollectionMetrics tenantMetrics = MetricsResourceHelper.getAllCollectionMetrics(repo,
                        "metaData.tenantId", id);

                tenantMetrics.put("== Totals ==", tenantMetrics.getTotals());
                metrics.put(id, tenantMetrics);
            }
        }

        CollectionMetric totals = new CollectionMetric(0, 0.0);
        for (Map.Entry<String, CollectionMetrics> c : metrics.entrySet()) {
            if (c.getKey().equals("== Totals ==")) {
                continue;
            }
            totals.add(c.getValue().getTotals());
        }
        metrics.put("== Totals ==", new CollectionMetrics().aggregate("== Totals ==", totals.entityCount, totals.size));

        return Response.ok(metrics).build();
    }

    @GET
    @Path("{id}")
    public Response getTenantMetrics(@PathParam("id") final String tenantId, @Context final UriInfo uriInfo) {

        SecurityUtil.ensureAuthenticated();

        // Access for users with admin rights and (either the SLC_OPERATOR or an tenant-level ADMIN)
        if (!(SecurityUtil.hasRight(Right.ADMIN_ACCESS) && (SecurityUtil.hasRole(RoleInitializer.SLC_OPERATOR) || SecurityUtil
                .getTenantId().equals(tenantId)))) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view tenant metrics.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        Map<String, CollectionMetrics> metrics = new HashMap<String, CollectionMetrics>();
        CollectionMetrics collMetrics = MetricsResourceHelper.getAllCollectionMetrics(repo, "metaData.tenantId",
                tenantId);
        collMetrics.put("== Totals ==", collMetrics.getTotals());

        metrics.put(tenantId, collMetrics);

        CollectionMetric totals = new CollectionMetric(0, 0.0);
        for (Map.Entry<String, CollectionMetrics> c : metrics.entrySet()) {
            if (c.getKey().equals("== Totals ==")) {
                continue;
            }
            totals.add(c.getValue().getTotals());
        }

        metrics.put("== Totals ==", new CollectionMetrics().aggregate("== Totals ==", totals.entityCount, totals.size));

        return Response.ok(metrics).build();
    }
}
