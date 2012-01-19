package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.landingzone.validation.ZipFileValidator;

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
    private ZipFileValidator validator;

    private MessageSource messageSource;

    @Override
    @Profiled(tag = "ZipFileProcessor - file {$0.getIn().getHeader(\"CamelFileNameOnly\")} - batch {$0.getExchangeId()}")
    public void process(Exchange exchange) throws Exception {

        log.info("Received zip file: " + exchange.getIn());
        File zipFile = exchange.getIn().getBody(File.class);

        BatchJob job = BatchJob.createDefault();

        FaultsReport fr = job.getFaultsReport();

        if (validator.isValid(zipFile, fr)) {

            // extract the zip file
            File dir = ZipFileUtil.extract(zipFile);
            log.info("Extracted zip file to {}", dir.getAbsolutePath());

            try {
                // find manifest (ctl file)
                File ctlFile = ZipFileUtil.findCtlFile(dir);

                // send control file back
                exchange.getIn().setBody(ctlFile, File.class);

                return;
            } catch (IOException ex) {
                fr.error(messageSource.getMessage("SL_ERR_MSG4", new Object[] { zipFile.getName() }, null), this);
            }
        }

        exchange.getIn().setBody(job, BatchJob.class);
        exchange.getIn().setHeader("hasErrors", fr.hasErrors());
    }

    public ZipFileValidator getValidator() {
        return validator;
    }

    public void setValidator(ZipFileValidator validator) {
        this.validator = validator;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
