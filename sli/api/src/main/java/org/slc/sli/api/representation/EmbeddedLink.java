package org.slc.sli.api.representation;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class EmbeddedLink {
    @JsonProperty("rel") String rel;
    @JsonIgnore @JsonProperty("type") String type;
    @JsonProperty("href") String href;
    
    public EmbeddedLink(String rel, String type, String href) {
        this.rel = rel;
        this.type = type;
        this.href = href;
    }
    
    public EmbeddedLink() {
    }

    @JsonIgnore
    public String getRel() {
        return rel;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    @JsonIgnore
    public String getHref() {
        return href;
    }
    
}
