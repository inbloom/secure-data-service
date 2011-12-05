package org.slc.sli.api.representation;

import org.codehaus.jackson.annotate.JsonProperty;

public class EmbededLink {
    @JsonProperty("rel") String rel;
    @JsonProperty("type") String type;
    @JsonProperty("href") String href;
    
    public EmbededLink(String rel, String type, String href) {
        this.rel = rel;
        this.type = type;
        this.href = href;
    }
    
    public EmbededLink() {
    }
    
}
