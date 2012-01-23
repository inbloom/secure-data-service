package org.slc.sli.api.resources.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.security.SLIPrincipal;

import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_EXPOSE_TYPE_AGGREGATIONS;
import static org.slc.sli.api.resources.util.ResourceConstants.RESOURCE_PATH_AGG;

/**
 * Performs tasks common to both Resource and HomeResource to eliminate code-duplication. These
 * tasks include creating a list of embedded links, adding links to a list regarding associations,
 * and resolving a new URI based on parameters.
 * 
 * @author kmyers <kmyers@wgen.net>
 * 
 */
public class ResourceUtil {
    
    public static final String SELF = "self";
    public static final String LINKS = "links";
    
    /**
     * Creates a new LinkedList and adds a link for self, then returns that list. When not creating
     * a self link, all other parameters can be null.
     * 
     * @param uriInfo
     *            base URI
     * @param userId
     *            unique identifier of user/object
     * @param defn
     *            entity definition for user/object
     * @param createSelfLink
     *            whether or not to include a self link
     * @return
     */
    public static List<EmbeddedLink> getSelfLink(final UriInfo uriInfo, final String userId, final EntityDefinition defn) {
        
        // create a new linkedlist
        LinkedList<EmbeddedLink> links = new LinkedList<EmbeddedLink>();
        
        // add a "self" link
        if (defn != null) {
            links.add(new EmbeddedLink(SELF, defn.getType(), ResourceUtil.getURI(uriInfo, defn.getResourceName(),
                    userId).toString()));
        }
        
        // return
        return links;
    }
    
    /**
     * Looks up associations for the given entity (definition) and adds embedded links for each
     * association for the given user ID.
     * 
     * @param entityDefs
     *            all entity definitions
     * @param defn
     *            entity whose associations are being looked up
     * @param links
     *            list to add associations links to
     * @param id
     *            specific ID to append to links to create specific lookup link
     * @param uriInfo
     *            base URI
     */
    public static List<EmbeddedLink> getAssociationsLinks(final EntityDefinitionStore entityDefs,
            final EntityDefinition defn, final String id, final UriInfo uriInfo) {
        
        LinkedList<EmbeddedLink> links = new LinkedList<EmbeddedLink>();
        
        // look up all associations for supplied entity
        Collection<AssociationDefinition> associations = entityDefs.getLinked(defn);
        
        // loop through all associations to supplied entity type
        for (AssociationDefinition assoc : associations) {
            if (assoc.getSourceEntity().equals(defn)) {
                links.add(new EmbeddedLink(assoc.getRelNameFromSource(), assoc.getType(), ResourceUtil.getURI(uriInfo,
                        assoc.getResourceName(), id).toString()));
                links.add(new EmbeddedLink(assoc.getHoppedTargetLink(), assoc.getTargetEntity().getType(), ResourceUtil
                        .getURI(uriInfo, assoc.getResourceName(), id).toString() + "/targets"));
            } else if (assoc.getTargetEntity().equals(defn)) {
                links.add(new EmbeddedLink(assoc.getRelNameFromTarget(), assoc.getType(), ResourceUtil.getURI(uriInfo,
                        assoc.getResourceName(), id).toString()));
                links.add(new EmbeddedLink(assoc.getHoppedSourceLink(), assoc.getSourceEntity().getType(), ResourceUtil
                        .getURI(uriInfo, assoc.getResourceName(), id).toString() + "/targets"));
            }
        }
        return links;
    }
    
    /**
     * Returns the URI for aggregations
     * 
     * @param uriInfo
     *            The base URI
     * @return A list of links pointing to the base Url for aggregations
     */
    public static List<EmbeddedLink> getAggregateLink(final UriInfo uriInfo) {
        List<EmbeddedLink> links = new ArrayList<EmbeddedLink>();
        
        links.add(new EmbeddedLink(ResourceUtil.LINKS, ENTITY_EXPOSE_TYPE_AGGREGATIONS, uriInfo.getBaseUriBuilder()
                .path(RESOURCE_PATH_AGG).build().toString()));
        
        return links;
    }
    
    /**
     * Helper method to convert MultivaluedMap to a Map
     * 
     * @param map
     * @return
     */
    public static Map<String, String> convertToMap(Map<String, List<String>> map) {
        Map<String, String> results = new HashMap<String, String>();
        
        if (map != null) {
            for (Map.Entry<String, List<String>> e : map.entrySet()) {
                results.put(e.getKey(), e.getValue().get(0));
            }
        }
        
        return results;
    }
    
    /**
     * Returns a URI based on the supplied URI with the type and id appended to the base URI.
     * 
     * @param uriInfo
     *            URI of current actions
     * @param type
     *            string representing API exposure of object
     * @param id
     *            unique ID of an entity
     * @return URI for searching base URI for entity type for ID
     */
    public static URI getURI(UriInfo uriInfo, String type, String id) {
        return uriInfo.getBaseUriBuilder().path(type).path(id).build();
    }
    
    /**
     * Analyzes security context to get SLIPrincipal for user.
     * 
     * @return SLIPrincipal from security context
     */
    public static SLIPrincipal getSLIPrincipalFromSecurityContext() {
        // lookup security/login information
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal;
    }
    
}
