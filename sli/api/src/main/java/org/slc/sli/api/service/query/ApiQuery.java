package org.slc.sli.api.service.query;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.domain.NeutralCriteria;
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
    
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("offset=");
        stringBuffer.append(super.offset);
        stringBuffer.append("&limit=");
        stringBuffer.append(super.limit);
        
        if (super.includeFields != null) {
            stringBuffer.append("&includeFields=");
            stringBuffer.append(super.includeFields);
        }
        
        if (super.excludeFields != null) {
            stringBuffer.append("&excludeFields=");
            stringBuffer.append(super.excludeFields);
        }
        
        if (super.sortBy != null) {
            stringBuffer.append("&sortBy=");
            stringBuffer.append(super.sortBy);
        }
        
        if (super.sortOrder != null) {
            stringBuffer.append("&sortOrder=");
            stringBuffer.append(super.sortOrder);
        }
        
        for (NeutralCriteria neutralCriteria : super.queryCriteria) {
            stringBuffer.append("&");
            stringBuffer.append(neutralCriteria.getKey());
            stringBuffer.append(neutralCriteria.getOperator());
            stringBuffer.append(neutralCriteria.getValue());
        }
        
        return stringBuffer.toString();
    }
}
