package org.slc.sli.api.representation;

import java.util.LinkedList;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Representation of a collection of entity references returned by the API.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
public class CollectionResponse extends LinkedList<CollectionResponse.EntityReference> {
    
    private static final long serialVersionUID = -7328415047032909315L;
    
    /**
     * Single reference to an entity.
     *
     * @author Ryan Farris <rfarris@wgen.net>
     *
     */
    public static class EntityReference {
        @JsonProperty("id")
        private final String id;
        @JsonProperty("entityType")
        private final String entityType;
        @JsonProperty("link")
        private final EmbeddedLink link;
        
        public EntityReference(final String id, final String entityType, final EmbeddedLink link) {
            this.id = id;
            this.entityType = entityType;
            this.link = link;
        }
        
        @JsonIgnore
        public String getId() {
            return id;
        }
        
        @JsonIgnore
        public String getEntityType() {
            return entityType;
        }
        
        @JsonIgnore
        public EmbeddedLink getLink() {
            return link;
        }
        
    }
    
    public void add(final String id, final String rel, final String type, final String href) {
        this.add(new EntityReference(id, type, new EmbeddedLink(rel, type, href)));
    }
    
}
