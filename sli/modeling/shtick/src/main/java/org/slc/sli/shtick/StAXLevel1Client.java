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

package org.slc.sli.shtick;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
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
            throw new IllegalArgumentException("inner");
        }
        this.inner = inner;
    }
    
    @Override
    public List<Entity> get(final String token, final URI uri) throws IOException, StatusCodeException {
        final String body = inner.get(token, uri, "application/xml");
        return deserialize(body);
    }
    
    @Override
    public void delete(final String token, final URI uri) throws IOException, StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public URI post(final String token, final Entity data, final URI uri) throws IOException, StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public void put(final String token, final Entity data, final URI uri) throws IOException, StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void patch(String token, Entity data, URI uri) throws IOException, StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
    
    private List<Entity> deserialize(final String body) throws IOException {
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
            throw new ClientRuntimeException(body, e);
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
                        return new Entity(elementName.getLocalPart(), data);
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
