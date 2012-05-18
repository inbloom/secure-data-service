package org.slc.sli.modeling.psm;

import static org.slc.sli.modeling.xml.XMLStreamReaderTools.skipElement;

import java.io.BufferedInputStream;
import java.io.Closeable;
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

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.XmiAttributeName;

public final class PsmConfigReader {

    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
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
            throw new RuntimeException(e);
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
        assertName(PsmConfigElements.COLLECTION_NAME, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(PsmConfigElements.COLLECTION_NAME, reader);
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
        return new PsmCollection(sb.toString());
    }

    private static final PsmResource readResource(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.RESOURCE_NAME, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(PsmConfigElements.RESOURCE_NAME, reader);
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
        return new PsmResource(sb.toString());
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
        PsmResource resource = null;
        PsmCollection collection = null;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(PsmConfigElements.CLASS_TYPE, reader)) {
                        final String className = readClassName(PsmConfigElements.CLASS_TYPE, reader);
                        final Set<ModelElement> elements = modelIndex.lookupByName(new QName(className));
                        type = assertNotNull(resolveClass(elements, className));
                    } else if (match(PsmConfigElements.RESOURCE_NAME, reader)) {
                        resource = readResource(reader);
                    } else if (match(PsmConfigElements.COLLECTION_NAME, reader)) {
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
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        // TODO:
        return new PsmDocument<Type>(type, resource, collection);
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
            throw new RuntimeException(msg);
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
