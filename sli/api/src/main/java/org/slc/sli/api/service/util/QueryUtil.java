package org.slc.sli.api.service.util;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * utility class to provide spring data query support in api service layer
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */
public class QueryUtil {
    
    private static String[] reservedQueryKeys = { "start-index", "max-results", "query" };

    public static Query stringToQuery(String queryString) {
        Query mongoQuery = new Query();
        if (queryString == null)
            queryString = "";
        String[] queryStrings = queryString.split("&");
        for (String query : queryStrings) {
            if (!isReservedQueryKey(query)) {
                Criteria criteria = null;
                if (query.contains(">=")) {
                    String[] keyAndValue = getKeyAndValue(query, ">=");
                    if (keyAndValue != null)
                        criteria = Criteria.where("body." + keyAndValue[0]).gte(keyAndValue[1]);
                } else if (query.contains("<=")) {
                    String[] keyAndValue = getKeyAndValue(query, "<=");
                    if (keyAndValue != null)
                        criteria = Criteria.where("body." + keyAndValue[0]).lte(keyAndValue[1]);
                    
                } else if (query.contains("<>")) {
                    String[] keyAndValue = getKeyAndValue(query, "<>");
                    if (keyAndValue != null)
                        criteria = Criteria.where("body." + keyAndValue[0]).ne(keyAndValue[1]);
                } else if (query.contains("=")) {
                    String[] keyAndValue = getKeyAndValue(query, "=");
                    if (keyAndValue != null)
                        criteria = Criteria.where("body." + keyAndValue[0]).is(keyAndValue[1]);
                    
                } else if (query.contains("<")) {
                    String[] keyAndValue = getKeyAndValue(query, "<");
                    if (keyAndValue != null)
                        criteria = Criteria.where("body." + keyAndValue[0]).lt(keyAndValue[1]);
                    
                } else if (query.contains(">")) {
                    String[] keyAndValue = getKeyAndValue(query, ">");
                    if (keyAndValue != null)
                        criteria = Criteria.where("body." + keyAndValue[0]).gt(keyAndValue[1]);
                }
                if (criteria != null)
                    mongoQuery.addCriteria(criteria);
            }
        }
        return mongoQuery;
    }
    
    private static boolean isReservedQueryKey(String queryString) {
        boolean found = false;
        for (String key : reservedQueryKeys) {
            if (queryString.indexOf(key) >= 0)
                found = true;
        }
        return found;
    }
    
    // TODO may need to add error handling for wrong formatted query
    private static String[] getKeyAndValue(String queryString, String operator) {
        String[] keyAndValue = queryString.split(operator);
        if (keyAndValue.length != 2)
            return null;
        else
            return keyAndValue;
    }
}
