package org.slc.sli.api.client;

import java.util.Map;

/**
 * 
 * Simple class used to define a set of query parameters.
 * 
 * @author asaarela
 */
public class Query {
    
    private static final String SORT_ORDER_KEY = "sort-order";
    private static final String SORT_ASCENDING = "ascending";
    private static final String SORT_DESCENDING = "descending";
    private static final String START_INDEX_KEY = "start-index";
    private static final String MAX_RESULTS_KEY = "max-results";
    
    private Map<String, Object> params;
    
    /**
     * Build a query, specifying optional values for sorting, field searching, and pagination.
     */
    public class QueryBuilder {
        private Map<String, Object> params;
        
        /**
         * Instantiate a new builder
         * 
         * @return QueryBuilder instance.
         */
        public QueryBuilder create() {
            return new QueryBuilder();
        }
        
        /**
         * Indicate the results should be returned in ascending order.
         * 
         * @return Updated QueryBuilder instance.
         */
        public QueryBuilder sortAscending() {
            params.put(SORT_ORDER_KEY, SORT_ASCENDING);
            return this;
        }
        
        /**
         * Indicate the results should be returned in descending order.
         * 
         * @return Updated QueryBuilder instance.
         */
        public QueryBuilder sortDescending() {
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
         * @return Updated QueryBuilder instance.
         */
        public QueryBuilder filterEqual(final String fieldName, final String value) {
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
         * @return Updated QueryBuilder instance.
         */
        public QueryBuilder paginate(final int startIndex, final int maxResults) {
            params.put(START_INDEX_KEY, startIndex);
            params.put(MAX_RESULTS_KEY, maxResults);
            return this;
        }
        
        /**
         * Construct a new Query instance.
         * 
         * @return Query representing the values set on this builder.
         */
        public Query build() {
            Query rval = new Query();
            rval.params = params;
            return rval;
        }
    }
    
    /**
     * Get the query parameters associated with this query instance.
     * 
     * @return
     */
    public Map<String, Object> getParameters() {
        return params;
    }
    
}
