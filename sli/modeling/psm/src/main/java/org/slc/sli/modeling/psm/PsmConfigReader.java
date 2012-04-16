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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xmi.XmiAttributeName;

public final class PsmConfigReader {
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public static final PsmConfig<Identifier> readConfig(final String fileName) throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readConfig(istream);
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
    public static final PsmConfig<Identifier> readConfig(final InputStream stream) {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                final PsmConfig<Identifier> model = readDocument(reader);
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
    
    private static final PsmConfig<Identifier> readDocument(final XMLStreamReader reader) throws XMLStreamException {
        assertStartDocument(reader);
        PsmConfig<Identifier> dm = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(PsmConfigElements.PSM_CFG, reader)) {
                        dm = assertNotNull(readConfig(reader));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_DOCUMENT: {
                    return validateNotNull(dm, "Missing root element: " + PsmConfigElements.PSM_CFG);
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }
    
    private static final PsmConfig<Identifier> readConfig(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.PSM_CFG, reader);
        final List<PsmClassType<Identifier>> domains = new LinkedList<PsmClassType<Identifier>>();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(PsmConfigElements.PSM_CLASS_TYPE, reader)) {
                        domains.add(assertNotNull(readClassType(reader)));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(PsmConfigElements.PSM_CFG, reader);
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
        return new PsmConfig<Identifier>(domains);
    }
    
    private static final Identifier readReference(final QName name, final XMLStreamReader reader)
            throws XMLStreamException {
        assertStartElement(reader);
        assertName(name, reader);
        final Identifier id = getIdRef(reader);
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
        return id;
    }
    
    private static final PsmCollection readCollection(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.COLLECTION, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(PsmConfigElements.COLLECTION, reader);
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
        assertName(PsmConfigElements.RESOURCE, reader);
        final StringBuilder sb = new StringBuilder();
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(PsmConfigElements.RESOURCE, reader);
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
    
    private static final Identifier getIdRef(final XMLStreamReader reader) {
        final String value = reader.getAttributeValue("", XmiAttributeName.IDREF.getLocalName());
        if (value != null) {
            return Identifier.fromString(value);
        } else {
            throw new AssertionError(XmiAttributeName.IDREF.getLocalName());
        }
    }
    
    private static final PsmClassType<Identifier> readClassType(final XMLStreamReader reader) throws XMLStreamException {
        assertStartElement(reader);
        assertName(PsmConfigElements.PSM_CLASS_TYPE, reader);
        Identifier type = null;
        PsmResource resource = null;
        PsmCollection collection = null;
        boolean done = false;
        while (!done && reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(PsmConfigElements.PIM_CLASS_REF, reader)) {
                        final Identifier classRef = readReference(PsmConfigElements.PIM_CLASS_REF, reader);
                        type = assertNotNull(classRef);
                    } else if (match(PsmConfigElements.RESOURCE, reader)) {
                        resource = readResource(reader);
                    } else if (match(PsmConfigElements.COLLECTION, reader)) {
                        collection = readCollection(reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(PsmConfigElements.PSM_CLASS_TYPE, reader);
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
        return new PsmClassType<Identifier>(type, resource, collection);
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
