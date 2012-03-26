package org.slc.sli.ingestion;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.mongodb.MongoException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.StagingMongoTemplate;

/**
 *
 */
public class XMLFileExpander {

    private Map<String, String> referenceTable = new HashMap<String, String>();
    private String inputFileName = null;
    private File inputFileReference = null;
    private File inputFileEntity = null;
    private File outputFile = null;
    private double maxMemoryUsed = 0.0;
    private long elapsedTime = 0;
    private double inputFileSize = 0.0;
    private double outputFileSize = 0.0;
    private DefaultHandler referenceHandler = null;
    private DefaultHandler entityHandler = null;
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    private SAXParser saxReferenceParser = null;
    private SAXParser saxEntityParser = null;
    private long origFreeMemory = 0;
    private long minFreeMemory = 0;
    private DataOutputStream outputFileDataStream = null;
    private Runtime runtime = Runtime.getRuntime();
    private int referencesProcessed = 0;
    private int entitiesProcessed = 0;
    private final String REFERENCE_DATABASE_NAME = "REFERENCE_DATABASE";

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    private final Logger LOG = LoggerFactory.getLogger(XMLFileExpander.class);

    /**
     * @param xmlFileName
     * @throws IOException
     */
    public void expandXMLFile(String xmlFileName) throws Exception {

        try {
            // SAX parsers setup.
            createSaxReferenceParser();
            createSaxEntityParser();

            // Set XML file name.
            inputFileName = xmlFileName;

            // Get the input file size.
            inputFileReference = new File("C:\\users\\tshewchuk\\workspace\\data\\test_xml_expander\\", inputFileName);
            inputFileEntity = new File("C:\\users\\tshewchuk\\workspace\\data\\test_xml_expander\\", inputFileName);
            inputFileSize = (double) (inputFileReference.length()) / 1000000.0;

            // Get the starting time, in milliseconds.
            long startTimeMillis = System.currentTimeMillis();

            // Open the XML output file for writing.
            outputFile = new File("C:\\users\\tshewchuk\\workspace\\data\\test_xml_expander\\", "temp-" + inputFileName);
            outputFile.createNewFile();
            FileOutputStream outputFileStream = new FileOutputStream(outputFile);
            outputFileDataStream = new DataOutputStream(outputFileStream);

            // Get initial free memory.
            resetMemory();

            // Create the reference database.
            createReferenceDatabase();

            // Parse the XML input file for extraction of all references.
            saxReferenceParser.parse(inputFileReference, referenceHandler);

            // Parse the XML input file for extraction of all entities.
            saxEntityParser.parse(inputFileReference, entityHandler);

            // Rename the output file to the input file.
            // outputFile.renameTo(inputFileReference);

            // Close the output file.
            closeOutputFile();

            // Drop the reference database.
            dropReferenceDatabase();

            // Get the output file size.
            outputFileSize = (double) (outputFile.length()) / 1000000.0;

            // Get the elapsed time, in seconds.
            elapsedTime = (System.currentTimeMillis() - startTimeMillis) / 1000;

            // Log statistics.
            logStatistics();

        } catch (Exception e) {
            // Report error and rethrow.
            System.out.println(e.getMessage());
            throw (e);
        }
    }

    /**
     *
     */
    private void resetMemory() {
        // Reset the amount of free memory available
        runtime.gc();  // Run the garbage collector.
        origFreeMemory = runtime.freeMemory();
        minFreeMemory = (long) (0.1 * (double) origFreeMemory);
    }

    /**
     *
     */
    private void setMaxMemoryUsed() {
        // Return if the available heap memory is too low.
        double memoryUsed = (double) (origFreeMemory - runtime.freeMemory());
        if (maxMemoryUsed < memoryUsed) {
            maxMemoryUsed = memoryUsed;
        }
    }

    /**
     *
     */
    private boolean memoryLow() {
        // Return if the available heap memory is too low.
        boolean tooLow = false;
        if (runtime.freeMemory() < minFreeMemory) {
            tooLow = true;
            double freeMemoryMb = ((double) runtime.freeMemory()) / 1000.0;
            System.out.println("WARNING: Free memory is critically low (" + freeMemoryMb + "MB remaining)!!!");
        }
        return tooLow;
    }

    /**
    *
    */
    private void addReferenceToTable(String referenceId, String referenceBody) {
        // Add the next reference from the input file to the reference table.
        referenceTable.put(referenceId, referenceBody);
    }

    /**
    *
    */
    private void clearReferenceTable() {
        // Clear the reference table.
        referenceTable.clear();
    }

    private String hashCode(String key) {
        final long[] byteTable = createLookupTable();
        final long HSTART = 0xBB40E64DA205B064L;
        final long HMULT = 7664345821815920749L;

        // Calculate the key integral hash code.
          long h = HSTART;
          final long hmult = HMULT;
          final long[] ht = byteTable;
          for (int len = key.length(), i = 0; i < len; i++) {
            h = (h * hmult) ^ ht[key.charAt(i) & 0xff];
          }

          // Convert the integral hash code into a String, and return.
          return convertToRecordId(h);
      }

    private final long[] createLookupTable() {
        long[] byteTable = new long[256];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 31; j++) {
                h = (h >>> 7) ^ h;
                h = (h << 11) ^ h;
                h = (h >>> 10) ^ h;
            }
            byteTable[i] = h;
        }
        return byteTable;
    }

    /**
     *
     */
    private String convertToRecordId(long hash) {
        byte hexString[] = Long.toHexString(hash).getBytes();
        String recordId = String.valueOf(hexString[0]);
        for (int i=1; i<5; i++) {
            recordId += "-" + String.valueOf(hexString[i]);
        }
        return recordId;
    }

    /**
     * @throws MongoException
     * @throws UnknownHostException
     *
     */
    private void createReferenceDatabase() throws UnknownHostException, MongoException {
        // Create the references database.
        neutralRecordMongoAccess.changeMongoTemplate(REFERENCE_DATABASE_NAME);
    }

    /**
    *
    */
    private void addReferenceToDatabase(String referenceId, String referenceBody) {
        // Add the next reference from the input file to the reference database.

        // First, convert the reference to a neutral record.
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordType("reference");
        neutralRecord.setRecordId(hashCode(referenceId));
        neutralRecord.setAttributeField("referenceBody", referenceBody);

        // Now, add it to the database.
        neutralRecordMongoAccess.getRecordRepository().create(neutralRecord);
    }

    /**
    *
    */
    private String getReferenceFromDatabase(String referenceId) {
        // Get the specified reference from the reference database.
        String hashedReferenceId = hashCode(referenceId);
        NeutralRecord neutralRecord = neutralRecordMongoAccess.getRecordRepository().findById(null, hashedReferenceId);
        return neutralRecord.getAttributes().get(referenceId).toString();
    }

    /**
     *
     */
    private void dropReferenceDatabase() {
        // Drop the references database.
        neutralRecordMongoAccess.dropDatabase();
    }

    /**
     * @throws IOException
     *
     */
    private void addEntityToOutputFile(String entityBody) throws IOException {
        // Add the next reference from the input file to the reference table.
        outputFileDataStream.writeBytes(entityBody);
    }

    /**
     * @throws IOException
     *
     */
    private void closeOutputFile() throws IOException {
        // Clear the reference table.
        outputFileDataStream.close();
    }

    /**
    *
    */
    private void createReferenceDB() {
        // Create the reference staging database.
    }

    /**
    *
    */
    private void writeReferenceToDB(String referenceId, String referenceBody) {
        // Write the reference to the staging database.
    }

    /**
    *
    */
    private void dropReferenceDB() {
        // Drop the reference staging database.
    }

    /**
    *
    */
    private void logStatistics() {
        // Log the elapsed time/memory/size statistics for the XML file expansion.
        long elapsedHours = elapsedTime / 3600;
        elapsedTime %= 3600;
        long elapsedMinutes = elapsedTime / 60;
        long elapsedSeconds = elapsedTime % 60;
        System.out.println(inputFileName + " reference expansion statistics:");
        System.out.println("Input file size: " + inputFileSize + " MBytes.");
        System.out.println("Output file size: " + outputFileSize + " MBytes.");
        System.out.println("References processed: " + referencesProcessed);
        System.out.println("Entities processed: " + entitiesProcessed);
        System.out.println("Elapsed time: " + elapsedHours + " hours, " + elapsedMinutes + " minutes, "
                + elapsedSeconds + " seconds.");
        double maxMemoryUsedMb = maxMemoryUsed / 1000.0;
        System.out.println("Maximum memory used: " + maxMemoryUsedMb + " MBytes.");
    }

    /**
     * @throws SAXException
     * @throws ParserConfigurationException
     *
     */
    private void createSaxReferenceParser() throws Exception {

        // Create the Reference SAX parser and handler.
        saxReferenceParser = saxParserFactory.newSAXParser();

        referenceHandler = new DefaultHandler() {

            String referenceName = null;
            String referenceId = null;
            String referenceBody = null;
            boolean isReference = false;

            public void startElement(String uri, String localName, String qualifiedName, Attributes attributes)
                    throws SAXException {
                // Set flag according to element type.
                if (isReference || (qualifiedName.contains("Reference") && attributes.getQName(0).equals("id"))) {
                    if (!isReference) {
                        isReference = true;
                        referenceName = qualifiedName;
                        referenceBody = "<" + qualifiedName;
                    } else {
                        referenceBody += "<" + qualifiedName;
                    }
                    for (int i = 0; i < attributes.getLength(); i++) {
                        referenceBody += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
                        if (attributes.getQName(i).equals("id")) {
                            referenceId = attributes.getValue(i);
                        }
                    }
                    referenceBody += ">";
                }
            }

            public void endElement(String uri, String localName, String qualifiedName) throws SAXException {

                // Set flag according to element type.
                if (isReference) {
                    referenceBody += "</" + qualifiedName + ">";
                    if (qualifiedName.equals(referenceName)) {

                        // This reference is completed; add it to the table.
                        addReferenceToDatabase(referenceId, referenceBody);
                        isReference = false;
                        referencesProcessed++;

                    }
                }

            }

            public void characters(char ch[], int start, int length) throws SAXException {
                if (isReference) {
                    for (int i = start; i < start + length; i++) {
                        referenceBody += ch[i];
                    }
                }
            }

        };

    }

    /**
     * @throws SAXException
     * @throws ParserConfigurationException
     *
     */
    private void createSaxEntityParser() throws Exception {

        // Create the Entity SAX parser and handler.
        saxEntityParser = saxParserFactory.newSAXParser();

        entityHandler = new DefaultHandler() {

            boolean isEntity = false;
            String entityName = null;
            String entityBody = null;
            String referenceId = null;
            boolean isReference = false;
            boolean isStart = false;

            public void startDocument() throws SAXException {
                // Write the input file header to the output file.
                try {
                    FileInputStream inputFileStream = new FileInputStream(inputFileEntity);
                    DataInputStream inputFileDataStream = new DataInputStream(inputFileStream);
                    BufferedReader inputFileReader = new BufferedReader(new InputStreamReader(inputFileDataStream));
                    String header = inputFileReader.readLine() + "\n";
                    inputFileReader.close();
                    addEntityToOutputFile(header);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            public void startElement(String uri, String localName, String qualifiedName, Attributes attributes)
                    throws SAXException {

                // Write the input file interchange entity header to the output file.
                if (qualifiedName.startsWith("Interchange")) {
                    entityBody = "<" + qualifiedName;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        entityBody += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
                    }
                    entityBody += ">\n";
                    try {
                        addEntityToOutputFile(entityBody);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Set flag according to element type.
                else if (qualifiedName.contains("Reference") && attributes.getQName(0).equals("id")) {
                    isReference = true;
                    entityName = qualifiedName;
                } else if (isEntity || !(isReference || qualifiedName.startsWith("Interchange"))) {
                    if (qualifiedName.contains("Reference") && attributes.getQName(0).equals("ref")) {
                        // Look up the reference ID from the table, and add the reference to the
                        // entity.
                        referenceId = attributes.getValue(0);
                        entityBody += getReferenceFromDatabase(referenceId);
                    } else {
                        if (!isEntity) {
                            isEntity = true;
                            entityName = qualifiedName;
                            entityBody = "<" + qualifiedName;
                        } else {
                            entityBody += "<" + qualifiedName;
                        }
                        for (int i = 0; i < attributes.getLength(); i++) {
                            entityBody += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
                        }
                        entityBody += ">";
                    }
                }

            }

            public void endElement(String uri, String localName, String qualifiedName) throws SAXException {
                // Set flag according to element type.
                if (isEntity) {
                    if (!qualifiedName.contains("Reference")) {
                        entityBody += "</" + qualifiedName + ">";
                    }
                    if (qualifiedName.equals(entityName)) {
                        isEntity = false;
                        entityBody += "\n";
                        entitiesProcessed++;

                        // This entity is completed; add it to the table.
                        try {
                            addEntityToOutputFile(entityBody);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else if (isReference && qualifiedName.equals(entityName)) {
                    isReference = false;
                }
            }

            public void characters(char ch[], int start, int length) throws SAXException {
                if (isEntity) {
                    for (int i = start; i < start + length; i++) {
                        entityBody += ch[i];
                    }
                }
            }

        };

    }

    public NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return neutralRecordMongoAccess;
    }

    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

}
