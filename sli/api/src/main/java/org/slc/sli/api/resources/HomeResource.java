package org.slc.sli.api.resources;

//
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.Home;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.domain.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Jersey resource for home entity and associations.
 * 
 */
@Path("home")
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.XML_MEDIA_TYPE })
public class HomeResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(HomeResource.class);
    
    final EntityDefinitionStore entityDefs;
    
    @Autowired
    HomeResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }
    
    /**
     * Returns the initial information when a user logs in.
     * 
     */
    @GET
    @Produces({ Resource.JSON_MEDIA_TYPE })
    public Response getHomeUri(@Context final UriInfo uriInfo) {
        
        // TODO: refactor common code from getHomeUri and GetHomeUriXML

        // get the entity ID and EntityDefinition for user
        Entry<String, EntityDefinition> entityInfo = this.getEntityInfoForUser().entrySet().iterator().next();
        String userId = entityInfo.getKey();
        EntityDefinition defn = entityInfo.getValue();
        
        // prepare a list of links with the self link
        List<EmbeddedLink> links = ResourceUtil.getSelfLink(uriInfo, userId, defn);
        
        // add links for all of the entity's associations for this ID
        links.addAll(ResourceUtil.getAssociationsLinks(this.entityDefs, defn, userId, uriInfo));
        
        //add the aggregate link
        links.addAll(ResourceUtil.getAggregateLink(uriInfo));
        
        // create a final map of links to relevant links
        HashMap<String, Object> linksMap = new HashMap<String, Object>();
        linksMap.put(ResourceUtil.LINKS, links);
        
        // return as browser response
        return Response.ok(linksMap).build();
    }
    
    /**
     * Returns the initial information when a user logs in.
     * 
     */
    @GET
    @Produces({ Resource.XML_MEDIA_TYPE })
    public Response getHomeUriXML(@Context final UriInfo uriInfo) {
        
        // TODO: refactor common code from getHomeUri and GetHomeUriXML

        // get the entity ID and EntityDefinition for user
        Entry<String, EntityDefinition> entityInfo = this.getEntityInfoForUser().entrySet().iterator().next();
        String userId = entityInfo.getKey();
        EntityDefinition defn = entityInfo.getValue();
        
        // prepare a list of links with the self link
        List<EmbeddedLink> links = ResourceUtil.getSelfLink(uriInfo, userId, defn);
        
        // add links for all of the entity's associations for this ID
        links.addAll(ResourceUtil.getAssociationsLinks(this.entityDefs, defn, userId, uriInfo));
        
        // create a final map of links to relevant links
        HashMap<String, Object> linksMap = new HashMap<String, Object>();
        linksMap.put(ResourceUtil.LINKS, links);
        
        // return as browser response
        Home home = new Home(linksMap, defn.getStoredCollectionName());
        return Response.ok(home).build();
    }
    
    /**
     * Analyzes security context to get ID and EntityDefinition for user.
     * 
     * @return ID and EntityDefinition from security context
     */
    private Map<String, EntityDefinition> getEntityInfoForUser() {
        Map<String, EntityDefinition> map = new HashMap<String, EntityDefinition>();
        
        // get the Entity for the logged in user
        Entity entity = ResourceUtil.getSLIPrincipalFromSecurityContext().getEntity();
        if (entity != null) {
            EntityDefinition entityDefinition = this.entityDefs.lookupByEntityType(entity.getType());        
            map.put(entity.getEntityId(), entityDefinition);
            map.put(entity.getEntityId(), entityDefinition);
        }
        
        return map;
    }
}