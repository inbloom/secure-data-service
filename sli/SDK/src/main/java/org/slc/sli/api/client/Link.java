package org.slc.sli.api.client;

import java.net.URL;

/**
 * 
 * Client representation of a URL link as provided by the SLI API ReSTful service.
 * 
 * @author asaarela
 */
public interface Link {
    
    /**
     * Get the name of this link.
     * 
     * @return String link name
     */
    public String getLinkName();
    
    /**
     * Get the link Resource.
     * 
     * @Return Resource URL.
     */
    public URL getResourceURL();
    
}
