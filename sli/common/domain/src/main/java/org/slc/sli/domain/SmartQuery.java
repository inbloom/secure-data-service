package org.slc.sli.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a query object to be used by the DAL
 * 
 * Use NeutralQuery or ApiQuery instead
 * 
 * @author srupasinghe
 *
 */
public class SmartQuery {
    protected String includeFields;
    protected String excludeFields;
    protected int offset;
    protected int limit;
    protected String sortBy;
    protected SortOrder sortOrder;
    protected Map<String, String> fields = new HashMap<String, String>();
    
    public static SmartQueryBuilder makeQuery() {
        return new SmartQueryBuilder();
    }

    /**
     * Sort order enumeration
     */
    public enum SortOrder { ascending, descending }


    public String getIncludeFields() {
        return includeFields;
    }

    public String getExcludeFields() {
        return excludeFields;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
    
    public String getSortBy() {
        return sortBy;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
    
    public Map<String, String> getFields() {
        return fields;
    }

    /**
     * Builder for SmartQuery
     * @author srupasinghe
     *
     */
    public static class SmartQueryBuilder {
        private SmartQuery query = new SmartQuery();
        
        public SmartQueryBuilder setIncludeFields(String includeFields) {
            query.includeFields = includeFields;
            return this;
        }
        
        public SmartQueryBuilder setExcludeFields(String excludeFields) {     
            query.excludeFields = excludeFields;
            return this;
        }
        
        public SmartQueryBuilder setOffset(int offset) {
            query.offset = offset;
            return this;
        }
        
        public SmartQueryBuilder setLimit(int limit) {
            query.limit = limit;
            return this;
        }
        
        public SmartQueryBuilder setSortBy(String sortBy) {
            query.sortBy = sortBy;
            return this;
        }
        
        public SmartQueryBuilder setSortOrder(SortOrder sortOrder) {
            query.sortOrder = sortOrder;
            return this;
        }
        
        public SmartQueryBuilder addField(String field, String value) {
            query.fields.put(field, value);
            return this;
        }
        
        public SmartQuery build() {
            return query;
        }
    }
}
