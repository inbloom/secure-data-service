package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
public class ControlFilePreProcessor implements Processor {

    private LandingZone landingZone;

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    public ControlFilePreProcessor(LandingZone lz) {
        this.landingZone = lz;
    }

    /**
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {

        File controlFile = exchange.getIn().getBody(File.class);
        
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        
        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        try {

            if (batchJobId == null) {
                batchJobId = NewBatchJob.createId(controlFile.getName());
                log.info("Created job [{}]", batchJobId);
            }

            ControlFile cf = ControlFile.parse(controlFile);            
            // TODO JobLogStatus.createBatchJob(file)
            // Batchjob state management should start here so we can store parsing errors of the ctl file in the db.
            // May not need to store this stage ... JobLogStatus.startStage(batchJobId, stageName)

            // set headers for ingestion routing
            exchange.getIn().setBody(new ControlFileDescriptor(cf, landingZone), ControlFileDescriptor.class);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
            exchange.getIn().setHeader("BatchJobId", batchJobId);

            // TODO May not need this ... JobLogStatus.completeStage(batchJobId, stageName)

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:",  exception);
//          TODO  logIngestionError(batchJobId, "CONTROLFILEPREPROCESSOR", "fileId", null, null, "recordIdentifier", timeStr,
//                    "ERROR", "errorType", exception.toString());
        }

    }

}
