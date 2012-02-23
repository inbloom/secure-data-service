package org.slc.sli.api.resources.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.v1.PathConstants;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.validation.schema.ReferenceSchema;

/**
 * Performs tasks common to both Resource and HomeResource to eliminate code-duplication. These
 * tasks include creating a list of embedded links, adding links to a list regarding associations,
 * and resolving a new URI based on parameters.
 *
 * @author kmyers <kmyers@wgen.net>
 *
 */
public class ResourceUtil {

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
            links.add(new EmbeddedLink(ResourceConstants.SELF, defn.getType(), ResourceUtil.getURI(uriInfo, defn.getResourceName(),
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
    @Deprecated
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
    public static List<EmbeddedLink> getAssociationLinksForEntity(final EntityDefinitionStore entityDefs,
            final EntityDefinition defn, final String id, final UriInfo uriInfo) {

        LinkedList<EmbeddedLink> links = new LinkedList<EmbeddedLink>();

        // look up all associations for supplied entity
        Collection<AssociationDefinition> associations = entityDefs.getLinked(defn);

        // loop through all associations to supplied entity type
        for (AssociationDefinition assoc : associations) {
            if (assoc.getSourceEntity().equals(defn)) {
                links.add(new EmbeddedLink(assoc.getRelNameFromSource(), assoc.getType(), 
                        getURI(uriInfo, PathConstants.V1, defn.getResourceName(), id, assoc.getResourceName()).toString()));
                
                links.add(new EmbeddedLink(assoc.getHoppedTargetLink(), assoc.getTargetEntity().getType(), 
                        getURI(uriInfo, PathConstants.V1, defn.getResourceName(), id, assoc.getResourceName(), assoc.getTargetEntity().getResourceName()).toString()));
                
            } else if (assoc.getTargetEntity().equals(defn)) {
                links.add(new EmbeddedLink(assoc.getRelNameFromTarget(), assoc.getType(), 
                        getURI(uriInfo, PathConstants.V1, defn.getResourceName(), id, assoc.getResourceName()).toString()));
                
                links.add(new EmbeddedLink(assoc.getHoppedSourceLink(), assoc.getSourceEntity().getType(), 
                        getURI(uriInfo, PathConstants.V1, defn.getResourceName(), id, assoc.getResourceName(), assoc.getTargetEntity().getResourceName()).toString()));
            }
        }
        return links;
    }
    

    /**
     * Returns a list of links that are referenced by the specified entity body.
     *
     * @param uriInfo
     *            The base URI
     * @param store 
     *            All entity definitions
     * @param definition
     *            entity whose references (away from) are being generated
     * @param body
     *            instance of the definition that contains values
     *            
     * @return A list of links pointing to the referenced IDs
     */
    public static List<EmbeddedLink> getReferenceLinks(final UriInfo uriInfo, EntityDefinitionStore store, EntityDefinition definition, EntityBody body) {
        
        //new list to store links
        List<EmbeddedLink> links = new ArrayList<EmbeddedLink>();
        
        //loop for all fields on the entity that are reference fields
        for (Map.Entry<String, ReferenceSchema> entry : definition.getReferenceFields().entrySet()) {
            //get what value is stored in the reference field
            Object value = body.get(entry.getKey());
            
            //if the reference field contains a value
            if (value != null && value instanceof String) {
                //cast object to a string because all references are strings
                String id = (String) value;
                //determine what (collection) is being referenced 
                String reference = entry.getValue().getAppInfo().getReferenceType();
                //determine how the API exposes that (collection)
                String resourceName = store.lookupByEntityType(reference).getResourceName();
                //add a new link to the collection for the associated ID
                links.add(new EmbeddedLink(entry.getKey(), reference, ResourceUtil.getURI(uriInfo, resourceName, id).toString()));
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

        links.add(new EmbeddedLink(ResourceConstants.LINKS, ResourceConstants.ENTITY_EXPOSE_TYPE_AGGREGATIONS, uriInfo.getBaseUriBuilder()
                .path(ResourceConstants.RESOURCE_PATH_AGG).build().toString()));

        return links;
    }
    
    /**
     * Adds the value to a list and then puts the list into the query parameters associated to the given key.
     * 
     * @param queryParameters where to put the value once added to a list
     * @param key key value for new parameter
     * @param value resulting value for new parameter
     */
    public static void putValue(MultivaluedMap<String, String> queryParameters, String key, String value) {
        List<String> values = new ArrayList<String>();
        values.add(value);
        queryParameters.put(key, values);
    }

    /**
     * Adds the value to a list and then puts the list into the query parameters associated to the given key.
     * 
     * @param queryParameters where to put the value once added to a list
     * @param key key value for new parameter
     * @param value resulting value for new parameter
     */
    public static void putValue(MultivaluedMap<String, String> queryParameters, String key, int value) {
        ResourceUtil.putValue(queryParameters, key, "" + value);
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
     * Returns a URI based on the supplied URI with the paths appended to the base URI.
     * @param uriInfo
     *              URI of current actions
     * @param paths
     *              Paths that need to be appended
     * @return
     */
    public static URI getURI(UriInfo uriInfo, String ...paths) {
        UriBuilder builder = uriInfo.getBaseUriBuilder();
        
        for (String path : paths) {
            builder.path(path);
        }
        
        return builder.build();
    }

    /**
     * Analyzes security context to get SLIPrincipal for user.
     *
     * @return SLIPrincipal from security context
     */
    public static SLIPrincipal getSLIPrincipalFromSecurityContext() {
        

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Login Required");
        }

        // lookup security/login information
        SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
        return principal;
    }

}
