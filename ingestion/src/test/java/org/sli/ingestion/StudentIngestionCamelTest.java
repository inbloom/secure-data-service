package org.sli.ingestion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slc.sli.repository.StudentRepository;
import org.sli.ingestion.routes.IngestionRouteBuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;


public class StudentIngestionCamelTest extends StudentIngestionTest {

	@Autowired
	private StudentRepository studentRepository;

	
	@Test
	public void testDirectRoute() throws IOException, SAXException, Exception {

		studentRepository.deleteAll();
		
		int numberOfStudents = 2;
		
		// Set up input file
		String xmlRecords = this.createStudentInterchangeXml(numberOfStudents);
		File xmlRecordsFile = this.createTestFile(xmlRecords);		
		
		// Create camel context w/ routes
		CamelContext context = new DefaultCamelContext();
		context.addRoutes( getRouteBuilder() );
		
		// Get camel producer template to send input message
		ProducerTemplate template = context.createProducerTemplate();
		
		// Start camel context
		context.start();

		// Send message
		template.sendBody("direct:start", xmlRecordsFile);
		
		Thread.sleep(1000);
		
		// Stop camel context
		context.stop();
		
		this.verifyStudents(studentRepository, numberOfStudents);
		
	}
	
	/*
	@Test
	public void testFromFile() throws IOException, SAXException, Exception {

		studentRepository.deleteAll();
		
		int numberOfStudents = 2;
		
		// Set up input file
		String xmlRecords = this.createStudentInterchangeXml(numberOfStudents);
		File xmlRecordsFile = this.createTestFile( xmlRecords, "/home/ingestion/lz/inbound/test.xml");		
		    		
		// Create camel context w/ routes
	    // TODO: use IngestionRouteBuilder - some config problem causes an error setting up the routes
		CamelContext context = new DefaultCamelContext();		
		//context.addRoutes( new IngestionRouteBuilder() );
		context.addRoutes( getRouteBuilder2() );
		
		// Start camel context
		context.start();		
		
		Thread.sleep(1000);
		
		// Stop camel context
		context.stop();
		
		this.verifyStudents(studentRepository, numberOfStudents);
		
	}
	*/
	
	/*
	 * This route mimics the one in IngestionRouteBuilder, but starts with direct input
	 * instead of reading from the file system
	 */
	protected RouteBuilder getRouteBuilder() {
		
		return new RouteBuilder() {
			
			public void configure() {
				
                from("direct:start").process(getEdFiXmlProcessor()).to("seda:persist");
                
                from("seda:persist").process(getPersistenceProcessor()); 
			}
		};
	}

	/*
	 * This route is the same as the one in IngestionRouteBuilder.
	 * TODO: figure out why IngestionRouteBuilder causes problems running this test
	 */
	protected RouteBuilder getRouteBuilder2() {
		
		return new RouteBuilder() {
			
			public void configure() {
				
                from("file:/home/ingestion/lz/inbound?move=/home/ingestion/lz/inbound/.done&moveFailed=.error").process(getEdFiXmlProcessor()).to("seda:persist");
                
                from("seda:persist").process(getPersistenceProcessor()); 
			}
		};
	}
	
	private File createTestFile(String xmlRecords, String filePath) throws IOException {
		
	    File xmlRecordsFile = new File(filePath);
        BufferedOutputStream outputStream = null;
    
        try {
		    outputStream = new BufferedOutputStream( new FileOutputStream(xmlRecordsFile) );
	        outputStream.write(xmlRecords.getBytes());		    
        }
        finally {
	        outputStream.close();
        }
        return xmlRecordsFile;
	}
	
}
