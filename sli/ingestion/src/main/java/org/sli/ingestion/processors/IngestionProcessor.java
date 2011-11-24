package org.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;


/**
 * Ingestion Processor.
 * 
 * Base Camel Ingestion Processor which provides common ingestion behavior.
 * 
 */

@Component
public abstract class IngestionProcessor implements Processor {

    Logger log = LoggerFactory.getLogger(IngestionProcessor.class);
    
    public static final String SLI_DOMAIN_PACKAGE = "org.slc.sli.domain";

    @Autowired
    private ContextManager contextManager;
    
    private ObjectMapper jsonMapper;
    
    public IngestionProcessor() {
        super();
                
        // Setup Jackson JSON mapper
        this.jsonMapper = new ObjectMapper();
    }
    
    @PostConstruct
    public void init() {
    }
    
    public ContextManager getContextManager() {
        return this.contextManager;
    }    
    
    public ObjectMapper getJsonMapper() {
        return this.jsonMapper;
    }
    
    /**
     * Camel Exchange process callback method
     * 
     * @param exchange
     */
    @Override
    public void process(Exchange exchange) throws IOException, SAXException {
                
        // Indicate Camel processing
        log.info("processing: {}", exchange.getIn().getHeaders().toString());

        // Extract Ingestion input file
        File ingestionInputFile = exchange.getIn().getBody(File.class);
        
        // Setup Ingestion processor output file
        File ingestionOutputFile = File.createTempFile("camel_", ".tmp");
        ingestionOutputFile.deleteOnExit();
        
        // Allow Ingestion processor to process Camel exchange file
        this.processIngestionStream(ingestionInputFile, ingestionOutputFile);
        
        // Update Camel Exchange processor output result
        exchange.getOut().setBody(ingestionOutputFile);        
        
    }

    public abstract void processIngestionStream(File inputFile, File outputFile) throws IOException, SAXException;

}
