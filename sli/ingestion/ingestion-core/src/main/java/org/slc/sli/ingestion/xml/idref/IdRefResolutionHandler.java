/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.xml.idref;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.cache.BucketCache;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.referenceresolution.ReferenceResolutionStrategy;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.CoreMessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.util.FileUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * @author okrook
 *
 */
public class IdRefResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {
    public static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionHandler.class);

    private static final QName ID_ATTR = new QName("id");
    private static final QName REF_ATTR = new QName("ref");
    private static final QName REF_RESOLVED_ATTR = new QName("refResolved");

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newInstance();
    private static final XMLEventFactory EVENT_FACTORY = XMLEventFactory.newInstance();

    private static final String ENCODING = "UTF-8";

    private Map<String, ReferenceResolutionStrategy> supportedResolvers;
    private Set<String> idReferenceInterchanges;

    private String namespace;
    private String batchJobId;
    private int passCount;

    @Autowired
    private BucketCache bucketCache;

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        return null;
    }

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry fileEntry, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {

        if (!idReferenceInterchanges.contains(fileEntry.getFileType().getName())) {
            LOG.info("Not resolving id-references for file: {} (type: {})", fileEntry.getFileName(), fileEntry
                    .getFileType().getName());
            return fileEntry;
        }
        batchJobId = fileEntry.getBatchJobId();

        File file = fileEntry.getFile();

        file = process(file, report, reportStats);

        fileEntry.setFile(file);

        return fileEntry;
    }

    protected File process(File xml, AbstractMessageReport report, ReportStats reportStats) {
        bucketCache.flushBucket(namespace);
        namespace = batchJobId + "_" + xml.getName() + "_pass_" + (++passCount);

        StopWatch sw = new StopWatch("Processing " + xml.getName());

        sw.start("Find IDRefs to resolve");
        Set<String> referencedIds = findIDRefsToResolve(xml, report, reportStats);
        sw.stop();

        if (!referencedIds.isEmpty()) {

            File semiResolvedXml = null;
            sw.start("Find matching entities");
            findAndCacheEntityXmlForIds(referencedIds, xml, report, reportStats);
            sw.stop();

            sw.start("Resolve IDRefs");
            semiResolvedXml = resolveIdRefs(xml, report, reportStats);
            sw.stop();

            if (LOG.isDebugEnabled()) {
                LOG.debug("ID Ref time {}", sw.prettyPrint());
            } else {
                LOG.info("ID Ref time {}", sw.shortSummary());
            }

            if (semiResolvedXml != null) {
                FileUtils.renameFile(semiResolvedXml, xml);
                return process(xml, report, reportStats);
            }
        }
        return xml;
    }

    protected Set<String> findIDRefsToResolve(final File xml, final AbstractMessageReport report,
            final ReportStats reportStats) {
        final Set<String> idRefs = new HashSet<String>();

        XmlEventVisitor collectIdRefsToResolve = new XmlEventVisitor() {
            private Stack<StartElement> parents = new Stack<StartElement>();
            private String currentXPath;

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {

                // TODO: only support xpaths we have configuration for
                if (xmlEvent.isStartElement()) {
                    StartElement start = xmlEvent.asStartElement();

                    parents.push(start);
                    currentXPath = getCurrentXPath(parents);
                    if (!isSupportedRef(currentXPath) && start.getAttributeByName(REF_ATTR) != null
                            && start.getAttributeByName(REF_RESOLVED_ATTR) == null) {
                        if (!isInnerRef(parents)) {
                            report.warning(reportStats, CoreMessageCode.CORE_0020, currentXPath);
                        }
                        return false;
                    }
                    return start.getAttributeByName(REF_RESOLVED_ATTR) == null
                            && start.getAttributeByName(REF_ATTR) != null;
                } else if (xmlEvent.isEndElement()) {
                    parents.pop();
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

        browse(xml, collectIdRefsToResolve, report, reportStats);

        return idRefs;
    }

    protected void findAndCacheEntityXmlForIds(final Set<String> idsToResolve, final File xml,
            final AbstractMessageReport report, final ReportStats reportStats) {

        XmlEventVisitor collectRefContent = new XmlEventVisitor() {

            @Override
            public boolean isSupported(XMLEvent xmlEvent) {

                if (xmlEvent.isStartElement()) {
                    Attribute id = xmlEvent.asStartElement().getAttributeByName(ID_ATTR);

                    if (id != null && idsToResolve.remove(id.getValue())) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                String id = xmlEvent.asStartElement().getAttributeByName(ID_ATTR).getValue();
                String content = getXmlContentForId(xmlEvent, eventReader, report, reportStats);
                bucketCache.addToBucket(namespace, id, new TransformableXmlString(content, false));
            }

            @Override
            public boolean canAcceptMore() {
                return !idsToResolve.isEmpty();
            }
        };
        browse(xml, collectRefContent, report, reportStats);
    }

    private String getXmlContentForId(XMLEvent xmlEvent, XMLEventReader eventReader,
            final AbstractMessageReport report, final ReportStats reportStats) {

        String xmlSnippetString = null;
        XMLEventWriter writer = null;

        try {

            StringWriter stringWriter = new StringWriter();

            writer = OUTPUT_FACTORY.createXMLEventWriter(stringWriter);

            final XMLEventWriter wr = writer;

            XmlEventVisitor writeRefContent = new XmlEventVisitor() {

                private Stack<StartElement> parents = new Stack<StartElement>();
                private Map<String, Integer> parentIds = new HashMap<String, Integer>();

                @Override
                public boolean isSupported(XMLEvent xmlEvent) {
                    return true;
                }

                @Override
                public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                    XMLEvent theXmlEvent = xmlEvent;
                    if (theXmlEvent.isStartElement()) {
                        StartElement start = theXmlEvent.asStartElement();
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

                                theXmlEvent = EVENT_FACTORY.createStartElement(start.getName(), newAttrs.iterator(),
                                        start.getNamespaces());

                                report.warning(reportStats, CoreMessageCode.CORE_0021, ref.getValue());
                            }
                        }
                    } else if (theXmlEvent.isEndElement()) {
                        popParent();
                    }

                    wr.add(theXmlEvent);
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

            // need to force-visit the xmlEvent because the browse method will begin with .next()
            writeRefContent.visit(xmlEvent, eventReader);

            browse(eventReader, writeRefContent);

            xmlSnippetString = stringWriter.toString();

        } catch (XMLStreamException xse) {
            LOG.error("Exception getting xml snippet content in idref", xse);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (XMLStreamException e) {
                    writer = null;
                }
            }
        }

        return xmlSnippetString;
    }

    protected File resolveIdRefs(final File xml, final AbstractMessageReport report, final ReportStats reportStats) {
        File newXml = null;

        BufferedOutputStream out = null;
        XMLEventWriter writer = null;

        try {
            newXml = File.createTempFile("tmp", ".xml", FileUtils.getOrCreateSubDir(xml.getParentFile(), ".idref"));

            out = new BufferedOutputStream(new FileOutputStream(newXml));
            writer = OUTPUT_FACTORY.createXMLEventWriter(out, ENCODING);

            final XMLEventWriter wr = writer;

            XmlEventVisitor replaceRefContent = new XmlEventVisitor() {
                private Stack<StartElement> parents = new Stack<StartElement>();

                @Override
                public boolean isSupported(XMLEvent xmlEvent) {
                    return true;
                }

                @Override
                public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                    XMLEvent theXmlEvent = xmlEvent;
                    String contentToAdd = null;

                    if (theXmlEvent.isStartElement()) {
                        StartElement start = theXmlEvent.asStartElement();
                        parents.push(start);

                        Attribute refResolved = start.getAttributeByName(REF_RESOLVED_ATTR);

                        if (refResolved == null) {

                            Attribute ref = start.getAttributeByName(REF_ATTR);

                            if (ref != null) {

                                @SuppressWarnings("unchecked")
                                Iterator<Attribute> attrs = start.getAttributes();
                                List<Attribute> newAttrs = new ArrayList<Attribute>();

                                while (attrs.hasNext()) {
                                    newAttrs.add(attrs.next());
                                }

                                Object cacheLookupObject = bucketCache.getFromBucket(namespace, ref.getValue());

                                if (cacheLookupObject instanceof TransformableXmlString) {
                                    Attribute id = start.getAttributeByName(ID_ATTR);

                                    if (id != null && id.getValue().equals(ref.getValue())) {
                                        newAttrs.add(EVENT_FACTORY.createAttribute(REF_RESOLVED_ATTR, "false"));
                                        report.warning(reportStats, CoreMessageCode.CORE_0022, ref.getValue());
                                    } else {

                                        contentToAdd = resolveRefs(getCurrentXPath(parents),
                                                (TransformableXmlString) cacheLookupObject, ref.getValue(), report,
                                                reportStats);
                                    }
                                } else {
                                    // unable to resolve reference, no matching id for ref
                                    if (isSupportedRef(getCurrentXPath(parents))) {
                                        report.warning(reportStats, CoreMessageCode.CORE_0023, ref.getValue());
                                    }
                                }
                                newAttrs.add(EVENT_FACTORY.createAttribute(REF_RESOLVED_ATTR,
                                        contentToAdd == null ? "false" : "true"));

                                theXmlEvent = EVENT_FACTORY.createStartElement(start.getName(), newAttrs.iterator(),
                                        start.getNamespaces());
                            }
                        }
                    } else if (theXmlEvent.isEndElement()) {
                        parents.pop();
                    }

                    wr.add(theXmlEvent);

                    if (theXmlEvent.isStartDocument()) {
                        XMLEvent newLine = EVENT_FACTORY.createCharacters(NEW_LINE);
                        wr.add(newLine);
                    }

                    if (contentToAdd != null) {
                        addContent(contentToAdd, wr);
                    }
                }

                @Override
                public boolean canAcceptMore() {
                    return true;
                }

                private String resolveRefs(String currentXPath, TransformableXmlString cachedContent, String id,
                        AbstractMessageReport report, ReportStats reportStats) {

                    String transformedContent = cachedContent.string;
                    ReferenceResolutionStrategy rrs = supportedResolvers.get(currentXPath);
                    // At this point, only supported references are ready to be resolved

                    if (!cachedContent.isTransformed) {

                        // Resolved content is not cached yet, so lets transform it and cache
                        // it.
                        transformedContent = rrs.resolve(currentXPath, cachedContent.string);
                        if (transformedContent == null) {
                            report.warning(reportStats, CoreMessageCode.CORE_0024, id);
                        } else {
                            bucketCache
                                    .addToBucket(namespace, id, new TransformableXmlString(transformedContent, true));
                        }
                    }

                    return transformedContent;
                }
            };

            browse(xml, replaceRefContent, report, reportStats);

            writer.flush();

        } catch (Exception e) {
            closeResources(writer, out);

            org.apache.commons.io.FileUtils.deleteQuietly(newXml);
            newXml = null;

            report.error(reportStats, CoreMessageCode.CORE_0025, xml.getName());
        } finally {
            closeResources(writer, out);
        }

        return newXml;
    }

    private void browse(final File xml, XmlEventVisitor browser, AbstractMessageReport report, ReportStats reportStats) {
        BufferedInputStream xmlStream = null;
        XMLEventReader eventReader = null;
        try {
            xmlStream = new BufferedInputStream(new FileInputStream(xml));

            eventReader = INPUT_FACTORY.createXMLEventReader(xmlStream);

            browse(eventReader, browser);

        } catch (Exception e) {
            report.error(reportStats, CoreMessageCode.CORE_0025, xml.getName());
        } finally {
            if (eventReader != null) {
                try {
                    eventReader.close();
                } catch (XMLStreamException e) {
                    eventReader = null;
                }
            }

            IOUtils.closeQuietly(xmlStream);
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

    private void addContent(String contentToAdd, final XMLEventWriter xmlEventWriter) {
        XmlEventVisitor addToXml = new XmlEventVisitor() {

            @Override
            public void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException {
                XMLEvent theXmlEvent = xmlEvent;
                if (theXmlEvent.isStartElement()) {
                    StartElement start = theXmlEvent.asStartElement();

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

                    theXmlEvent = EVENT_FACTORY.createStartElement(start.getName(), newAttrs.iterator(),
                            start.getNamespaces());
                }

                xmlEventWriter.add(theXmlEvent);
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

        try {
            XMLEventReader eventReader = INPUT_FACTORY.createXMLEventReader(new StringReader(contentToAdd));

            browse(eventReader, addToXml);

        } catch (XMLStreamException e) {
            LogUtil.error(LOG, "Exception reading xml stream for idref", e);
        }
    }

    private void closeResources(XMLEventWriter writer, BufferedOutputStream out) {

        if (writer != null) {
            try {
                writer.close();
            } catch (XMLStreamException e) {
                // writer = null; // This is bad in Java, so it's commented out!
                ;  // Do nothing.
            }
        }
        IOUtils.closeQuietly(out);

    }

    String getCurrentXPath(Stack<StartElement> parents) {
        StringBuffer sb = new StringBuffer();
        for (StartElement start : parents) {
            sb.append('/').append(start.getName().getLocalPart());
        }
        return sb.toString();
    }

    boolean isInnerRef(Stack<StartElement> parents) {
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

    boolean isSupportedRef(String xPath) {
        ReferenceResolutionStrategy rrs = supportedResolvers.get(xPath);
        if (rrs != null) {
            return true;
        }
        return false;
    }

    public Map<String, ReferenceResolutionStrategy> getSupportedResolvers() {
        return supportedResolvers;
    }

    public void setSupportedResolvers(Map<String, ReferenceResolutionStrategy> supportedResolvers) {
        this.supportedResolvers = supportedResolvers;
    }

    public void setIdReferenceInterchanges(Set<String> idReferenceInterchanges) {
        this.idReferenceInterchanges = idReferenceInterchanges;
    }

    @SuppressWarnings("serial")
    private static final class TransformableXmlString implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String string;
        private final boolean isTransformed;

        public TransformableXmlString(String string, boolean isTransformed) {
            this.string = string;
            this.isTransformed = isTransformed;
        }

        @Override
        public String toString() {
            return "TransformableXmlString [string=" + string + ", isTransformed=" + isTransformed + "]";
        }
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // TODO Auto-generated method stub
        return null;
    }
}
