package org.slc.sli.ingestion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.util.MD5;
import org.slc.sli.repository.StudentRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class StudentIngestionCamelTest {
    
    @Autowired
    private EdFiProcessor edFiProcessor;
    
    @Autowired
    private PersistenceProcessor persistenceProcessor;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Test
    public void testDirectRoute() throws Exception {
        
        studentRepository.deleteAll();
        
        int numberOfStudents = 2;
        
        // Set up input file
        String xmlRecords = StudentIngestionTest.createStudentInterchangeXml(numberOfStudents);
        File xmlRecordsFile = IngestionTest.createTestFile(IngestionTest.INGESTION_FILE_PREFIX,
                IngestionTest.INGESTION_XML_FILE_SUFFIX, xmlRecords);
        
        // Create Ingestion File Entry
        IngestionFileEntry xmlRecordsFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                xmlRecordsFile.getName(), MD5.calculate(xmlRecordsFile));
        xmlRecordsFileEntry.setFile(xmlRecordsFile);
        
        // Create camel context w/ routes
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(getRouteBuilder());
        
        // Get camel producer template to send input message
        ProducerTemplate template = context.createProducerTemplate();
        
        // Start camel context
        context.start();
        
        // Send message
        template.sendBody("direct:start", xmlRecordsFileEntry);
        
        Thread.sleep(1000);
        
        // Stop camel context
        context.stop();
        
        StudentIngestionTest.verifyStudents(studentRepository, numberOfStudents);
        
    }
    
    /*
     * @Test public void testFromFile() throws IOException, SAXException,
     * Exception {
     * 
     * studentRepository.deleteAll();
     * 
     * int numberOfStudents = 2;
     * 
     * // Set up input file String xmlRecords =
     * this.createStudentInterchangeXml(numberOfStudents); File xmlRecordsFile =
     * this.createTestFile( xmlRecords, "/home/ingestion/lz/inbound/test.xml");
     * 
     * // Create camel context w/ routes // TODO: use IngestionRouteBuilder -
     * some config problem causes an error setting up the routes CamelContext
     * context = new DefaultCamelContext(); //context.addRoutes( new
     * IngestionRouteBuilder() ); context.addRoutes( getRouteBuilder2() );
     * 
     * // Start camel context context.start();
     * 
     * Thread.sleep(1000);
     * 
     * // Stop camel context context.stop();
     * 
     * this.verifyStudents(studentRepository, numberOfStudents);
     * 
     * }
     */
    
    /*
     * This route mimics the one in IngestionRouteBuilder, but starts with
     * direct input instead of reading from the file system
     */
    protected RouteBuilder getRouteBuilder() {
        
        return new RouteBuilder() {
            
            public void configure() {
                
                from("direct:start").process(edFiProcessor).to("seda:persist");
                
                from("seda:persist").process(persistenceProcessor);
            }
        };
    }
    
    /*
     * This route is the same as the one in IngestionRouteBuilder. TODO: figure
     * out why IngestionRouteBuilder causes problems running this test
     */
    protected RouteBuilder getRouteBuilder2() {
        
        return new RouteBuilder() {
            
            public void configure() {
                
                from("file:/home/ingestion/lz/inbound?move=/home/ingestion/lz/inbound/.done&moveFailed=.error")
                        .process(edFiProcessor).to("seda:persist");
                
                from("seda:persist").process(persistenceProcessor);
            }
        };
    }
    
    @SuppressWarnings("unused")
    private File createTestFile(String xmlRecords, String filePath) throws IOException {
        
        File xmlRecordsFile = new File(filePath);
        BufferedOutputStream outputStream = null;
        
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(xmlRecordsFile));
            outputStream.write(xmlRecords.getBytes());
        } finally {
            outputStream.close();
        }
        return xmlRecordsFile;
    }
    
}
