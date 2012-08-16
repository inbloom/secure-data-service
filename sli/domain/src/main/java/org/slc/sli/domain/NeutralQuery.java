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
        this(0);
    }

    /**
     * Creates a new neutral query with no criteria.
     */
    public NeutralQuery(int limit) {
        this.queryCriteria = new ArrayList<NeutralCriteria>();
        this.orQueries = new ArrayList<NeutralQuery>();
        this.offset = 0;
        this.limit = limit;
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


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((excludeFields == null) ? 0 : excludeFields.hashCode());
        result = prime * result + ((includeFields == null) ? 0 : includeFields.hashCode());
        result = prime * result + limit;
        result = prime * result + offset;
        result = prime * result + ((orQueries == null) ? 0 : orQueries.hashCode());
        result = prime * result + ((queryCriteria == null) ? 0 : queryCriteria.hashCode());
        result = prime * result + ((sortBy == null) ? 0 : sortBy.hashCode());
        result = prime * result + ((sortOrder == null) ? 0 : sortOrder.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NeutralQuery other = (NeutralQuery) obj;
        if (excludeFields == null) {
            if (other.excludeFields != null) {
                return false;
            }
        } else if (!excludeFields.equals(other.excludeFields)) {
            return false;
        }
        if (includeFields == null) {
            if (other.includeFields != null) {
                return false;
            }
        } else if (!includeFields.equals(other.includeFields)) {
            return false;
        }
        if (limit != other.limit) {
            return false;
        }
        if (offset != other.offset) {
            return false;
        }
        if (orQueries == null) {
            if (other.orQueries != null) {
                return false;
            }
        } else if (!orQueries.equals(other.orQueries)) {
            return false;
        }
        if (queryCriteria == null) {
            if (other.queryCriteria != null) {
                return false;
            }
        } else if (!queryCriteria.equals(other.queryCriteria)) {
            return false;
        }
        if (sortBy == null) {
            if (other.sortBy != null) {
                return false;
            }
        } else if (!sortBy.equals(other.sortBy)) {
            return false;
        }
        if (sortOrder != other.sortOrder) {
            return false;
        }
        return true;
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


    /**
     * Adds a criteria to the list of criteria this neutral query tracks.
     *
     * @param criteria neutral criteria that must be satisfied for query results
     */
    public void addCriteria(NeutralCriteria criteria) {
        this.queryCriteria.add(criteria);
    }

    /**
     * Adds a criteria to the list of criteria this neutral query tracks.
     *
     * @param criteria neutral criteria that must be satisfied for query results
     */
    public void prependCriteria(NeutralCriteria criteria) {
        this.queryCriteria.add(0, criteria);
    }

    /**
     * Returns all criteria associated to this query.
     *
     * @return all criteria that must be satisfied for query results
     */
    public List<NeutralCriteria> getCriteria() {
        return this.queryCriteria;
    }

    public String getIncludeFieldString() {
        return this.includeFields;
    }

    public String getExcludeFieldString() {
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

    public void setIncludeFieldString(String newIncludeFields) {
        this.includeFields = newIncludeFields;
    }

    public void setExcludeFieldString(String newExcludeFields) {
        this.excludeFields = newExcludeFields;
    }

    public void setSortBy(String newSortBy) {
        this.sortBy = newSortBy;
    }

    public void setSortOrder(SortOrder newSortOrder) {
        this.sortOrder = newSortOrder;
    }

    @Override
    public String toString() {
        return "NeutralQuery [includeFields=" + includeFields + ", excludeFields=" + excludeFields + ", offset="
                + offset + ", limit=" + limit + ", sortBy=" + sortBy + ", sortOrder=" + sortOrder + ", queryCriteria="
                + queryCriteria + ", orQueries=" + orQueries + "]";
    }

}
