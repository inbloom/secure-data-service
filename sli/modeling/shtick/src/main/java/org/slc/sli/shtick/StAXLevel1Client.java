package org.slc.sli.shtick;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class StAXLevel1Client implements Level1Client {

    private final Level0Client inner;

    public StAXLevel1Client() {
        this(new JaxRSLevel0Client());
    }

    public StAXLevel1Client(final Level0Client inner) {
        if (inner == null) {
            throw new NullPointerException("inner");
        }
        this.inner = inner;
    }

    @Override
    public List<RestEntity> getRequest(final String token, final URL url) throws URISyntaxException, IOException,
            RestException {
        final String body = inner.getRequest(token, url, "application/xml");
        return deserialize(body);
    }

    @Override
    public void deleteRequest(final String token, final URL url) throws URISyntaxException, IOException, RestException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public URL postRequest(final String token, final RestEntity data, final URL url) throws URISyntaxException,
            IOException, RestException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void putRequest(final String token, final RestEntity data, final URL url) throws URISyntaxException,
            IOException, RestException {
        throw new UnsupportedOperationException("TODO");
    }

    private List<RestEntity> deserialize(final String body) throws IOException {
        final StringReader sw = new StringReader(body);
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(sw);
            try {
                return readDocument(reader);
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(body, e);
        }
    }

    private static final List<RestEntity> readDocument(final XMLStreamReader reader) throws XMLStreamException {
        if (XMLStreamConstants.START_DOCUMENT == reader.getEventType()) {
            final List<RestEntity> entities = new ArrayList<RestEntity>();
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

    private static final List<RestEntity> readDocumentElement(final XMLStreamReader reader) throws XMLStreamException {
        final QName elementName = reader.getName();
        final List<RestEntity> entities = new ArrayList<RestEntity>();
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

    private static final RestEntity readEntity(final XMLStreamReader reader) throws XMLStreamException {
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
                    return new RestEntity(elementName.getLocalPart(), data);
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

    /**
     * The return value might be a {@link Map} or {@link String}.
     * <p>
     * The {@link Map} occurs when child elements are detected. The {@link String} is otherwise.
     * </p>
     */
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
                    if (data.size() > 0) {
                        return data;
                    } else {
                        return coerceValue(sb.toString());
                    }
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

    /**
     * FIXME: This is a hack so that we can continue testing.
     * It converts String values to Boolean if they look like a Boolean.
     * <p>
     * A better solution might be to use xsi:type as a hint in the XML.
     * </p>
     */
    private static Object coerceValue(final String value) {
        if ("true".equals(value)) {
            return Boolean.TRUE;
        } else if ("false".equals(value)) {
            return Boolean.FALSE;
        } else {
            return value;
        }
    }
}
