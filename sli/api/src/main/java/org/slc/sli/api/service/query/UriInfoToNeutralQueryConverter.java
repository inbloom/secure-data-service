package org.slc.sli.api.service.query;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;

/**
 * Converts a String into a database independent NeutralQuery object.
 * It is up to the specific database implementation to turn the NeutralQuery
 * into the appropriate type of parameter for the associated DB engine.
 * 
 * @author kmyers
 * 
 */
public class UriInfoToNeutralQueryConverter {
    
    /**
     * Keywords that the API handles through specific bindings in a NeutralQuery.
     * 
     * @author kmyers
     * 
     */
    protected interface NeutralCriteriaImplementation {
        public void convert(NeutralQuery neutralQuery, Object value);
    }
    
    /* Criteria keywords and what to do with them when encountered */
    private Map<String, NeutralCriteriaImplementation> reservedQueryKeywordImplementations;
    
    public UriInfoToNeutralQueryConverter() {
        reservedQueryKeywordImplementations = new HashMap<String, NeutralCriteriaImplementation>();
        
        // limit
        reservedQueryKeywordImplementations.put(ParameterConstants.LIMIT, new NeutralCriteriaImplementation() {
            @Override
            public void convert(NeutralQuery neutralQuery, Object value) {
                neutralQuery.setLimit(Integer.parseInt((String) value));
            }
        });
        
        // skip
        reservedQueryKeywordImplementations.put(ParameterConstants.OFFSET, new NeutralCriteriaImplementation() {
            @Override
            public void convert(NeutralQuery neutralQuery, Object value) {
                neutralQuery.setOffset(Integer.parseInt((String) value));
            }
        });
        
        // includeFields
        reservedQueryKeywordImplementations.put(ParameterConstants.INCLUDE_FIELDS, new NeutralCriteriaImplementation() {
            @Override
            public void convert(NeutralQuery neutralQuery, Object value) {
                neutralQuery.setIncludeFields((String) value);
            }
        });
        
        // excludeFields
        reservedQueryKeywordImplementations.put(ParameterConstants.EXCLUDE_FIELDS, new NeutralCriteriaImplementation() {
            @Override
            public void convert(NeutralQuery neutralQuery, Object value) {
                neutralQuery.setExcludeFields((String) value);
            }
        });
        
        // sortBy
        reservedQueryKeywordImplementations.put(ParameterConstants.SORT_BY, new NeutralCriteriaImplementation() {
            @Override
            public void convert(NeutralQuery neutralQuery, Object value) {
                neutralQuery.setSortBy((String) value);
            }
        });
        
        // sortOrder
        reservedQueryKeywordImplementations.put(ParameterConstants.SORT_ORDER, new NeutralCriteriaImplementation() {
            @Override
            public void convert(NeutralQuery neutralQuery, Object value) {
                if (value.equals(ParameterConstants.SORT_DESCENDING)) {
                    neutralQuery.setSortOrder(NeutralQuery.SortOrder.descending);
                } else {
                    neutralQuery.setSortOrder(NeutralQuery.SortOrder.ascending);
                }
            }
        });
    }
    
    /**
     * Converts a & separated list of criteria into a neutral criteria object. Adds all
     * criteria to the provided neutralQuery.
     * 
     * @param neutralQuery
     *            object to add criteria to
     * @return a non-null neutral query containing any specified criteria
     */
    public NeutralQuery convert(NeutralQuery neutralQuery, UriInfo uriInfo) {
        if (neutralQuery != null && uriInfo != null) {
            String queryString = uriInfo.getRequestUri().getQuery();
            if (queryString != null) {
                try {
                    for (String criteriaString : queryString.split("&")) {
                        NeutralCriteria neutralCriteria = new NeutralCriteria(criteriaString);
                        NeutralCriteriaImplementation nci = this.reservedQueryKeywordImplementations.get(neutralCriteria.getKey());
                        if (nci == null) {
                            if (!neutralCriteria.getKey().equals("full-entities")
                                    && (!ParameterConstants.OPTIONAL_FIELDS.equals(neutralCriteria.getKey()))
                                    && (!ParameterConstants.INCLUDE_CUSTOM.equals(neutralCriteria.getKey()))) {
                                neutralQuery.addCriteria(neutralCriteria);
                            }
                        } else {
                            nci.convert(neutralQuery, neutralCriteria.getValue());
                        }
                    }
                } catch (RuntimeException re) {
                    error("error parsing query String {} {}", re.getMessage(), queryString);
                    throw new QueryParseException(re.getMessage(), queryString);
                }
            }
        }
        
        return neutralQuery;
    }
    
    /**
     * Converts a & separated list of criteria into a neutral criteria object. Creates a new
     * NeutralQuery with all associated criteria.
     * 
     * @return a neutral implementation of the query string
     */
    public NeutralQuery convert(UriInfo uriInfo) {
        return this.convert(new NeutralQuery(), uriInfo);
    }
}
