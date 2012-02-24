package org.slc.sli.api.client.impl;

import java.net.URL;

import org.slc.sli.api.client.Link;

/**
 * A basic Link resource associated with an Entity.
 * 
 * @author asaarela
 */
public class BasicLink implements Link {
    
    private final URL resource;
    private final String linkName;
    
    /**
     * Construct a new link
     */
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
}
