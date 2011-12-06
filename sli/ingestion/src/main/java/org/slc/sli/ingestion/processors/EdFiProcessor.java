package org.slc.sli.ingestion.processors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;


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
public class EdFiProcessor extends IngestionProcessor implements Processor {

   // Logging
   Logger log = LoggerFactory.getLogger(EdFiProcessor.class);

   // Constants
   public static final String FILE_SUFFIX_XML = ".xml";
   public static final String FILE_SUFFIX_CSV = ".csv";
   public static final String SMOOKS_CONFIG_DEFAULT = "smooks-config.xml";
   public static final String SMOOKS_CONFIG_XML = "smooks-config-xml.xml";
   public static final String SMOOKS_CONFIG_CSV = "smooks-config-csv.xml";
   public static final String SMOOKS_CSV_RECORD = "csv-record";

   public static final String EDFI_XPATH_STUDENT = "InterchangeStudent/Student";
   public static final String EDFI_XPATH_SCHOOL = "InterchangeEducationOrganization/School";
   public static final String EDFI_XPATH_STUDENT_SCHOOL_ASSOCIATION = "InterchangeStudentEnrollment/StudentSchoolAssociation";


   /**
    * Consumes the EDFI Interchange XML input file, parses, and writes the SLI Neutral format records to the output file
    *
    * @param inputFile
    * @param outputFile
    */
   public void processIngestionStream(File inputFile, File outputFile) throws IOException, SAXException {
      this.convertEdfiToNeutralFormat(inputFile, outputFile);
   }

   /**
    * Use smooks to convert Ed-Fi File Input to SLI neutral data format.
    * The neutral records are to be written and read using NeutralRecordFileWriter and NeutralRecordFileReader respectively.
    * See smooks-config.xml for the actual mappings being used.
    *
    * @param inputFile - the inbound file
    * @param outputFile - the outbound File used when constructing the NeutralRecordFileWriter via which entities
    *                         will be serialized
    * @throws IOException
    * @throws SAXException
    */
   public void convertEdfiToNeutralFormat(File inputFile, File outputFile) throws IOException, SAXException {

      // Create Ingestion XML input stream
      InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));

      // Create Ingestion Neutral record writer
      NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(outputFile);

      // Determine Smooks configuration file name
      String smooksConfigFileName = getSmooksConfigFileName(inputFile);

      // Create and configure smooks instance.
      Smooks smooks = new Smooks(smooksConfigFileName);

      // Setup Smooks EDFI XML mapping visitor
      smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), SMOOKS_CSV_RECORD);
      smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), EDFI_XPATH_STUDENT);
      smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), EDFI_XPATH_SCHOOL);
      smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), EDFI_XPATH_STUDENT_SCHOOL_ASSOCIATION);

      // Leverage Smooks XML mapping visitor to convert Interchange XML into Ingestion Neutral record instances
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
    * Routine to determine the Smooks configuration file name based upon the EDFI file(s) being ingested.
    * TODO - Factor in Ingestion config parameters in order to make decision
    *
    * @param inputFile - the inbound file
    * @return configFile - the Smooks configuration file name
    */
   public String getSmooksConfigFileName(File inputFile) {

       // Determine EDFI input file type
       String smooksConfigFileName = SMOOKS_CONFIG_DEFAULT;

       String edfiFileName = inputFile.getName();
       if (edfiFileName.endsWith(FILE_SUFFIX_XML)) {
          smooksConfigFileName = SMOOKS_CONFIG_XML;
       } else if (edfiFileName.endsWith(FILE_SUFFIX_CSV)) {
          smooksConfigFileName = SMOOKS_CONFIG_CSV;
       }

       return smooksConfigFileName;
   }
}
