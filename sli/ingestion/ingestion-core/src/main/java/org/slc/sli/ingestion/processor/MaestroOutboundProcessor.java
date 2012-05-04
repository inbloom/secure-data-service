package org.slc.sli.ingestion.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates music sheets of work items that can be distributed to pit nodes.
 *
 * @author smelody
 * @author shalka
 */
@Component
public class MaestroOutboundProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.MAESTRO_MUSIC_SHEET_CREATION;
    private static final Logger LOG = LoggerFactory.getLogger(MaestroOutboundProcessor.class);

    private static final String ATTENDANCE_COLLECTION = "attendance";
    private static final String ASSESSMENT_COLLECTION = "studentAssessmentAssociation";
    private static final String STUDENT_SECTION_ASSOCIATION = "studentSectionAssociation";
    private static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";
    
    private static final int ATTENDANCE_RECORD_CUTOFF = 30000;
    private static final int ENROLLMENT_RECORD_CUTOFF = 15000;
    private static final int ASSESSMENT_RECORD_CUTOFF = 20000;
    
    private static final int ATTENDANCE_CHUNK = 1000;
    private static final int ASSESSMENT_CHUNK = 1000;
    private static final int STUDENT_SCHOOL_CHUNK = 1000;
    private static final int STUDENT_SECTION_CHUNK = 1000;
        
    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private BatchJobDAO batchJobDAO;
        
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) throws Exception {
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            handleMusicSheetCreation(exchange, batchJobId);
        }
    }

    private void handleMusicSheetCreation(Exchange exchange, String batchJobId) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);
            boolean hasErrors = false;
            
            List<WorkNote> maestroMusicSheet = new ArrayList<WorkNote>();
            
            long attendanceCount = neutralRecordMongoAccess.getRecordRepository().getCollection("attendance").count();
            LOG.info("Found attendances: {}", attendanceCount);
            if (attendanceCount > ATTENDANCE_RECORD_CUTOFF) {
                LOG.info("Splitting student attendance interchange.");
                for (long i = 0; i < attendanceCount; i += ATTENDANCE_CHUNK) {
                    long chunk = ((i + ATTENDANCE_CHUNK) > attendanceCount) ? (attendanceCount) : (i + ATTENDANCE_CHUNK);
                    maestroMusicSheet.add(new WorkNoteImpl(batchJobId, ATTENDANCE_COLLECTION, i, chunk - 1));
                }                
            }
            
            long assessmentCount = neutralRecordMongoAccess.getRecordRepository().getCollection("studentAssessmentAssociation").count();
            LOG.info("Found assessments: {}", assessmentCount);
            if (assessmentCount > ASSESSMENT_RECORD_CUTOFF) {
                LOG.info("Splitting student assessment interchange.");
                for (long i = 0; i < assessmentCount; i += ASSESSMENT_CHUNK) {
                    long chunk = ((i + ASSESSMENT_CHUNK) > assessmentCount) ? (assessmentCount) : (i + ASSESSMENT_CHUNK);
                    maestroMusicSheet.add(new WorkNoteImpl(batchJobId, ASSESSMENT_COLLECTION, i, chunk - 1));
                }
            }
            
            long studentSchoolCount = neutralRecordMongoAccess.getRecordRepository().getCollection("studentSchoolAssociation").count();
            long studentSectionCount = neutralRecordMongoAccess.getRecordRepository().getCollection("studentSectionAssociation").count();
            LOG.info("Found student school associations: {}", studentSchoolCount);
            LOG.info("Found student section associations: {}", studentSectionCount);
            if (studentSchoolCount + studentSectionCount > ENROLLMENT_RECORD_CUTOFF) {
                LOG.info("Splitting student enrollment interchange.");
                for (long i = 0; i < studentSchoolCount; i++) {
                    long chunk = ((i + STUDENT_SCHOOL_CHUNK) > studentSchoolCount) ? (studentSchoolCount) : (i + STUDENT_SCHOOL_CHUNK);
                    maestroMusicSheet.add(new WorkNoteImpl(batchJobId, STUDENT_SCHOOL_ASSOCIATION, i, chunk - 1));
                }
                
                for (long i = 0; i < studentSectionCount; i++) {
                    long chunk = ((i + STUDENT_SECTION_CHUNK) > studentSectionCount) ? (studentSectionCount) : (i + STUDENT_SECTION_CHUNK);
                    maestroMusicSheet.add(new WorkNoteImpl(batchJobId, STUDENT_SECTION_ASSOCIATION, i, chunk - 1));
                }
            }
            
            // long studentDisciplineCount = neutralRecordMongoAccess.getRecordRepository().getCollection("studentDisciplineIncidentAssociation").count();
            // LOG.warn("iii - Found student discipline associations: {}", studentDisciplineCount);

            exchange.getOut().setBody(maestroMusicSheet);
            setExchangeHeaders(exchange, hasErrors);
        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }
    
    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Exception:", exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasError) {
        if (hasError) {
            exchange.getIn().setHeader("hasErrors", hasError);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        } else {
            //Keep the current processing for now
            exchange.getIn().setHeader("IngestionMessageType", MessageType.DATA_TRANSFORMATION.name());
        }
    }
}
