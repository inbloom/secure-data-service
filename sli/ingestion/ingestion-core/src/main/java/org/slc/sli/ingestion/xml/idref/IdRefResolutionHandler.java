package org.slc.sli.ingestion.xml.idref;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.StopWatch;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.referenceresolution.ReferenceResolutionStrategy;
import org.slc.sli.ingestion.util.FileUtils;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * @author okrook
 *
 */
public class IdRefResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> implements
        MessageSourceAware {
    public static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionHandler.class);

    private static final QName ID_ATTR = new QName("id");
    private static final QName REF_ATTR = new QName("ref");
    private static final QName REF_RESOLVED_ATTR = new QName("refResolved");

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newInstance();
    private static final XMLEventFactory EVENT_FACTORY = XMLEventFactory.newInstance();

    private Map<String, ReferenceResolutionStrategy> supportedResolvers;
    private MessageSource messageSource;

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        File file = fileEntry.getFile();

        file = process(file, errorReport);

        fileEntry.setFile(file);

        return fileEntry;
    }

    protected File process(File xml, ErrorReport errorReport) {
        StopWatch sw = new StopWatch("Processing " + xml.getName());

        sw.start("Find IDRefs to resolve");
        Set<String> idRefsToResolve = findIDRefsToResolve(xml, errorReport);
        sw.stop();

        if (idRefsToResolve.isEmpty()) {
            return xml;
        }

        SnippetFile refContent = null;
        File semiResolvedXml = null;

        try {
            sw.start("Find matching entities");
            refContent = findMatchingEntities(xml, idRefsToResolve, errorReport);
            sw.stop();

            sw.start("Resolve IDRefs");
            semiResolvedXml = resolveIdRefs(xml, refContent, errorReport);
            sw.stop();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (refContent != null) {
                refContent.delete();
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("ID Ref time {}", sw.prettyPrint());
        } else {
            LOG.info("ID Ref time {}", sw.shortSummary());
        }

        if (semiResolvedXml == null) {
            return xml;
        } else {
            FileUtils.renameFile(semiResolvedXml, xml);
            return process(xml, errorReport);
        }
    }

    protected Set<String> findIDRefsToResolve(final File xml, ErrorReport errorReport) {
        final Set<String> idRefs = new HashSet<String>();

        XmlEventVisitor collectIdRefsToResolve = new XmlEventVisitor() {

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

                idRefs.add(ref.getValue());
            }

            @Override
            public boolean canAcceptMore() {
                return true;
            }

        };

        browse(xml, collectIdRefsToResolve, errorReport);

        return idRefs;
    }

    protected SnippetFile findMatchingEntities(final File xml, final Set<String> ids, final ErrorReport errorReport)
            throws IOException {

        final SnippetFile snippets = SnippetFile.create("snippet", ".xml", xml.getParentFile());

        XmlEventVisitor collectRefContent = new XmlEventVisitor() {
            private int idsToProcess = ids.size();

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {
                if (xmlEvent.isStartElement()) {
                    StartElement start = xmlEvent.asStartElement();

                    Attribute id = start.getAttributeByName(ID_ATTR);

                    return id != null && ids.contains(id.getValue()) && !snippets.contains(id.getValue());
                }

                return false;
            }

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                StartElement start = xmlEvent.asStartElement();

                Attribute id = start.getAttributeByName(ID_ATTR);

                try {
                    OutputStream os = snippets.allocateOutputStream(id.getValue());

                    writeContent(os, xmlEvent, eventReader, errorReport);

                    snippets.commitSnippet(id.getValue());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                idsToProcess--;
            }

            @Override
            public boolean canAcceptMore() {
                return idsToProcess > 0;
            }

        };

        browse(xml, collectRefContent, errorReport);

        return snippets;
    }

    protected File resolveIdRefs(final File xml, final SnippetFile snippets, final ErrorReport errorReport) {
        File newXml = null;

        BufferedOutputStream out = null;
        XMLEventWriter writer = null;
        SnippetFile smooksSnippets = null;

        try {
            final SnippetFile smooks = SnippetFile.create("smooks", ".xml", xml.getParentFile());
            smooksSnippets = smooks;

            newXml = File.createTempFile("tmp", ".xml", xml.getParentFile());

            out = new BufferedOutputStream(new FileOutputStream(newXml));

            writer = OUTPUT_FACTORY.createXMLEventWriter(out);
            final XMLEventWriter wr = writer;

            XmlEventVisitor replaceRefContent = new XmlEventVisitor() {
                Stack<StartElement> parents = new Stack<StartElement>();

                @Override
                public boolean isSupported(XMLEvent xmlEvent) {
                    return true;
                }

                @Override
                public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {

                    InputStream contentToAdd = null;

                    if (xmlEvent.isStartElement()) {
                        StartElement start = xmlEvent.asStartElement();
                        parents.push(start);

                        Attribute refResolved = start.getAttributeByName(REF_RESOLVED_ATTR);
                        Attribute ref = start.getAttributeByName(REF_ATTR);

                        if (refResolved == null && ref != null) {
                            Attribute id = start.getAttributeByName(ID_ATTR);

                            @SuppressWarnings("unchecked")
                            Iterator<Attribute> attrs = start.getAttributes();
                            ArrayList<Attribute> newAttrs = new ArrayList<Attribute>();

                            while (attrs.hasNext()) {
                                newAttrs.add(attrs.next());
                            }

                            String resolved = "false";

                                if (id != null && id.getValue().equals(ref.getValue())) {
                                    errorReport.warning(
                                            MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG4",
                                                    ref.getValue()), IdRefResolutionHandler.class);
                                } else {
                                    try {
                                        contentToAdd = resolveRefs(getCurrentXPath(), snippets, ref, errorReport);

                                        resolved = contentToAdd != null ? "true" : "false";
                                    } catch (IOException e) {
                                        resolved = "false";
                                    }
                                }

                            newAttrs.add(EVENT_FACTORY.createAttribute(REF_RESOLVED_ATTR, resolved));
                            xmlEvent = EVENT_FACTORY.createStartElement(start.getName(), newAttrs.iterator(),
                                    start.getNamespaces());
                        }
                    } else if (xmlEvent.isEndElement()) {
                        parents.pop();
                    }

                    wr.add(xmlEvent);

                    if (xmlEvent.isStartDocument()) {
                        XMLEvent newLine = EVENT_FACTORY.createCharacters(NEW_LINE);
                        wr.add(newLine);
                    }

                    if (contentToAdd != null) {
                        addContent(contentToAdd, wr, errorReport);
                    }
                }

                @Override
                public boolean canAcceptMore() {
                    return true;
                }

                private String getCurrentXPath() {
                    StringBuffer sb = new StringBuffer();

                    for (StartElement start : parents) {
                        sb.append('/').append(start.getName().getLocalPart());
                    }

                    return sb.toString();
                }

                private boolean isInnerRef() {
                    for (StartElement start : parents) {
                        Attribute attValue = start.getAttributeByName(REF_RESOLVED_ATTR);
                        if (attValue != null) {
                            boolean refResolved = Boolean.parseBoolean(attValue.getValue());
                            if (refResolved) {
                                return true;
                            }
                        }

                    }
                    return false;
                }

                private InputStream resolveRefs(String currentXPath, SnippetFile snippets, Attribute ref,
                        ErrorReport errorReport) throws IOException {

                    InputStream is = snippets.get(ref.getValue());

                    if (is != null) {
                        ReferenceResolutionStrategy rrs = supportedResolvers.get(currentXPath);
                        if (rrs == null) {
                            if (!isInnerRef()) {
                                LOG.debug(MessageSourceHelper
                                        .getMessage(messageSource, "IDREF_WRNG_MSG2", currentXPath));
                                errorReport.warning(
                                        MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG2", currentXPath),
                                        IdRefResolutionHandler.class);
                            }
                        } else if (!smooks.contains(ref.getValue())) {

                            // Resolved content is not cached yet, so lets resolve it and cache it.

                            rrs.resolve(currentXPath, is, smooks.allocateOutputStream(ref.getValue()));

                            smooks.commitSnippet(ref.getValue());

                            if (smooks.get(ref.getValue()) == null) {
                                LOG.debug(MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG1",
                                        ref.getValue()));
                                errorReport.warning(MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG1",
                                        ref.getValue()), IdRefResolutionHandler.class);
                            }
                        }
                    } else {
                        LOG.debug(MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG3"));
                        errorReport.warning(
                                MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG3", ref.getValue()),
                                IdRefResolutionHandler.class);
                    }

                    return smooks.get(ref.getValue());
                }
            };

            browse(xml, replaceRefContent, errorReport);

            writer.flush();
        } catch (Exception e) {
            closeResources(writer);

            IOUtils.closeQuietly(out);

            org.apache.commons.io.FileUtils.deleteQuietly(newXml);
            newXml = null;

            LOG.debug(MessageSourceHelper.getMessage(messageSource, "IDREF_ERR_MSG1", xml.getName()));
            errorReport.error(MessageSourceHelper.getMessage(messageSource, "IDREF_ERR_MSG1", xml.getName()),
                    IdRefResolutionHandler.class);
        } finally {
            closeResources(writer);
            IOUtils.closeQuietly(out);

            if (smooksSnippets != null) {
                smooksSnippets.delete();
            }
        }

        return newXml;
    }

    private void browse(final File xml, XmlEventVisitor browser, ErrorReport errorReport) {
        BufferedInputStream xmlStream = null;

        try {
            xmlStream = new BufferedInputStream(new FileInputStream(xml));

            browse(xmlStream, browser, errorReport);

        } catch (Exception e) {
            LOG.debug(MessageSourceHelper.getMessage(messageSource, "IDREF_ERR_MSG1", xml.getName()));
            errorReport.error(MessageSourceHelper.getMessage(messageSource, "IDREF_ERR_MSG1", xml.getName()),
                    IdRefResolutionHandler.class);
        } finally {
            IOUtils.closeQuietly(xmlStream);
        }
    }

    private void browse(InputStream is, XmlEventVisitor browser, ErrorReport errorReport) throws XMLStreamException {
        XMLEventReader eventReader = null;
        try {
            eventReader = INPUT_FACTORY.createXMLEventReader(is);

            browse(eventReader, browser);

        } finally {
            if (eventReader != null) {
                try {
                    eventReader.close();
                } catch (XMLStreamException e) {
                    eventReader = null;
                }
            }
        }
    }

    private void browse(XMLEventReader eventReader, XmlEventVisitor browser) throws XMLStreamException {
        while (eventReader.hasNext() && browser.canAcceptMore()) {
            XMLEvent xmlEvent = eventReader.nextEvent();

            if (browser.isSupported(xmlEvent)) {
                browser.visit(xmlEvent, eventReader);
            }
        }
    }

    private void writeContent(OutputStream os, XMLEvent xmlEvent, XMLEventReader eventReader,
            final ErrorReport errorReport) {
        XMLEventWriter writer = null;

        try {
            writer = OUTPUT_FACTORY.createXMLEventWriter(os);

            final XMLEventWriter wr = writer;

            XmlEventVisitor writeRefContent = new XmlEventVisitor() {
                Stack<StartElement> parents = new Stack<StartElement>();
                Map<String, Integer> parentIds = new HashMap<String, Integer>();

                @Override
                public boolean isSupported(XMLEvent xmlEvent) {
                    return true;
                }

                @Override
                public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                    if (xmlEvent.isStartElement()) {
                        StartElement start = xmlEvent.asStartElement();
                        pushParent(start);

                        Attribute refResolved = start.getAttributeByName(REF_RESOLVED_ATTR);

                        if (refResolved == null) {
                            Attribute ref = start.getAttributeByName(REF_ATTR);

                            // Set "ref" as unresolved if circular reference is detected
                            if (ref != null && isParent(ref.getValue())) {
                                @SuppressWarnings("unchecked")
                                Iterator<Attribute> attrs = start.getAttributes();
                                ArrayList<Attribute> newAttrs = new ArrayList<Attribute>();

                                while (attrs.hasNext()) {
                                    newAttrs.add(attrs.next());
                                }

                                newAttrs.add(EVENT_FACTORY.createAttribute(REF_RESOLVED_ATTR, "false"));

                                xmlEvent = EVENT_FACTORY.createStartElement(start.getName(), newAttrs.iterator(),
                                        start.getNamespaces());

                                errorReport.warning(MessageSourceHelper.getMessage(messageSource, "IDREF_WRNG_MSG5",
                                        ref.getValue()), IdRefResolutionHandler.class);
                            }
                        }
                    } else if (xmlEvent.isEndElement()) {
                        popParent();
                    }

                    wr.add(xmlEvent);
                }

                @Override
                public boolean canAcceptMore() {
                    return !parents.isEmpty();
                }

                private void pushParent(StartElement parent) {
                    parents.push(parent);
                    Attribute id = parent.getAttributeByName(ID_ATTR);
                    if (id != null) {
                        Integer count = parentIds.get(id.getValue());
                        if (count == null) {
                            count = 1;
                        } else {
                            count = count++;
                        }

                        parentIds.put(id.getValue(), count);
                    }
                }

                private void popParent() {
                    StartElement parent = parents.pop();
                    Attribute id = parent.getAttributeByName(ID_ATTR);
                    if (id != null) {
                        Integer count = parentIds.get(id.getValue());
                        if (count <= 1) {
                            parentIds.remove(id.getValue());
                        } else {
                            parentIds.put(id.getValue(), count--);
                        }
                    }
                }

                private boolean isParent(String id) {
                    return parentIds.containsKey(id);
                }
            };

            // persist the input event manually
            writeRefContent.visit(xmlEvent, eventReader);

            browse(eventReader, writeRefContent);

            writer.flush();
        } catch (Exception e) {
            closeResources(writer);
        } finally {
            closeResources(writer);
        }
    }

    private void addContent(InputStream contentToAdd, final XMLEventWriter xmlEventWriter, ErrorReport errorReport) throws XMLStreamException {
        XmlEventVisitor addToXml = new XmlEventVisitor() {

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                if (xmlEvent.isStartElement()) {
                    StartElement start = xmlEvent.asStartElement();

                    @SuppressWarnings("unchecked")
                    Iterator<Attribute> attrs = start.getAttributes();
                    ArrayList<Attribute> newAttrs = new ArrayList<Attribute>();

                    // Strip out "id" attribute if found, so it does not interfere with
                    // findMatchingEntities logic
                    while (attrs.hasNext()) {
                        Attribute attribute = attrs.next();
                        if (!ID_ATTR.getLocalPart().equals(attribute.getName().getLocalPart())) {
                            newAttrs.add(attribute);
                        }
                    }

                    xmlEvent = EVENT_FACTORY.createStartElement(start.getName(), newAttrs.iterator(),
                            start.getNamespaces());
                }

                xmlEventWriter.add(xmlEvent);
            }

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {
                return !(xmlEvent.isStartDocument() || xmlEvent.isEndDocument());
            }

            @Override
            public boolean canAcceptMore() {
                return true;
            }
        };

        browse(contentToAdd, addToXml, errorReport);
    }

    private void closeResources(XMLEventWriter writer) {

        if (writer != null) {
            try {
                writer.close();
            } catch (XMLStreamException e) {
                writer = null;
            }
        }
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Map<String, ReferenceResolutionStrategy> getSupportedResolvers() {
        return supportedResolvers;
    }

    public void setSupportedResolvers(Map<String, ReferenceResolutionStrategy> supportedResolvers) {
        this.supportedResolvers = supportedResolvers;
    }
}
