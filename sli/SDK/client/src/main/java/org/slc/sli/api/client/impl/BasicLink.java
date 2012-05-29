package org.slc.sli.api.client.impl;

import java.net.URL;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.transform.LinkDeserializer;
import org.slc.sli.api.client.impl.transform.LinkSerializer;

/**
 * A basic Link resource associated with an Entity.
 * 
 * @author asaarela
 */
@JsonSerialize(using = LinkSerializer.class)
@JsonDeserialize(using = LinkDeserializer.class)
public class BasicLink implements Link {
    
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
    public BasicLink(final String linkName, final URL resource) {
        this.linkName = linkName;
        this.resource = resource;
    }
    
    @Override
    public String getLinkName() {
        return linkName;
    }
    
    @Override
    public URL getResourceURL() {
        return resource;
    }
    
    @Override
    public String toString() {
        return "rel=" + linkName + ",href=" + resource;
    }
    
}
