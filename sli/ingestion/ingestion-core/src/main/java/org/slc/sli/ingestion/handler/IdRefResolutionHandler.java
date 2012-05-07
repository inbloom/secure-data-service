package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.referenceresolution.ReferenceResolutionStrategy;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * @author npandey
 *
 */
public class IdRefResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, String> strategyMap;
    private List<Object> refObject;
    private Set<Object> idObject;
    private String interchangeName;
    private int resolveInEachParse = 2;
    private Stack<String> parentStack = new Stack<String>();
    private String parentElement;

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
                    Attribute refAttribute = getAttribute(xmlEvent, "ref");
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

        File outputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_RESOLVED.xml");
        try {

            loadRefs(inputFile);
            loadIds(inputFile, outputFile);
            resolveReferences(inputFile, outputFile);

        } catch (FileNotFoundException fileNotFoundException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + fileNotFoundException.getMessage(),
                    errorReport, log);
        } catch (XMLStreamException xmlStreamException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + xmlStreamException.getMessage(),
                    errorReport, log);
        }

        return inputFile;
    }

    private void loadIds(File inputFile, File outputFile) throws XMLStreamException, FileNotFoundException {

        idObject = new HashSet<Object>();

        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLOutputFactory xof =  XMLOutputFactory.newInstance();
        XMLEventFactory  eventFactory = XMLEventFactory.newInstance();

        XMLEventReader xmlr = xmlif.createXMLEventReader(inputFile.getName(), new FileInputStream(inputFile.getAbsolutePath()));
        XMLEventWriter xmlw = xof.createXMLEventWriter(new FileOutputStream(outputFile.getAbsolutePath()));
        XMLEvent event;

        while (xmlr != null && xmlr.hasNext()) {
            event = xmlr.nextEvent();
            boolean idResolvedValue = false;
            if (event.getEventType() == XMLEvent.START_ELEMENT) {

                Attribute id = getAttribute(event, "id");
                Attribute idResolved = getAttribute(event, "resolved");
                if (idResolved != null) {
                    idResolvedValue = Boolean.parseBoolean(idResolved.getValue());
                }
                if (id != null && !idResolvedValue) {
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
        xmlr.close();
        xmlw.close();
    }

    private void loadRefs(File inputFile) throws XMLStreamException, FileNotFoundException {
        refObject = new ArrayList<Object>();
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        String fileName = inputFile.getName();

        XMLEventReader xmlr = xmlif.createXMLEventReader(fileName, new FileInputStream(inputFile.getAbsolutePath()));
        XMLEvent event;

        while (xmlr != null && xmlr.hasNext()) {
            event = xmlr.nextEvent();
            if (event.getEventType() == XMLEvent.START_ELEMENT) {

                String elementName = event.asStartElement().getName().getLocalPart();
                if (elementName.startsWith("Interchange")) {
                    interchangeName = elementName;
                }


                Attribute ref = getAttribute(event, "ref");
                Attribute refResolved = getAttribute(event, "resolved");
                if (refResolved == null && ref != null && refObject.size() < resolveInEachParse) {
                    refObject.add(ref.getValue());
                }
            }
        }
        xmlr.close();
    }

    private void resolveReferences(File inputFile, File outputFile) throws FileNotFoundException, XMLStreamException {


        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLOutputFactory xof =  XMLOutputFactory.newInstance();
        XMLEventFactory  eventFactory = XMLEventFactory.newInstance();

        XMLEventReader xmlr = xmlif.createXMLEventReader(inputFile.getName(), new FileInputStream(inputFile.getAbsolutePath()));
        XMLEventWriter xmlw = xof.createXMLEventWriter(new FileOutputStream(outputFile.getAbsolutePath()));
        XMLEvent event;

        while (xmlr != null && xmlr.hasNext()) {
            event = xmlr.nextEvent();
            if (event.getEventType() == XMLEvent.START_ELEMENT) {

                String elementName = event.asStartElement().getName().getLocalPart();
                parentStack.push(elementName);


                Attribute ref = event.asStartElement().getAttributeByName(new QName("ref"));
                if (ref == null) {
                    continue;
                }
                boolean resolved = false;
                Attribute resolvedAttr = event.asStartElement().getAttributeByName(new QName("resolved"));
                if (resolvedAttr != null) {
                    resolved = Boolean.parseBoolean(resolvedAttr.getValue());
                }
                String resolvedString = null;
                ReferenceResolutionStrategy resolutionStrategy;
                if (resolvedAttr == null || !resolved) {
                    String refValue = ref.getValue();
                    if (idObject.contains(refValue)) {
                        //UN: Check if simple strategy or not and invoke the execute function accordingly.
                        if (strategyMap.containsKey(refValue)) {
                            String strategy = strategyMap.get(refValue);
                            resolutionStrategy = applicationContext.getBean(strategy, ReferenceResolutionStrategy.class);
                            parentElement = parentStack.elementAt((parentStack.size() - 2));
                            String resolvedReference = resolutionStrategy.resolveReference(elementName, refValue, parentElement, inputFile, interchangeName);
                            if (resolvedReference == null) {
                                //UN: Add resolved = true to the current element
                            } else {
                                //UN: Add the resolved = true to the return string and add the return string to the file.
                            }
                        }
                    }
                }
            } else if (event.getEventType() == XMLEvent.END_ELEMENT) {
                parentStack.pop();
            } else {

            }
        }
        xmlr.close();
        xmlw.close();
    }

    private Attribute getAttribute(XMLEvent startElementEvent, String attributeName) {

        Attribute attribute = startElementEvent.asStartElement().getAttributeByName(new QName(attributeName));
        return attribute;
    }

    public Map<String, String> getStrategyMap() {
        return strategyMap;
    }

    public void setStrategyMap(Map<String, String> strategyMap) {
        this.strategyMap = strategyMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
