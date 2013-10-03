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


import static org.slc.sli.bulk.extract.LogUtil.audit;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.pub.PublicDataExtractor;
import org.slc.sli.bulk.extract.pub.PublicDataFactory;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Extract the Public Data for the State Education Agency.
 * @author ablum
 */
@Component
public class StatePublicDataExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(StatePublicDataExtractor.class);

    @Autowired
    private BulkExtractMongoDA bulkExtractMongoDA;

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> entityRepository;

    @Autowired
    private EntityExtractor extractor;

    private PublicDataFactory factory = new PublicDataFactory();
    private DateTime startTime;

    private static final String STATE_EDUCATION_AGENCY = "State Education Agency";

    @Autowired
    private SecurityEventUtil securityEventUtil;

    /**
     * Creates unencrypted SEA public data bulk extract files if any are needed for the given tenant.
     *
     * @param tenant name of tenant to extract
     * @param tenantDirectory for the extract
     * @param startTime of the extract
     */
    public void execute(String tenant, File tenantDirectory, DateTime startTime) {
        TenantContext.setTenantId(tenant);
        this.startTime = startTime;
        String seaId = retrieveSEAId();

        if(seaId == null) {
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                    "SEA public data extract", LogLevelType.TYPE_ERROR,
                    BEMessageCode.BE_SE_CODE_0012));
            LOG.error("Unable to trigger extract for the tenant");
            return;
        }

        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                seaId + " SEA public data extract", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0013));


        Map<String, PublicKey> clientKeys = bulkExtractMongoDA.getAppPublicKeys();
        if(clientKeys == null || clientKeys.isEmpty()) {
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                    "SEA public data extract", LogLevelType.TYPE_INFO,
                    BEMessageCode.BE_SE_CODE_0014));
            LOG.info("No authorized application to extract data.");
            return;
        }

        ExtractFile extractFile = createExtractFile(tenantDirectory, seaId, clientKeys);

        extractPublicData(extractFile);

        extractFile.closeWriters();
        try {
            extractFile.getManifestFile().generateMetaFile(startTime);
        } catch (IOException e) {
            LOG.error("Error creating metadata file: {}", e.getMessage());
        }

        extractFile.generateArchive();

        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                seaId + " SEA public data extract", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0015));
        updateBulkExtractDb(seaId, extractFile);
    }

    /**
     * Extract the public data for the SEA.
     * @param extractFile the extract file to extract to
     */
    protected void extractPublicData(ExtractFile extractFile) {
        for (PublicDataExtractor data : factory.buildAllPublicDataExtracts(extractor)) {
            data.extract(extractFile);
        }
    }

    /**
     * Retrieve the SEA's id for the tenant. Fails if more than one SEA found.
     * @return
     *      SEA Id
     */
    public String retrieveSEAId() {
        String seaId = null;
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ORGANIZATION_CATEGORIES,
                NeutralCriteria.CRITERIA_IN, Arrays.asList(STATE_EDUCATION_AGENCY)));
        final Iterable<Entity> entities = entityRepository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);

        if (entities == null || !entities.iterator().hasNext()) {
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                    "SEA public data extract", LogLevelType.TYPE_ERROR, BEMessageCode.BE_SE_CODE_0016));
            LOG.error("No SEA is available for the tenant");
        } else {
            Iterator<Entity> iterator = entities.iterator();
            Entity seaEntity = iterator.next();
            if (iterator.hasNext()) {
                audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                        "SEA public data extract", LogLevelType.TYPE_ERROR, BEMessageCode.BE_SE_CODE_0017));
                LOG.error("More than one SEA is found for the tenant");
            } else {
                seaId = seaEntity.getEntityId();
            }
        }

        return seaId;
    }

    /**
     * Update the bulk extract files db record.
     * @param seaId the SEA Id
     * @param extractFile the extract file
     */
    protected void updateBulkExtractDb(String seaId, ExtractFile extractFile) {
        for(Map.Entry<String, File> archiveFile : extractFile.getArchiveFiles().entrySet()) {
            bulkExtractMongoDA.updateDBRecord(TenantContext.getTenantId(), archiveFile.getValue().getAbsolutePath(), archiveFile.getKey(), startTime.toDate(), false, seaId, true);
        }
    }

    /**
     * Creates an extract file instance.
     * @param tenantDirectory the parent directory of the file.
     * @param seaId the SEA Id.
     * @param clientKeys the public keys for registered apps.
     * @return an extract file instance.
     */
    protected ExtractFile createExtractFile(File tenantDirectory, String seaId, Map<String, PublicKey> clientKeys) {
        ExtractFile file = new ExtractFile(tenantDirectory, Launcher.getArchiveName(seaId,
                startTime.toDate()), clientKeys, securityEventUtil);
        file.setEdorg(seaId);
        return file;
    }

    /**
     * Set securityEventUtil.
     * @param securityEventUtil
     *          securityEventUtil
     */
    public void setSecurityEventUtil(SecurityEventUtil securityEventUtil) {
        this.securityEventUtil = securityEventUtil;
    }
}
