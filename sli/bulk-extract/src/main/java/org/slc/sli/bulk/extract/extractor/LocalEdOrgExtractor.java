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
package org.slc.sli.bulk.extract.extractor;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Creates local ed org tarballs
 */
public class LocalEdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);
    private Repository<Entity> repository;
    private Map<String, String> edorgToLEACache;
    private String baseDirectory;
    private BulkExtractMongoDA bulkExtractMongoDA;

    /**
     * Creates unencrypted LEA bulk extract files if any are needed for the given tenant
     * @param tenant name of tenant to extract
     */
    public void execute(String tenant, ExtractFile file, DateTime startTime) {
        //TODO replace stub do it
        LOG.debug("STUB!");
        
        Set<String> leas = getAllLEAs(getBulkExtractLEAsPerApp());
        edorgToLEACache = buildEdorgCache(leas);
        
    }

    /**
     * Attempts to get all of the LEAs that should have a LEA level extract scheduled.
     *
     * @return a set of the LEA ids that need a bulk extract
     */
    public Set<String> getAllLEAs(Map<String, Set<String>> bulkExtractLEAs) {
        Set<String> leas = new HashSet<String>();
        for (Map.Entry<String, Set<String>> entry : bulkExtractLEAs.entrySet()) {
            leas.addAll(entry.getValue());
        }
        return leas;
    }

    /**
     * Returns a map that maps an edorg to it's top level LEA, used as a cache
     * to speed up extract
     * 
     * @param leas
     * @return
     */
    private Map<String, String> buildEdorgCache(Set<String> leas) {
        Map<String, String> cache = new HashMap<String, String>();
        for (String lea : leas) {
            Set<String> children = getChildEdOrgs(Arrays.asList(lea));
            for (String child : children) {
                cache.put(child, lea);
            }
        }
        return cache;
    }

    /**
     * A helper function to get the list of approved app ids that have bulk extract enabled
     *
     * @return a set of approved bulk extract app ids
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBulkExtractApps() {
        // Butt-hole table scans prevent us from using a query, so we'll just scan all the apps!
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("isBulkExtract", NeutralCriteria.OPERATOR_EQUAL,
                true));
        appQuery.addCriteria(new NeutralCriteria("registration.status", NeutralCriteria.OPERATOR_EQUAL, "APPROVED"));
        TenantContext.setIsSystemCall(true);
        Iterable<Entity> apps = repository.findAll("application", new NeutralQuery());
        TenantContext.setIsSystemCall(false);
        Set<String> appIds = new HashSet<String>();
        for (Entity app : apps) {
            if (app.getBody().containsKey("isBulkExtract") && (Boolean) app.getBody().get("isBulkExtract") == true) {
                if (((String) ((Map<String, Object>) app.getBody().get("registration")).get("status"))
                        .equals("APPROVED")) {
                    appIds.add(app.getEntityId());
                }
            }
        }
        return appIds;
    }
    
    /**
     * Returns a list of child edorgs given a collection of parents
     * 
     * @param edOrgs
     * @return a set of child edorgs
     */
    private Set<String> getChildEdOrgs(Collection<String> edOrgs) {
        if (edOrgs.isEmpty()) {
            return new HashSet<String>();
        }
        
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, edOrgs));
        Iterable<Entity> childrenIds = repository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
        Set<String> children = new HashSet<String>();
        for (Entity child : childrenIds) {
            children.add(child.getEntityId());
        }
        if (!children.isEmpty()) {
            children.addAll(getChildEdOrgs(children));
        }
        return children;
    }

    /**
     * Attempts to get all of the LEAs per app that should have a LEA level extract scheduled.
     *
     * @return a set of the LEA ids that need a bulk extract per app
     */
    @SuppressWarnings("unchecked")
    public Map<String, Set<String>> getBulkExtractLEAsPerApp() {
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("applicationId", NeutralCriteria.CRITERIA_IN,
                getBulkExtractApps()));
        Iterable<Entity> apps = repository.findAll("applicationAuthorization", appQuery);
        Map<String, Set<String>> edorgIds = new HashMap<String, Set<String>>();
        for (Entity app : apps) {
            Set<String> edorgs = new HashSet<String>((Collection<String>) app.getBody().get("edorgs"));
            edorgIds.put((String) app.getBody().get("applicationId"), edorgs);
        }
        return edorgIds;
    }

    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }

    public Repository<Entity> getRepository() {
        return repository;
    }
    
    /**
     * Given the tenant, appId, the LEA id been extracted and a timestamp,
     * give me an extractFile for this combo
     * 
     * @param
     */
    public ExtractFile getExtractFilePerAppPerLEA(String tenant, String appId, String edorg, DateTime startTime, boolean isDelta) {
        ExtractFile extractFile = new ExtractFile(getAppSpecificDirectory(tenant, appId),
                getArchiveName(edorg, startTime.toDate(), isDelta),
                bulkExtractMongoDA.getClientIdAndPublicKey(appId, Arrays.asList(edorg)));
        extractFile.setEdorg(edorg);
        return extractFile;
    }
    
    private String getArchiveName(String edorg, Date startTime, boolean isDelta) {
        return edorg + "-" + Launcher.getTimeStamp(startTime) + (isDelta ? "-delta" : "");
    }

    private File getAppSpecificDirectory(String tenant, String app) {
        String tenantPath = baseDirectory + File.separator + TenantAwareMongoDbFactory.getTenantDatabaseName(tenant);
        File appDirectory = new File(tenantPath, app);
        appDirectory.mkdirs();
        return appDirectory;
    }
    
    /**
     * Set base dir.
     * 
     * @param baseDirectory
     *            Base directory of all bulk extract processes
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    /**
     * Set bulkExtractMongoDA.
     * 
     * @param bulkExtractMongoDA
     *            the bulkExtractMongoDA to set
     */
    public void setBulkExtractMongoDA(BulkExtractMongoDA bulkExtractMongoDA) {
        this.bulkExtractMongoDA = bulkExtractMongoDA;
    }

}
