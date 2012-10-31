package org.slc.sli.search.process.impl;

import org.slc.sli.search.connector.SearchEngineConnector;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.connector.SourceDatastoreConnector.Tenant;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.Admin;
import org.slc.sli.search.process.Extractor;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.process.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminImpl implements Admin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Extractor extractor;
    private SourceDatastoreConnector source;
    private SearchEngineConnector searchEngine;
    private Indexer indexer;
    private Loader loader;
    
    private Tenant getTenant(String tenantId) {
        for (Tenant t : source.getTenants()) {
            if (t.getTenantId().equals(tenantId))
                return t;
        }
        throw new IllegalArgumentException("Invalid tenantId " + tenantId);
    }

    public void reload(String tenantId) {
        logger.info("Received reload " + tenantId);
        Tenant tenant = getTenant(tenantId);
        searchEngine.executeDelete(searchEngine.getIndexUri(), tenant.getDbName());
        extractor.execute(tenant, Action.INDEX);
    }
    public void reloadAll() {
        logger.info("Received reloadAll");
        searchEngine.executeDelete(searchEngine.getBaseUrl());
        extractor.execute(Action.INDEX);
    }
    public void reconcile(String tenantId) {
        logger.info("Received reconcile " + tenantId);
        extractor.execute(getTenant(tenantId), Action.UPDATE);
    }
    public void reconcileAll() {
        logger.info("Received reconcileAll");
        extractor.execute(Action.UPDATE);
    }
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
