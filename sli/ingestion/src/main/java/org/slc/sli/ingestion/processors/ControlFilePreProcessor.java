package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.util.IngestionTimer;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
public class ControlFilePreProcessor implements Processor {

    private LandingZone landingZone;

    private Logger log = LoggerFactory.getLogger(ControlFileProcessor.class); 
    
    public ControlFilePreProcessor(LandingZone lz) {
        this.landingZone = lz;
    }

    /**
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        
        IngestionTimer.startTimer();
        
        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        ControlFile cf = ControlFile.parse(exchange.getIn().getBody(File.class));

        log.info("JMT control file preprocessor setting body");
        
        exchange.getIn().setBody(new ControlFileDescriptor(cf, landingZone), ControlFileDescriptor.class);
        
        
    }

}
