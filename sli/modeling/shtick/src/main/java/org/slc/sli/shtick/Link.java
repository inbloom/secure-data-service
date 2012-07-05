package org.slc.sli.shtick;

import java.net.URL;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A basic Link resource associated with an Entity.
 *
 * @author asaarela
 */
@JsonSerialize(using = JacksonRestLinkSerializer.class)
@JsonDeserialize(using = JacksonRestLinkDeserializer.class)
public final class Link {

    private final String linkName;
    private final URL resource;

    /**
     * Construct a new link
     *
     * @param linkName
     *            Name of the link.
     * @param resource
     *            Resource for the link.
     */
    @JsonCreator
    public Link(final String linkName, final URL resource) {
        this.linkName = linkName;
        this.resource = resource;
    }

    public String getLinkName() {
        return linkName;
    }

    public URL getResourceURL() {
        return resource;
    }

    @Override
    public String toString() {
        return "rel=" + linkName + ",href=" + resource;
    }

}
