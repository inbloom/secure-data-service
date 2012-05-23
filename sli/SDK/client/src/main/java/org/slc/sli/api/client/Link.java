package org.slc.sli.api.client;


/**
 *
 * Client representation of a URL link resource as provided by the SLI API ReSTful service. Each
 * link has a name (for example, 'getStudents') and an associated URL.
 *
 * @author asaarela
 */
public interface Link {

    /** The resource name for the link */
    public static final String LINK_RESOURCE_KEY = "rel";

    /** Key to lookup the resource URI for a link */
    public static final String LINK_HREF_KEY = "href";

    /**
     * Get the resource name of this link.
     *
     * @return String link resource name
     */
    public String getLinkName();

    /**
     * Get the link URL.
     *
     * @return java.net.URL for this resource.
     */
    public java.net.URL getResourceURL();
}
