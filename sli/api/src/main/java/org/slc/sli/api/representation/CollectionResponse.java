package org.slc.sli.api.representation;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionResponse {
    
    public static class EntityReference {
        @JsonProperty("id")
        String id;
        @JsonProperty("link")
        EmbededLink link;
        
        public EntityReference(String id, EmbededLink link) {
            this.id = id;
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public EmbededLink getLink() {
            return link;
        }
    }
    
    @JsonProperty("entity")
    ArrayList<EntityReference> entities = new ArrayList<EntityReference>();
    
    public void add(String id, String rel, String type, String href) {
        entities.add(new EntityReference(id, new EmbededLink(rel, type, href)));
    }
    
    public List<EntityReference> getEntities() {
        return entities;
    }
}
