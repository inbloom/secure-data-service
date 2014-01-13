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
package org.slc.sli.search.connector.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.search.connector.SearchEngineConnector;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.NestedMapUtil;
import org.slc.sli.search.util.RecoverableIndexerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * A convenience class for elasticsearch bulk operations
 *
 */
public class ESOperation extends ESConnector implements SearchEngineConnector {
    private static final Logger LOG = LoggerFactory.getLogger(ESOperation.class);
    private int retryCount = 2;
    private long retryWaitMillis = 200;

    private String esUrl;
    private String bulkUri;

    private String mGetUri;

    private String mUpdateUri;

    private String indexUri;

    private String indexTypeUri;

    private final Map<Action, BulkOperation> actionMap =
            Maps.uniqueIndex(
                    Arrays.asList(new IndexOperation(), new DeleteOperation(),
                                  new UpdateOperation(), new GetUpdateOperation()),
                    new Function<BulkOperation, Action>() {
                        @Override
                        public Action apply(final BulkOperation op) {
                            return op.getAction();
                        }
                    });

    @Override
    public void execute(Action a, List<IndexEntity> docs) {
        actionMap.get(a).execute(docs);
    }

    public void setSearchUrl(String esUrl) {
        this.esUrl = esUrl;
        this.bulkUri = esUrl + "/_bulk";
        this.mGetUri = esUrl + "/_mget";
        this.indexUri = esUrl + "/{index}";
        this.indexTypeUri = esUrl + "/{index}/{type}";
        this.mUpdateUri = indexTypeUri + "/{id}/_update";
    }

    /**
     * A base class for each operation
     *
     */
    abstract class BulkOperation {
        void execute(List<IndexEntity> docs) {
            if (docs == null || docs.isEmpty()) {
                return;
            }
            tryExecute(docs, 0);
        }
        
        void tryExecute(List<IndexEntity> docs, int currentRetryCount) {
            try {
                try {
                    doExecute(docs);
                } catch (RecoverableIndexerException e) {
                    if (currentRetryCount < retryCount) {
                        LOG.error("An exception trying to execute ES operation. Retrying...", e);
                        Thread.sleep(retryWaitMillis);
                        tryExecute(docs, currentRetryCount + 1);
                    }
                }
            } catch (Exception e) {
                LOG.error("Something happened while performing ES operation. Will not re-try.", e);
            }
        }

        abstract void doExecute(List<IndexEntity> docs);
        abstract Action getAction();
    }

    /**
     * Takes a collection of index requests, builds a bulk http message to send to elastic search
     *
     * @param indexRequests
     */
    class IndexOperation extends BulkOperation {
        @Override
        public void doExecute(List<IndexEntity> docs) {
            LOG.info("Preparing _bulk request with " + docs.size() + " records");
            // create bulk http message
            // add each index request to the message
            String message = IndexEntityUtil.getBulkIndexJson(docs);
            LOG.info("Sending _bulk request with " + docs.size() + " records");
            // send the message
            executePost(getBulkUri(), message);
            LOG.info("Bulk index response: OK");
        }
        @Override
        Action getAction() {
            return Action.INDEX;
        }
    }

    /**
     * Takes a collection of delete requests, send a bulk delete to elastic search
     * @param docs
     */
    class DeleteOperation extends BulkOperation {
        @Override
        public void doExecute(List<IndexEntity> docs) {
            LOG.info("Preparing _bulk delete request with " + docs.size() + " records");

            String message = IndexEntityUtil.getBulkDeleteJson(docs);
            LOG.info("Sending _bulk delete request with " + docs.size() + " records");
            // send the message
            executePost(getBulkUri(), message);
            LOG.info("Bulk delete response: OK");
        }
        @Override
        Action getAction() {
            return Action.DELETE;
        }
    }

    /**
     * Takes a collection of index requests, builds a bulk http message to send to elastic search
     *
     * @param indexRequests
     */
     class UpdateOperation extends BulkOperation {
        @Override
        public void doExecute(List<IndexEntity> docs) {
            LOG.info("Sending update requests for " + docs.size() + " records");
            StringBuilder sb = new StringBuilder();
            LOG.info(IndexEntityUtil.toUpdateJson(docs.get(0)));
            for (IndexEntity ie : docs) {
                try {
                    sb.append(executePost(
                            getUpdateUri(), IndexEntityUtil.toUpdateJson(ie), ie.getIndex(), ie.getType(), ie.getId()).toString());
                }
                catch (Exception e) {
                    LOG.error("Unable to update entry for " + ie, e);
                }
            }
            LOG.info(sb.toString());
            // TODO: do we need to check the response status of each part of the bulk request?

        }
        @Override
        Action getAction() {
            return Action.QUICK_UPDATE;
        }
    }

    /**
     * Update operation which is a two-step get and re-index if changed
     *
     * @param indexRequests
     */
    class GetUpdateOperation extends IndexOperation {
        @SuppressWarnings("unchecked")
        @Override
        public void doExecute(List<IndexEntity> updates) {
            LOG.info("Preparing _mget request with " + updates.size() + " records");
            Map<String, IndexEntity> indexUpdateMap = new LinkedHashMap<String, IndexEntity>();
            Map<String, Object> entityBody;
            for (IndexEntity ie : updates) {
                // merge updates for the same entity
                if (indexUpdateMap.containsKey(ie.getId())) {
                    entityBody = indexUpdateMap.get(ie.getId()).getBody();
                    if (NestedMapUtil.merge(entityBody, ie.getBody())) {
                        ie = new IndexEntity(ie.getIndex(), ie.getType(), ie.getId(), entityBody);
                    }
                }
                indexUpdateMap.put(ie.getId(), ie);
            }
            try {
                String request = IndexEntityUtil.getBulkGetJson(indexUpdateMap.values());
                LOG.info("Sending _mget request with " + updates.size() + " records");
                String body = executePost(getMGetUri(), request);
                Map<String, Object> orig;
                List<Map<String, Object>> docs = (List<Map<String, Object>>)IndexEntityUtil.getEntity(body).get("docs");
                IndexEntity ie;
                final List<IndexEntity> reindex = new ArrayList<IndexEntity>();
                for (Map<String, Object> entity : docs) {
                    if (entity == null) {
                        continue;
                    }
                    if (entity.containsKey("exists") && (Boolean)entity.get("exists")) {
                        orig = (Map<String, Object>) entity.get("_source");
                        try {
                            ie = indexUpdateMap.remove(IndexEntityUtil.getIndexEntity(entity).getId());

                            if (ie != null) {
                                // if an update happened, re-index, if no update, skip the insert
                                if (NestedMapUtil.merge(orig, ie.getBody())) {
                                    reindex.add(new IndexEntity(ie.getIndex(), ie.getType(), ie.getId(), orig));
                                }
                            } else {
                                LOG.error("Unable to match response from get " + entity.get("_id"));
                            }
                        } catch (Exception e) {
                            LOG.error("Unable to process entry from ES for re-index " + entity.get("_id"));
                        }
                    } else { // if doesn't exist, add
                        ie = indexUpdateMap.remove(entity.get("_id"));
                        if (ie != null) {
                            reindex.add(ie);
                        }
                    }
                }
                if (!reindex.isEmpty()) {
                    super.doExecute(reindex);
                }
            } catch (Exception re) {
                LOG.error("Error on mget.", re);
            }
        }
        @Override
        Action getAction() {
            return Action.UPDATE;
        }
    }
    @Override
    public void deleteIndex(String index) {
        LOG.info("Deleting index " + index);
        executeDelete(getIndexUri(), index);
    }

    @Override
    public void createIndex(String index) {
        if (executeHead(getIndexUri(), index) != HttpStatus.OK) {
            LOG.info("Creating new index " + index);
            executePost(getIndexUri(), null, index);
        }
    }

    @Override
    public void putMapping(String index, String type, String mapping) {
        HttpStatus response = executePut(getIndexTypeUri() + "/_mapping?ignore_conflicts=true", mapping, index, type);
        LOG.info(String.format("Mapping response for %s/%s: %s ", index, type, response));
    }

    @Override
    public String getBaseUrl() {
        return esUrl;
    }

    public String getBulkUri() {
        return bulkUri;
    }

    public String getMGetUri() {
        return mGetUri;
    }

    public String getUpdateUri() {
        return mUpdateUri;
    }

    public String getIndexUri() {
        return indexUri;
    }

    public String getIndexTypeUri() {
        return indexTypeUri;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public void setRetryWaitMillis(long retryWaitMillis) {
        this.retryWaitMillis = retryWaitMillis;
    }
}
