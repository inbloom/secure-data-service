package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splits an XML file into multiple, smaller interchange files that can be packaged and dispatched to internal landing zones.
 * @author smelody
 *
 */
@Component
public class XmlSplitterProcessor  implements Processor {


    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";
    
    private static final Logger LOG = LoggerFactory.getLogger(NoExtractProcessor.class);

    private void setExchangeHeaders(Exchange exchange, boolean hasErrors) {
        exchange.getIn().setHeader("hasErrors", hasErrors);
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.XML_FILE_SPLIT.name());
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        //We need to extract the TenantID for each thread, so the DAL has access to it.
        try {
            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);
            ControlFile cf = cfd.getFileItem();
            String tenantId = cf.getConfigProperties().getProperty("tenantId");
            TenantContext.setTenantId(tenantId);
        } catch (NullPointerException ex) {
            LOG.error("Could Not find Tenant ID.");
            TenantContext.setTenantId(null);
        }

        setExchangeHeaders(exchange, false);


    }
}
