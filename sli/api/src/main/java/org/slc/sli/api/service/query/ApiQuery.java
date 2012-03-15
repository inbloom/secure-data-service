package org.slc.sli.api.service.query;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.domain.NeutralQuery;

/**
 * Converts a String into a database independent NeutralQuery object.
 * The API uses URIs to provide a place for query input.
 * 
 * @author kmyers
 *
 */
public class ApiQuery extends NeutralQuery {
    
    private static final UriInfoToNeutralQueryConverter QUERY_CONVERTER = new UriInfoToNeutralQueryConverter();
    
    /**
     * Constructor. Reads the query portion of the URI into a neutral query (this).
     * 
     * @param uriInfo
     */
    public ApiQuery(UriInfo uriInfo) {
        if (uriInfo != null) {
            ApiQuery.QUERY_CONVERTER.convert(this, uriInfo);
        }
    }
}
