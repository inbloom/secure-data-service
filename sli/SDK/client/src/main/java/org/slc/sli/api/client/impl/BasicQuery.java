package org.slc.sli.api.client.impl;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.api.client.Query;

/**
 * 
 * Simple class used to define a set of query parameters.
 * 
 * @author asaarela
 */
public class BasicQuery implements Query {
    
    
    private static final String SORT_BY_KEY = "sort-by";
    private static final String SORT_ORDER_KEY = "sort-order";
    private static final String SORT_ASCENDING = "ascending";
    private static final String SORT_DESCENDING = "descending";
    private static final String START_INDEX_KEY = "start-index";
    private static final String MAX_RESULTS_KEY = "max-results";
    private static final String FULL_ENTITIES_KEY = "full-entities";
    
    /** Represents an empty query with no query parameters */
    public static final Query EMPTY_QUERY = Builder.create().build();
    public static final Query TARGETS_QUERY = Builder.create().targets().build();
    public static final Query FULL_ENTITIES_QUERY = Builder.create().fullEntities().build();
    
    private Map<String, Object> params;
    private boolean useTargets = false;
    
    /**
     * Build a query, specifying optional values for sorting, field searching, and pagination.
     */
    public static class Builder {
        private final Map<String, Object> params = new HashMap<String, Object>();
        private boolean useTargets = false;
        
        /**
         * Instantiate a new builder
         * 
         * @return Builder instance.
         */
        public static Builder create() {
            return new Builder();
        }
        
        /**
         * Indicate we want the targets of an association.
         */
        public Builder targets() {
            useTargets = true;
            return this;
        }
        
        /**
         * Indicate the results should be returned in ascending order.
         * 
         * @return Updated Builder instance.
         */
        public Builder sortAscending() {
            params.put(SORT_ORDER_KEY, SORT_ASCENDING);
            return this;
        }
        
        /**
         * Indicate the results should be returned in descending order.
         * 
         * @return Updated Builder instance.
         */
        public Builder sortDescending() {
            params.put(SORT_ORDER_KEY, SORT_DESCENDING);
            return this;
        }
        
        /**
         * Filter results where fieldName is equal to value.
         * 
         * @param fieldName
         *            Field to filter on.
         * @param value
         *            The value to look for.
         * @return Updated Builder instance.
         */
        public Builder filterEqual(final String fieldName, final String value) {
            params.put(fieldName, value);
            return this;
        }
        
        /**
         * Apply pagination to the request results.
         * 
         * @param startIndex
         *            Start of the result window.
         * @param maxResults
         *            Maximum number of results to return.
         * @return Updated Builder instance.
         */
        public Builder paginate(final int startIndex, final int maxResults) {
            params.put(START_INDEX_KEY, startIndex);
            params.put(MAX_RESULTS_KEY, maxResults);
            return this;
        }
        
        /**
         * Construct a new BasicQuery instance.
         * 
         * @return BasicQuery representing the values set on this builder.
         */
        public Query build() {
            BasicQuery rval = new BasicQuery();
            rval.params = params;
            rval.useTargets = useTargets;
            return rval;
        }
        
        /**
         * @param sortField
         */
        public Builder sortBy(String sortField) {
            params.put(SORT_BY_KEY, sortField);
            return this;
        }
        
        /**
         * @param startIndex
         */
        public Builder startIndex(int startIndex) {
            params.put(START_INDEX_KEY, startIndex);
            return this;
        }
        
        /**
         * Return full entities, not just links.
         */
        public Builder fullEntities() {
            params.put(FULL_ENTITIES_KEY, true);
            return this;
        }
        
        /**
         * @param maxResults
         */
        public Builder maxResults(int maxResults) {
            params.put(MAX_RESULTS_KEY, maxResults);
            return this;
        }
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return params;
    }
    
    @Override
    public boolean targets() {
        return useTargets;
    }
    
}
