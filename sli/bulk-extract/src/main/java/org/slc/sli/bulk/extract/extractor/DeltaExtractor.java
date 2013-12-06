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
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.context.resolver.TypeResolver;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.DeltaRecord;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.Operation;
import org.slc.sli.bulk.extract.extractor.EntityExtractor.CollectionWrittenRecord;
import org.slc.sli.bulk.extract.files.EntityWriterManager;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.PublicEntityDefinition;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.DeltaJournal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;

/**
 * This class should be concerned about how to generate the delta files per EdOrg
 * per app, and the public delta files.
 *
 * It gets an iterator of deltas, and determine which app/EdOrg would need this
 * delta entity. It does not care about how to retrieve the deltas nor how the
 * delta files are generated.
 *
 * @author ycao
 *
 */
@Component
public class DeltaExtractor implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(DeltaExtractor.class);

    @Autowired
    DeltaEntityIterator deltaEntityIterator;

    @Autowired
    LocalEdOrgExtractor leaExtractor;

    @Autowired
    EdOrgExtractHelper helper;

    @Autowired
    EntityExtractor entityExtractor;

    @Autowired
    BulkExtractMongoDA bulkExtractMongoDA;

    @Autowired
    EntityWriterManager entityWriteManager;

    @Autowired
    TypeResolver typeResolver;

    @Autowired
    @Qualifier("secondaryRepo")
    Repository<Entity> repo;

    @Autowired
    private SecurityEventUtil securityEventUtil;

    Set<String> subdocs = EmbeddedDocumentRelations.getSubDocuments();

    private Map<String, ExtractFile> appPerEdOrgExtractFiles = new HashMap<String, ExtractFile>();
    private Map<String, EntityExtractor.CollectionWrittenRecord> appPerLeaCollectionRecords = new HashMap<String, EntityExtractor.CollectionWrittenRecord>();
    private Set<String> publicEntityTypes = new HashSet<String>();

    public static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

    public static final String DATE_FIELD = "date";
    public static final String TIME_FIELD = "t";

    @Override
    public void afterPropertiesSet() throws Exception {
        // Public entities list.
        for (PublicEntityDefinition entity : PublicEntityDefinition.values()) {
            publicEntityTypes.add(entity.getEntityName());
        }
    }

    /**
     * Creates delta data bulk extract files if any are needed for the given tenant.
     *
     * @param tenant - name of tenant to extract
     * @param tenantDirectory - Base directory of the tenant
     * @param deltaUptoTime - Extract for all deltas up to this time
     */
    public void execute(String tenant, File tenantDirectory, DateTime deltaUptoTime) {
        TenantContext.setTenantId(tenant);

        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(), "Delta Extract Initiation",
                LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0019, DATE_TIME_FORMATTER.print(deltaUptoTime)));

        Map<String, Set<String>> appsPerEdOrg = appsPerEdOrg();

        ExtractFile publicDeltaExtractFile = createPublicExtractFile(tenantDirectory, deltaUptoTime);

        deltaEntityIterator.init(tenant, deltaUptoTime);
        while (deltaEntityIterator.hasNext()) {
            DeltaRecord delta = deltaEntityIterator.next();

            if (delta.getOp() == Operation.UPDATE) {
                extractUpdate(delta, publicDeltaExtractFile, appsPerEdOrg, tenantDirectory, deltaUptoTime);
            } else if (delta.getOp() == Operation.DELETE) {
                extractDelete(delta, publicDeltaExtractFile, appsPerEdOrg, tenantDirectory, deltaUptoTime);
            } else if (delta.getOp() == Operation.PURGE) {
                extractPurge(delta, publicDeltaExtractFile, appsPerEdOrg, tenantDirectory, deltaUptoTime);
            }
        }

        logEntityCounts();

        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(), "Delta Extract Finished",
                LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0021, DATE_TIME_FORMATTER.print(deltaUptoTime)));

        finalizeExtraction(tenant, deltaUptoTime);
    }


    private void extractUpdate(DeltaRecord delta, ExtractFile publicDeltaExtractFile, Map<String, Set<String>> appsPerEdOrg,
            File tenantDirectory, DateTime deltaUptoTime) {
        // Extract public entities separately.
        if (isPublicEntity(delta.getEntity().getType())) {
            if (delta.isSpamDelete()) {
                spamDeletes(delta, publicDeltaExtractFile);
            }

            writeUpdate("public", delta, publicDeltaExtractFile);
        }

        if (delta.isSpamDelete()) {
            spamPrivateDeletes(delta, delta.getBelongsToEdOrgs(), tenantDirectory, deltaUptoTime, appsPerEdOrg);
        }

        for (String edOrg : delta.getBelongsToEdOrgs()) {
            // We have apps for this edOrg.
            if (appsPerEdOrg.containsKey(edOrg)) {
                ExtractFile extractFile = getExtractFile(edOrg, tenantDirectory, deltaUptoTime, appsPerEdOrg.get(edOrg));

                writeUpdate(edOrg, delta, extractFile);
            }
        }
    }

    private void extractDelete(DeltaRecord delta, ExtractFile publicDeltaExtractFile, Map<String, Set<String>> appsPerEdOrg,
            File tenantDirectory, DateTime deltaUptoTime) {
        // Extract public entities separately.
        if (isPublicEntity(delta.getEntity().getType())) {
            spamDeletes(delta, publicDeltaExtractFile);
        }

        spamPrivateDeletes(delta, Collections.<String> emptySet(), tenantDirectory, deltaUptoTime, appsPerEdOrg);
    }

    private void extractPurge(DeltaRecord delta, ExtractFile publicDeltaExtractFile, Map<String, Set<String>> appsPerEdOrg,
            File tenantDirectory, DateTime deltaUptoTime) {
        // Extract public entities separately.
        if (isPublicEntity(delta.getEntity().getType())) {
            logPurge(delta, publicDeltaExtractFile);
        }

        logPrivatePurge(delta, tenantDirectory, deltaUptoTime, appsPerEdOrg);
    }

    private Map<String, Set<String>> appsPerEdOrg() {
        Map<String, Set<String>> edOrgsPerApp = helper.getBulkExtractEdOrgsPerApp();
        Map<String, Set<String>> appsPerEdOrg = reverse(edOrgsPerApp);

        return appsPerEdOrg;
    }

    private void writeUpdate(String edOrg, DeltaRecord delta, ExtractFile extractFile) {
        EntityExtractor.CollectionWrittenRecord record = getCollectionRecord(edOrg, delta.getType());
        entityExtractor.write(delta.getEntity(), extractFile, record, null);
    }

    private void logEntityCounts() {
        for (Map.Entry<String, EntityExtractor.CollectionWrittenRecord> entry : appPerLeaCollectionRecords.entrySet()) {
            EntityExtractor.CollectionWrittenRecord record = entry.getValue();
            LOG.info(String.format("Processed for %s: %s", entry.getKey(), record.toString()));
        }
    }

    private void spamPrivateDeletes(DeltaRecord delta, Set<String> exceptions, File tenantDirectory, DateTime deltaUptoTime,
            Map<String, Set<String>> appsPerEdOrg) {
        for (Map.Entry<String, Set<String>> entry : appsPerEdOrg.entrySet()) {
            String edOrg = entry.getKey();

            if (exceptions.contains(edOrg)) {
                continue;
            }

            ExtractFile extractFile = getExtractFile(edOrg, tenantDirectory, deltaUptoTime, entry.getValue());

            spamDeletes(delta, extractFile);
        }
    }

    private void spamDeletes(DeltaRecord delta, ExtractFile extractFile) {
        // For some entities we have to spam delete the same id in two
        // collections since we cannot reliably retrieve the "type". For example,
        // teacher/staff or edorg/school, if the entity has been deleted, all we know is
        // it a staff or edorg, but it may be stored as teacher or school in vendor db,
        // so we must spam delete the id in both teacher/staff or edorg/school collection.
        Entity entity = delta.getEntity();

        Set<String> types = typeResolver.resolveType(entity.getType());
        for (String type : types) {
            Entity e = new MongoEntity(type, entity.getEntityId(), new HashMap<String, Object>(), null);
            entityWriteManager.writeDeleteFile(e, extractFile);
        }
    }

    private void logPrivatePurge(DeltaRecord delta, File tenantDirectory, DateTime deltaUptoTime,
            Map<String, Set<String>> appsPerEdOrg) {
        for (Map.Entry<String, Set<String>> entry : appsPerEdOrg.entrySet()) {
            String edOrg = entry.getKey();
            ExtractFile extractFile = getExtractFile(edOrg, tenantDirectory, deltaUptoTime, entry.getValue());

            logPurge(delta, extractFile);
        }
    }

    private void logPurge(DeltaRecord delta, ExtractFile extractFile) {
        DateTime date = new DateTime(delta.getEntity().getBody().get(TIME_FIELD));
        Entity purgeEntity = new MongoEntity(DeltaJournal.PURGE, null, new HashMap<String, Object>(), null);
        purgeEntity.getBody().put(DATE_FIELD, DATE_TIME_FORMATTER.print(date));

        entityWriteManager.writeDeleteFile(purgeEntity, extractFile);
    }

    // finalize the extraction, if any error occured, do not wipe the delta
    // collections so we could
    // rerun it if we decided to
    private void finalizeExtraction(String tenant, DateTime startTime) {
        boolean allSuccessful = true;

        for (ExtractFile extractFile : appPerEdOrgExtractFiles.values()) {
            extractFile.closeWriters();
            boolean success = extractFile.finalizeExtraction(startTime);

            if (success) {
                for (Entry<String, File> archiveFile : extractFile.getArchiveFiles().entrySet()) {
                    bulkExtractMongoDA.updateDBRecord(tenant, archiveFile.getValue().getAbsolutePath(),
                            archiveFile.getKey(), startTime.toDate(), true, extractFile.getEdorg(),
                            (extractFile.getEdorg() == null));
                }
            }
            allSuccessful &= success;
        }

        if (allSuccessful) {
            // delta files are generated successfully, we can safely remove
            // those deltas now
            LOG.info("Delta generation succeed.  Clearing delta collections for any entities before: " + startTime);
            deltaEntityIterator.removeAllDeltas(tenant, startTime);
        }

    }

    private CollectionWrittenRecord getCollectionRecord(String lea, String type) {
        String key = lea + "|" + type;

        if (!appPerLeaCollectionRecords.containsKey(key)) {
            EntityExtractor.CollectionWrittenRecord collectionRecord = new EntityExtractor.CollectionWrittenRecord(type);
            appPerLeaCollectionRecords.put(key, collectionRecord);
        }

        return appPerLeaCollectionRecords.get(key);
    }

    private ExtractFile getExtractFile(String edOrg, File tenantDirectory, DateTime deltaUptoTime, Set<String> appsForEdOrg) {
        if (!appPerEdOrgExtractFiles.containsKey(edOrg)) {
            ExtractFile appPerEdOrgExtractFile = getExtractFilePerEdOrg(tenantDirectory, edOrg, deltaUptoTime, appsForEdOrg);
            appPerEdOrgExtractFiles.put(edOrg, appPerEdOrgExtractFile);
        }

        return appPerEdOrgExtractFiles.get(edOrg);
    }

    private Map<String, Set<String>> reverse(Map<String, Set<String>> leasPerApp) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        for (Map.Entry<String, Set<String>> entry : leasPerApp.entrySet()) {
            for (String lea : entry.getValue()) {
                if (!result.containsKey(lea)) {
                    Set<String> apps = new HashSet<String>();
                    apps.add(entry.getKey());
                    result.put(lea, apps);
                }
                result.get(lea).add(entry.getKey());
            }
        }

        return result;
    }

    private ExtractFile getExtractFilePerEdOrg(File tenantDirectory, String edorg, DateTime startTime, Set<String> appsForEdOrg) {
        List<String> edorgList = Arrays.asList(edorg);
        Map<String, PublicKey> appKeyMap = new HashMap<String, PublicKey>();
        for (String appId : appsForEdOrg) {
            appKeyMap.putAll(bulkExtractMongoDA.getClientIdAndPublicKey(appId, edorgList));
        }

        ExtractFile extractFile = new ExtractFile(tenantDirectory,
                getArchiveName(edorg, startTime.toDate()), appKeyMap, securityEventUtil);
        extractFile.setEdorg(edorg);

        return extractFile;
    }

    private String getArchiveName(String edorg, Date startTime) {
        return edorg + "-" + Launcher.getTimeStamp(startTime) + "-delta";
    }

    private boolean isPublicEntity(String entityType) {
        return publicEntityTypes.contains(entityType);
    }

    private ExtractFile createPublicExtractFile(File tenantDirectory, DateTime deltaUptoTime) {
        Map<String, PublicKey> clientKeys = bulkExtractMongoDA.getAppPublicKeys();
        if (clientKeys == null || clientKeys.isEmpty()) {
            audit(securityEventUtil.createSecurityEvent(this.getClass().getName(),
                    "Public delta data extract", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0014));
            LOG.info("No authorized application to extract data.");
        }

        ExtractFile file = new ExtractFile(tenantDirectory, getPublicArchiveName(deltaUptoTime.toDate()), clientKeys, securityEventUtil);
        file.setEdorg(null);

        appPerEdOrgExtractFiles.put("public", file);

        return file;
    }

    private String getPublicArchiveName(Date startTime) {
        return "public-" + Launcher.getTimeStamp(startTime) + "-delta";
    }

    /**
     * Set securityEventUtil.
     *
     * @param securityEventUtil
     *            securityEventUtil
     */
    public void setSecurityEventUtil(SecurityEventUtil securityEventUtil) {
        this.securityEventUtil = securityEventUtil;
    }

}
