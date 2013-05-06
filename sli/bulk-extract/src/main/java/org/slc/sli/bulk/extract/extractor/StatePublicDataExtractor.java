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
import org.slc.sli.bulk.extract.pub.PublicDataExtractor;
import org.slc.sli.bulk.extract.pub.PublicDataFactory;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

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
            LOG.error("Unable to trigger extract for the tenant");
            return;
        }

        Map<String, PublicKey> clientKeys = bulkExtractMongoDA.getAppPublicKeys();
        if(clientKeys == null || clientKeys.isEmpty()) {
            LOG.info("No authorized application to extract data.");
            return;
        }
        ExtractFile extractFile = createExtractFile(tenantDirectory, seaId, clientKeys);

        extractPublicData(seaId, extractFile);

        extractFile.closeWriters();
        try {
            extractFile.getManifestFile().generateMetaFile(startTime);
        } catch (IOException e) {
            LOG.error("Error creating metadata file: {}", e.getMessage());
        }

        extractFile.generateArchive();

        updateBulkExtractDb(startTime, seaId, extractFile);
    }

    /**
     * Extract the public data for the SEA.
     * @param seaId the ID of the SEA to extract
     * @param extractFile the extract file to extract to
     */
    protected void extractPublicData(String seaId, ExtractFile extractFile) {
        for (PublicDataExtractor data : factory.buildAllPublicDataExtracts(extractor)) {
            data.extract(seaId, extractFile);
        }
    }

    /**
     * Retrieve the SEA's id for the tenant. Fails if more than one SEA found.
     * @return
     *      SEA Id
     */
    protected String retrieveSEAId() {
        String seaId = null;
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ORGANIZATION_CATEGORIES,
                NeutralCriteria.CRITERIA_IN, Arrays.asList(STATE_EDUCATION_AGENCY)));
        final Iterable<Entity> entities = entityRepository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);

        if (entities == null || !entities.iterator().hasNext()) {
            LOG.error("No SEA is available for the tenant");
        } else {
            Iterator<Entity> iterator = entities.iterator();
            Entity seaEntity = iterator.next();
            if (iterator.hasNext()) {
                LOG.error("More than one SEA is found for the tenant");
            } else {
                seaId = seaEntity.getEntityId();
            }
        }

        return seaId;
    }

    /**
     * Update the bulk extract files db record.
     * @param startTime the time when the extract was initiated
     * @param seaId the SEA Id
     */
    protected void updateBulkExtractDb(DateTime startTime, String seaId, ExtractFile extractFile) {
        for(Map.Entry<String, File> archiveFile : extractFile.getArchiveFiles().entrySet()) {
            bulkExtractMongoDA.updateDBRecord(TenantContext.getTenantId(), archiveFile.getValue().getAbsolutePath(), archiveFile.getKey(), startTime.toDate(), false, seaId, true);
        }
    }

    protected ExtractFile createExtractFile(File tenantDirectory, String seaId, Map<String, PublicKey> clientKeys) {
        return new ExtractFile(tenantDirectory, Launcher.getArchiveName(seaId,
                startTime.toDate()), clientKeys);
    }
}
