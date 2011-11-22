package org.sli.ingestion.routes;

import org.apache.camel.spring.SpringRouteBuilder;
import org.sli.ingestion.processors.EdFiXmlProcessor;
import org.sli.ingestion.processors.PersistenceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

	@Autowired
	EdFiXmlProcessor xmlProcessor;

    @Autowired(required=true)
	PersistenceProcessor persistenceProcessor;

    @Override
	public void configure() throws Exception {

		
		// FIXME parameterize ingestion lz directory
		from("file:/home/ingestion/lz/inbound?move=/home/ingestion/lz/inbound/.done&moveFailed=.error")
				.process(xmlProcessor)
				.to("seda:persist");		
		
		from("seda:persist").process(persistenceProcessor);

	}

}
