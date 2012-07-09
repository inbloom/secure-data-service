package org.slc.sli.modeling.xmicomp;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xml.XMLStreamReaderTools;

public final class XmiMappingReader {
    
    /**
     * A programmatic assertion that we have the reader positioned on the
     * correct element.
     * 
     * @param expectLocalName
     *            The local name that we expect.
     * @param reader
     *            The reader.
     */
    private static final void assertName(final QName name, final XMLStreamReader reader) {
        if (!match(name, reader)) {
            throw new AssertionError(reader.getLocalName());
        }
    }
    
    private static final <T> T assertNotNull(final T obj) {
        if (obj != null) {
            return obj;
        } else {
            throw new AssertionError();
        }
    }
    
    private static final void assertStartDocument(final XMLStreamReader reader) {
        if (XMLStreamConstants.START_DOCUMENT != reader.getEventType()) {
            throw new AssertionError(reader.getLocalName());
        }
    }
    
    private static final void assertStartElement(final XMLStreamReader reader) {
        if (XMLStreamConstants.START_ELEMENT != reader.getEventType()) {
            throw new AssertionError(reader.getLocalName());
        }
    }
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final boolean match(final QName name, final XMLStreamReader reader) {
        return name.getLocalPart().equals(reader.getLocalName());
    }
    
    public static final XmiComparison readDocument(final File file) throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(file));
        try {
            return readDocument(istream);
        } finally {
            closeQuiet(istream);
        }
    }
    
    /**
     * Reads XMI from an {@link InputStream}.
     * 
     * @param stream
     *            The {@link InputStream}.
     * @return The parsed {@link Model}.
     */
    public static final XmiComparison readDocument(final InputStream stream) {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                return readDocument(reader);
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static final XmiComparison readDocument(final XMLStreamReader reader) throws XMLStreamException {
        assertStartDocument(reader);
        XmiComparison dm = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.DOCUMENT_ELEMENT, reader)) {
                        return dm = assertNotNull(readMappingList(reader));
                    } else {
                        XMLStreamReaderTools.skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_DOCUMENT: {
                    return validateNotNull(dm, "Missing root element: " + XmiMappingConstants.DOCUMENT_ELEMENT);
                }
                case XMLStreamConstants.PROCESSING_INSTRUCTION: {
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }
    
    private static final String readElementContent(final QName elementName, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(elementName, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(elementName, reader);
                    done = true;
                    break;
                }
                case XMLStreamConstants.CHARACTERS: {
                    sb.append(reader.getText());
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        return sb.toString().trim();
    }
    
    private static final XmiFeature readFeature(final QName elementName, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(elementName, reader);
        String name = null;
        String type = null;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.NAME, reader)) {
                        name = readElementContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.TYPE, reader)) {
                        type = readElementContent(reader.getName(), reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(elementName, reader);
                    done = true;
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        return new XmiFeature(name, type);
    }
    
    private static final XmiMapping readMapping(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(XmiMappingConstants.MAPPING, reader);
        XmiFeature lhs = null;
        XmiFeature rhs = null;
        XmiMappingStatus status = null;
        String comment = null;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.LHS_FEATURE, reader)) {
                        lhs = readFeature(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.RHS_FEATURE, reader)) {
                        rhs = readFeature(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.STATUS, reader)) {
                        status = readStatus(reader);
                    } else if (match(XmiMappingConstants.COMMENT, reader)) {
                        comment = readElementContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.LHS_MISSING, reader)) {
                        XMLStreamReaderTools.skipElement(reader);
                    } else if (match(XmiMappingConstants.RHS_MISSING, reader)) {
                        XMLStreamReaderTools.skipElement(reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiMappingConstants.MAPPING, reader);
                    done = true;
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        return new XmiMapping(lhs, rhs, status, comment);
    }
    
    public static final XmiComparison readMappingList(final String fileName) throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readDocument(istream);
        } finally {
            closeQuiet(istream);
        }
    }
    
    private static final XmiComparison readMappingList(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(XmiMappingConstants.DOCUMENT_ELEMENT, reader);
        XmiDefinition lhsModel = null;
        XmiDefinition rhsModel = null;
        final List<XmiMapping> mappingList = new LinkedList<XmiMapping>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.MAPPING, reader)) {
                        mappingList.add(assertNotNull(readMapping(reader)));
                    } else if (match(XmiMappingConstants.LHS_MODEL, reader)) {
                        lhsModel = assertNotNull(readModel(reader));
                    } else if (match(XmiMappingConstants.RHS_MODEL, reader)) {
                        rhsModel = assertNotNull(readModel(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiMappingConstants.DOCUMENT_ELEMENT, reader);
                    return new XmiComparison(lhsModel, rhsModel, mappingList);
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }
    
    private static final XmiDefinition readModel(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        final QName elementName = reader.getName();
        assertName(elementName, reader);
        String name = null;
        String version = null;
        String xmi = null;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.NAME, reader)) {
                        name = readElementContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.VERSION, reader)) {
                        version = readElementContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.FILE, reader)) {
                        xmi = readElementContent(reader.getName(), reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(elementName, reader);
                    done = true;
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        return new XmiDefinition(name, version, xmi);
    }
    
    private static final XmiMappingStatus readStatus(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(XmiMappingConstants.STATUS, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiMappingConstants.STATUS, reader);
                    done = true;
                    break;
                }
                case XMLStreamConstants.CHARACTERS: {
                    sb.append(reader.getText());
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        // Intentionally make the coupling loose between the serialization and
        // the enumeration.
        final String value = sb.toString().trim();
        if ("match".equals(value)) {
            return XmiMappingStatus.MATCH;
        }
        else if ("ignorable".equals(value)) {
            return XmiMappingStatus.IGNORABLE;
        }
        else if ("align".equals(value)) {
            return XmiMappingStatus.ALIGN;
        }
        else if ("unknown".equals(value)) {
            return XmiMappingStatus.UNKNOWN;
        }
        else if ("transient".equals(value)) {
            return XmiMappingStatus.TRANSIENT;
        }
        else {
            throw new AssertionError(XmiMappingConstants.STATUS + " : " + value);
        }
    }
    
    private static final <T> T validateNotNull(final T obj, final String msg) {
        if (obj != null) {
            return obj;
        } else {
            throw new RuntimeException(msg);
        }
    }
    
}
