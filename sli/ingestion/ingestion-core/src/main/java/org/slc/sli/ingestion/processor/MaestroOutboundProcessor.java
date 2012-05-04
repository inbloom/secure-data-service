package org.slc.sli.ingestion.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Creates music sheets of work items that can be distributed to pit nodes.
 *
 * @author smelody
 *
 */
@Component
public class MaestroOutboundProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO Auto-generated method stub

    }

}
