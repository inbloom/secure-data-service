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

package org.slc.sli.ingestion.tenant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Mongo implementation for access to tenant data.
 *
 * @author jtully
 */
@Component
public class TenantMongoDA implements TenantDA {
    protected static final Logger LOG = LoggerFactory.getLogger(TenantDA.class);

    private static final String TENANT = "tenant";
    private static final String LANDING_ZONE_PATH = "landingZone.path";
    private static final String LANDING_ZONE_INGESTION_SERVER = "landingZone.ingestionServer";
    public static final String TENANT_ID = "tenantId";
    public static final String DB_NAME = "dbName";
    public static final String INGESTION_SERVER = "ingestionServer";
    public static final String PATH = "path";
    public static final String LANDING_ZONE = "landingZone";
    public static final String PRELOAD_DATA = "preload";
    public static final String PRELOAD_STATUS = "status";
    public static final String PRELOAD_FILES = "files";
    public static final String TENANT_COLLECTION = "tenant";
    public static final String TENANT_TYPE = "tenant";
    public static final String EDUCATION_ORGANIZATION = "educationOrganization";
    public static final String DESC = "desc";
    public static final String ALL_STATUS_FIELDS = "body.landingZone.$.preload.status";
    public static final String STATUS_FIELD = "landingZone.preload.status";

    private static final String TENANT_READY_FIELD = "body.tenantIsReady";

    private Repository<Entity> entityRepository;
    private static final NeutralCriteria PRELOAD_READY_CRITERIA = new NeutralCriteria(LANDING_ZONE + "." + PRELOAD_DATA
            + "." + PRELOAD_STATUS, "=", "ready");

    @Override
    public List<String> getLzPaths() {
        return findTenantPathsByIngestionServer();
    }

    @Override
    public String getTenantId(String lzPath) {
        return findTenantIdByLzPath(lzPath);
    }

    @Override
    public void insertTenant(TenantRecord tenant) {
        if (entityRepository.findOne(TENANT_COLLECTION,
                new NeutralQuery(new NeutralCriteria(TENANT_ID, "=", tenant.getTenantId()))) == null) {
            entityRepository.create(TENANT_COLLECTION, getTenantBody(tenant));
        }
    }

    private Map<String, Object> getTenantBody(TenantRecord tenant) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(TENANT_ID, tenant.getTenantId());
        body.put(DB_NAME, TenantIdToDbName.convertTenantIdToDbName(tenant.getDbName()));
        List<Map<String, String>> landingZones = new ArrayList<Map<String, String>>();
        if (tenant.getLandingZone() != null) {
            for (LandingZoneRecord landingZoneRecord : tenant.getLandingZone()) {
                Map<String, String> landingZone = new HashMap<String, String>();
                landingZone.put(EDUCATION_ORGANIZATION, landingZoneRecord.getEducationOrganization());
                landingZone.put(INGESTION_SERVER, landingZoneRecord.getIngestionServer());
                landingZone.put(PATH, landingZoneRecord.getPath());
                landingZone.put(DESC, landingZoneRecord.getDesc());
                landingZones.add(landingZone);
            }
        }
        body.put(LANDING_ZONE, landingZones);
        return body;
    }

    private List<String> findTenantPathsByIngestionServer() {
        List<String> tenantPaths = new ArrayList<String>();

        Iterable<Entity> entities = entityRepository.findAll(TENANT_COLLECTION, new NeutralQuery());

        for (Entity entity : entities) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> landingZones = (List<Map<String, String>>) entity.getBody().get(LANDING_ZONE);
            if (landingZones != null) {
                for (Map<String, String> landingZone : landingZones) {
                    String path = landingZone.get(PATH);
                    if (path != null) {
                        tenantPaths.add(path);
                    }
                }
            }
        }
        return tenantPaths;
    }

    private NeutralCriteria byServerQuery(String targetIngestionServer) {
        return new NeutralCriteria(LANDING_ZONE_INGESTION_SERVER, "=", targetIngestionServer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getTenantEdOrg(String lzPath) {

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(LANDING_ZONE_PATH, "=", lzPath));
        Entity entity = entityRepository.findOne(TENANT_COLLECTION, query);
        if ( null == entity ) {
			return null;
		}
        Map<String, Object> body = entity.getBody();
        if ( null == body ) {
			return null;
		}
        BasicDBList lzArr = (BasicDBList) body.get(LANDING_ZONE);
        if ( null == lzArr ) {
			return null;
		}
        for( Object lzObj : lzArr ) {
        	Map<String, Object> lz = (Map<String, Object>) lzObj;
        	String path = (String) lz.get(PATH);
        	if ( null != path && path.equals(lzPath) ) {
				return (String) lz.get(EDUCATION_ORGANIZATION);
			}
        }
        return null;
    }

    private String findTenantIdByLzPath(String lzPath) {
        String tenantId = null;

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(LANDING_ZONE_PATH, "=", lzPath));
        Entity entity = entityRepository.findOne(TENANT_COLLECTION, query);

        if (entity != null && entity.getBody() != null && entity.getBody().get(TENANT_ID) != null) {
            tenantId = entity.getBody().get(TENANT_ID).toString();
        }
        return tenantId;
    }

    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, List<String>> getPreloadFiles(String ingestionServer) {
        Iterable<Entity> tenants = entityRepository.findAll(
                TENANT_COLLECTION,
                new NeutralQuery(byServerQuery(ingestionServer)).addCriteria(PRELOAD_READY_CRITERIA).setIncludeFields(
                        Arrays.asList(LANDING_ZONE + "." + PRELOAD_DATA, LANDING_ZONE_PATH,
                                LANDING_ZONE_INGESTION_SERVER)));
        Map<String, List<String>> fileMap = new HashMap<String, List<String>>();
        for (Entity tenant : tenants) {
            if (markPreloadStarted(tenant)) { // only return this if the tenant is not already in
                                              // the
                // started state
                List<Map<String, Object>> landingZones = (List<Map<String, Object>>) tenant.getBody().get(LANDING_ZONE);
                for (Map<String, Object> landingZone : landingZones) {
                    if (landingZone.get(INGESTION_SERVER).equals(ingestionServer)) {
                        List<String> files = new ArrayList<String>();
                        Map<String, Object> preloadData = (Map<String, Object>) landingZone.get(PRELOAD_DATA);
                        if (preloadData != null) {
                            if ("ready".equals(preloadData.get(PRELOAD_STATUS))) {
                                files.addAll((Collection<? extends String>) preloadData.get(PRELOAD_FILES));
                            }
                            fileMap.put((String) landingZone.get(PATH), files);
                        }
                    }
                }
            }
        }
        return fileMap;
    }

    private boolean markPreloadStarted(Entity tenant) {
        return entityRepository.doUpdate(
                TENANT_COLLECTION,
                new NeutralQuery().addCriteria(new NeutralCriteria("_id", "=", tenant.getEntityId())).addCriteria(
                        PRELOAD_READY_CRITERIA),
                Update.update("body." + TenantMongoDA.LANDING_ZONE + ".$." + TenantMongoDA.PRELOAD_DATA + "."
                        + TenantMongoDA.PRELOAD_STATUS, "started"));

    }

    @Override
    public boolean tenantDbIsReady(String tenantId) {
        boolean isPartitioned = false;

        // checking for indexes ensures that the scripts were capable of running
        TenantContext.setTenantId(tenantId);
        TenantContext.setIsSystemCall(false);
        boolean isIndexed = entityRepository.count("system.indexes", new Query()) > 0;

        if (isIndexed) {

            // checking for flag that will only be set after scripts run
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("tenantId", "=", tenantId));
            query.addCriteria(new NeutralCriteria(TENANT_READY_FIELD, "=", true, false));

            try {
                TenantContext.setIsSystemCall(true);
                isPartitioned = entityRepository.count(TENANT_COLLECTION, query) > 0;
            } finally {
                TenantContext.setIsSystemCall(false);
            }
        }

        return isPartitioned;
    }

    @Override
    public void setTenantReadyFlag(String tenantId) {

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(TENANT_ID, "=", tenantId));

        Update update = new Update();
        update.set(TENANT_READY_FIELD, true);

        try {
            TenantContext.setIsSystemCall(true);
            entityRepository.doUpdate(TENANT, query, update);
        } finally {
            TenantContext.setIsSystemCall(false);
        }
    }

    @Override
    public void unsetTenantReadyFlag(String tenantId){
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(TENANT_ID, "=", tenantId));

        Update update = new Update();
        update.unset(TENANT_READY_FIELD);

        try {
            TenantContext.setIsSystemCall(true);
            entityRepository.doUpdate(TENANT, query, update);
        } finally {
            TenantContext.setIsSystemCall(false);
        }
    }

    @Override
    public boolean updateAndAquireOnboardingLock(String tenantId) {

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(TENANT_ID, "=", tenantId));
        query.addCriteria(new NeutralCriteria("tenantIsReady", "exists", false));

        Update update = new Update();
        update.set(TENANT_READY_FIELD, false);

        try {
            TenantContext.setIsSystemCall(true);
            return entityRepository.findAndUpdate(TENANT, query, update) == null ? false : true;
        } finally {
            TenantContext.setIsSystemCall(false);
        }

    }


    @Override
    public void removeInvalidTenant(String lzPath) {
        BasicDBObject match = new BasicDBObject("body.landingZone.path", lzPath);
        BasicDBObject update = new BasicDBObject("body.landingZone", new BasicDBObject("path", lzPath));
        entityRepository.getCollection(TENANT_COLLECTION).update(match, new BasicDBObject("$pull", update));
    }

    @Override
    public Map<String, List<String>> getPreloadFiles() {
        NeutralQuery preloadReadyTenantQuery = new NeutralQuery().addCriteria(
                new NeutralCriteria(STATUS_FIELD, "=", "ready")).setIncludeFields(
                Arrays.asList(LANDING_ZONE + "." + PRELOAD_DATA, LANDING_ZONE_PATH, TENANT_ID));
        Update update = Update.update(ALL_STATUS_FIELDS, "started");

        Map<String, List<String>> fileMap = new HashMap<String, List<String>>();
        Entity tenant;
        while ((tenant = entityRepository.findAndUpdate(TENANT_COLLECTION, preloadReadyTenantQuery, update)) != null) {
            LOG.info("Found new tenant to preload! [" + tenant.getBody().get(TENANT_ID) + "]");
            List<Map<String, Object>> landingZones = (List<Map<String, Object>>) tenant.getBody().get(LANDING_ZONE);
            for (Map<String, Object> landingZone : landingZones) {
                List<String> files = new ArrayList<String>();
                Map<String, Object> preloadData = (Map<String, Object>) landingZone.get(PRELOAD_DATA);
                if (preloadData != null) {
                    files.addAll((Collection<? extends String>) preloadData.get(PRELOAD_FILES));
                    fileMap.put((String) landingZone.get(PATH), files);
                }
            }
        }
        return fileMap;
    }

    @Override
    public List<String> getAllTenantDbs () {
        List<String> tenantDbs = new ArrayList<String>();
        Iterable<Entity> entities = entityRepository.findAll(TENANT_COLLECTION, new NeutralQuery());

        for(Entity entity : entities) {
            String collection = (String) entity.getBody().get(DB_NAME);
            tenantDbs.add(collection);
        }

        return tenantDbs;
    }

}
