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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.metadata.ManifestFile;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bulk extractor to extract data for a tenant.
 *
 * @author tke
 *
 */
public class TenantExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(TenantExtractor.class);

    private BulkExtractMongoDA bulkExtractMongoDA;

    private Map<String, String> entitiesToCollections;

    private EntityExtractor entityExtractor;

    private ManifestFile metaDataFile;

    @Autowired
    private SecurityEventUtil securityEventUtil;

    /**
     * Extract all the entities from a tenant.
     *
     * @param tenant
     *            TenantId
     * @param extractFile
     *            Extract archive file
     * @param startTime
     *            start time stamp
     */
    public void execute(String tenant, ExtractFile extractFile, DateTime startTime) {
        Set<String> uniqueCollections = new HashSet<String>(entitiesToCollections.values());

        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                "Tenant full extract", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0003));

        Map<String, PublicKey> clientKeys = bulkExtractMongoDA.getAppPublicKeys();
        if (clientKeys == null || clientKeys.isEmpty()) {
            LOG.info("No authorized application to extract data.");
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                    tenant + " full extract", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0004, tenant));
            return;
        }
        extractFile.setClientKeys(clientKeys);
        for (String collection : uniqueCollections) {
            entityExtractor.extractEntities(extractFile, collection);
            extractFile.closeWriters();
        }

        try {
            metaDataFile = extractFile.getManifestFile();
            metaDataFile.generateMetaFile(startTime);
        } catch (IOException e) {
            LOG.error("Error creating metadata file: {}", e.getMessage());
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                  "Tenant full extract", LogLevelType.TYPE_ERROR, BEMessageCode.BE_SE_CODE_0005, e.getMessage()));
        }

        try {
            extractFile.generateArchive();
        } catch (Exception e) {
            LOG.error("Error generating archive file: {}", e.getMessage());
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                     "Tenant full extract", LogLevelType.TYPE_ERROR,
                    BEMessageCode.BE_SE_CODE_0006, e.getMessage()));
        }

        for (Entry<String, File> archiveFile : extractFile.getArchiveFiles().entrySet()) {
            bulkExtractMongoDA.updateDBRecord(tenant, archiveFile.getValue().getAbsolutePath(),
                    archiveFile.getKey(), startTime.toDate(), false, null, false);
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                    archiveFile.getValue().getAbsolutePath(), LogLevelType.TYPE_INFO,
                    archiveFile.getKey(), BEMessageCode.BE_SE_CODE_0007));
        }

        audit(SecurityEventUtil.createSecurityEvent(this.getClass().getName(),
                "Completed tenant-level bulk extract", "Tenant full extract", LogLevelType.TYPE_INFO));
    }

    /**
     * get bulkExtractMongoDA.
     *
     * @return bulkExtractMongoDA
     */
    public BulkExtractMongoDA getBulkExtractMongoDA() {
        return bulkExtractMongoDA;
    }

    /**
     * set bulkExtractMongoDA.
     *
     * @param bulkExtractMongoDA
     *            bulk extractMongoDA
     */
    public void setBulkExtractMongoDA(BulkExtractMongoDA bulkExtractMongoDA) {
        this.bulkExtractMongoDA = bulkExtractMongoDA;
    }

    /**
     * Set entities to collections map.
     *
     * @param entitiesToCollections
     *            to collections map
     */
    public void setEntitiesToCollections(Map<String, String> entitiesToCollections) {
        this.entitiesToCollections = entitiesToCollections;
    }

    /**
     * get entity extractor.
     *
     * @return entity extractor
     */
    public EntityExtractor getEntityExtractor() {
        return entityExtractor;
    }

    /**
     * set entity extractor.
     *
     * @param entityExtractor
     *            entity extractor
     */
    public void setEntityExtractor(EntityExtractor entityExtractor) {
        this.entityExtractor = entityExtractor;
    }

    /**
     * set securityEventUtil.
     * @param securityEventUtil
     *          securityEventUtil
     */
    public void setSecurityEventUtil(SecurityEventUtil securityEventUtil) {
        this.securityEventUtil = securityEventUtil;
    }
}
