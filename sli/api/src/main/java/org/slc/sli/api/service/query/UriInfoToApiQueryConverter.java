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


package org.slc.sli.api.service.query;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.constants.Constraints;
import org.slc.sli.common.constants.ParameterConstants;
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
public class UriInfoToApiQueryConverter {

    private static final Logger LOG = LoggerFactory.getLogger(UriInfoToApiQueryConverter.class);

    //private SelectionConverter selectionConverter = new Selector2MapOfMaps();

    /**
     * Keywords that the API handles through specific bindings in a NeutralQuery.
     *
     * @author kmyers
     *
     */
    protected interface NeutralCriteriaImplementation {
        public void convert(ApiQuery apiQuery, Object value);
    }

    /* Criteria keywords and what to do with them when encountered */
    private Map<String, NeutralCriteriaImplementation> reservedQueryKeywordImplementations;

    public UriInfoToApiQueryConverter() {
        reservedQueryKeywordImplementations = new HashMap<String, NeutralCriteriaImplementation>();

        // countOnly - DS-1046
        // if true, retrieve only the count for this query.
        reservedQueryKeywordImplementations.put(ParameterConstants.COUNT_ONLY, new NeutralCriteriaImplementation() {
            @Override
            /**
             * set the "count only" flag in the criterion according to whether the parameter
             * is 'false' or not.  Anything other than (case-insensitive) 'false' is regarded
             * as true.
             */
            public void convert(ApiQuery apiQuery, Object value) {
            	String stringValue = value.toString().toLowerCase();
            	if ("true".equals(stringValue) || "false".equals(stringValue))
            	{
            	    boolean countOnly = "true".equals(stringValue);
            	    apiQuery.setCountOnly(countOnly);
            	}
            	else
            	{
            		throw new IllegalArgumentException("countOnly parameter value must be 'true' or 'false', found '" + stringValue + "'");
            	}
            }
        });
        
        // selector
        reservedQueryKeywordImplementations.put(ParameterConstants.SELECTOR, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                //disabling selectors for 1.0
                throw new QueryParseException("Invalid Query Parameter", (String) value);

//                String stringValue = (String) value;
//                stringValue = stringValue.replaceAll(" ", "");
//                apiQuery.setSelector(selectionConverter.convert(stringValue));
            }
        });

        // limit
        reservedQueryKeywordImplementations.put(ParameterConstants.LIMIT, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                int limit = Integer.parseInt((String) value);

                if (limit < 0) {
                    throw new QueryParseException("Limit cannot be less than zero", (String) value);
                }

                apiQuery.setLimit(limit);
            }
        });

        // skip
        reservedQueryKeywordImplementations.put(ParameterConstants.OFFSET, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                int offset = Integer.parseInt((String) value);

                if (offset < 0) {
                    throw new QueryParseException("Offset cannot be less than zero", (String) value);
                }

                apiQuery.setOffset(offset);
            }
        });

        // includeFields
        reservedQueryKeywordImplementations.put(ParameterConstants.INCLUDE_FIELDS, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                apiQuery.setIncludeFieldString((String) value);
            }
        });

        // excludeFields
        reservedQueryKeywordImplementations.put(ParameterConstants.EXCLUDE_FIELDS, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                apiQuery.setExcludeFieldString((String) value);
            }
        });

        // sortBy
        reservedQueryKeywordImplementations.put(ParameterConstants.SORT_BY, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                apiQuery.setSortBy((String) value);
            }
        });

        // sortOrder
        reservedQueryKeywordImplementations.put(ParameterConstants.SORT_ORDER, new NeutralCriteriaImplementation() {
            @Override
            public void convert(ApiQuery apiQuery, Object value) {
                if (value.equals(ParameterConstants.SORT_DESCENDING)) {
                    apiQuery.setSortOrder(NeutralQuery.SortOrder.descending);
                } else {
                    apiQuery.setSortOrder(NeutralQuery.SortOrder.ascending);
                }
            }
        });
    }

    public ApiQuery convert(ApiQuery apiQuery, URI requestURI) {
        if (requestURI == null) {
            return apiQuery;
        }

        return convert(apiQuery, requestURI.getQuery());
    }

    public ApiQuery convert(ApiQuery apiQuery, UriInfo uriInfo) {
        if (uriInfo == null) {
            return apiQuery;
        }

        return convert(apiQuery, uriInfo.getRequestUri());
    }

    /**
     * Converts a & separated list of criteria into a neutral criteria object. Adds all
     * criteria to the provided neutralQuery.
     *
     * @param apiQuery
     *            object to add criteria to
     * @return a non-null neutral query containing any specified criteria
     */
    public ApiQuery convert(ApiQuery apiQuery, String queryString) {
        if (apiQuery != null && queryString != null) {
            try {
                for (String criteriaString : queryString.split("&")) {
                    String modifiedCriteriaString = URLDecoder.decode(criteriaString, "UTF-8");
                    NeutralCriteria neutralCriteria = new NeutralCriteria(modifiedCriteriaString);
                    neutralCriteria.setRemovable(true);

                    // There is a set of NeutralCriteriaImplemention objects for "reserved Query Keywords";
                    // get that implementation if we have one for this keyword.
                    NeutralCriteriaImplementation nci = this.reservedQueryKeywordImplementations.get(neutralCriteria.getKey());
                    // if we have such an implementation, alter the apiQuery with it
                    if (nci != null)
                    {
                        nci.convert(apiQuery, neutralCriteria.getValue());
                    }
                    else
                    {
                        // we don't have a special NeutralCriteriaImplementation for this keyword; if it is
                        // not one of the ones in this list, add the NeutralCriteria created from this
                        // criteria string (modifiedCriteriaString)
                        if (!neutralCriteria.getKey().equals("full-entities")
                                && (!ParameterConstants.OPTIONAL_FIELDS.equals(neutralCriteria.getKey()))
                                && (!ParameterConstants.VIEWS.equals(neutralCriteria.getKey()))
                                && (!ParameterConstants.INCLUDE_CUSTOM.equals(neutralCriteria.getKey()))
                                && (!ParameterConstants.INCLUDE_AGGREGATES.equals(neutralCriteria.getKey()))
                                && (!ParameterConstants.INCLUDE_CALCULATED.equals(neutralCriteria.getKey()))) 
                        { apiQuery.addCriteria(neutralCriteria); 
                        }
                    }
                }
            } catch (RuntimeException re) {
                LOG.error("error parsing query String {} {}", re.getMessage(), queryString);
                throw (QueryParseException) new QueryParseException(re.getMessage(), queryString).initCause(re);
            } catch (UnsupportedEncodingException e) {
                LOG.error("Unable to decode query string as UTF-8: {}", queryString);
                throw (QueryParseException) new QueryParseException(e.getMessage(), queryString).initCause(e);
            }

            int limit = apiQuery.getLimit();
            if (0 == limit || limit > Constraints.HARD_ENTITY_COUNT_LIMIT) {
                apiQuery.setLimit(Constraints.HARD_ENTITY_COUNT_LIMIT);
            }
        }
        return apiQuery;
    }

    /**
     * Converts a & separated list of criteria into a neutral criteria object. Creates a new
     * NeutralQuery with all associated criteria.
     *
     * @return a neutral implementation of the query string
     */
    public ApiQuery convert(UriInfo uriInfo) {
        return this.convert(new ApiQuery(), uriInfo);
    }
}
