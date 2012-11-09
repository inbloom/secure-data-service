package org.slc.sli.search.process;

import org.slc.sli.search.entity.IndexEntity;

/**
 * Indexes provided entities with the search engine
 *
 */
public interface Indexer extends Process {

    void index(IndexEntity entity);

}