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

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Creates local ed org tarballs
 */
public class LocalEdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);
    private Repository<Entity> repository;

    @Autowired
    LocalEdOrgExtractHelper helper;

    private Map<String, ExtractFile> edOrgToLEAExtract;

    private EntityExtractor entityExtractor;
    private Map<String, String> entitiesToCollections;
    private BulkExtractMongoDA bulkExtractMongoDA;

    private String baseDirectory;

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

        edOrgToLEAExtract = buildLEAToExtractFile();

        // 2. EXTRACT
        extractEdOrgs();
        // TODO extract other entities

        // 3. ARCHIVE
        for(String lea : helper.getBulkExtractLEAs()) {
            // close files
            edOrgToLEAExtract.get(lea).closeWriters();

            // generate lea manifest file
            try {
                edOrgToLEAExtract.get(lea).getManifestFile().generateMetaFile(startTime);
            } catch (IOException e) {
                LOG.error("Error creating metadata file: {}", e.getMessage());
            }

            // generate lea archive
            try {
                edOrgToLEAExtract.get(lea).generateArchive();
            } catch (Exception e) {
                LOG.error("Error generating archive file: {}", e.getMessage());
            }

            // update db to point to new archive
            Map<String, Set<String>> leaToApps = leaToApps();
            for(Entry<String, File> archiveFile : edOrgToLEAExtract.get(lea).getArchiveFiles().entrySet()) {
                Set<String> apps = leaToApps.get(lea);
            	for(String app : apps) {
                    bulkExtractMongoDA.updateDBRecord(tenant, archiveFile.getValue().getAbsolutePath(), app,
                            startTime.toDate(), false, lea);
            	}
            }
        }
    }

    private void extractEdOrgs() {
        Map<String, Set<String>> leaToEdorgCache = buildEdOrgCache();

        for (String lea : new HashSet<String>(leaToEdorgCache.keySet())) {
            ExtractFile extractFile = edOrgToLEAExtract.get(lea);
            Criteria criteria = new Criteria("_id");
            criteria.in(new ArrayList<String>(leaToEdorgCache.get(lea)));
			Query query = new Query(criteria);
			entityExtractor.setExtractionQuery(query);
			entityExtractor.extractEntities(extractFile, "educationOrganization");
        }
    }

    private Map<String, ExtractFile> buildLEAToExtractFile() {
        Map<String, ExtractFile> edOrgToLEAExtract = new HashMap<String, ExtractFile>();

        Map<String, PublicKey> appPublicKeys = bulkExtractMongoDA.getAppPublicKeys();
        for (String lea : helper.getBulkExtractLEAs()) {

            File leaDirectory = new File(tenantDirectory.getAbsoluteFile(), lea);
            leaDirectory.mkdirs();
            ExtractFile extractFile = new ExtractFile(leaDirectory, getArchiveName(lea, startTime.toDate()), appPublicKeys);

            edOrgToLEAExtract.put(lea, extractFile);
            for (String child : getChildEdOrgs(Arrays.asList(lea))) {
                edOrgToLEAExtract.put(child, extractFile);
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
            Set<String> children = getChildEdOrgs(Arrays.asList(lea));
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

    public void setHelper(LocalEdOrgExtractHelper helper) {
        this.helper = helper;
    }
}
