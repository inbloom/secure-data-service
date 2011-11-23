package org.sli.ingestion.processors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sli.ingestion.NeutralRecordFileWriter;
import org.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;


/**
 * Camel Processor interface for transforming Ed-Fi XML files into the 
 * Ingestion neutral format.
 *
 * Based on the inbound message, this Processor creates a file writer for 
 * Ingestion format, and configures smooks to parse the xml content and 
 * instantiate / serialize a NeutralRecord instance, from each Ed-Fi entity 
 * encountered, via that writer.
 * 
 * @author Jim Abramson
 *
 */

@Component
public class EdFiXmlProcessor extends IngestionProcessor implements Processor {
    
    Logger log = LoggerFactory.getLogger(EdFiXmlProcessor.class);

    /**
     * Consumes the EDFI Interchange XML input file, parses, and writes the SLI Neutral format records to the output file
     * 
     * @param inputFile
     * @param outputFile
     */
    public void processIngestionStream(File inputFile, File outputFile) throws IOException, SAXException {
        this.convertXmlToNeutralFormat(inputFile, outputFile);
    }
    
    /**
     * Use smooks to convert Ed-Fi XML to SLI neutral data format.  
     * The neutral records are to be written and read using NeutralRecordFileWriter and NeutralRecordFileReader respectively. 
     * See smooks-config.xml for the actual mappings being used.
     * 
     * @param inputFile - the inbound XML file
     * @param outputFile - the outbound File used when constructing the NeutralRecordFileWriter via which entities 
     *                         will be serialized
     * @throws IOException
     * @throws SAXException
     */
    public void convertXmlToNeutralFormat(File inputFile, File outputFile) throws IOException, SAXException {

        // Create Ingestion XML input stream
        InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
        
        // Create Ingestion Neutral record writer
        NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(outputFile);
        
        // Create and configure smooks instance.
        Smooks smooks = new Smooks("smooks-config.xml");
        
        // Setup Smooks EDFI XML mapping visitor
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), "InterchangeStudent/Student");
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), "InterchangeEducationOrganization/School");
        smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), "InterchangeStudentEnrollment/StudentSchoolAssociation");

        // Leverage Smooks XML mapping visitor to convert Interchange XML into Ingestion Neutral record instances
        try {
            smooks.filterSource(new StreamSource(inputStream));            
        
        } finally {
            IOUtils.closeQuietly(inputStream);
            fileWriter.close();
        }
    }

}
