package org.slc.sli.ingestion.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
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
    private List<String> refObject = new ArrayList<String>();;
    private Map<String, String> idObject = new HashMap<String, String>();;
    private String interchangeName;
    private int resolveInEachParse;
    private Stack<String> parentStack = new Stack<String>();
    private String parentElement;
    private final String refAtr = "ref";
    private final String idAtr = "id";
    private final String resolvedAtr = "resolved";

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
            fileEntry.setFile(file);
        }
        return fileEntry;
    }



    private File resolveReferences(File inputFile, ErrorReport errorReport, Logger log) {

        File outputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_RESOLVED.xml");
        try {

            refObject = loadRefs(inputFile);
            idObject = loadIds(inputFile);
            resolveReferencesInBatch(inputFile, outputFile, errorReport, log);

        } catch (FileNotFoundException fileNotFoundException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + fileNotFoundException.getMessage(),
                    errorReport, log);
        } catch (XMLStreamException xmlStreamException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + xmlStreamException.getMessage(),
                    errorReport, log);
        } catch (Exception exception) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + exception.getMessage(),
                    errorReport, log);
        }

        return outputFile;
    }


    private List<String> loadRefs(File inputFile) throws XMLStreamException, FileNotFoundException {
        List<String> refsList = new ArrayList<String>();
        XMLEventReader xmlr = XMLInputFactory.newInstance().createXMLEventReader(inputFile.getName(), new FileInputStream(inputFile.getAbsolutePath()));
        XMLEvent event;

        while (xmlr != null && xmlr.hasNext() && refsList.size() < resolveInEachParse) {
            event = xmlr.nextEvent();
            if (event.getEventType() == XMLEvent.START_ELEMENT) {
                Attribute ref = getAttribute(event, refAtr);
                Attribute refResolved = getAttribute(event, resolvedAtr);
                if (ref != null && refResolved == null) {
                    refsList.add(ref.getValue());
                }
            }
        }
        xmlr.close();
        return refsList;
    }

    private Map<String, String> loadIds(File inputFile) throws XMLStreamException, FileNotFoundException {
        Map<String, String> idsMap = new HashMap<String, String>();
        XMLEventReader xmlr = XMLInputFactory.newInstance().createXMLEventReader(inputFile.getName(), new FileInputStream(inputFile));
        XMLEvent event;

        while (xmlr != null && xmlr.hasNext() && idsMap.size() < resolveInEachParse) {
            event = xmlr.nextEvent();
            if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                Attribute id = getAttribute(event, idAtr);
                if (id != null && refObject.contains(id.getValue())  && !idObject.containsKey(id.getValue())) {
                   idsMap.put(id.getValue(), getContent(event, xmlr));
                }
            }
        }
        xmlr.close();
        return idsMap;
    }



    private void resolveReferencesInBatch(File inputFile, File outputFile, ErrorReport errorReport, Logger log) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLEventReader xmlr = xmlif.createXMLEventReader(inputFile.getName(), new FileInputStream(inputFile.getAbsolutePath()));
        XMLEventWriter xmlw = XMLOutputFactory.newInstance().createXMLEventWriter(new FileOutputStream(outputFile.getAbsolutePath()));
        XMLEvent event;
        ReferenceResolutionStrategy resolutionStrategy;

        String referenceReplaced = "";
        while (xmlr != null && xmlr.hasNext()) {
            event = xmlr.nextEvent();

            if (event.getEventType() == XMLEvent.START_ELEMENT) {

                String elementName = event.asStartElement().getName().getLocalPart();
                parentStack.push(elementName);

                if (elementName.startsWith("Interchange")) {
                    interchangeName = elementName;
                    xmlw.add(event);
                    continue;
                }

                Attribute ref = getAttribute(event, refAtr);
                if (ref == null) {
                    xmlw.add(event);
                    continue;
                }

                Attribute resolvedAttr = getAttribute(event, resolvedAtr);
                String refValue = ref.getValue();

                if (resolvedAttr == null && idObject.containsKey(refValue)) {
                        if (strategyMap.containsKey(elementName)) {
                            parentElement = parentStack.elementAt((parentStack.size() - 2));
                            resolutionStrategy = applicationContext.getBean(strategyMap.get(elementName), ReferenceResolutionStrategy.class);
                            String resolvedReference = resolutionStrategy.resolveReference(elementName, refValue, parentElement, idObject.get(refValue), interchangeName);
                            boolean contentReplaced = writeResolvedReference(event, xmlw, resolvedReference);
                            if (contentReplaced) {
                                referenceReplaced = elementName;
                            }
                    } else {
                        xmlw.add(event);
                        xmlw.add(XMLEventFactory.newInstance().createAttribute(resolvedAtr, "false"));
                        logError(elementName + " has not been configured to use a reference resolution strategy.", errorReport, log);
                    }
                } else {
                    xmlw.add(event);
                }
            } else if (event.getEventType() == XMLEvent.END_ELEMENT) {
                parentStack.pop();
                if (referenceReplaced.isEmpty()) {
                    xmlw.add(event);
                } else if (referenceReplaced.equals(event.asEndElement().getName().getLocalPart())) {
                    referenceReplaced = "";
                }
            } else {
                if (referenceReplaced.isEmpty()) {
                    xmlw.add(event);
                }
            }
        }
        xmlr.close();
        xmlw.close();
    }

    private int countReferences(File file, ErrorReport errorReport, Logger log) {
        int count = 0;
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xif.createXMLEventReader(new FileInputStream(file));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.getEventType() == XMLEvent.START_ELEMENT) {
                    Attribute refAttribute = getAttribute(xmlEvent, refAtr);
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

    private String getContent(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {

        StringWriter sw = new StringWriter(1024);
        XMLEventWriter xmlw = XMLOutputFactory.newInstance().createXMLEventWriter(sw);
        StringBuilder sb = new StringBuilder();
        Stack<XMLEvent> events = new Stack<XMLEvent>();
        events.add(xmlEvent);

        xmlw.add(xmlEvent);
        while (eventReader.hasNext() && !events.isEmpty()) {
            XMLEvent tmp = eventReader.nextEvent();

            if (tmp.isStartElement()) {
                events.add(tmp);
            }

            xmlw.add(tmp);
            if (tmp.isEndElement()) {
                XMLEvent top = events.peek();

                if (tmp.asEndElement().getName().equals(top.asStartElement().getName())) {
                    events.pop();
                } else {
                    throw new XMLStreamException("Unexpected end of the element");
                }
            }
        }
        return sw.getBuffer().toString();
    }

    private boolean writeResolvedReference(XMLEvent event, XMLEventWriter xmlw, String resolvedReference) throws XMLStreamException {

        boolean contentReplaced = false;
        if (resolvedReference == null) {
            xmlw.add(event);
            xmlw.add(XMLEventFactory.newInstance().createAttribute(resolvedAtr, "false"));
        } else {
            XMLEventReader xmlResolvedReader = XMLInputFactory.newInstance().createXMLEventReader(new ByteArrayInputStream(resolvedReference.getBytes()));
            XMLEvent resolvedEvent;
            boolean isFirst = true;
            contentReplaced = true;
            while (xmlResolvedReader != null && xmlResolvedReader.hasNext()) {
                resolvedEvent = xmlResolvedReader.nextEvent();
                if (resolvedEvent.isStartElement()) {
                    xmlw.add(resolvedEvent);
                    if (isFirst) {
                        xmlw.add(XMLEventFactory.newInstance().createAttribute(resolvedAtr, "true"));
                        isFirst = false;
                    }
                } else if (resolvedEvent.isStartDocument() || resolvedEvent.isEndDocument()) {
                    continue;
                } else {
                    xmlw.add(resolvedEvent);
                }
            }
            xmlResolvedReader.close();
        }
        return contentReplaced;
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

    public int getResolveInEachParse() {
        return resolveInEachParse;
    }

    public void setResolveInEachParse(int resolveInEachParse) {
        this.resolveInEachParse = resolveInEachParse;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
