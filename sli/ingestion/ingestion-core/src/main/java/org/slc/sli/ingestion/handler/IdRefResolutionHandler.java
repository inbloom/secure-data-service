package org.slc.sli.ingestion.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.referenceresolution.ReferenceResolutionStrategy;
import org.slc.sli.ingestion.util.XmlEventBrowser;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * @author npandey
 *
 */
public class IdRefResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, String> strategyMap;
    private int resolveInEachParse;

    private static final QName ID_ATTR = new QName("id");
    private static final QName REF_ATTR = new QName("ref");
    private static final QName REF_RESOLVED_ATTR = new QName("resolved");
    private static final String NEW_LINE = System.getProperty("line.separator");

    XMLInputFactory xmlif = XMLInputFactory.newInstance();
    XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        Logger log = LoggerFactory.getLogger(IdRefResolutionHandler.class);
        File file = fileEntry.getFile();
        String fileName = file.getAbsolutePath();
        File outputFile;
        int numOfRefUnResolved = countReferences(file, errorReport, log);

        while (numOfRefUnResolved > 0) {
            outputFile = resolveReferences(file, errorReport, log);
            if (file == null) {
                logError("Error while processing", errorReport, log);
               break;
            }
            file = outputFile;
            numOfRefUnResolved -= resolveInEachParse;
        }

        file.renameTo(new File(fileName));
        if (file != null) {
            fileEntry.setFile(file);
        }
        return fileEntry;
    }



    private File resolveReferences(File inputFile, ErrorReport errorReport, Logger log) {

        File outputFile = null;
        try {
            List<String> refObject = new ArrayList<String>();
            Map<String, String> idObject = new HashMap<String, String>();

            refObject = loadRefs(inputFile);
            idObject = loadIds(inputFile, refObject);
            outputFile = resolveReferencesInBatch(inputFile, idObject, refObject, errorReport, log);

        } catch (FileNotFoundException fileNotFoundException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + fileNotFoundException.getMessage(),
                    errorReport, log);
        } catch (XMLStreamException xmlStreamException) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + xmlStreamException.getMessage(),
                    errorReport, log);
        } catch (Exception exception) {
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + exception.getMessage(),
                    errorReport, log);
            exception.printStackTrace();
        }

        return outputFile;
    }


    private List<String> loadRefs(File inputFile) throws XMLStreamException, FileNotFoundException {
        final List<String> refsList = new ArrayList<String>();

        XmlEventBrowser collectIdRefsToResolve = new XmlEventBrowser() {

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {
                if (xmlEvent.isStartElement()) {
                    StartElement start = xmlEvent.asStartElement();

                    return start.getAttributeByName(REF_RESOLVED_ATTR) == null
                            && start.getAttributeByName(REF_ATTR) != null;
                }

                return false;
            }

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) {
                StartElement start = xmlEvent.asStartElement();

                Attribute ref = start.getAttributeByName(REF_ATTR);

                refsList.add(ref.getValue());
            }

            @Override
            public boolean canAcceptMore() {
                if (refsList.size() == resolveInEachParse) {
                    return false;
                }
                return true;
            }

        };

        browse(inputFile, collectIdRefsToResolve);

        return refsList;
    }

    private Map<String, String> loadIds(File inputFile, final List<String> refList) throws XMLStreamException, FileNotFoundException {
        final Map<String, String> idsMap = new HashMap<String, String>();

        for (String id : refList) {
            idsMap.put(id, "");
        }

        XmlEventBrowser collectRefContent = new XmlEventBrowser() {

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {
                if (xmlEvent.isStartElement()) {
                    StartElement start = xmlEvent.asStartElement();

                    Attribute id = start.getAttributeByName(ID_ATTR);

                    return id != null && refList.contains(id.getValue());
                }

                return false;
            }

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                StartElement start = xmlEvent.asStartElement();

                Attribute id = start.getAttributeByName(ID_ATTR);

                String content = getContent(xmlEvent, eventReader);
                idsMap.put(id.getValue(), content);
            }

            @Override
            public boolean canAcceptMore() {
                return true;
            }

        };

        browse(inputFile, collectRefContent);

        return idsMap;
    }



    private File resolveReferencesInBatch(File inputFile, final Map<String, String> idObject, final List<String> refObject, ErrorReport errorReport, Logger log) throws XMLStreamException, IOException {

        File tmp = File.createTempFile("tmp", ".xml", inputFile.getParentFile());

        BufferedOutputStream out = null;
        XMLEventWriter writer = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(tmp));
            writer = xmlof.createXMLEventWriter(out);
            final Stack<String> parentStack = new Stack<String>();

            final XMLEventWriter wr = writer;

            XmlEventBrowser replaceRefContent = new XmlEventBrowser() {

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {
                return true;
            }

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                String contentToAdd = null;

                String interchangeName = "";
                //String parentElement = "";



                if (xmlEvent.isStartElement()) {
                    StartElement start = xmlEvent.asStartElement();
                    String elementName = start.getName().getLocalPart();

                    parentStack.push(elementName);

                    if (elementName.startsWith("Interchange")) {
                        interchangeName = elementName;
                        wr.add(xmlEvent);
                        return;
                    }

                    Attribute id = start.getAttributeByName(ID_ATTR);
                    Attribute ref = start.getAttributeByName(REF_ATTR);



                    if (ref != null && refObject.contains(ref.getValue())) {

                        wr.add(xmlEvent);

                        if ((id != null && id.getValue().equals(ref.getValue())) || !idObject.containsKey(ref.getValue()) && !idObject.get(ref.getValue()).equals("")) {

                            wr.add(eventFactory.createAttribute(REF_RESOLVED_ATTR, "false"));
                        } else {

                            contentToAdd = idObject.get(ref.getValue());
                            if (contentToAdd != null && !"".equals(contentToAdd)) {
                                if (strategyMap.containsKey(elementName)) {
                                    List<XMLEvent> content = resolveAndWriteReference(xmlEvent, elementName, ref.getValue(), parentStack, contentToAdd, interchangeName);
                                    for (XMLEvent event : content) {
                                        wr.add(event);
                                    }
                            } else {
                                wr.add(xmlEvent);
                                wr.add(XMLEventFactory.newInstance().createAttribute(REF_RESOLVED_ATTR, "false"));
                                //logError(elementName + " has not been configured to use a reference resolution strategy.", errorReport, log);
                            }

                                }
                        }
                    } else {
                        wr.add(xmlEvent);
                    }
                } else if (xmlEvent.isStartDocument()) {
                    XMLEvent newLine = eventFactory.createCharacters(NEW_LINE);
                    wr.add(newLine);
                } else if (xmlEvent.isEndElement()) {
                    parentStack.pop();
                        wr.add(xmlEvent);
                } else {
                        wr.add(xmlEvent);
                    }
            }

            @Override
            public boolean canAcceptMore() {
                return true;
            }
        };

        browse(inputFile, replaceRefContent);
        writer.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }
        inputFile.delete();
        return tmp;
    }

    private int countReferences(File file, ErrorReport errorReport, Logger log) {
        int count = 0;
        try {
            FileInputStream stream = new FileInputStream(file);
            XMLEventReader xmlEventReader = xmlif.createXMLEventReader(stream);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.getEventType() == XMLEvent.START_ELEMENT) {
                    Attribute refAttribute = getAttribute(xmlEvent, REF_ATTR);
                    if (refAttribute != null) {
                        count++;
                    }
                }
            }

            xmlEventReader.close();
            stream.close();
        } catch (FileNotFoundException fileNotFoundException) {
            logError("Error configuring parser for XML file " + file.getName() + ": " + fileNotFoundException.getMessage(),
                    errorReport, log);
            return 0;
        } catch (XMLStreamException xmlStreamException) {
            logError("Error configuring parser for XML file " + file.getName() + ": " + xmlStreamException.getMessage(),
                    errorReport, log);
            return 0;
        } catch (IOException ioException) {
            logError("Error configuring parser for XML file " + file.getName() + ": " + ioException.getMessage(),
                    errorReport, log);
            return 0;
        }
        return count;
    }

    private String getContent(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {

        StringWriter sw = new StringWriter(1024);
        XMLEventWriter xmlw = xmlof.createXMLEventWriter(sw);
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
        xmlw.close();
        return sw.getBuffer().toString();
    }

    private List<XMLEvent> resolveAndWriteReference(XMLEvent xmlEvent, String elementName, String refValue, Stack<String> parentStack, String idContent, String interchangeName) throws XMLStreamException {

        final List<XMLEvent> events = new ArrayList<XMLEvent>();
        ReferenceResolutionStrategy resolutionStrategy;

            String parentElement = parentStack.elementAt((parentStack.size() - 2));
            resolutionStrategy = applicationContext.getBean(strategyMap.get(elementName), ReferenceResolutionStrategy.class);

            String resolvedReference = resolutionStrategy.resolve(interchangeName, parentElement, elementName, idContent);
            if (resolvedReference == null) {
                events.add(XMLEventFactory.newInstance().createAttribute(REF_RESOLVED_ATTR, "false"));
            } else {
                events.add(XMLEventFactory.newInstance().createAttribute(REF_RESOLVED_ATTR, "true"));

                XMLEventReader xmlResolvedReader = xmlif.createXMLEventReader(new StringReader(resolvedReference));


                XmlEventBrowser convertToXml = new XmlEventBrowser() {
                    boolean isFirst = true;

                    @Override
                    public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                        if (xmlEvent.isStartElement()) {
                            StartElement start = xmlEvent.asStartElement();

                            if (isFirst) {
                                @SuppressWarnings("unchecked")
                                Iterator<Attribute> attrs = start.getAttributes();
                                ArrayList<Attribute> newAttrs = new ArrayList<Attribute>();

                                while (attrs.hasNext()) {
                                    Attribute attribute = attrs.next();
                                    String attributeName = attribute.getName().getLocalPart();
                                    if (attribute != null && !attributeName.equals(ID_ATTR.getLocalPart())) {
                                        newAttrs.add(attrs.next());
                                    }

                                }
                                xmlEvent = eventFactory.createStartElement(start.getName(), newAttrs.iterator(), start.getNamespaces());
                                isFirst = false;
                            }

                            events.add(xmlEvent);

                        } else if (xmlEvent.isStartDocument() || xmlEvent.isEndDocument()) {
                            events.add(eventFactory.createCharacters(NEW_LINE));
                        } else {
                            events.add(xmlEvent);
                        }
                    }

                    @Override
                    public boolean isSupported(XMLEvent xmlEvent) {
                        return true;
                    }

                    @Override
                    public boolean canAcceptMore() {
                        return true;
                    }
                };

                browse(xmlResolvedReader, convertToXml);

            }
            return events;
    }

    private void browse(final File xml, XmlEventBrowser browser) {
        BufferedInputStream xmlStream = null;
        XMLEventReader eventReader = null;
        try {
            xmlStream = new BufferedInputStream(new FileInputStream(xml));

            eventReader = xmlif.createXMLEventReader(xmlStream);

            browse(eventReader, browser);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            if (eventReader != null) {
                try {
                    eventReader.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }

            if (xmlStream != null) {
                try {
                    xmlStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void browse(XMLEventReader eventReader, XmlEventBrowser browser) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent xmlEvent = eventReader.nextEvent();

            if (browser.canAcceptMore() && browser.isSupported(xmlEvent)) {
                browser.visit(xmlEvent, eventReader);
            }
        }
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

    private Attribute getAttribute(XMLEvent startElementEvent, QName attributeName) {

        Attribute attribute = startElementEvent.asStartElement().getAttributeByName(attributeName);
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
