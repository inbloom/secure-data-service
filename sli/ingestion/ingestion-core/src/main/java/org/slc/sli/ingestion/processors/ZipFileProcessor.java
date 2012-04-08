package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.util.performance.Profiled;

/**
 * Zip file handler.
 *
 * @author okrook
 *
 */
@Component
public class ZipFileProcessor implements Processor, MessageSourceAware {

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Autowired
    private ZipFileHandler handler;

    private MessageSource messageSource;

    @Override
    @Profiled
    public void process(Exchange exchange) throws Exception {

        try {
            log.info("Received zip file: " + exchange.getIn());

            File zipFile = exchange.getIn().getBody(File.class);

            BatchJob job = BatchJob.createDefault(zipFile.getName());
            // TODO JobLogStatus.createBatchJob(file)
            // Need to refactor to allow us to log errors below into the db

            ErrorReport errorReport = job.getFaultsReport();

            File ctlFile = handler.handle(zipFile, errorReport);

            if (errorReport.hasErrors()) {
                exchange.getIn().setBody(job, BatchJob.class);
                exchange.getIn().setHeader("hasErrors", errorReport.hasErrors());
                exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
            } else if (ctlFile != null) {
                exchange.getIn().setBody(ctlFile, File.class);
            }
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:", exception);
        }
    }

    public ZipFileHandler getHandler() {
        return handler;
    }

    public void setHandler(ZipFileHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
