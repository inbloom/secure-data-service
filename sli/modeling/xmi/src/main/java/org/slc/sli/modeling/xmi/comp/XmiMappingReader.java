/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.modeling.xmi.comp;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.slc.sli.modeling.xml.XMLStreamReaderTools;

/**
 * Read an XMI file.
 */
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
        IOUtils.closeQuietly(closeable);
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
            throw new XmiCompRuntimeException(e);
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
                        dm = assertNotNull(readMappingList(reader));
                        return dm;
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
    
    private static final String readStringContent(final QName elementName, final XMLStreamReader reader)
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
    
    private static final boolean readBooleanContent(final QName elementName, final XMLStreamReader reader,
            final boolean defaultValue) throws XMLStreamException {
        final String s = readStringContent(elementName, reader);
        return (s != null && s.length() > 0) ? Boolean.valueOf(s) : defaultValue;
    }
    
    private static final XmiFeature readFeature(final QName elementName, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(elementName, reader);
        String className = null;
        String name = null;
        boolean exists = true;
        boolean classExists = true;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.NAME, reader)) {
                        name = readStringContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.OWNER_NAME, reader)) {
                        className = readStringContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.EXISTS, reader)) {
                        exists = readBooleanContent(reader.getName(), reader, true);
                    } else if (match(XmiMappingConstants.OWNER_EXISTS, reader)) {
                        classExists = readBooleanContent(reader.getName(), reader, true);
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
        return new XmiFeature(name, exists, className, classExists);
    }
    
    private static final XmiMapping readMapping(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(XmiMappingConstants.MAPPING, reader);
        XmiFeature lhs = null;
        XmiFeature rhs = null;
        XmiMappingStatus status = null;
        String comment = null;
        String tracking = "";
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiMappingConstants.LHS_FEATURE, reader)) {
                        if (lhs == null) {
                            lhs = readFeature(reader.getName(), reader);
                        } else {
                            throw new XMLStreamException("duplicate " + XmiMappingConstants.LHS_FEATURE + " tag.");
                        }
                    } else if (match(XmiMappingConstants.RHS_FEATURE, reader)) {
                        rhs = readFeature(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.STATUS, reader)) {
                        status = readStatus(reader);
                    } else if (match(XmiMappingConstants.COMMENT, reader)) {
                        comment = readStringContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.TRACKING, reader)) {
                        tracking = readStringContent(reader.getName(), reader);
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
        return new XmiMapping(lhs, rhs, status, tracking, comment);
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
                        name = readStringContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.VERSION, reader)) {
                        version = readStringContent(reader.getName(), reader);
                    } else if (match(XmiMappingConstants.FILE, reader)) {
                        xmi = readStringContent(reader.getName(), reader);
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
        if (XmiMappingValues.STATUS_MATCH.equals(value)) {
            return XmiMappingStatus.MATCH;
        } else if (XmiMappingValues.STATUS_IGNORABLE.equals(value)) {
            return XmiMappingStatus.IGNORABLE;
        } else if (XmiMappingValues.STATUS_ALIGN.equals(value)) {
            return XmiMappingStatus.ALIGN;
        } else if (XmiMappingValues.STATUS_UNKNOWN.equals(value)) {
            return XmiMappingStatus.UNKNOWN;
        } else if (XmiMappingValues.STATUS_TRANSIENT.equals(value)) {
            return XmiMappingStatus.TRANSIENT;
        } else if (XmiMappingValues.STATUS_BUG.equals(value)) {
            return XmiMappingStatus.BUG;
        } else if (XmiMappingValues.STATUS_FINANCIAL.equals(value)) {
            return XmiMappingStatus.FINANCIAL;
        } else {
            throw new AssertionError(XmiMappingConstants.STATUS + " : " + value);
        }
    }
    
    private static final <T> T validateNotNull(final T obj, final String msg) {
        if (obj != null) {
            return obj;
        } else {
            throw new XmiCompRuntimeException(msg);
        }
    }
    
}
