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


package org.slc.sli.modeling.docgen;

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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

import static org.slc.sli.modeling.xml.XMLStreamReaderTools.skipElement;
import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

/**
 *
 */
public final class DocumentationReader {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentationReader.class);

    public DocumentationReader() {
        throw new UnsupportedOperationException();
    }

    /**
     * A programmatic assertion that we have the reader positioned on the correct element.
     *
     * @param expectLocalName The local name that we expect.
     * @param reader          The reader.
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
            LOG.warn(e.getMessage());
        }
    }

    private static final String getName(final XMLStreamReader reader) {
        final String value = reader.getAttributeValue("", XmiAttributeName.NAME.getLocalName());
        if (value != null) {
            return value;
        } else {
            throw new AssertionError(XmiAttributeName.NAME.getLocalName());
        }
    }

    private static final boolean match(final QName name, final XMLStreamReader reader) {
        return name.getLocalPart().equals(reader.getLocalName());
    }

    private static final String readDescription(final XMLStreamReader reader) throws XMLStreamException {
        return readElementText(DocumentationElements.DESCRIPTION, reader);
    }

    private static final String readProlog(final XMLStreamReader reader) throws XMLStreamException {
        return readElementText(DocumentationElements.PROLOG, reader);
    }

    private static final String readEpilog(final XMLStreamReader reader) throws XMLStreamException {
        return readElementText(DocumentationElements.EPILOG, reader);
    }

    private static final Diagram readDiagram(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(DocumentationElements.DIAGRAM, reader);
        String title = null;
        String source = null;
        String prolog = "";
        String epilog = "";
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(DocumentationElements.TITLE, reader)) {
                        title = assertNotNull(readTitle(reader));
                    } else if (match(DocumentationElements.SOURCE, reader)) {
                        source = assertNotNull(readSource(reader));
                    } else if (match(DocumentationElements.PROLOG, reader)) {
                        prolog = assertNotNull(readProlog(reader));
                    } else if (match(DocumentationElements.EPILOG, reader)) {
                        epilog = assertNotNull(readEpilog(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(DocumentationElements.DIAGRAM, reader);
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
        assertNotNull(title);
        validateNotNull(source, "Missing source for diagram with title : " + title);
        return new Diagram(title, source, prolog, epilog);
    }

    private static final Documentation<Type> readDocument(final ModelIndex mapper, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartDocument(reader);
        Documentation<Type> dm = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(DocumentationElements.PIM_CFG, reader)) {
                        dm = assertNotNull(readDomains(mapper, reader));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_DOCUMENT: {
                    return validateNotNull(dm, "Missing root element: " + DocumentationElements.PIM_CFG);
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    /**
     * Reads XMI from an {@link InputStream}.
     *
     * @param stream The {@link InputStream}.
     * @return The parsed {@link Model}.
     */
    public static final Documentation<Type> readDocumentation(final InputStream stream, final ModelIndex mapper) {
        if (stream == null) {
            throw new IllegalArgumentException("stream");
        }
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                final Documentation<Type> model = readDocument(mapper, reader);
                return model;
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new DocumentGeneratorRuntimeException(e);
        }
    }

    public static final Documentation<Type> readDocumentation(final File file, final ModelIndex mapper)
            throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("file");
        }
        final InputStream istream = new BufferedInputStream(new FileInputStream(file));
        try {
            return readDocumentation(istream, mapper);
        } finally {
            closeQuiet(istream);
        }
    }

    public static final Documentation<Type> readDocumentation(final String fileName, final ModelIndex mapper)
            throws FileNotFoundException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName");
        }
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readDocumentation(istream, mapper);
        } finally {
            closeQuiet(istream);
        }
    }

    private static final Domain<Type> readDomain(final ModelIndex mapper, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(DocumentationElements.DOMAIN, reader);
        String title = null;
        String description = "";
        final List<Entity<Type>> entities = new LinkedList<Entity<Type>>();
        final List<Diagram> diagrams = new LinkedList<Diagram>();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(DocumentationElements.TITLE, reader)) {
                        title = assertNotNull(readTitle(reader));
                    } else if (match(DocumentationElements.DESCRIPTION, reader)) {
                        description = assertNotNull(readDescription(reader));
                    } else if (match(DocumentationElements.ENTITY, reader)) {
                        entities.add(assertNotNull(readEntity(mapper, reader)));
                    } else if (match(DocumentationElements.DIAGRAM, reader)) {
                        diagrams.add(assertNotNull(readDiagram(reader)));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(DocumentationElements.DOMAIN, reader);
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
        return new Domain<Type>(title, description, entities, diagrams);
    }

    private static final Documentation<Type> readDomains(final ModelIndex mapper, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(DocumentationElements.PIM_CFG, reader);
        final List<Domain<Type>> domains = new LinkedList<Domain<Type>>();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(DocumentationElements.DOMAIN, reader)) {
                        domains.add(assertNotNull(readDomain(mapper, reader)));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(DocumentationElements.PIM_CFG, reader);
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
        return new Documentation<Type>(domains);
    }

    private static final String readElementText(final QName name, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(name, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    sb.append(readElementText(reader.getName(), reader));
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(name, reader);
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
        return collapseWhitespace(sb.toString());
    }

    private static final Entity<Type> readEntity(final ModelIndex mapper, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(DocumentationElements.ENTITY, reader);
        String title = null;
        Type type = null;
        final List<Diagram> diagrams = new LinkedList<Diagram>();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(DocumentationElements.TITLE, reader)) {
                        title = assertNotNull(readTitle(reader));
                    } else if (match(DocumentationElements.CLASS, reader)) {
                        final String className = readClassName(DocumentationElements.CLASS, reader);
                        @SuppressWarnings("deprecation")
                        final Set<ModelElement> elements = mapper.lookupByName(new QName(className));
                        type = assertNotNull(resolveClass(elements, className));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(DocumentationElements.ENTITY, reader);
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
        assertNotNull(title);
        validateNotNull(type, "Missing reference for entity with title : " + title);
        return new Entity<Type>(title, type, diagrams);
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

    private static final String readSource(final XMLStreamReader reader) throws XMLStreamException {
        return readElementText(DocumentationElements.SOURCE, reader);
    }

    private static final String readTitle(final XMLStreamReader reader) throws XMLStreamException {
        return readElementText(DocumentationElements.TITLE, reader);
    }

    private static final <T> T validateNotNull(final T obj, final String msg) {
        if (obj != null) {
            return obj;
        } else {
            throw new DocumentGeneratorRuntimeException(msg);
        }
    }
}
