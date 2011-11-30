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
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
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

    @Autowired
    LocalFileSystemLandingZone lz;
    
	@Override
    public void configure() throws Exception {

		String inboundDir = lz.getDirectory().getPath();
		
        from("file:"+inboundDir+"?include=^(.*)\\.ctl$&move="+inboundDir+"/.done&moveFailed="+inboundDir+"/.error")
                .process(ctlFileProcessor)
                .to("seda:jobs");
        
        from("seda:jobs")
        		.process(new Processor() {

        			// TEMPORARY SOLUTION
        			// inline implementation exists solely to convert the input of type BatchJob to 
        			// type File (the first file in that job).
        			// really we'd like to keep BatchJob as the message content, but it will
        			// require refactoring of all the downstream processors/components.
        			
					@Override
					public void process(Exchange exchange) throws Exception {
						BatchJob job = exchange.getIn().getBody(BatchJob.class);
						File file0 = job.getFiles().get(0);
						exchange.getOut().setBody(file0);

						// we can use message headers to relay any job config params however.
						exchange.getOut().setHeader("jobId", job.getId());
						exchange.getOut().setHeader("jobCreationDate", job.getCreationDate());
						if (job.getProperty("dry-run")!=null) {
							exchange.getOut().setHeader("dry-run", true);
						}
						
					}

		        })
		        .process(xmlProcessor)
		        .to("seda:persist");

        from("seda:persist")
        		.log("persist: jobId: " + header("jobId").toString())
        		.choice()
        			.when(header("dry-run").isEqualTo(true))
        				.log("dry-run specified; data will not be published")
    				.otherwise()
        				.log("publishing data now!")
        				.process(persistenceProcessor);
    }

}
