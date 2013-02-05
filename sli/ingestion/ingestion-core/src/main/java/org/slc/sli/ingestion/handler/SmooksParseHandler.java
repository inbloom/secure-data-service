/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ElementSource;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.smooks.SliDeltaManager;
import org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.NeutralRecordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.stereotype.Component;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.mongodb.MongoException;

/**
 * 
 * @author slee
 *
 */
@Component
public class SmooksParseHandler extends AbstractIngestionHandler<IngestionFileEntry, FileProcessStatus>
    implements SAXVisitBefore, SAXVisitAfter, SliDocumentLocatorHandler, ElementSource {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksParseHandler.class);


    private Smooks smooks;
    private int group;
    private String targetSelector;
    private List<String> targetSelectors;
    private ProducerTemplate producer;
    private ThreadLocal<Map<String, Integer>> occurences = new ThreadLocal<Map<String, Integer>>();
    private ThreadLocal<Map<String, List<NeutralRecord>>> queuedWrites = new ThreadLocal<Map<String, List<NeutralRecord>>>();
    private ThreadLocal<IngestionFileEntry> fileEntry = new ThreadLocal<IngestionFileEntry>();
    private ThreadLocal<Integer> recordsPerisisted = new ThreadLocal<Integer>();
    private ThreadLocal<Map<String, Long>> duplicateCounts = new ThreadLocal<Map<String, Long>>();
    private ThreadLocal<FileEntryWorkNote> feWorkNote = new ThreadLocal<FileEntryWorkNote>();
    private ThreadLocal<Map<String, Object>> headers = new ThreadLocal<Map<String, Object>>();

    /** Constant to write a log message every N records. */
    private static final int FLUSH_QUEUE_THRESHOLD = 10000;
    private static final int FIRST_INSTANCE = 1;

    private String feFileName;

    private int visitBeforeLineNumber;
    private int visitBeforeColumnNumber;
    private int visitAfterLineNumber;
    private int visitAfterColumnNumber;
    private Locator locator;

    private String beanId;
    private NeutralRecordMongoAccess nrMongoStagingWriter;
    private DeterministicUUIDGeneratorStrategy dIdStrategy;
    private DeterministicIdResolver dIdResolver;
    private Set<String> recordLevelDeltaEnabledEntities;

    @Autowired
    private BatchJobDAO batchJobDAO;

    private AbstractMessageReport errorReport;
    private ReportStats reportStats;
    private Source source;

    @Override
    protected FileProcessStatus doHandling(IngestionFileEntry item, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        this.errorReport = report;
        this.reportStats = reportStats;
        this.source = new FileSource(item.getResourceId());

        try {
            generateNeutralRecord(item, fileProcessStatus);
        } catch (IOException e) {
            errorReport.error(reportStats, source, CoreMessageCode.CORE_0016);
        } catch (SAXException e) {
            errorReport.error(reportStats, source, CoreMessageCode.CORE_0017);
        }

        return fileProcessStatus;
    }

    @SuppressWarnings("unchecked")
    void generateNeutralRecord(IngestionFileEntry fe, FileProcessStatus fileProcessStatus) throws IOException, SAXException {

        InputStream inputStream = null;

        try {
            LOG.info("SmooksParseHandler: handling {} {}", targetSelector, fe.getFileName());
            fileEntry.set(fe);
            this.feFileName = fileEntry.get().getFileName();

            // extract FileEntryWorkNote from the envelope
            String owner = fe.getFileType().getName();
            Map<String, Object> envelope = fe.getEnvelopeByOwner(owner);
            
            if (envelope.containsKey("headers")) {
                headers.set( (Map<String, Object>) envelope.get("headers") );
            }
            if (envelope.containsKey("feWorkNote")) {
                feWorkNote.set( (FileEntryWorkNote) envelope.get("feWorkNote") );
            }

            // Convert XML file to neutral records, and stage.
            occurences.set(new HashMap<String, Integer>());
            queuedWrites.set(new HashMap<String, List<NeutralRecord>>());
            duplicateCounts.set(new HashMap<String, Long>());
            recordsPerisisted.set(0);

            inputStream = fe.getFileStream();
            smooks.filterSource(new StreamSource(inputStream));

            writeAndClearQueuedNeutralRecords();

            fileProcessStatus.setTotalRecordCount(recordsPerisisted.get());
            fileProcessStatus.setDuplicateCounts(duplicateCounts.get());
            LOG.info("Parsed and persisted {} records to staging db from file: {}.", recordsPerisisted.get(), fe.getFileName());
        } catch (SmooksException se) {
            errorReport.error(reportStats, source, CoreMessageCode.CORE_0020, fe.getFileName());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        visitAfterLineNumber = (locator == null ? -1 : locator.getLineNumber());
        visitAfterColumnNumber = (locator == null ? -1 : locator.getColumnNumber());

        Throwable terminationError = executionContext.getTerminationError();
        if (terminationError == null) {            
            NeutralRecord neutralRecord = getProcessedNeutralRecord(executionContext);

            if (neutralRecord == null) {
                Object[] obj = {element.getName().getLocalPart(), fileEntry.get().getFileName(), visitBeforeLineNumber, visitBeforeColumnNumber};
                LOG.info("Processing of {} in {} ({}:{}) is skipped", obj);
            } else if (!recordLevelDeltaEnabledEntities.contains(neutralRecord.getRecordType())) {
                queueNeutralRecordForWriting(neutralRecord);
            } else {

                // Handle record hash checking according to various modes.
                String rhMode = TenantContext.getBatchProperty(AttributeType.DUPLICATE_DETECTION.getName());
                boolean modeDisable = (null != rhMode) && rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE);
                boolean modeDebugDrop = (null != rhMode)
                        && rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DEBUG_DROP);

                if (modeDisable
                        || (!modeDebugDrop && !SliDeltaManager.isPreviouslyIngested(neutralRecord, batchJobDAO,
                                dIdStrategy, dIdResolver, errorReport, reportStats))) {
                    queueNeutralRecordForWriting(neutralRecord);
                } else {
                    String type = neutralRecord.getRecordType();
                    Long count = duplicateCounts.get().containsKey(type) ? duplicateCounts.get().get(type) : Long.valueOf(0);
                    duplicateCounts.get().put(type, Long.valueOf(count.longValue() + 1));
                }
            }

            if (recordsPerisisted.get() % FLUSH_QUEUE_THRESHOLD == 0) {
                writeAndClearQueuedNeutralRecords();
            }

        } else {
            // Indicate Smooks Validation Failure
            if (errorReport != null) {
                errorReport.error(reportStats, new ElementSourceImpl(this), CoreMessageCode.CORE_0019, element.getName().toString());
            }
        }
    }

    @Override
    public void visitBefore(SAXElement element,
            ExecutionContext executionContext) throws SmooksException, IOException {
        visitBeforeLineNumber = (locator == null ? -1 : locator.getLineNumber());
        visitBeforeColumnNumber = (locator == null ? -1 : locator.getColumnNumber());
        currentElement = element;
    }

    public Smooks getSmooks() {
        return smooks;
    }

    public void setSmooks(Smooks smooks) {
        this.smooks = smooks;
        for (String s : targetSelectors) {
            smooks.addVisitor(this, s);
        }
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getTargetSelector() {
        return targetSelector;
    }

    public void setTargetSelector(String targetSelector) {
        this.targetSelector = targetSelector;
    }

    public List<String> getTargetSelectors() {
        return targetSelectors;
    }

    public void setTargetSelectors(List<String> targetSelectors) {
        this.targetSelectors = targetSelectors;
    }

    public ProducerTemplate getProducer() {
        return producer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    /**
     * Adds the Neutral Record to the queue of Neutral Records waiting to be written.
     *
     * @param record
     *            Neutral Record to be written to data store.
     */
    private void queueNeutralRecordForWriting(NeutralRecord record) {
        if (!queuedWrites.get().containsKey(record.getRecordType())) {
            queuedWrites.get().put(record.getRecordType(), new ArrayList<NeutralRecord>());
        }
        queuedWrites.get().get(record.getRecordType()).add(record);
        recordsPerisisted.set(recordsPerisisted.get()+1);
    }

    /**
     * Write all neutral records currently contained in the queue, and clear the queue.
     */
    private void sendQueuedNeutralRecords() {

        Map<String, List<NeutralRecord>> map = new HashMap<String, List<NeutralRecord>>();
        for (Map.Entry<String, List<NeutralRecord>> entry : queuedWrites.get().entrySet()) {
            if (entry.getValue().size() > 0) {
                String recordType = entry.getKey();
                map.put(recordType, new ArrayList<NeutralRecord>());
                map.get(recordType).addAll(entry.getValue());
                LOG.info("Sending {} records of type {} ", entry.getValue().size(), entry.getKey());
                queuedWrites.get().get(entry.getKey()).clear();
            }            
        }
        
        if (map.size() > 0 ) {
            FileEntryWorkNote fn = feWorkNote.get();
            fn.setQueuedRecords(map);
            producer.sendBodyAndHeaders("direct:stagingProcessor", fn, headers.get());
        }
    }
    private void writeAndClearQueuedNeutralRecords() {
        if (recordsPerisisted.get() != 0) {
            for (Map.Entry<String, List<NeutralRecord>> entry : queuedWrites.get().entrySet()) {
                if (entry.getValue().size() > 0) {
                    try {
                        nrMongoStagingWriter.insertResources(entry.getValue(), entry.getKey());
                        LOG.info("Persisted {} records of type {} ", entry.getValue().size(), entry.getKey());
                        queuedWrites.get().get(entry.getKey()).clear();
                    } catch (DataAccessResourceFailureException darfe) {
                        LOG.error("Exception processing record with entityPersistentHandler", darfe);
                    } catch (InvalidDataAccessApiUsageException ex) {
                        LOG.error("Exception processing record with entityPersistentHandler", ex);
                    } catch (InvalidDataAccessResourceUsageException ex) {
                        LOG.error("Exception processing record with entityPersistentHandler", ex);
                    } catch (MongoException me) {
                        LOG.error("Exception processing record with entityPersistentHandler", me);
                    } catch (UncategorizedMongoDbException ex) {
                        LOG.error("Exception processing record with entityPersistentHandler", ex);
                    }
                }
            }
        }
    }

    private NeutralRecord getProcessedNeutralRecord(ExecutionContext executionContext) {
        NeutralRecord neutralRecord = (NeutralRecord) executionContext.getBeanContext().getBean(beanId);
        if (neutralRecord == null) {
            return null;
        }
        neutralRecord.setBatchJobId(fileEntry.get().getBatchJobId());
        neutralRecord.setSourceFile(fileEntry.get().getFileName());

        if (this.occurences.get().containsKey(neutralRecord.getRecordType())) {
            int temp = this.occurences.get().get(neutralRecord.getRecordType()).intValue() + 1;
            this.occurences.get().put(neutralRecord.getRecordType(), temp);
            neutralRecord.setLocationInSourceFile(temp);
        } else {
            this.occurences.get().put(neutralRecord.getRecordType(), FIRST_INSTANCE);
            neutralRecord.setLocationInSourceFile(FIRST_INSTANCE);
        }

        neutralRecord.setVisitBeforeLineNumber(visitBeforeLineNumber);
        neutralRecord.setVisitBeforeColumnNumber(visitBeforeColumnNumber);
        neutralRecord.setVisitAfterLineNumber(visitAfterLineNumber);
        neutralRecord.setVisitAfterColumnNumber(visitAfterColumnNumber);

        // scrub empty strings in NeutralRecord (this is needed for the current way we parse CSV
        // files)
        neutralRecord.setAttributes(NeutralRecordUtils.decodeAndTrimXmlStrings(neutralRecord.getAttributes()));
        if (String.class.isInstance(neutralRecord.getLocalId())) {
            neutralRecord.setLocalId(((String) neutralRecord.getLocalId()).trim());
        }

        return neutralRecord;
    }

    public void setRecordLevelDeltaEnabledEntities(Set<String> recordLevelDeltaEnabledEntities) {
        this.recordLevelDeltaEnabledEntities = recordLevelDeltaEnabledEntities;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public void setNrMongoStagingWriter(ResourceWriter<NeutralRecord> nrMongoStagingWriter) {
        this.nrMongoStagingWriter = (NeutralRecordMongoAccess) nrMongoStagingWriter;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

    public void setdIdStrategy(DeterministicUUIDGeneratorStrategy dIdStrategy) {
        this.dIdStrategy = dIdStrategy;
    }

    public void setdIdResolver(DeterministicIdResolver dIdResolver) {
        this.dIdResolver = dIdResolver;
    }

    @Override
    public String getResourceId() {
        return feFileName;
    }

    @Override
    public int getVisitBeforeLineNumber() {
        return visitBeforeLineNumber;
    }

    @Override
    public int getVisitBeforeColumnNumber() {
        return visitBeforeColumnNumber;
    }

    @Override
    public String getElementType() {
        return currentElement.getName().getLocalPart();
    }

    private SAXElement currentElement;

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    protected List<FileProcessStatus> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // Blank instantiation of this (never-called) method.
        return null;
    }

    @Override
    public String getStageName() {
        return BatchJobStageType.EDFI_PROCESSOR.getName();
    }
}
