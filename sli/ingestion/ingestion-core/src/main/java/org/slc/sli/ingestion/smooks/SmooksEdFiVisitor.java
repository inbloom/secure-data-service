package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.annotation.StreamResultWriter;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.NeutralRecordUtils;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor that writes a neutral record or reports errors encountered.
 * 
 * @author dduran
 * 
 */
@StreamResultWriter
public final class SmooksEdFiVisitor implements SAXElementVisitor {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(SmooksEdFiVisitor.class);
    
    /** Constant to write a log message every N records. */
    private static final int FLUSH_QUEUE_THRESHOLD = 10000;
    
    private ResourceWriter<NeutralRecord> nrMongoStagingWriter;
    
    private final String beanId;
    private final String batchJobId;
    private final ErrorReport errorReport;
    private final IngestionFileEntry fe;
    
    private Map<String, Integer> occurences;
    private Map<String, List<NeutralRecord>> queuedWrites;
    private int recordsPerisisted;
    
    /**
     * Get records persisted to data store. If there are still queued writes waiting, flush the
     * queue by writing to data store before returning final count.
     * 
     * @return Final number of records persisted to data store.
     */
    public int getRecordsPerisisted() {
        boolean stillPendingWrites = false;
        for (Map.Entry<String, List<NeutralRecord>> entry : queuedWrites.entrySet()) {
            if (entry.getValue().size() > 0) {
                stillPendingWrites = true;
                break;
            }
        }
        if (stillPendingWrites) {
            writeAndClearQueuedNeutralRecords();
        }
        return recordsPerisisted;
    }
    
    private SmooksEdFiVisitor(String beanId, String batchJobId, ErrorReport errorReport, IngestionFileEntry fe) {
        this.beanId = beanId;
        this.batchJobId = batchJobId;
        this.errorReport = errorReport;
        this.fe = fe;
        this.occurences = new HashMap<String, Integer>();
        this.recordsPerisisted = 0;
        this.queuedWrites = new HashMap<String, List<NeutralRecord>>();
    }
    
    public static SmooksEdFiVisitor createInstance(String beanId, String batchJobId, ErrorReport errorReport,
            IngestionFileEntry fe) {
        return new SmooksEdFiVisitor(beanId, batchJobId, errorReport, fe);
    }
    
    @Override
    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws IOException {
        
        Throwable terminationError = executionContext.getTerminationError();
        if (terminationError == null) {            
            NeutralRecord neutralRecord = getProcessedNeutralRecord(executionContext);
            queueNeutralRecordForWriting(neutralRecord);
            
            if (recordsPerisisted % FLUSH_QUEUE_THRESHOLD == 0) {
                writeAndClearQueuedNeutralRecords();
            }
        } else {
            
            // Indicate Smooks Validation Failure
            LOG.error("Error: Smooks validation failure at element " + element.getName().toString());
            
            if (errorReport != null) {
                errorReport.error(terminationError.getMessage(), SmooksEdFiVisitor.class);
            }
        }
    }
    
    /**
     * Adds the Neutral Record to the queue of Neutral Records waiting to be written.
     * 
     * @param record
     *            Neutral Record to be written to data store.
     */
    private void queueNeutralRecordForWriting(NeutralRecord record) {
        if (!queuedWrites.containsKey(record.getRecordType())) {
            queuedWrites.put(record.getRecordType(), new ArrayList<NeutralRecord>());
        }
        queuedWrites.get(record.getRecordType()).add(record);
        this.recordsPerisisted++;
    }
    
    /**
     * Write all neutral records currently contained in the queue, and clear the queue.
     */
    private void writeAndClearQueuedNeutralRecords() {
        if (recordsPerisisted != 0) {
            for (Map.Entry<String, List<NeutralRecord>> entry : queuedWrites.entrySet()) {
                nrMongoStagingWriter.insertResources(entry.getValue(), entry.getKey(), batchJobId);
                LOG.info("Persisted {} records of type {} ", entry.getValue().size(), entry.getKey());
                queuedWrites.get(entry.getKey()).clear();
            }
        }
    }
    
    private NeutralRecord getProcessedNeutralRecord(ExecutionContext executionContext) {
        NeutralRecord neutralRecord = (NeutralRecord) executionContext.getBeanContext().getBean(beanId);
        neutralRecord.setBatchJobId(batchJobId);
        neutralRecord.setSourceFile(fe == null ? "" : fe.getFileName());
        
        if (this.occurences.containsKey(neutralRecord.getRecordType())) {
            int temp = this.occurences.get(neutralRecord.getRecordType()).intValue() + 1;
            this.occurences.put(neutralRecord.getRecordType(), temp);
            neutralRecord.setLocationInSourceFile(temp);
        } else {
            this.occurences.put(neutralRecord.getRecordType(), 0);
            neutralRecord.setLocationInSourceFile(0);
        }
        
        // scrub empty strings in NeutralRecord (this is needed for the current way we parse CSV
        // files)
        neutralRecord.setAttributes(NeutralRecordUtils.scrubEmptyStrings(neutralRecord.getAttributes()));
        if (String.class.isInstance(neutralRecord.getLocalId())) {
            neutralRecord.setLocalId(((String) neutralRecord.getLocalId()).trim());
        }
        
        return neutralRecord;
    }
    
    public void setNrMongoStagingWriter(ResourceWriter<NeutralRecord> nrMongoStagingWriter) {
        this.nrMongoStagingWriter = nrMongoStagingWriter;
    }
    
    /* we are not using the below visitor hooks */
    
    @Override
    public void visitBefore(SAXElement element, ExecutionContext executionContext) {
        // nothing
    }
    
    @Override
    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) {
        // nothing
    }
    
    @Override
    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) {
        // nothing
        
    }
}
