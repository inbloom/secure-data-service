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
        String id;
        @JsonProperty("link")
        EmbeddedLink link;
        
        public EntityReference(String id, EmbeddedLink link) {
            this.id = id;
            this.link = link;
        }
        
        @JsonIgnore
        public String getId() {
            return id;
        }
        
        @JsonIgnore
        public EmbeddedLink getLink() {
            return link;
        }
    }
    
    public void add(String id, String rel, String type, String href) {
        this.add(new EntityReference(id, new EmbeddedLink(rel, type, href)));
    }
    
}
