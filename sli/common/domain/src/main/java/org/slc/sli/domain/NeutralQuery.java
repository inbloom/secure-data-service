package org.slc.sli.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A neutral query contains a list of criteria that is independent of any specific database implementation.
 * 
 * @author kmyers
 *
 */
public class NeutralQuery {

    private String includeFields;
    private String excludeFields;
    private int offset;
    private int limit;
    private String sortBy;
    private SortOrder sortOrder;

    /**
     *  A list of all criteria that make up this query 
     */
    private List<NeutralCriteria> queryCriteria;

    /**
     *  A list of all criteria that make up this query 
     */
    private List<NeutralQuery> orQueries;
    
    /**
     * Sort order enumeration
     */
    public enum SortOrder { ascending, descending }
    
    
    /**
     * Creates a new neutral query with no criteria.
     */
    public NeutralQuery() {
        this.queryCriteria = new ArrayList<NeutralCriteria>();
        this.orQueries = new ArrayList<NeutralQuery>();
        this.offset = 0;
        this.limit = 0;
    }
    

    /**
     * Creates a new neutral query with no criteria.
     */
    public NeutralQuery(NeutralQuery otherNeutralQuery) {
        this.offset = otherNeutralQuery.offset;
        this.limit = otherNeutralQuery.limit;
        this.includeFields = otherNeutralQuery.includeFields;
        this.excludeFields = otherNeutralQuery.excludeFields;
        this.sortBy = otherNeutralQuery.sortBy;
        this.sortOrder = otherNeutralQuery.sortOrder;
        
        this.queryCriteria = new ArrayList<NeutralCriteria>(otherNeutralQuery.queryCriteria);
        
        this.orQueries = new ArrayList<NeutralQuery>(otherNeutralQuery.orQueries);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public boolean equals(Object o) {
        
        if (o instanceof NeutralQuery) {
            NeutralQuery neutralQuery = (NeutralQuery) o;
            
            if (this.offset != neutralQuery.offset) {
                return false;
            } 
            
            if (this.limit != neutralQuery.limit) {
                return false;
            }

            boolean includeFieldsMatch = this.valuesMatch(this.includeFields, neutralQuery.includeFields);
            boolean excludeFieldsMatch = this.valuesMatch(this.excludeFields, neutralQuery.excludeFields);
            boolean sortByFieldsMatch = this.valuesMatch(this.sortBy, neutralQuery.sortBy);
            boolean sortOrdersMatch = this.valuesMatch(this.sortOrder, neutralQuery.sortOrder);
            
            if (!includeFieldsMatch || !excludeFieldsMatch || !sortByFieldsMatch || !sortOrdersMatch) {
                return false;
            }
            
            if (this.queryCriteria.size() != neutralQuery.queryCriteria.size()) {
                return false;
            }
            
            for (NeutralCriteria nc : this.queryCriteria) {
                if (!neutralQuery.queryCriteria.contains(nc)) {
                    return false;
                }
            }
            
            if (this.orQueries.size() != neutralQuery.orQueries.size()) {
                return false;
            }
            
            for (NeutralQuery nq : this.orQueries) {
                if (!neutralQuery.orQueries.contains(nq)) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Creates a new neutral query and adds to it the given criteria.
     * 
     * @param neutralCriteria
     */
    public NeutralQuery(NeutralCriteria neutralCriteria) {
        this();
        this.addCriteria(neutralCriteria);
    }

    public void addOrQuery(NeutralQuery orQuery) {
        this.orQueries.add(orQuery);
    }

    public List<NeutralQuery> getOrQueries() {
        return this.orQueries;
    }

    private boolean valuesMatch(Object value1, Object value2) {
        
        //both null? they match
        if (value1 == null && value2 == null) {
            return true;
        }
        
        //one null the other not? they dont match
        if ((value1 == null && value2 != null) || (value1 != null && value2 == null)) {
            return false;
        }
        
        return value1.equals(value2);
    }
    
    
    
    /**
     * Adds a criteria to the list of criteria this neutral query tracks.
     * 
     * @param criteria neutral criteria that must be satisfied for query results
     */
    public void addCriteria(NeutralCriteria criteria) {
        this.queryCriteria.add(criteria);
    }
    
    /**
     * Returns all criteria associated to this query. 
     * 
     * @return all criteria that must be satisfied for query results
     */
    public List<NeutralCriteria> getCriteria() {
        return this.queryCriteria;
    }
    
    public String getIncludeFields() {
        return this.includeFields;
    }
    
    public String getExcludeFields() {
        return this.excludeFields;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public String getSortBy() {
        return this.sortBy;
    }
    
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }
    
    public void setLimit(int newLimit) {
        this.limit = newLimit;
    }
    
    public void setOffset(int newOffset) {
        this.offset = newOffset;
    }
    
    public void setIncludeFields(String newIncludeFields) {
        this.includeFields = newIncludeFields;
    }
    
    public void setExcludeFields(String newExcludeFields) {
        this.excludeFields = newExcludeFields;
    }
    
    public void setSortBy(String newSortBy) {
        this.sortBy = newSortBy;
    }
    
    public void setSortOrder(SortOrder newSortOrder) {
        this.sortOrder = newSortOrder;
    }
    
    public String toString() {
        String toString = "";
        
        if (this.offset != 0) {
            toString += (" offset " + this.offset);
        }
        
        if (this.limit != 0) {
            toString += (" limit " + this.limit);
        }
        
        if (this.includeFields != null) {
            toString += (" includeFields " + this.includeFields);
        }
        
        if (this.excludeFields != null) {
            toString += (" excludeFields " + this.excludeFields);
        }
        
        if (this.sortBy != null) {
            toString += (" sortBy " + this.sortBy);
        }
        
        if (this.sortOrder != null) {
            toString += (" sortOrder " + this.sortOrder);
        }
        
        for (NeutralCriteria neutralCriteria : this.queryCriteria) {
            toString += (" " + neutralCriteria.getKey() + " " + neutralCriteria.getOperator() + " " + neutralCriteria.getValue());
        }
        
        return toString;
    }
}
