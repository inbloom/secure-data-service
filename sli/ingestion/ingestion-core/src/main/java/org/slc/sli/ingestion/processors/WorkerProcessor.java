package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * First processor in the worker processing route.
 *
 * @author smelody
 *
 */
@Component
public class WorkerProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOG.debug( exchange.toString() );
    }

}
