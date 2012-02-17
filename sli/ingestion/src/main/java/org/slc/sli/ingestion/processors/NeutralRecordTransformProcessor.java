package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.milyn.Smooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.util.performance.Profiled;

/**
 * Processor for transforming NeutralRecord to Entity
 *
 * @author dduran
 *
 */
@Component
public class NeutralRecordTransformProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordTransformProcessor.class);

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     */
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) {
        try {
            BatchJob job = exchange.getIn().getBody(BatchJob.class);
            LOG.info("transforming NeutralRecord files to Entity files in BatchJob: {}", job);

            for (IngestionFileEntry fe : job.getFiles()) {

                transformNeutralRecordToEntity(fe);

                job.getFaultsReport().append(fe.getFaultsReport());
            }

            if (job.getErrorReport().hasErrors()) {
                exchange.getIn().setHeader("hasErrors", job.getErrorReport().hasErrors());
            }
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Exception:", exception);
        }

        exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
    }

    private void transformNeutralRecordToEntity(IngestionFileEntry fe) throws IOException, SAXException {

        Smooks smooks = null;
        try {
            File entityOutputFile = createTempFile();

            smooks = new Smooks("smooks_conf/nre/nre.xml");
            smooks.filterSource(new StreamSource(new FileInputStream(fe.getNeutralRecordFile())), new StreamResult(
                    entityOutputFile));

            fe.setEntityFile(entityOutputFile);
        } finally {
            smooks.close();
        }
    }

    private File createTempFile() throws IOException {
        File outputFile = File.createTempFile("entity_", ".tmp");
        outputFile.deleteOnExit();
        return outputFile;
    }

}
