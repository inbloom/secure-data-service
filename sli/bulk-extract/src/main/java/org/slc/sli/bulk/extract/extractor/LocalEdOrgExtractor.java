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
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.lea.EdorgExtractor;
import org.slc.sli.bulk.extract.lea.LEAExtractFileMap;
import org.slc.sli.bulk.extract.lea.LEAExtractorFactory;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Creates local ed org tarballs
 */
public class LocalEdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);
    private Repository<Entity> repository;

    @Autowired
    LocalEdOrgExtractHelper helper;

    private LEAExtractFileMap leaToExtractFileMap;
    private EntityExtractor entityExtractor;
    private Map<String, String> entitiesToCollections;
    private BulkExtractMongoDA bulkExtractMongoDA;
    private LEAExtractorFactory factory;

    private File tenantDirectory;
    private DateTime startTime;

    /**
     * Creates unencrypted LEA bulk extract files if any are needed for the given tenant
     * @param tenant name of tenant to extract
     */
    public void execute(String tenant, File tenantDirectory, DateTime startTime) {

        // 1. SETUP
        TenantContext.setTenantId(tenant);
        this.tenantDirectory = tenantDirectory;
        this.startTime = startTime;
        this.factory = new LEAExtractorFactory();

        leaToExtractFileMap = new LEAExtractFileMap(buildLEAToExtractFile());

        // 2. EXTRACT
        EdorgExtractor edorg = factory.buildEdorgExtractor(entityExtractor, leaToExtractFileMap);
        edorg.extractEntities(buildEdOrgCache());
        leaToExtractFileMap.closeFiles();

        // TODO extract other entities
        leaToExtractFileMap.buildManifestFiles(startTime);
        leaToExtractFileMap.archiveFiles();


        // 3. ARCHIVE
        updateBulkExtractDb(tenant, startTime);
    }

    private void updateBulkExtractDb(String tenant, DateTime startTime) {
        for(String lea : helper.getBulkExtractLEAs()) {
            // update db to point to new archive
            Map<String, Set<String>> leaToApps = leaToApps();
            for (Entry<String, File> archiveFile : leaToExtractFileMap.getExtractFileForLea(lea).getArchiveFiles()
                    .entrySet()) {
                Set<String> apps = leaToApps.get(lea);
            	for(String app : apps) {
                    bulkExtractMongoDA.updateDBRecord(tenant, archiveFile.getValue().getAbsolutePath(), app,
                            startTime.toDate(), false, lea);
            	}
            }
        }
    }

    private Map<String, ExtractFile> buildLEAToExtractFile() {
        Map<String, ExtractFile> edOrgToLEAExtract = new HashMap<String, ExtractFile>();

        Map<String, PublicKey> appPublicKeys = bulkExtractMongoDA.getAppPublicKeys();
        for (String lea : helper.getBulkExtractLEAs()) {
            ExtractFile file = factory.buildLEAExtractFile(tenantDirectory.getAbsolutePath(), lea,
                    getArchiveName(lea, startTime.toDate()),
                    appPublicKeys);
            edOrgToLEAExtract.put(lea, file);
            for (String child : helper.getChildEdOrgs(Arrays.asList(lea))) {
                edOrgToLEAExtract.put(child, file);
            }

        }
        return edOrgToLEAExtract;
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
     * @return
     */
    private Map<String, Set<String>> buildEdOrgCache() {
        Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
        for (String lea : helper.getBulkExtractLEAs()) {
            Set<String> children = helper.getChildEdOrgs(Arrays.asList(lea));
            children.add(lea);
            cache.put(lea, children);
        }
        return cache;
    }
    
    public Map<String, Set<String>> leaToApps() {
    	Map<String, Set<String>> result = new HashMap<String, Set<String>>();
    	Map<String, Set<String>> beAppsToLEAs = helper.getBulkExtractLEAsPerApp();
    	for(String app : beAppsToLEAs.keySet()) {
    		for(String lea : beAppsToLEAs.get(app)) {
    			if (result.get(lea) == null) {
    				result.put(lea, new HashSet<String>());
    			}
    			result.get(lea).add(app);
    		}
    	}
    	return result;
    }
    


    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }

    public Repository<Entity> getRepository() {
        return repository;
    }
    
    private String getArchiveName(String edOrg, Date startTime) {
        return edOrg + "-" + Launcher.getTimeStamp(startTime);
    }

	public EntityExtractor getEntityExtractor() {
		return entityExtractor;
	}

	public void setEntityExtractor(EntityExtractor entityExtractor) {
		this.entityExtractor = entityExtractor;
	}

	public Map<String, String> getEntitiesToCollections() {
		return entitiesToCollections;
	}

	public void setEntitiesToCollections(Map<String, String> entitiesToCollections) {
		this.entitiesToCollections = entitiesToCollections;
	}

	public BulkExtractMongoDA getBulkExtractMongoDA() {
		return bulkExtractMongoDA;
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

    public void setHelper(LocalEdOrgExtractHelper helper) {
        this.helper = helper;
    }
}
