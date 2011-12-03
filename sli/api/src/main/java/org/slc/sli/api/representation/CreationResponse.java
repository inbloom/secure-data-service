package org.slc.sli.api.representation;

import org.codehaus.jackson.annotate.JsonProperty;

public class CreationResponse {
    @JsonProperty("id")
    String id;
    @JsonProperty("entity")
    EmbededLink entityLink;
    
    public CreationResponse(String id, String type, String href) {
        this.id = id;
        this.entityLink = new EmbededLink("self", type, href);
    }
}
