package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZipFileProcessor implements Processor {

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Autowired
    LocalFileSystemLandingZone lz;

    @Override
    public void process(Exchange exchange) throws Exception {

        log.info("Received zip file: " + exchange.getIn());
        File zipFile = exchange.getIn().getBody(File.class);

        // extract the zip file
        File dir = ZipFileUtil.extract(zipFile);
        log.info("Extracted zip file to {}", dir.getAbsolutePath());

        // find manifest (ctl file)
        File ctlFile = ZipFileUtil.findCtlFile(dir);

        // send control file back
        exchange.getOut().setBody(ctlFile);
    }
}
