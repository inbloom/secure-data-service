package org.slc.sli.ingestion.routes;

import java.io.File;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.BatchJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    @Autowired
    EdFiProcessor xmlProcessor;

    @Autowired
    ControlFileProcessor ctlFileProcessor;

    @Autowired(required = true)
    PersistenceProcessor persistenceProcessor;

    // TODO strangely, the annotation technique works fine when running the 
    // service, but it fails in unit testing.  Disabling for now, in favor of 
    // traditional properties implementation.
    // @Value("#{ingestionProperties.properties['landingzone.inbounddir']}")
    String inboundDir;
    
    @Autowired
    Properties ingestionProperties;
    
	@Override
    public void configure() throws Exception {

		inboundDir = ingestionProperties.getProperty("landingzone.inbounddir");
		
        from("file:"+inboundDir+"?include=^(.*)\\.ctl$&move="+inboundDir+"/.done&moveFailed="+inboundDir+"/.error")
                .process(ctlFileProcessor)
                .to("seda:jobs");
        
        from("seda:jobs")
        		.process(new Processor() {

        			// inline implementation exists solely to convert the input of type BatchJob to 
        			// the first file in that job.
        			
					@Override
					public void process(Exchange exchange) throws Exception {
						BatchJob job = exchange.getIn().getBody(BatchJob.class);
						File file0 = job.getFiles().get(0);
						exchange.getOut().setBody(file0);
					}	
		        	
		        })
		        .process(xmlProcessor)
		        .to("seda:persist");

        from("seda:persist").process(persistenceProcessor);
    }

}
