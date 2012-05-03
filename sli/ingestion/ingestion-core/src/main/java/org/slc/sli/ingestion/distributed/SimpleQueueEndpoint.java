package org.slc.sli.ingestion.distributed;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.service.QueueService;

/**
 * Camel endpoint for fetching available work items from our simple queue.
 * @author smelody
 *
 */
@Component
public class SimpleQueueEndpoint extends DefaultEndpoint implements Endpoint {

    @Autowired
    private QueueService queueService;

    @Override
    public boolean isSingleton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Producer createProducer() throws Exception {

        return new SimpleQueueProducer( this );
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
