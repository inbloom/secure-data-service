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
package org.slc.sli.search.process.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SearchEngineConnector;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.SearchIndexerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Indexer is responsible for building elastic search index requests and
 * sending them to the elastic search server for processing.
 * 
 * @author dwu
 * 
 */
public class IndexerImpl implements Indexer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
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
    private final ConcurrentHashMap<String, Boolean> knownIndexesMap = new ConcurrentHashMap<String, Boolean>();
    
    public void init() {
        indexRequests = new LinkedBlockingQueue<IndexEntity>(bulkSize * indexerWorkerPoolSize);
        queueWatcherExecutor = Executors.newScheduledThreadPool(indexerWorkerPoolSize);
        logger.info("Indexer started");
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
        public void run() {
            try {
                if (!indexRequests.isEmpty()) {
                    queueWatcherExecutor.execute(new Runnable() {
                        
                        public void run() {
                            flushIndexQueue();
                        }
                    }); 
                }
            } catch (Throwable t) {
                logger.info("Unable to index with elasticsearch", t);
            }
        }
    }
    
    /**
     * flush/index accumulated index records
     */
    public void flushIndexQueue() {
        final List<IndexEntity> col = new ArrayList<IndexEntity>();
        indexRequests.drainTo(col, bulkSize);
        if (!col.isEmpty())
            executeBulk(col);
    }
    
    
    /* (non-Javadoc)
     * @see org.slc.sli.search.process.Indexer#index(org.slc.sli.search.entity.IndexEntity)
     */
    public void index(IndexEntity ie) {
        try {
            if (ie != null) {
                addIndexMappingIfNeeded(ie.getIndex());
                indexRequests.put(ie);
            }
        } catch (InterruptedException e) {
            throw new SearchIndexerException("Shutting down...");
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
        if (!knownIndexesMap.containsKey(index)) {
            logger.info("Updating mappings for " + index);
            try {
                connector.createIndex(index);
                for (IndexConfig config : indexConfigStore.getConfigs()) {
                    if (!config.isChildDoc()) {
                        connector.putMapping(index, config.getCollectionName(), IndexEntityUtil.getBodyForIndex(config.getMapping()));
                    }
                }
                
            } catch (Exception e) {
                logger.info("Index " + index + " already exists");
            }
            knownIndexesMap.put(index, Boolean.TRUE);
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
    
    public String getHealth() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor)queueWatcherExecutor;
        return getClass() + ": {indexRequests size:" + indexRequests.size() + ", active count:" + tpe.getActiveCount() +
            ", completed count:" + tpe.getCompletedTaskCount() + "}";
    }
}
