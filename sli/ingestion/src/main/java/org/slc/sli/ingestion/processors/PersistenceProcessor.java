package org.slc.sli.ingestion.processors;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

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
    private MongoEntityRepository repository;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     */
    @Override
    public void process(Exchange exchange) throws IOException, SAXException {

        long startTime = System.currentTimeMillis();

        BatchJob job = exchange.getIn().getBody(BatchJob.class);

        // Indicate Camel processing
        log.info("processing: {}", job);

        for (IngestionFileEntry fe : job.getFiles()) {

            // Setup Ingestion processor output file
            File ingestionOutputFile = File.createTempFile("camel_", ".tmp");
            ingestionOutputFile.deleteOnExit();

            // Allow Ingestion processor to process Camel exchange file
            this.processIngestionStream(fe.getNeutralRecordFile(), ingestionOutputFile, exchange);
        }

        // Update Camel Exchange processor output result
        exchange.getIn().setBody(job);
        
        // Update Camel Exchange header with status
        exchange.getIn().setHeader("records.processed", "5");

        long endTime = System.currentTimeMillis();

        // Log statistics
        log.info("Persisted Ingestion files for batch job [{}] in {} ms", job, endTime - startTime);

    }

    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances
     *
     * @param inputFile
     * @param outputFile
     * @param exchange
     */
    public void processIngestionStream(File inputFile, File outputFile, Exchange exchange) throws IOException, SAXException {
    	
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
                Entity ingestionInstance = Translator.mapFromNeutralRecord(ingestionRecord);

                this.persist(ingestionInstance);

                // Update Ingestion counter
                ingestionCounter++;
            }
        } finally {
            fileReader.close();
        }

        String status = "processed " + ingestionCounter + " records.";

        if (exchange != null) {
        	exchange.getIn().getHeader("records.processed", ingestionCounter);
        }
        
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
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances
     *
     * @param inputFile
     * @param outputFile
     */
    public void processIngestionStream(File inputFile, File outputFile) throws IOException, SAXException {
        this.processIngestionStream(inputFile, outputFile, null);
    }


    /**
     * Persists the SLI Ingestion instance using the appropriate SLI repository.
     *
     * @param instance
     * @return
     */
    public Entity persist(Entity instance) {
        return repository.create(instance.getType(), instance.getBody());
    }

}
