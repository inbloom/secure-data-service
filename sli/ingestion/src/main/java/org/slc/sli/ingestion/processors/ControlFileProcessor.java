package org.slc.sli.ingestion.processors;


import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControlFileProcessor implements Processor {

    @Autowired
    LandingZone lz;	
	
	@Override
	public void process(Exchange exchange) throws Exception {

		// TODO handle invalid control file (user error)
		// TODO handle IOException or other system error
		ControlFile controlFile = ControlFile.parse(exchange.getIn().getBody(File.class));
		
		// generate a BatchJob from the ControlFile
		BatchJob job = new BatchJobAssembler(lz).assembleJob(controlFile);

		// TODO set properties on the exchange based on job properties
		// TODO set faults on the exchange if the control file sucked (?)
		
		// set the exchange outbound message to the value of the job
		exchange.getOut().setBody(job);
		
	}
	
}
