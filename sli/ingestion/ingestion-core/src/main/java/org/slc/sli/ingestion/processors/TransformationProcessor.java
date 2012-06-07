package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.TransformationFactory;
import org.slc.sli.ingestion.transformation.Transmogrifier;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Camel processor for transformation of data.
 * 
 * @author dduran
 * 
 */
@Component
public class TransformationProcessor implements Processor {
    
    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.TRANSFORMATION_PROCESSOR;
    
    private static final Logger LOG = LoggerFactory.getLogger(TransformationProcessor.class);
    
    @Autowired
    private TransformationFactory transformationFactory;
    
    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;


    /**
     * Camel Exchange process callback method
     * 
     * @param exchange
     */
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) {
        
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        
        if (workNote == null || workNote.getBatchJobId() == null) {
            handleNoBatchJobId(exchange);
        } else {
            processTransformations(workNote, exchange);
        }
    }
    
    private void processTransformations(WorkNote workNote, Exchange exchange) {
        String batchJobId = workNote.getBatchJobId();
        Stage stage = initializeStage(workNote);
        
        Metrics metrics = Metrics.newInstance(workNote.getIngestionStagedEntity().getCollectionNameAsStaged());

        // FIXME: transformation needs to actually count processed records and errors
        
        Criteria limiter = Criteria.where("creationTime").gte(workNote.getRangeMinimum()).lte(workNote.getRangeMaximum());
        Query query = new Query().addCriteria(limiter);
        
        long recordsToProcess = neutralRecordMongoAccess.getRecordRepository().countForJob(
                workNote.getIngestionStagedEntity().getCollectionNameAsStaged(), query, batchJobId);
        
        metrics.setRecordCount(recordsToProcess);
        stage.getMetrics().add(metrics);
        
        NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);
        try {
            performDataTransformations(workNote, newJob);
            
        } catch (Exception e) {
            handleProcessingExceptions(exchange, batchJobId, e);
        } finally {
            BatchJobUtils.stopStageChunkAndAddToJob(stage, newJob);
            batchJobDAO.saveBatchJobStageSeparatelly(batchJobId, stage);
        }
    }
    
    private Stage initializeStage(WorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);
        stage.setProcessingInformation("stagedEntity="
                + workNote.getIngestionStagedEntity().getCollectionNameAsStaged() + ", rangeMin="
                + workNote.getRangeMinimum() + ", rangeMax=" + workNote.getRangeMaximum() + ", batchSize="
                + workNote.getBatchSize());
        return stage;
    }
    
    /**
     * Invokes transformations strategies
     * 
     * @param workNote
     *            TODO
     * @param job
     */
    void performDataTransformations(WorkNote workNote, Job job) {
        LOG.info("performing data transformation BatchJob: {}", job);
        
        Transmogrifier transmogrifier = transformationFactory.createTransmogrifier(workNote, job);
        
        transmogrifier.executeTransformations();
        
    }
    
    private void handleNoBatchJobId(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }
    
    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }
    
    public TransformationFactory getTransformationFactory() {
        return transformationFactory;
    }
    
    public void setTransformationFactory(TransformationFactory transformationFactory) {
        this.transformationFactory = transformationFactory;
    }
    
}
