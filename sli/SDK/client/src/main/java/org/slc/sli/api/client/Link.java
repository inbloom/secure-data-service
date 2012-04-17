package org.slc.sli.api.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * Client representation of a URL link resource as provided by the SLI API ReSTful service. Each
 * link has a name (for example, 'getStudents') and an associated URL.
 *
 * @author asaarela
 */
@XmlRootElement
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
    @XmlElement(name = "rel")
    public String getLinkName();

    /**
     * Get the link URL.
     *
     * @return java.net.URL for this resource.
     */
    @XmlElement(name = "href")
    public java.net.URL getResourceURL();

}
