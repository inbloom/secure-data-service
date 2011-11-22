package org.slc.sli.api.representation;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation that has links within the response body document.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public class LinkedRepresentation {
    
    private List<LinkRepresentation> links = new ArrayList<LinkRepresentation>();
    
    /**
     * Adds a new link
     * 
     * @param link
     */
    public void addLink(LinkRepresentation link) {
        
        links.add(link);
    }
    
    /**
     * @return the links
     */
    public List<LinkRepresentation> getLinks() {
        return links;
    }
    
    /**
     * @param links
     *            the links to set
     */
    public void setLinks(List<LinkRepresentation> links) {
        this.links = links;
    }
    
}
