package org.slc.sli.search.transform;

import java.util.Map;

import org.slc.sli.search.config.IndexConfig;

/**
 * Filter records from indexing
 */
public interface Filter {
    /**
     * Whether or not the entity satisfies indexing criteria
     * @param config - index config
     * @param entity - entity to transform
     */
    public boolean matchesCondition(IndexConfig config, Map<String, Object> entity);
}
