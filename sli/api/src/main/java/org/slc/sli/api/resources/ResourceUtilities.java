package org.slc.sli.api.resources;

import java.net.URI;

import javax.ws.rs.core.Response.ResponseBuilder;

public class ResourceUtilities {
    
    /**
     * Properly formats a link and adds it as a header to the passed-in ResponseBuilder.
     * 
     * @param builder
     *            has link header added to it.
     * @param uri
     * @param rel
     */
    public static void buildLinkHeader(ResponseBuilder builder, URI uri, String rel) {
        builder.header("Link", "<" + uri.toASCIIString() + ">;rel=" + rel);
    }
    
    /**
     * The header for the location
     */
    public static final String LOCATION_HEADER = "Location";
    
    public static final String JSON_MEDIA_TYPE = "application/json";
    public static final String XML_MEDIA_TYPE = "application/xml";
}
