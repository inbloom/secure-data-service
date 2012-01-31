package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.perf4j.aop.Profiled;

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
public class ControlFilePreProcessor implements Processor {

    private LandingZone landingZone;

    public ControlFilePreProcessor(LandingZone lz) {
        this.landingZone = lz;
    }

    /**
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    @Profiled(tag = "ControlFilePreProcessor - file {$0.getIn().getHeader(\"CamelFileNameOnly\")} - batch {$0.getExchangeId()}")
    public void process(Exchange exchange) throws Exception {
        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        ControlFile cf = ControlFile.parse(exchange.getIn().getBody(File.class));

        exchange.getIn().setBody(new ControlFileDescriptor(cf, landingZone), ControlFileDescriptor.class);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
    }

}
