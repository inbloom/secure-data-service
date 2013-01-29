/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.client.impl;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.util.Query;

/**
 * 
 * Simple class used to define a set of query parameters.
 * 
 * @author asaarela
 */
public class BasicQuery implements Query {
    
    /** Represents an empty query with no query parameters */
    public static final Query EMPTY_QUERY = Builder.create().build();
    
    /** Represents a query with no query parameters that includes custom entities */
    public static final Query CUSTOM_ENTITY_QUERY = Builder.create().fullEntities().customEntities().build();
    
    /** Represents a simple query that requests a response containing full entities. */
    public static final Query FULL_ENTITIES_QUERY = Builder.create().fullEntities().build();
    
    private Map<String, Object> params;
    
    /**
     * Build a query, specifying optional values for sorting, field searching, and pagination.
     */
    public static class Builder {
        private final Map<String, Object> params = new HashMap<String, Object>();
        
        /**
         * Instantiate a new builder
         * 
         * @return Builder instance.
         */
        public static Builder create() {
            return new Builder();
        }
        
        /**
         * Indicate the results should be returned in ascending order.
         * 
         * @return Updated Builder instance.
         */
        public Builder sortAscending() {
            params.put(ParameterConstants.SORT_ORDER, ParameterConstants.SORT_ASCENDING);
            return this;
        }
        
        /**
         * Indicate the results should be returned in descending order.
         * 
         * @return Updated Builder instance.
         */
        public Builder sortDescending() {
            params.put(ParameterConstants.SORT_ORDER, ParameterConstants.SORT_DESCENDING);
            return this;
        }
        
        /**
         * Filter results where fieldName is equal to value.
         * 
         * @param fieldName
         *            Field to filter on.
         * @param value
         *            The value to look for.
         * 
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
         * 
         * @return Updated Builder instance.
         */
        public Builder paginate(final int startIndex, final int maxResults) {
            params.put(ParameterConstants.OFFSET, startIndex);
            params.put(ParameterConstants.LIMIT, maxResults);
            return this;
        }
        
        /**
         * Include custom entities in the query response. Defaults to 'false'.
         * 
         * @return Updated Builder instance.
         */
        public Builder customEntities() {
            params.put(ParameterConstants.INCLUDE_CUSTOM, true);
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
            return rval;
        }
        
        /**
         * @param sortField
         * 
         * @return Updated Builder instance.
         */
        public Builder sortBy(String sortField) {
            params.put(ParameterConstants.SORT_BY, sortField);
            return this;
        }
        
        /**
         * @param startIndex
         * 
         * @return Updated Builder instance.
         */
        public Builder startIndex(int startIndex) {
            params.put(ParameterConstants.OFFSET, startIndex);
            return this;
        }
        
        /**
         * Return full entities, not just links.
         * 
         * @return Updated Builder instance.
         */
        public Builder fullEntities() {
            params.put(ParameterConstants.FULL_ENTITIES, true);
            return this;
        }
        
        /**
         * @param maxResults
         * 
         * @return Updated Builder instance.
         */
        public Builder maxResults(int maxResults) {
            params.put(ParameterConstants.LIMIT, maxResults);
            return this;
        }
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return params;
    }
}
