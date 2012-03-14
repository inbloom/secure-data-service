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
     * Sort order enumeration
     */
    public enum SortOrder { ascending, descending }
    
    
    /**
     * Creates a new neutral query with no criteria.
     */
    public NeutralQuery() {
        this.queryCriteria = new ArrayList<NeutralCriteria>();
        this.offset = 0;
        this.limit = 0;
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
}
