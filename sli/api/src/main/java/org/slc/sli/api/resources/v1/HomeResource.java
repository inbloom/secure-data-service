package org.slc.sli.api.resources.v1;

//
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.Home;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.common.constants.v1.PathConstants;
import org.slc.sli.domain.Entity;

/**
 *
 * Provides initial information for a user.This includes providing different links to self and associated
 * resources.
 *
 * @author pghosh
 *
 */
@Path(PathConstants.V1 + "/" + "home")
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
public class HomeResource {

    final EntityDefinitionStore entityDefs;

    @Autowired
    HomeResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }

    /**
     * Provides a set of initial information when a user logs in. This
     * includes a self link and links to resources with which the user
     * is associated.
     *
     * @param uriInfo
     *              URI information including path and query parameters
     * @return A list of links applicable to the user currently logged in.
     */
    @GET
    public Response getHomeUri(@Context final UriInfo uriInfo) {

        Home home = null;

        // get the entity ID and EntityDefinition for user
        Pair<String, EntityDefinition> pair = this.getEntityInfoForUser();
        if (pair != null) {
            String userId = pair.getLeft();
            EntityDefinition defn = pair.getRight();

            EntityBody body = new EntityBody();
            body.put("id", userId);

            // prepare a list of links with the self link
            List<EmbeddedLink> links = ResourceUtil.getLinks(this.entityDefs, defn, body, uriInfo);

            // create a final map of links to relevant links
            HashMap<String, Object> linksMap = new HashMap<String, Object>();
            linksMap.put(ResourceConstants.LINKS, links);

            // return as browser response
            home = new Home(defn.getStoredCollectionName(), linksMap);
        } else {
            throw new AccessDeniedException("No entity mapping found for user");
        }

        return Response.ok(home).build();
    }

    /**
     * Analyzes security context to get ID and EntityDefinition for user.
     *
     * @return Pair containing ID and EntityDefinition from security context
     */
    private Pair<String, EntityDefinition> getEntityInfoForUser() {
        Pair<String, EntityDefinition> pair = null;

        // get the Entity for the logged in user
        Entity entity = ResourceUtil.getSLIPrincipalFromSecurityContext().getEntity();
        if (entity != null && !entity.getEntityId().equals("-133")) {
            EntityDefinition entityDefinition = this.entityDefs.lookupByEntityType(entity.getType());
            pair = Pair.of(entity.getEntityId(), entityDefinition);
        }

        return pair;
    }
}
