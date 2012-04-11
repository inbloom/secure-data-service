package org.slc.sli.ingestion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * Class to resolve and expand references to Ed-Fi references within ingested XML files.
 *
 * @author tshewchuk
 *
 */
public class ReferenceResolver extends DefaultHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceResolver.class);

    private Map<String, String> referenceObjects;

    private BufferedWriter outputFileWriter;

    private String currentXMLString;
    private String tempVal;
    private String topElementName;
    private boolean isValidEntity;

    private Set<String> referenceObjectList;

    public ReferenceResolver(Set<String> referenceSet, Map<String, String> referenceMap) {
        referenceObjects = referenceMap;
        referenceObjectList = referenceSet;

        tempVal = "";
        topElementName = "";
        currentXMLString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        isValidEntity = true;
    }

    /**
     * Main method of the reference resolver.
     *
     * @param inputFilePath
     *            Full pathname of input XML file to be expanded.
     * @param outputFilePath
     *            Full pathname of expanded output XML file to be written.
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public void execute(String inputFilePath, String outputFilePath) throws SAXException, ParserConfigurationException, IOException {
        // Resolve references to references within the input file using the reference memory map.
        SAXParserFactory spf = SAXParserFactory.newInstance();
        File inputFile = new File(inputFilePath);
        try {
            FileWriter fstream = new FileWriter(outputFilePath);
            outputFileWriter = new BufferedWriter(fstream);
            SAXParser sp = spf.newSAXParser();
            sp.parse(inputFile, this);
            outputFileWriter.close();
        } catch (SAXException se) {
            LOG.error("Error resolving references in XML file " + inputFile.getName() + ": " + se.getMessage());
            throw (se);
        } catch (ParserConfigurationException pce) {
            LOG.error("Error configuring parser for XML file " + inputFile.getName() + ": " + pce.getMessage());
            throw (pce);
        } catch (IOException ie) {
            LOG.error("Error writing expanded XML file " + inputFile.getName() + ": " + ie.getMessage());
            throw (ie);
        }
    }

    /**
     * SAX parser callback method for XML element start.
     *
     * @param uri
     *            Element URI returned by SAX
     * @param localName
     *            Element local name returned by SAX
     * @param qName
     *            Element qualified name returned by SAX
     * @param attributes
     *            Element attribute name/value set returned by SAX
     *
     * @throws SAXException
     *             Parser exception thrown by SAX
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // Mark if top-level element.
        if ((!qName.startsWith("Interchange")) && topElementName.isEmpty()) {
            topElementName = qName;
        }

        // Expand XML references.
        if ((qName.equals(topElementName)) && isValidEntity && referenceObjectList.contains(qName) && (attributes.getValue("id") != null)) {
                // Reference top-level element.
                isValidEntity = false;
        } else if (isValidEntity && referenceObjectList.contains(qName) && (attributes.getValue("ref") != null)) {
            // Reference to reference - get reference body from map.
            String refId = attributes.getValue("ref");
            if (referenceObjects.containsKey(refId)) {
                isValidEntity = false;
                currentXMLString += referenceObjects.get(refId);
            } else {
                // Unresolved reference!  Abort processing.
                SAXException se = new SAXException("Unresolved reference, id=\"" + refId + "\"");
                throw (se);
            }
        }

        // Process non-reference XML element.
        if (isValidEntity) {
            currentXMLString += tempVal;
            currentXMLString += "<" + qName;
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    currentXMLString += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
                }
            }
            currentXMLString += ">";
        }
    }

    /**
     * SAX parser callback method for XML element internal characters.
     *
     * @param uri
     *            Element URI returned by SAX
     * @param localName
     *            Element local name returned by SAX
     * @param qName
     *            Element qualified name returned by SAX
     * @param attributes
     *            Element attribute name/value set returned by SAX
     *
     * @throws SAXException
     *             Parser exception thrown by SAX
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Get characters between element markers.
        tempVal = "";
        for (int i = start; i < start + length; i++) {
            tempVal += String.valueOf(ch[i]);
        }
    }

    /**
     * SAX parser callback method for XML element end.
     *
     * @param uri
     *            Element URI returned by SAX
     * @param localName
     *            Element local name returned by SAX
     * @param qName
     *            Element qualified name returned by SAX
     * @param attributes
     *            Element attribute name/value set returned by SAX
     *
     * @throws SAXException
     *             Parser exception thrown by SAX
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // Unmark if top-level element.
        if (qName.equals(topElementName)) {
            topElementName = "";
        }

        // Write element to output file.
        if (!isValidEntity && referenceObjectList.contains(qName)) {
            isValidEntity = true;
        } else if (isValidEntity) {
            currentXMLString += tempVal;
            tempVal = "\n";
            currentXMLString += "</" + qName + ">";

            writeXML(currentXMLString);

            currentXMLString = "";
        }
    }

    private void writeXML(String xml) throws SAXException {
        try {
            outputFileWriter.write(xml);
        } catch (IOException e) {
            SAXException se = new SAXException(e.getMessage());
            throw (se);
        }
    }

}
