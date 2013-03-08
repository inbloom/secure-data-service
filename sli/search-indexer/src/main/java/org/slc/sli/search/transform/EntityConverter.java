package org.slc.sli.search.transform;

import java.util.List;
import java.util.Map;

import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;

public interface EntityConverter {

    /**
     * Transform the given entity map to a list of IndexEntities
     * 
     * @param index
     *            tenantDB hash
     * @param action
     * @param entityMap
     * @return IndexEntity to be indexed by ElasticSearch
     * 
     */
    public List<IndexEntity> convert(String index, Action action, Map<String, Object> entityMap, boolean decrypt);

}
