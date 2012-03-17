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
@Deprecated
public class EntityQuery {
    protected String includeFields;
    protected String excludeFields;
    protected int offset;
    protected int limit;
    protected String sortBy;
    protected SortOrder sortOrder;
    protected Map<String, String> fields = new HashMap<String, String>();
    
    public static EntityQueryBuilder makeQuery() {
        return new EntityQueryBuilder();
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
     * Builder for EntityQuery
     * @author srupasinghe
     *
     */
    public static class EntityQueryBuilder {
        private EntityQuery query = new EntityQuery();
        
        public EntityQueryBuilder setIncludeFields(String includeFields) {
            query.includeFields = includeFields;
            return this;
        }
        
        public EntityQueryBuilder setExcludeFields(String excludeFields) {     
            query.excludeFields = excludeFields;
            return this;
        }
        
        public EntityQueryBuilder setOffset(int offset) {
            query.offset = offset;
            return this;
        }
        
        public EntityQueryBuilder setLimit(int limit) {
            query.limit = limit;
            return this;
        }
        
        public EntityQueryBuilder setSortBy(String sortBy) {
            query.sortBy = sortBy;
            return this;
        }
        
        public EntityQueryBuilder setSortOrder(SortOrder sortOrder) {
            query.sortOrder = sortOrder;
            return this;
        }
        
        public EntityQueryBuilder addField(String field, String value) {
            query.fields.put(field, value);
            return this;
        }
        
        public EntityQuery build() {
            return query;
        }
    }
}
