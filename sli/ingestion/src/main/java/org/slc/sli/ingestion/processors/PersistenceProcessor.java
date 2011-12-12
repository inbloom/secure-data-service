package org.slc.sli.ingestion.processors;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.InterchangeAssociation;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.Translator;

/**
 * Ingestion Persistence Processor.
 * 
 * Specific Ingestion Persistence Processor which provides specific SLI Ingestion instance
 * persistence behavior.
 * 
 */

@Component
public class PersistenceProcessor implements Processor {
    
    Logger log = LoggerFactory.getLogger(PersistenceProcessor.class);
    
    @Autowired
    private ContextManager contextManager;
    
    public ContextManager getContextManager() {
        return this.contextManager;
    }
    
    /**
     * Camel Exchange process callback method
     * 
     * @param exchange
     */
    @Override
    public void process(Exchange exchange) throws IOException, SAXException {
        
        long startTime = System.currentTimeMillis();

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
        
        exchange.getOut().setHeader("jobId", exchange.getIn().getHeader("jobId"));
        exchange.getOut().setHeader("jobCreationDate", exchange.getIn().getHeader("jobCreationDate"));
        exchange.getOut().setHeader("dry-run", exchange.getIn().getHeader("dry-run"));
        
        long endTime = System.currentTimeMillis();
        
        // Log statistics
        log.info("Persisted Ingestion file [{}] in {} ms", ingestionInputFile.getName(), endTime - startTime);

    }
    
    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances
     * 
     * @param inputFile
     * @param outputFile
     */
    public void processIngestionStream(File inputFile, File outputFile) throws IOException, SAXException {
        
        // Create Ingestion Neutral record reader
        NeutralRecordFileReader fileReader = new NeutralRecordFileReader(inputFile);
        
        // Ingestion Neutral record
        NeutralRecord ingestionRecord;
        
        // Ingestion counter
        int ingestionCounter = 0;
        
        try {
            
            // Iterate Ingestion neutral records/lines
            while (fileReader.hasNext()) {
                
                // Read next Ingestion neutral record/line
                ingestionRecord = fileReader.next();
                
                log.debug("processing " + ingestionRecord);
                
                // Map Ingestion Neutral JSON format into instance
                Object ingestionInstance = Translator.mapFromNeutralRecord(ingestionRecord);
                
                // Initialize Ingestion association instances
                if (ingestionInstance instanceof InterchangeAssociation) {
                    
                    // Init Ingestion association instance
                    ((InterchangeAssociation) ingestionInstance).init(this.getContextManager());
                    
                    // Persist Ingestion association instance
                    this.persist(((InterchangeAssociation) ingestionInstance).getAssociation());
                } else {
                    
                    // Persist Ingestion instance
                    this.persist(ingestionInstance);
                }
                
                // Update Ingestion counter
                ingestionCounter++;
            }
        } finally {
            fileReader.close();
        }
        
        String status = "processed " + ingestionCounter + " records.";
        
        BufferedOutputStream outputStream = null;
        try {
            
            // Setup Ingestion processor output stream for Camel Exchange
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            
            // Output Ingestion processor results
            outputStream.write(status.getBytes());
            
        } finally {
            outputStream.close();
        }
        
        // Indicate processor status
        log.info(status);
        
    }
    
    /**
     * Persists the SLI Ingestion instance using the appropriate SLI repository.
     * 
     * @param instance
     * @return
     */
    public Object persist(Object instance) {
        
        // Lookup Repository for Ingestion instance class
        PagingAndSortingRepository repository = this.getContextManager().getRepositoryByConvention(
                instance.getClass().getName());
        
        // Persist the Ingestion instance
        return repository.save(instance);
    }
    
}
