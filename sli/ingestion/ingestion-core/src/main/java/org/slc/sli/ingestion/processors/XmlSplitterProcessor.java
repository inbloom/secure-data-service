package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.queues.MessageType;

/**
 * Splits an XML file into multiple, smaller interchange files that can be packaged and dispatched to internal landing zones.
 * @author smelody
 *
 */
@Component
public class XmlSplitterProcessor  implements Processor {


    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private void setExchangeHeaders(Exchange exchange, boolean hasErrors) {
        exchange.getIn().setHeader("hasErrors", hasErrors);
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.XML_FILE_SPLIT.name());
    }

    @Override
    public void process(Exchange exchange) throws Exception {


        setExchangeHeaders(exchange, false);


    }
}
