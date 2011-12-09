package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZipFileProcessor implements Processor {

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        long startTime = System.currentTimeMillis();

        log.info("Received zip file: " + exchange.getIn());
        File zipFile = exchange.getIn().getBody(File.class);

        // extract the zip file
        File dir = ZipFileUtil.extract(zipFile);
        log.info("Extracted zip file to {}", dir.getAbsolutePath());

        // find manifest (ctl file)
        File ctlFile = ZipFileUtil.findCtlFile(dir);

        // instantiate landing zone for unzipped files
        LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
        lz.setDirectory(dir);

        // instantiate job assembler
        BatchJobAssembler jobAssembler = new BatchJobAssembler();
        jobAssembler.setLandingZone(lz);

        // parse control file
        ControlFile controlFile = ControlFile.parse(ctlFile);
        log.info("Parsed control file: " + ctlFile.getName());

        // generate a BatchJob from the control file
        log.info("Assembling job: " + controlFile.getClass());
        BatchJob job = jobAssembler.assembleJob(controlFile);

        long endTime = System.currentTimeMillis();
        log.info("Assembled batch job [{}] in {} ms", job.getId(), endTime
                - startTime);

        // send control file back
        exchange.getOut().setBody(job);
    }
}
