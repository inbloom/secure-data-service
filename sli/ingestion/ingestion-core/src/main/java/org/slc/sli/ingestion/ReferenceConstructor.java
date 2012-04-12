package org.slc.sli.ingestion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * Class to construct a list of Ed-Fi references contained in ingested XML files.
 *
 * @author tshewchuk
 *
 */
public class ReferenceConstructor extends DefaultHandler {

    private Map<String, String> referenceObjects;

    private boolean isInsideReferenceEntity;
    private boolean startCharacters;
    private String currentReferenceXMLString;
    private StringBuffer tempVal;
    private String currentReferenceId;
    private String topElementName;

    public ReferenceConstructor() {
        tempVal = new StringBuffer();
        topElementName = "";
        referenceObjects = new HashMap<String, String>();
    }

    /**
     * Main method of the reference constructor.
     *
     * @param filePath
     *            Full pathname of input XML file from which references are extracted.
     *
     * @return Map<String, String>
     *         Memory map of all references within the input file.
     *
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public Map<String, String> execute(String filePath) throws SAXException, ParserConfigurationException, IOException {
        // Extract references bodies from the input file into the memory map.
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse(filePath, this);
        return referenceObjects;
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

        // Check if top-level element is a reference.
        if (qName.equals(topElementName) && qName.endsWith("Reference") && (attributes.getValue("id") != null)) {
            currentReferenceXMLString = new String();
            currentReferenceId = attributes.getValue("id");
            isInsideReferenceEntity = true;
        }

        // If in reference, add element to reference body.
        if (isInsideReferenceEntity) {
            currentReferenceXMLString += tempVal.toString();
            currentReferenceXMLString += "\t<" + qName;
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    currentReferenceXMLString += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
                }
            }
            currentReferenceXMLString += ">";
        }
        startCharacters = true;
    }

    /**
     * SAX parser callback method for XML element internal characters.
     *
     * @param ch
     *            Character array returned by SAX
     * @param start
     *            Start index in character array returned by SAX
     * @param length
     *            Length of character string returned by SAX
     *
     * @throws SAXException
     *             Parser exception thrown by SAX
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Get characters between element markers.
        if (startCharacters) {
            tempVal.delete(0, tempVal.length());
            startCharacters = false;
        }
        tempVal.append(ch, start, length);
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

        // If in reference, add reference to reference map.
        if (isInsideReferenceEntity) {
            currentReferenceXMLString += tempVal.toString();
            if ((tempVal.toString().contains("\n")) && tempVal.toString().trim().isEmpty()) {
                currentReferenceXMLString += "\t";
            }
            tempVal.append("\n\t");
            currentReferenceXMLString += "</" + qName + ">";
            if (qName.endsWith("Reference")) {
                referenceObjects.put(currentReferenceId, currentReferenceXMLString);
                isInsideReferenceEntity = false;
            }
        }
        startCharacters = true;
    }

}
