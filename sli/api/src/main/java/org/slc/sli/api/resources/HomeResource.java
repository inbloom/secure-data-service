package org.slc.sli.api.resources;

//
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        
        // use login data to resolve what type of user and ID of user
        Map<String, String> map = this.resolveUserDataFromSecurityContext();
        
        // remove user's type from map
        // TODO: if userType and id are still being used after
        // authentication updates, then use static strings in code
        String userType = map.remove("userType");
        String userId = map.remove("id");
        
        // look up known associations to user's type
        EntityDefinition defn = this.entityDefs.lookupByResourceName(userType);
        
        // prepare a list of links with the self link
        List<EmbeddedLink> links = ResourceUtil.getSelfLink(uriInfo, userId, defn);
        
        // add links for all of the entity's associations for this ID
        links.addAll(ResourceUtil.getAssociationsLinks(this.entityDefs, defn, userId, uriInfo));
        
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

        // use login data to resolve what type of user and ID of user
        Map<String, String> map = this.resolveUserDataFromSecurityContext();
        
        // remove user's type from map
        // TODO: if userType and id are still being used after
        // authentication updates, then use static strings in code
        String userType = map.remove("userType");
        String userId = map.remove("id");
        
        // look up known associations to user's type
        EntityDefinition defn = this.entityDefs.lookupByResourceName(userType);
        
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
     * Analyzes security context to determine exposure type and ID of user.
     * 
     * @return deduced ID from available security context
     */
    // call with student id
    private Map<String, String> resolveUserDataFromSecurityContext() {
        // map user fields to values
        Map<String, String> map = new HashMap<String, String>();
        
        // lookup security/login information
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
        // TODO: extract userType and ID (in some way) from authentication value
        map.put("userType", "students");
        map.put("id", "714c1304-8a04-4e23-b043-4ad80eb60992");
        // map.put("userType", "teachers");
        // map.put("id", "fa45033c-5517-b14b-1d39-c9442ba95782");
        
        return map;
    }
}