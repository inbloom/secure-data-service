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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.mongodb.MongoException;
import com.sun.tools.javac.util.List;
import com.sun.tools.jdi.LinkedHashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.StagingMongoTemplate;

/**
 *
 */
@Component
public class XMLFileExpander {

    private Map<String, String> referenceTable = null;
    private Map<String, Integer> referenceFrequencyTable = null;
    private int averageReferenceSize = 0;
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
    private int embeddedReferencesProcessed = 0;
    private int entitiesProcessed = 0;
    private final String REFERENCE_DATABASE_NAME = "REFERENCE_DATABASE---------------------------------";
    private final String REFERENCE_COLLECTION_NAME = "REFERENCE_COLLECTION";
    private final String REFERENCE_BODY_NAME = "REFERENCE_BODY";

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    private final Logger LOG = LoggerFactory.getLogger(XMLFileExpander.class);

    /**
     * @return the neutralRecordMongoAccess
     */
    public NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return neutralRecordMongoAccess;
    }

    /**
     * @param neutralRecordMongoAccess
     *            the neutralRecordMongoAccess to set
     */
    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

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
            openOutputFile();

            // Get initial free memory.
            resetMemory();

            // Create the reference frequency table.
            createReferenceFrequencyTable();

            // Create the reference database.
            createReferenceDatabase();

            // Parse the XML input file for extraction of all references.
            saxReferenceParser.parse(inputFileReference, referenceHandler);

            // Sort reference frequency table.
            sortReferenceFrequencyTable();

            // Create the reference table.
            createReferenceTable();

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
        origFreeMemory = getMemory();
        minFreeMemory = (long) (0.1 * (double) origFreeMemory);
    }

    /**
     *
     */
    private void setMaxMemoryUsed() {
        // Return if the available heap memory is too low.
        double memoryUsed = (double) (origFreeMemory - getMemory());
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
        if (getMemory() < minFreeMemory) {
            tooLow = true;
            double freeMemoryMb = ((double) getMemory()) / 1048576.0;
            System.out.println("WARNING: Free memory is critically low (" + freeMemoryMb + "MB remaining)!!!");
            setMaxMemoryUsed();
            }
        return tooLow;
    }

    /**
     *
     */
    private long getMemory() {
        // Return the amount of free memory available.
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        return (freeMemory + (maxMemory - allocatedMemory));
    }

    private void createReferenceTable() {

        // Calculate the average reference size.
        averageReferenceSize *= 1000;
        averageReferenceSize /= referencesProcessed;
        int capacity = (int) (((getMemory() * 9) / 10) / averageReferenceSize);  // Allocate 90% of
                                                                                // free memory.
        referenceTable = new HashMap<String, String>(capacity);

        // Create the reference table.
        Iterator<Entry<String, Integer>> ref = referenceFrequencyTable.entrySet().iterator();
        while (!memoryLow() && ref.hasNext()) {
            String referenceId = ref.next().getKey();
            referenceTable.put(referenceId, getReferenceFromDatabase(referenceId));
        }
        setMaxMemoryUsed();
        }

    /**
    *
    */
    private String getReferenceFromTable(String referenceId) {
        // Add the next reference from the input file to the reference table.
        String referenceBody = null;
        if (referenceTable.containsKey(referenceId)) {
            referenceBody = referenceTable.get(referenceId);
        }
        return referenceBody;
    }

    /**
    *
    */
    private void createReferenceFrequencyTable() {
        // Create the reference frequency table.
        int capacity = (int) ((getMemory() / 20) / 8);  // Allocate 5% of free memory.
        referenceFrequencyTable = new HashMap<String, Integer>(capacity);
    }

    /**
    *
    */
    private void updateReferenceFrequencyTable(String referenceId) {
        // Sort the reference frequency table.
        int freq = (referenceFrequencyTable.containsKey(referenceId) ? referenceFrequencyTable.get(referenceId) + 1 : 1);
        referenceFrequencyTable.put(referenceId, freq);
    }

    /**
    *
    */
    private void sortReferenceFrequencyTable() {
        // Sort reference frequency table.
        ArrayList<String> mapKeys = new ArrayList<String>(referenceFrequencyTable.keySet());
        ArrayList<Integer> mapValues = new ArrayList<Integer>(referenceFrequencyTable.values());
        ArrayList<Integer> sortedSet = new ArrayList<Integer>(mapValues);
        Collections.sort(sortedSet);
        Integer[] sortedArr = new Integer[sortedSet.size()];
        Integer[] sortedArray = sortedSet.toArray(sortedArr);
        int size = sortedArray.length;
        referenceFrequencyTable.clear();

        // Size the new reference list.
        int index;
        for (int i = 0; i < size; i++) {
            index = mapValues.indexOf(sortedArray[i]);
            referenceFrequencyTable.put(mapKeys.get(index), sortedArray[i]);
            mapValues.remove(index);
        }
    }

    /**
    *
    */
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

    /**
    *
    */
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
        for (int i = 1; i < 5; i++) {
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
        neutralRecord.setRecordType(REFERENCE_COLLECTION_NAME);
        neutralRecord.setRecordId(hashCode(referenceId));
        neutralRecord.setBatchJobId(referenceId);
        neutralRecord.setAttributeField(REFERENCE_BODY_NAME, referenceBody);

        // Now, add it to the database.
        neutralRecordMongoAccess.getRecordRepository().create(neutralRecord);
    }

    /**
    *
    */
    private String getReferenceFromDatabase(String referenceId) {
        // Get the specified reference from the reference database.
        String hashedReferenceId = hashCode(referenceId);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("batchJobId=" + referenceId));
        NeutralRecord neutralRecord = neutralRecordMongoAccess.getRecordRepository().findById(
                REFERENCE_COLLECTION_NAME, hashedReferenceId);
        /*
         * NeutralRecord neutralRecord = neutralRecordMongoAccess.getRecordRepository().findOne(
         * REFERENCE_COLLECTION_NAME, neutralQuery);
         */
        return neutralRecord.getAttributes().get(REFERENCE_BODY_NAME).toString();
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
    private void openOutputFile() throws IOException {
        // Open the output file.
        outputFile = new File("C:\\users\\tshewchuk\\workspace\\data\\test_xml_expander\\", "temp-" + inputFileName);
        outputFile.createNewFile();
        FileOutputStream outputFileStream = new FileOutputStream(outputFile);
        outputFileDataStream = new DataOutputStream(outputFileStream);
    }

    /**
     * @throws IOException
     *
     */
    private void addEntityToOutputFile(String entityBody) {
        // Add the next reference from the input file to the reference table.
        try {
            outputFileDataStream.writeBytes(entityBody);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     *
     */
    private void closeOutputFile() throws IOException {
        // Close the output file.
        outputFileDataStream.close();
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
        System.out.println("Embedded references processed: " + embeddedReferencesProcessed);
        System.out.println("Reference table size: " + referenceTable.size());
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
                } else if (qualifiedName.contains("Reference") && attributes.getQName(0).equals("ref")) {
                    // This is a reference to a reference, so update reference frequency table.
                    referenceId = attributes.getValue(0);
                    updateReferenceFrequencyTable(referenceId);
                    embeddedReferencesProcessed++;
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
                        if ((referencesProcessed % 1000) == 1) {  // Average every 1000 references.
                            averageReferenceSize += referenceBody.length();
                        }

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
                    addEntityToOutputFile(entityBody);
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
                        String referenceBody = getReferenceFromTable(referenceId);
                        if (referenceBody != null) {
                            entityBody += referenceBody;
                        } else {
                            entityBody += getReferenceFromDatabase(referenceId);
                        }
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
                if (isEntity || (qualifiedName.startsWith("Interchange"))) {
                    if (!qualifiedName.contains("Reference") || (qualifiedName.startsWith("Interchange"))) {
                        entityBody += "</" + qualifiedName + ">";
                    }
                    if (qualifiedName.equals(entityName) || (qualifiedName.startsWith("Interchange"))) {
                        if (!qualifiedName.startsWith("Interchange")) {
                            isEntity = false;
                            entityBody += "\n";
                            entitiesProcessed++;
                        }

                        // This entity is completed; add it to the table.
                        addEntityToOutputFile(entityBody);
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

}
