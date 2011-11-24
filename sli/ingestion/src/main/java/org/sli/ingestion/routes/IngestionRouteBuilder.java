package org.sli.ingestion.routes;

import java.util.Properties;

import org.apache.camel.spring.SpringRouteBuilder;
import org.sli.ingestion.processors.EdFiProcessor;
import org.sli.ingestion.processors.PersistenceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    @Autowired
    EdFiProcessor edfiProcessor;

    @Autowired(required = true)
    PersistenceProcessor persistenceProcessor;

    @Autowired
    Properties ingestionProperties;
    
    @Override
    public void configure() throws Exception {

        String inboundDir = ingestionProperties.getProperty("landingzone.inbounddir");
        
        from("file:" + inboundDir + "?move=" + inboundDir + "/.done&moveFailed=" + inboundDir + "/.error")
        .process(edfiProcessor)
        .to("seda:persist");        
        
        from("seda:persist").process(persistenceProcessor);

    }

}
