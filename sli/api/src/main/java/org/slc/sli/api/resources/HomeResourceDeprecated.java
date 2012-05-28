package org.slc.sli.api.resources;

//
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.Home;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.domain.Entity;

/**
 * Jersey resource for home entity and associations.
 *
 */
@Path("home")
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.XML_MEDIA_TYPE, Resource.SLC_XML_MEDIA_TYPE,
    Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE, Resource.SLC_LONG_XML_MEDIA_TYPE })
@Deprecated
public class HomeResourceDeprecated {

    private static final Logger LOG = LoggerFactory.getLogger(HomeResourceDeprecated.class);

    final EntityDefinitionStore entityDefs;

    @Autowired
    HomeResourceDeprecated(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }

    /**
     * Returns the initial information when a user logs in.
     * This includes a self link for the user's info, i.e. /staff/{GUID}.
     * In addition, there may be links for associations such as
     * /staff-educationOrganization-associations/{GUID}, and a link
     * for aggregation such as /aggregation.
     *
     * @response.representation.200.mediaType application/json by default. application/xml if
     *                                        specified.
     * @param uriInfo
     * @return Response
     */
    @GET
    public Response getHomeUri(@Context final UriInfo uriInfo) {

        Home home = null;

        // get the entity ID and EntityDefinition for user
        Pair<String, EntityDefinition> pair = this.getEntityInfoForUser();
        if (pair != null) {
            String userId = pair.getLeft();
            EntityDefinition defn = pair.getRight();

            // prepare a list of links with the self link
            List<EmbeddedLink> links = ResourceUtil.getSelfLink(uriInfo, userId, defn);

            // add links for all of the entity's associations for this ID
            links.addAll(ResourceUtil.getAssociationsLinks(this.entityDefs, defn, userId, uriInfo));

            // add the aggregation link
            links.addAll(ResourceUtil.getAggregateLink(uriInfo));

            // create a final map of links to relevant links
            HashMap<String, Object> linksMap = new HashMap<String, Object>();
            linksMap.put(ResourceConstants.LINKS, links);

            // return as browser response
            home = new Home(defn.getStoredCollectionName(), linksMap);
        } else {
            throw new InsufficientAuthenticationException("No entity mapping found for user");
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
        if (entity != null) {
            EntityDefinition entityDefinition = this.entityDefs.lookupByEntityType(entity.getType());
            pair = Pair.of(entity.getEntityId(), entityDefinition);
        }

        return pair;
    }
}
