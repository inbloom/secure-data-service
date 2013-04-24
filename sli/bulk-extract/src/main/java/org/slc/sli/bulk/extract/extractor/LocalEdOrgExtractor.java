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

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.metadata.ManifestFile;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Creates local ed org tarballs
 */
public class LocalEdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);
    private Repository<Entity> repository;
    private Map<String, Set<String>> leaToEdorgCache;
    private EntityExtractor entityExtractor;
    private Map<String, String> entitiesToCollections;
    private BulkExtractMongoDA bulkExtractMongoDA;
    private ManifestFile metaDataFile;
    private String baseDirectory;

    /**
     * Creates unencrypted LEA bulk extract files if any are needed for the given tenant
     * @param tenant name of tenant to extract
     */
    public void execute(String tenant, File tenantDirectory, DateTime startTime) {

        TenantContext.setTenantId(tenant);
        Map<String, PublicKey> appPublicKeys = bulkExtractMongoDA.getAppPublicKeys();

        leaToEdorgCache = buildEdOrgCache();
        
        for (String lea : new HashSet<String>(leaToEdorgCache.keySet())) {
            File leaDirectory = new File(tenantDirectory.getAbsoluteFile(), lea);
        	leaDirectory.mkdirs();
            ExtractFile extractFile = new ExtractFile(leaDirectory, getArchiveName(lea, startTime.toDate()),appPublicKeys);
			Criteria criteria = new Criteria("_id");
            criteria.in(new ArrayList<String>(leaToEdorgCache.get(lea)));
			Query query = new Query(criteria);
			entityExtractor.setExtractionQuery(query);
			entityExtractor.extractEntities(extractFile, "educationOrganization");
			extractFile.closeWriters();
            try {
                metaDataFile = extractFile.getManifestFile();
                metaDataFile.generateMetaFile(startTime);
            } catch (IOException e) {
                LOG.error("Error creating metadata file: {}", e.getMessage());
            }

            // generate archive
            try {
                extractFile.generateArchive();
            } catch (Exception e) {
                LOG.error("Error generating archive file: {}", e.getMessage());
            }
            
            Map<String, Set<String>> leaToApps = leaToApps();

            // update db to point to new archive
            for(Entry<String, File> archiveFile : extractFile.getArchiveFiles().entrySet()) {
                Set<String> apps = leaToApps.get(lea);
            	for(String app : apps) {
                    bulkExtractMongoDA.updateDBRecord(tenant, archiveFile.getValue().getAbsolutePath(), app,
                            startTime.toDate(), false, lea);
            	}
            }

        }
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
        Map<String, Set<String>> beAppsToLEAs = getBulkExtractLEAsPerApp();
        Set<String> leas = new HashSet<String>();
        for (String app : beAppsToLEAs.keySet()) {
            leas.addAll(beAppsToLEAs.get(app));
        }
        LOG.info("Authorized edorgs for extract are : {}", leas);

        Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
        for (String lea : leas) {
            // Add the leas to the cache so they can be used without thinking.
            Set<String> children = getChildEdOrgs(Arrays.asList(lea));
            children.add(lea);
            cache.put(lea, children);
        }
        return cache;
    }
    
    private Map<String, Set<String>> leaToApps() {
    	Map<String, Set<String>> result = new HashMap<String, Set<String>>();
    	Map<String, Set<String>> beAppsToLEAs = getBulkExtractLEAsPerApp();
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

}
