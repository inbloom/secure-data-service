package org.slc.sli.api.representation;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Representation of a link to an entity, association, or other resource.
 * Intended for use within response bodies, not headers.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
public class EmbeddedLink {
    @JsonProperty("rel")
    String rel;
    @JsonIgnore
    @JsonProperty("type")
    String type;
    @JsonProperty("href")
    String href;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((href == null) ? 0 : href.hashCode());
        result = prime * result + ((rel == null) ? 0 : rel.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmbeddedLink other = (EmbeddedLink) obj;
        if (href == null) {
            if (other.href != null)
                return false;
        } else if (!href.equals(other.href))
            return false;
        if (rel == null) {
            if (other.rel != null)
                return false;
        } else if (!rel.equals(other.rel))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EmbeddedLink [rel=" + rel + ", type=" + type + ", href=" + href + "]";
    }

}
