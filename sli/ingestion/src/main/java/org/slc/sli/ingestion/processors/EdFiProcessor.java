package org.slc.sli.ingestion.processors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;

/**
 * Camel Processor interface for transforming Ed-Fi XML and CSV files into the
 * Ingestion neutral format.
 * 
 * Based on the inbound message, this Processor creates a file writer for
 * Ingestion format, and configures smooks to parse the xml or csv content and
 * instantiate / serialize a NeutralRecord instance, from each Ed-Fi entity
 * encountered, via that writer.
 * 
 * @author Jim Abramson
 * 
 */

@Component
public class EdFiProcessor implements Processor {
    
    // Logging
    Logger log = LoggerFactory.getLogger(EdFiProcessor.class);
    
    // Constants
    public static final String SMOOKS_CONFIG_DEFAULT = "smooks-config.xml";
    public static final String SMOOKS_CONFIG_XML = "smooks-all-xml.xml";
    public static final String SMOOKS_CONFIG_STUDENT_CSV = "smooks-student-csv.xml";
    public static final String SMOOKS_CONFIG_SCHOOL_CSV = "smooks-school-csv.xml";
    public static final String SMOOKS_CONFIG_STUDENT_SCHOOL_ASSOCIATION_CSV = "smooks-studentSchoolAssociation-csv.xml";
    public static final String SMOOKS_CSV_RECORD = "csv-record";
    
    public static final String EDFI_XPATH_STUDENT = "InterchangeStudent/Student";
    public static final String EDFI_XPATH_SCHOOL = "InterchangeEducationOrganization/School";
    public static final String EDFI_XPATH_STUDENT_SCHOOL_ASSOCIATION = "InterchangeStudentEnrollment/StudentSchoolAssociation";
    
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
        
        // Extract Ingestion input file metadata entry
        IngestionFileEntry ingestionFileEntry = exchange.getIn().getBody(IngestionFileEntry.class);
        
        // Setup Ingestion processor output file
        File ingestionOutputFile = File.createTempFile("camel_", ".tmp");
        ingestionOutputFile.deleteOnExit();
        
        // Allow Ingestion processor to process Camel exchange file
        this.processIngestionStream(ingestionFileEntry, ingestionOutputFile);
        
        // Update Camel Exchange processor output result
        exchange.getOut().setBody(ingestionOutputFile);
        
        exchange.getOut().setHeader("jobId", exchange.getIn().getHeader("jobId"));
        exchange.getOut().setHeader("jobCreationDate", exchange.getIn().getHeader("jobCreationDate"));
        exchange.getOut().setHeader("dry-run", exchange.getIn().getHeader("dry-run"));
        
        long endTime = System.currentTimeMillis();
        
        // Log statistics
        log.info("Translated Ingestion file [{}] in {} ms", ingestionFileEntry.getFile().getName(), endTime - startTime);
        
    }
    
    /**
     * Consumes the EDFI Interchange XML input file, parses, and writes the SLI Neutral format
     * records to the output file
     * 
     * @param inputFileEntry
     * @param outputFile
     */
    public void processIngestionStream(IngestionFileEntry inputFile, File outputFile) throws IOException, SAXException {
        this.convertEdfiToNeutralFormat(inputFile, outputFile);
    }
    
    /**
     * Use smooks to convert Ed-Fi File Input to SLI neutral data format.
     * The neutral records are to be written and read using NeutralRecordFileWriter and
     * NeutralRecordFileReader respectively.
     * See smooks-config.xml for the actual mappings being used.
     * 
     * @param inputFile
     *            - the inbound file meta entry
     * @param outputFile
     *            - the outbound File used when constructing the NeutralRecordFileWriter via which
     *            entities
     *            will be serialized
     * @throws IOException
     * @throws SAXException
     */
    public void convertEdfiToNeutralFormat(IngestionFileEntry inputFileEntry, File outputFile) throws IOException,
            SAXException {
        
        // Extract input file
        File inputFile = inputFileEntry.getFile();
        
        // Create Ingestion XML input stream
        InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
        
        // Create Ingestion Neutral record writer
        NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(outputFile);
        
        // Determine Smooks configuration file name
        String smooksConfigFileName = getSmooksConfigFileName(inputFileEntry);
        
        // Create and configure smooks instance.
        Smooks smooks = new Smooks(smooksConfigFileName);
        
        // Setup Smooks EDFI XML mapping visitor
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), SMOOKS_CSV_RECORD);
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), EDFI_XPATH_STUDENT);
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), EDFI_XPATH_SCHOOL);
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), EDFI_XPATH_STUDENT_SCHOOL_ASSOCIATION);
        
        // Leverage Smooks XML mapping visitor to convert Interchange XML into Ingestion Neutral
        // record instances
        try {
            smooks.filterSource(new StreamSource(inputStream));
        } catch (SmooksException smooksException) {
            log.error("smooks exception encountered " + smooksException);
        } finally {
            IOUtils.closeQuietly(inputStream);
            fileWriter.close();
        }
    }
    
    /**
     * Routine to determine the Smooks configuration file name based upon the EDFI file(s) being
     * ingested.
     * 
     * @param ingestionFileEntry
     *            - the inbound ingestion meta file entry
     * @return configFile - the Smooks configuration file name
     */
    public String getSmooksConfigFileName(IngestionFileEntry ingestionFileEntry) {
        
        // Determine EDFI input file type
        String smooksConfigFileName = SMOOKS_CONFIG_DEFAULT;
        
        if (ingestionFileEntry.getFileFormat().equals(FileFormat.EDFI_XML)) {
            smooksConfigFileName = SMOOKS_CONFIG_XML;
        } else if (ingestionFileEntry.getFileFormat().equals(FileFormat.CSV)) {
            if (ingestionFileEntry.getFileType().equals(FileType.CSV_STUDENT)) {
                smooksConfigFileName = SMOOKS_CONFIG_STUDENT_CSV;
            } else if (ingestionFileEntry.getFileType().equals(FileType.CSV_SCHOOL)) {
                smooksConfigFileName = SMOOKS_CONFIG_SCHOOL_CSV;
            } else if (ingestionFileEntry.getFileType().equals(FileType.CSV_STUDENT_SCHOOL_ASSOCIATION)) {
                smooksConfigFileName = SMOOKS_CONFIG_STUDENT_SCHOOL_ASSOCIATION_CSV;
            }
        }
        
        return smooksConfigFileName;
    }
}
