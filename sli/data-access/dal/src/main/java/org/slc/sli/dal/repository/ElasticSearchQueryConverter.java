/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.dal.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slc.sli.api.constants.ParameterConstants;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Converter from Neutral Query to ES Query DSL
 */
@Component
public class ElasticSearchQueryConverter {
    public static final String Q = "q";
    private static final int IN_LIMIT = 1000;

    private interface Operator {
        FilterBuilder getFilter(NeutralCriteria criteria);
        QueryBuilder getQuery(NeutralCriteria criteria);
    }

    private Map<String, Operator> operationMap;

    public ElasticSearchQueryConverter() {
        Map<String, Operator> operationMap = new HashMap<String, ElasticSearchQueryConverter.Operator>();
        operationMap.put(NeutralCriteria.CRITERIA_GT, new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return FilterBuilders.rangeFilter(criteria.getKey()).gt(criteria.getValue());
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                return QueryBuilders.rangeQuery(criteria.getKey()).gt(criteria.getValue());
            }
        });
        operationMap.put(NeutralCriteria.CRITERIA_GTE, new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return FilterBuilders.rangeFilter(criteria.getKey()).gte(criteria.getValue());
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                return QueryBuilders.rangeQuery(criteria.getKey()).gte(criteria.getValue());
            }
        });
        final Operator terms = new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return FilterBuilders.termsFilter(criteria.getKey(), getTermTokens(criteria.getValue()));
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                Object[] terms = getTermTokens(criteria.getValue());
                if (terms.length > IN_LIMIT) {
                    BoolQueryBuilder bigQuery = QueryBuilders.boolQuery();
                    int length = terms.length, from = 0, to = Math.min(IN_LIMIT, length);
                    while (from <= length) {
                        bigQuery.should(QueryBuilders.termsQuery(criteria.getKey(), Arrays.copyOfRange(terms, from, to)));
                        from = to + 1; to = Math.min(to + IN_LIMIT, length);
                    }
                    return bigQuery.minimumNumberShouldMatch(1);
                } else {
                  return QueryBuilders.termsQuery(criteria.getKey(), terms);
                }
            }
        };
        final Operator query = new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return null;
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                if (Q.equals(criteria.getKey())) {
                    return QueryBuilders.queryString(criteria.getValue().toString().trim().toLowerCase()).analyzeWildcard(true).analyzer("simple");
                }
                String value = (String)criteria.getValue();
                // terms will work for not-analyzed fields and matchPhrase is for analyzed
                BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
                shouldQuery.should(terms.getQuery(criteria));
                shouldQuery.should(QueryBuilders.matchPhraseQuery(criteria.getKey(), value));
                shouldQuery.minimumNumberShouldMatch(1);
                return shouldQuery;
            }
        };
        operationMap.put(NeutralCriteria.CRITERIA_IN, terms);
        operationMap.put(NeutralCriteria.OPERATOR_EQUAL, query);
        operationMap.put("!=", new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return FilterBuilders.notFilter(FilterBuilders.termsFilter(criteria.getKey(), getTermTokens(criteria.getValue())));
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                return QueryBuilders.boolQuery().mustNot(query.getQuery(criteria));
            }
        });
        operationMap.put(NeutralCriteria.CRITERIA_LT, new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return FilterBuilders.rangeFilter(criteria.getKey()).lt(criteria.getValue());
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                return QueryBuilders.rangeQuery(criteria.getKey()).lt(criteria.getValue());
            }
        });
        operationMap.put(NeutralCriteria.CRITERIA_LTE, new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                return FilterBuilders.rangeFilter(criteria.getKey()).lte(criteria.getValue());
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
                return QueryBuilders.rangeQuery(criteria.getKey()).lte(criteria.getValue());
            }
        });

        operationMap.put(NeutralCriteria.CRITERIA_REGEX, new Operator() {
            @Override
            public FilterBuilder getFilter(NeutralCriteria criteria) {
                //using regex is case-sensitive
                return FilterBuilders.prefixFilter(criteria.getKey(), ((String)criteria.getValue()).trim());
            }
            @Override
            public QueryBuilder getQuery(NeutralCriteria criteria) {
            	String value = "*" + ((String)criteria.getValue()).trim() + "*";
                BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
                // wildcard will work for not-analyzed fields and queryString is for analyzed
                shouldQuery.should(QueryBuilders.wildcardQuery(criteria.getKey(), value));
                shouldQuery.should(QueryBuilders.queryString(value).field(criteria.getKey()));
                shouldQuery.minimumNumberShouldMatch(1);
                return shouldQuery;
            }
        });

        this.operationMap = Collections.unmodifiableMap(operationMap);
    }

    public FilterBuilder getFilter(NeutralCriteria criteria) {
        return this.operationMap.get(criteria.getOperator()).getFilter(criteria);
    }

    public QueryBuilder getQuery(NeutralCriteria criteria) {
        if(this.operationMap.containsKey(criteria.getOperator())) {
            return this.operationMap.get(criteria.getOperator()).getQuery(criteria);
        }
        return null;
    }

    /**
     * Build elasticsearch query
     *
     * @param client
     * @param query
     * @return
     */
    public QueryBuilder getQuery(NeutralQuery query) {
        if (query.getCriteria().size() == 1 && query.getOrQueries().isEmpty()) {
            return getQuery(query.getCriteria().get(0));
        } else {
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            // set query criteria
            for (NeutralCriteria criteria : query.getCriteria()) {
                if (ParameterConstants.SCHOOL_YEARS.equals(criteria.getKey())) {
                    // do not include schoolYears in the elastic Search, it is handled elsewhere
                    continue;
                }
                QueryBuilder queryBuilder = getQuery(criteria);
                if(queryBuilder != null) {
                    bqb.must(queryBuilder);
                }
            }
            for (NeutralQuery nq : query.getOrQueries()) {
                QueryBuilder queryBuilder = getQuery(nq);
                if( null != queryBuilder) {
                    bqb.should(queryBuilder);
                }
            }
            return bqb;
        }
    }

    @SuppressWarnings("unchecked")
    private static String[] getTermTokens(Object value) {
        return (value instanceof List) ? ((List<String>) value).toArray(new String[0]) : ((String) value).split(",");
    }
}