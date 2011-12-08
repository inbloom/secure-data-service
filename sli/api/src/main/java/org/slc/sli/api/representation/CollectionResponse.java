package org.slc.sli.api.representation;

import java.util.LinkedList;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionResponse extends LinkedList<CollectionResponse.EntityReference> {
    
    public static class EntityReference {
        @JsonProperty("id")
        String id;
        @JsonProperty("link")
        EmbededLink link;
        
        public EntityReference(String id, EmbededLink link) {
            this.id = id;
            this.link = link;
        }

        @JsonIgnore
        public String getId() {
            return id;
        }

        @JsonIgnore
        public EmbededLink getLink() {
            return link;
        }
    }
    
    
    public void add(String id, String rel, String type, String href) {
        this.add(new EntityReference(id, new EmbededLink(rel, type, href)));
    }
    
}
