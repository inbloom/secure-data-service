package org.slc.sli.ingestion.distributed;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultAsyncProducer;

public class SimpleQueueProducer extends DefaultAsyncProducer implements Producer {

    public SimpleQueueProducer(Endpoint endpoint) {
        super(endpoint);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        // TODO Auto-generated method stub
        return false;
    }



}
