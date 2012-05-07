package org.slc.sli.ingestion.processors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.routes.IngestionRouteBuilder;
import org.slc.sli.ingestion.routes.TenantRouteBuilder;
import org.slc.sli.ingestion.tenant.TenantPopulator;
import org.slc.sli.ingestion.tenant.TenantRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor for tenant on boarding requests
 * 
 * @author jtully
 *
 */
@Component
public class TenantProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(TenantProcessor.class);
    
    @Autowired
    private CamelContext camelContext;
    
    @Autowired
    private TenantPopulator tenantPopulator;
    
    @Autowired
    private ZipFileProcessor zipFileProcessor;

    @Autowired
    private ControlFilePreProcessor controlFilePreProcessor;
    
    @Autowired
    private IngestionRouteBuilder ingestionRouteBuilder;
    
    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            //get the tenant record from the JSON message body
            String tenantJson = exchange.getIn().getBody(String.class);
            TenantRecord tenant = TenantRecord.parse(tenantJson);
            
            //add the tenantRecord to the tenant collection
            tenantPopulator.addTenant(tenant, true);
            
            addTenantRoutes(tenant);
            
        } catch (Exception e) {
            exchange.getIn().setHeader("ErrorMessage", "Failed to add tenant.");
            LOG.error("Exception encountered adding tenant", e);
        }
    }
    
    /**
     * Dynamically add the route to the ingestion CamelContext for
     * the given TenantRecord
     * 
     * @throws Exception - thrown if a route cannot be added.
     * 
     */
    private void addTenantRoutes(TenantRecord tenant) throws Exception {
        TenantRouteBuilder tenantRouteBuilder = new TenantRouteBuilder(tenant, 
                ingestionRouteBuilder.getWorkItemQueueUri(),
                zipFileProcessor, controlFilePreProcessor);
        camelContext.addRoutes(tenantRouteBuilder);
    }
}
