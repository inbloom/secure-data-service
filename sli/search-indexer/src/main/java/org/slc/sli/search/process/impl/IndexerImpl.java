/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.search.process.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SearchEngineConnector;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.SearchIndexerException;

/**
 * Indexer is responsible for building elastic search index requests and
 * sending them to the elastic search server for processing.
 *
 * @author dwu
 *
 */
public class IndexerImpl implements Indexer {

    private static final Logger LOG = LoggerFactory.getLogger(IndexerImpl.class);
    // max period of time to assemble bulk request before sending it out
    private long aggregatePeriodInMillis;
    // max bulk size allowed
    private int bulkSize;
    // queue of index requests limited to bulkSize
    private LinkedBlockingQueue<IndexEntity> indexRequests;

    private int indexerWorkerPoolSize;

    private IndexConfigStore indexConfigStore;

    // how many indexer threads
    private ScheduledExecutorService queueWatcherExecutor;

    private SearchEngineConnector connector;

    // this is helpful to avoid adding index mappings for each index operation. The map does not have to be accurate
    // and no harm will be done if re-mapping is issued
    private final LoadingCache<String, Boolean> knownIndexesMap =
            CacheBuilder.newBuilder().maximumSize(30).expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, Boolean>() {
                @Override
                public Boolean load(String tenantId) { // no checked exception
                    addIndexMappingIfNeeded(tenantId);
                    return Boolean.TRUE;
                  }
                });

    public void init() {
        indexRequests = new LinkedBlockingQueue<IndexEntity>(bulkSize * indexerWorkerPoolSize);
        queueWatcherExecutor = Executors.newScheduledThreadPool(indexerWorkerPoolSize);
        LOG.info("Indexer started");
        queueWatcherExecutor.scheduleAtFixedRate(new IndexQueueMonitor(), aggregatePeriodInMillis, aggregatePeriodInMillis, TimeUnit.MILLISECONDS);
    }

    public void destroy() {
        queueWatcherExecutor.shutdown();
    }

    /**
     * Issue bulk index request if reached the size or not empty and last update past tolerance period
     *
     */
    private class IndexQueueMonitor implements Runnable {
        @Override
        public void run() {
            try {
                if (!indexRequests.isEmpty()) {
                    queueWatcherExecutor.execute(new Runnable() {

                        @Override
                        public void run() {
                            flushIndexQueue();
                        }
                    });
                }
            } catch (Exception e) {
                LOG.info("Unable to index with elasticsearch", e);
            }
        }
    }

    /**
     * flush/index accumulated index records
     */
    public void flushIndexQueue() {
        final List<IndexEntity> col = new ArrayList<IndexEntity>();
        indexRequests.drainTo(col, bulkSize);
        if (!col.isEmpty()) {
            executeBulk(col);
        }
    }


    /* (non-Javadoc)
     * @see org.slc.sli.search.process.Indexer#index(org.slc.sli.search.entity.IndexEntity)
     */
    @Override
    public void index(IndexEntity ie) {
        try {
            if (ie != null) {
                if (knownIndexesMap.get(ie.getIndex())) {
                    indexRequests.put(ie);
                } else {
                    throw new SearchIndexerException("Unable to create mappins for " + ie.getIndex());
                }
            }
        } catch (InterruptedException e) {
            throw new SearchIndexerException("Shutting down...", e);
        } catch (ExecutionException e) {
            throw new SearchIndexerException("Unable to create mappins for " + ie.getIndex(), e);
        }
    }

    public void executeBulk(List<IndexEntity> docs) {
        ListMultimap<Action, IndexEntity> docMap =  ArrayListMultimap.create();
        for (IndexEntity ie : docs) {
            docMap.put(ie.getAction(), ie);
        }
        for (Action a : docMap.keySet()) {
            execute(a, docMap.get(a));
        }
    }

    public void execute(Action a, List<IndexEntity> docs) {
        connector.execute(a, docs);
    }


    /**
     * Add index mapping if no index is found in the list of known indexes. Can be repeated.
     * @param index
     */
    public void addIndexMappingIfNeeded(String index) {
        LOG.info("Updating mappings for " + index);
        try {
            connector.createIndex(index);
            for (IndexConfig config : indexConfigStore.getConfigs()) {
                if (!config.isChildDoc()) {
                    connector.putMapping(index, config.getCollectionName(), IndexEntityUtil.getBodyForIndex(config.getMapping()));
                }
            }

        } catch (Exception e) {
            LOG.info("Index " + index + " already exists");
        }
    }

    public void setBulkSize(int bulkSize) {
        this.bulkSize = bulkSize;
    }

    public void setAggregatePeriod(long aggregatePeriodInMillis) {
        this.aggregatePeriodInMillis  = aggregatePeriodInMillis;
    }

    public void setIndexerWorkerPoolSize(int indexerWorkerPoolSize) {
        this.indexerWorkerPoolSize  = indexerWorkerPoolSize;
    }

    public void setIndexConfigStore(IndexConfigStore indexConfigStore) {
        this.indexConfigStore = indexConfigStore;
    }

    public void setSearchEngineConnector(SearchEngineConnector searchEngineConnector) {
        this.connector = searchEngineConnector;
    }

    @Override
    public void clearCache() {
        knownIndexesMap.invalidateAll();
    }

    @Override
    public String getHealth() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor)queueWatcherExecutor;
        return getClass() + ": {indexRequests size:" + indexRequests.size() + ", active count:" + tpe.getActiveCount() +
            ", completed count:" + tpe.getCompletedTaskCount() + "}";
    }
}
