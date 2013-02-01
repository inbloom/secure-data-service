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


package org.slc.sli.modeling.tools.xmi2Psm;

import static org.slc.sli.modeling.xml.XMLStreamReaderTools.skipElement;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slc.sli.modeling.psm.*;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read PSM.
 */
public final class PsmConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(PsmConfigReader.class);

    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    public static final PsmConfig<Type> readConfig(final String fileName, final ModelIndex mapper)
            throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readConfig(istream, mapper);
        } finally {
            closeQuiet(istream);
        }
    }

    public static final PsmConfig<Type> readConfig(final File file, final ModelIndex mapper)
            throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(file));
        try {
            return readConfig(istream, mapper);
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
    public static final PsmConfig<Type> readConfig(final InputStream stream, final ModelIndex mapper) {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                final PsmConfig<Type> model = readDocument(mapper, reader);
                return model;
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new PsmConfigRuntimeException(e);
        }
    }

    private static final boolean match(final QName name, final XMLStreamReader reader) {
        return name.getLocalPart().equals(reader.getLocalName());
    }

    private static final PsmConfig<Type> readDocument(final ModelIndex mapper, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartDocument(reader);
        PsmConfig<Type> dm = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                if (match(PsmConfigElements.DOCUMENTS, reader)) {
                    dm = assertNotNull(readConfig(mapper, reader));
                } else {
                    skipElement(reader);
                }
                break;
            }
            case XMLStreamConstants.END_DOCUMENT: {
                return validateNotNull(dm, "Missing root element: " + PsmConfigElements.DOCUMENTS);
            }
            default: {
                throw new AssertionError(reader.getEventType());
            }
            }
        }
        throw new AssertionError();
    }

    private static final PsmConfig<Type> readConfig(final ModelIndex mapper, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.DOCUMENTS, reader);
        final List<PsmDocument<Type>> domains = new LinkedList<PsmDocument<Type>>();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                if (match(PsmConfigElements.DOCUMENT, reader)) {
                    domains.add(assertNotNull(readClassType(mapper, reader)));
                } else {
                    throw new AssertionError(reader.getLocalName());
                }
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                assertName(PsmConfigElements.DOCUMENTS, reader);
                done = true;
                break;
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
        return new PsmConfig<Type>(domains);
    }

    private static final String readClassName(final QName name, final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(name, reader);
        final String className = getName(reader);
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                throw new AssertionError(reader.getLocalName());
            }
            case XMLStreamConstants.END_ELEMENT: {
                assertName(name, reader);
                done = true;
                break;
            }
            case XMLStreamConstants.CHARACTERS: {
                // Ignore.
                break;
            }
            default: {
                throw new AssertionError(reader.getEventType());
            }
            }
        }
        return className;
    }

    private static final PsmCollection readCollection(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.SINGULAR_RESOURCE_NAME, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                throw new AssertionError(reader.getLocalName());
            }
            case XMLStreamConstants.END_ELEMENT: {
                assertName(PsmConfigElements.SINGULAR_RESOURCE_NAME, reader);
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
        return new PsmCollection(sb.toString().trim());
    }

    private static final PsmResource readResource(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.GRAPH_RESOURCE_NAME, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                throw new AssertionError(reader.getLocalName());
            }
            case XMLStreamConstants.END_ELEMENT: {
                assertName(PsmConfigElements.GRAPH_RESOURCE_NAME, reader);
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
        return new PsmResource(sb.toString().trim());
    }

    private static final String getName(final XMLStreamReader reader) {
        final String value = reader.getAttributeValue("", XmiAttributeName.NAME.getLocalName());
        if (value != null) {
            return value;
        } else {
            throw new AssertionError(XmiAttributeName.NAME.getLocalName());
        }
    }

    private static final PsmDocument<Type> readClassType(final ModelIndex modelIndex, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.DOCUMENT, reader);
        ClassType type = null;
        PsmResource graphResource = null;
        PsmCollection collection = null;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                if (match(PsmConfigElements.CLASS_TYPE, reader)) {
                    final String className = readClassName(PsmConfigElements.CLASS_TYPE, reader);
                    @SuppressWarnings("deprecation")
                    final Set<ModelElement> elements = modelIndex.lookupByName(new QName(className));
                    type = assertNotNull(resolveClass(elements, className));
                } else if (match(PsmConfigElements.GRAPH_RESOURCE_NAME, reader)) {
                    graphResource = readResource(reader);
                } else if (match(PsmConfigElements.SINGULAR_RESOURCE_NAME, reader)) {
                    collection = readCollection(reader);
                } else {
                    throw new AssertionError(reader.getLocalName());
                }
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                assertName(PsmConfigElements.DOCUMENT, reader);
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
        return new PsmDocument<Type>(type, graphResource, collection);
    }

    private static final ClassType resolveClass(final Set<ModelElement> elements, final String className) {
        final List<ClassType> classTypes = new LinkedList<ClassType>();
        for (final ModelElement element : elements) {
            if (element instanceof ClassType) {
                final ClassType classType = (ClassType) element;
                classTypes.add(classType);
            }
        }
        if (classTypes.size() == 1) {
            return classTypes.get(0);
        }
        throw new IllegalArgumentException("className : " + className + " is not a valid class name.");
    }

    private static final <T> T assertNotNull(final T obj) {
        if (obj != null) {
            return obj;
        } else {
            throw new AssertionError();
        }
    }

    private static final <T> T validateNotNull(final T obj, final String msg) {
        if (obj != null) {
            return obj;
        } else {
            throw new PsmConfigRuntimeException(msg);
        }
    }

    /**
     * A programmatic assertion that we have the reader positioned on the correct element.
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
}
