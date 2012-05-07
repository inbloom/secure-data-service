package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * @author npandey
 *
 */
public class IdRefResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    List<Object> refObject;
    Set<Object> idObject;
    int resolveInEachParse = 10;

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        Logger log = LoggerFactory.getLogger(IdRefResolutionHandler.class);
        File file = fileEntry.getFile();
        File outputFile;
        int numOfRefUnResolved = countReferences(file, errorReport, log);
        while (numOfRefUnResolved > 0) {
            outputFile = resolveReferences(file, errorReport, log);
            file = outputFile;
            numOfRefUnResolved -= resolveInEachParse;
        }

        if (file != null) {
            // Return the expanded XML file.
            fileEntry.setFile(file);
        }
        return fileEntry;
    }

    private int countReferences(File file, ErrorReport errorReport, Logger log) {
        //parse and return the total number of references
        int count = 0;
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xif.createXMLEventReader(new FileInputStream(file));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.getEventType() == XMLEvent.START_ELEMENT) {
                    Attribute refAttribute = xmlEvent.asStartElement().getAttributeByName(new QName("ref"));
                    if (refAttribute != null) {
                        count++;
                    }
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            logError("Error configuring parser for XML file " + file.getName() + ": " + fileNotFoundException.getMessage(),
                    errorReport, log);
            return 0;
        } catch (XMLStreamException xmlStreamException) {
            logError("Error configuring parser for XML file " + file.getName() + ": " + xmlStreamException.getMessage(),
                    errorReport, log);
            return 0;
        }
        return count;
    }

    /**
     * Write an error message to the log and error files.
     *
     * @param errorMessage
     *            Error message to be written to the log and error files.
     * @param errorReport
     *            Error report to log errors.
     * @param log
     *            Logger to log errors and information.
     */
    private void logError(String errorMessage, ErrorReport errorReport, Logger log) {
        // Log errors.
        log.error(errorMessage);
        errorReport.error(errorMessage, ReferenceResolutionHandler.class);
    }

    private File resolveReferences(File inputFile, ErrorReport errorReport, Logger log) {

        String fileName = inputFile.getName();
        File outputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_RESOLVED.xml");
        try {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            XMLOutputFactory xof =  XMLOutputFactory.newInstance();
            XMLEventFactory  eventFactory = XMLEventFactory.newInstance();

            XMLEventReader xmlr = xmlif.createXMLEventReader(fileName, new FileInputStream(inputFile.getAbsolutePath()));
            XMLEventWriter xmlw = xof.createXMLEventWriter(new FileOutputStream(outputFile.getAbsolutePath()));
            XMLEvent event;

            refObject = new ArrayList<Object>();
            while (xmlr != null && xmlr.hasNext() && refObject.size() < resolveInEachParse) {
                event = xmlr.nextEvent();
                if (event.getEventType() == XMLEvent.START_ELEMENT) {
                    Attribute ref = event.asStartElement().getAttributeByName(new QName("ref"));
                    boolean resolvedValue = false;
                    Attribute resolved = event.asStartElement().getAttributeByName(new QName("resolved"));
                    if (resolved != null) {
                        resolvedValue = Boolean.parseBoolean(resolved.getValue());
                    }
                    if (ref != null && !resolvedValue) {
                        refObject.add(ref.getValue());
                    }
                }
            }

            idObject = new HashSet<Object>();
            xmlr = xmlif.createXMLEventReader(fileName, new FileInputStream(inputFile.getAbsolutePath()));
            while (xmlr != null && xmlr.hasNext()) {
                event = xmlr.nextEvent();
                if (event.getEventType() == XMLEvent.START_ELEMENT) {
                    Attribute id = event.asStartElement().getAttributeByName(new QName("id"));
                    boolean resolvedValue = false;
                    Attribute resolved = event.asStartElement().getAttributeByName(new QName("resolved"));
                    if (resolved != null) {
                        resolvedValue = Boolean.parseBoolean(resolved.getValue());
                    }
                    if (id != null && !resolvedValue) {
                        idObject.add(id.getValue());
                        XMLEvent attribute = eventFactory.createAttribute("resolved", "true");
                        xmlw.add(event);
                        xmlw.add(attribute);
                    } else {
                        xmlw.add(event);
                    }
                } else {
                    xmlw.add(event);
                }
            }

            xmlr = xmlif.createXMLEventReader(fileName, new FileInputStream(inputFile.getAbsolutePath()));
            while (xmlr != null && xmlr.hasNext()) {
                //Implement IIIrd phase
            }
            xmlr.close();
            xmlw.close();
        } catch (FileNotFoundException fileNotFoundException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + fileNotFoundException.getMessage(),
                    errorReport, log);
        } catch (XMLStreamException xmlStreamException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + xmlStreamException.getMessage(),
                    errorReport, log);
        }

        return inputFile;
    }


}
