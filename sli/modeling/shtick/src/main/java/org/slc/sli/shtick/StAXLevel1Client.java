package org.slc.sli.shtick;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;

public final class StAXLevel1Client implements Level1Client {

    private final Level0Client inner;

    public StAXLevel1Client() {
        this(new StandardLevel0Client());
    }

    public StAXLevel1Client(final Level0Client inner) {
        if (inner == null) {
            throw new NullPointerException("inner");
        }
        this.inner = inner;
    }

    @Override
    public List<Entity> getRequest(final String token, final URL url) throws URISyntaxException, IOException,
            SLIDataStoreException {
        try {
            final Response response = inner.getRequest(token, url, MediaType.APPLICATION_XML);
            return deserialize(response);
        } catch (final HttpRestException e) {
            throw new SLIDataStoreException(e);
        }
    }

    @Override
    public void deleteRequest(final String token, final URL url) throws URISyntaxException, IOException,
            SLIDataStoreException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public URL postRequest(final String token, final String data, final URL url) throws URISyntaxException,
            IOException, SLIDataStoreException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void putRequest(final String token, final String data, final URL url) throws URISyntaxException,
            IOException, SLIDataStoreException {
        throw new UnsupportedOperationException("TODO");
    }

    private List<Entity> deserialize(final Response response) throws IOException, SLIDataStoreException {
        final String readEntity = response.readEntity(String.class);
        final StringReader sw = new StringReader(readEntity);
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(sw);
            try {
                return readDocument(reader);
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(readEntity, e);
        }
    }

    private static final List<Entity> readDocument(final XMLStreamReader reader) throws XMLStreamException {
        if (XMLStreamConstants.START_DOCUMENT == reader.getEventType()) {
            final List<Entity> entities = new ArrayList<Entity>();
            while (reader.hasNext()) {
                reader.next();
                switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    entities.addAll(readDocumentElement(reader));
                    break;
                }
                case XMLStreamConstants.END_DOCUMENT: {
                    return entities;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
                }
            }
            throw new AssertionError();
        } else {
            throw new AssertionError(reader.getLocalName());
        }
    }

    private static final List<Entity> readDocumentElement(final XMLStreamReader reader) throws XMLStreamException {
        final QName elementName = reader.getName();
        final List<Entity> entities = new ArrayList<Entity>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                entities.add(readEntity(reader));
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                if (elementName.equals(reader.getName())) {
                    return entities;
                } else {
                    throw new AssertionError(reader.getName());
                }
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
        throw new AssertionError();
    }

    private static final Entity readEntity(final XMLStreamReader reader) throws XMLStreamException {
        final QName elementName = reader.getName();
        final Map<String, Object> data = new HashMap<String, Object>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                final QName key = reader.getName();
                final Object value = readValue(reader);
                data.put(key.getLocalPart(), value);
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                if (elementName.equals(reader.getName())) {
                    // Our best guess for the type is the local-name of the element.
                    return new GenericEntity(elementName.getLocalPart(), data);
                } else {
                    throw new AssertionError(reader.getName());
                }
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
        throw new AssertionError();
    }

    private static final Object readValue(final XMLStreamReader reader) throws XMLStreamException {
        final QName elementName = reader.getName();
        final StringBuilder sb = new StringBuilder();
        final Map<String, Object> data = new HashMap<String, Object>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                final QName key = reader.getName();
                final Object value = readValue(reader);
                data.put(key.getLocalPart(), value);
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                if (elementName.equals(reader.getName())) {
                    return data;
                } else {
                    throw new AssertionError(reader.getName());
                }
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
        throw new AssertionError();
    }
}
