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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.search.connector.SearchEngineConnector;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.connector.SourceDatastoreConnector.Tenant;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.Admin;
import org.slc.sli.search.process.Extractor;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.process.Loader;

public class AdminImpl implements Admin {
    private static final Logger LOG = LoggerFactory.getLogger(AdminImpl.class);
    private Extractor extractor;
    private SourceDatastoreConnector source;
    private SearchEngineConnector searchEngine;
    private Indexer indexer;
    private Loader loader;

    private Tenant getTenant(String tenantId) {
        for (Tenant t : source.getTenants()) {
            if (t.getTenantId().equals(tenantId)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid tenantId " + tenantId);
    }

    @Override
    public void reload(String tenantId) {
        LOG.info("Received reload " + tenantId);
        Tenant tenant = getTenant(tenantId);
        indexer.clearCache();
        searchEngine.deleteIndex(tenant.getDbName());
        extractor.execute(tenant, Action.INDEX);
    }
    @Override
    public void reloadAll() {
        LOG.info("Received reloadAll");
        searchEngine.executeDelete(searchEngine.getBaseUrl());
        indexer.clearCache();
        extractor.execute(Action.INDEX);
    }
    @Override
    public void reconcile(String tenantId) {
        LOG.info("Received reconcile " + tenantId);
        indexer.clearCache();
        extractor.execute(getTenant(tenantId), Action.UPDATE);
    }
    @Override
    public void reconcileAll() {
        LOG.info("Received reconcileAll");
        indexer.clearCache();
        extractor.execute(Action.UPDATE);
    }
    @Override
    public String getHealth() {
        StringBuilder status = new StringBuilder();
        status.append(extractor.getHealth());
        status.append(loader.getHealth());
        status.append(indexer.getHealth());
        return status.toString();
    }

    public void setExtractor(Extractor extractor) {
        this.extractor = extractor;
    }
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public void setSourceDatastoreConnector(SourceDatastoreConnector source) {
        this.source = source;
    }

    public void setSearchEngineConnector(SearchEngineConnector searchEngine) {
        this.searchEngine = searchEngine;
    }
}
