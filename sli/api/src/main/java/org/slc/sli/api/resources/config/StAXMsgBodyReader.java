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


package org.slc.sli.api.resources.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;

/**
 * Helper class to parse XML into EntityBody objects
 * @author jstokes
 */

@Component
public class StAXMsgBodyReader {
    // Namespace
    private static final String NS = "urn:sli";
    // List element marker
    private static final String LIST_ELEM = "member";

    /**
     * Deserializer for XML => EntityBody
     * @param body xml as an input stream
     * @return a EntityBody object that corresponds to the xml
     * @throws XMLStreamException
     */
    public EntityBody deserialize(final InputStream body) throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(body);
        try {
            return readDocument(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Helper method for digesting XML documents
     * @param reader XML reader
     * @return EntityBody representation that corresponds to the xml
     * @throws XMLStreamException on malformed XML
     */
    private static final EntityBody readDocument(final XMLStreamReader reader) throws XMLStreamException {
        if (XMLStreamConstants.START_DOCUMENT == reader.getEventType()) {
            EntityBody body = null;
            while (reader.hasNext()) {
                reader.next();
                switch (reader.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT : {
                        body = readDocumentElement(reader);
                        return body;
                    }
                    case XMLStreamConstants.END_DOCUMENT : {
                        return body;
                    }
                    case XMLStreamConstants.CHARACTERS: {
                        // Ignore
                        break;
                    }
                    default : {
                        throw new XMLStreamException();
                    }
                }
            }
        } else {
            throw new XMLStreamException(reader.getLocalName());
        }
        throw new XMLStreamException();
    }

    /**
     * Reads everything under the main document wrapper tag
     * @param reader Reader that we have for XML
     * @return EntityBody representation of the document
     * @throws XMLStreamException on malformed XML
     */
    private static final EntityBody readDocumentElement(final XMLStreamReader reader) throws XMLStreamException {
        final Map<String, Object> elements = new HashMap<String, Object>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT : {
                    final Pair<Object, Boolean> memberDataPair = readElement(reader);
                    addToElements(reader.getLocalName(), memberDataPair, elements);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT : {
                    return new EntityBody(elements);
                }
                case XMLStreamConstants.CHARACTERS : {
                    // Ignore
                    break;
                }
                default : {
                    throw new XMLStreamException();
                }
            }
        }
        throw new XMLStreamException();
    }

    /**
     * Adds elements to the underlying HashMap for EntityBody
     * @param key The position to insert into
     * @param memberDataPair A pair representing the Object to insert into (Left) as well as
     *                       a boolean value to determine whether the element is a part of a
     *                       list or a single value
     * @param elements The underlying map to insert into
     */
    @SuppressWarnings("unchecked")
    private static void addToElements(final String key, final Pair<Object, Boolean> memberDataPair,
                                                     final Map<String, Object> elements) throws XMLStreamException {
        final boolean member = memberDataPair.getRight();
        final Object toAdd = memberDataPair.getLeft();

        // Duplicate key for a non list item
        if (elements.containsKey(key) && !member) {
            throw new XMLStreamException("Reassignment of key for non list member");
        }

        if (member) {
            if (elements.containsKey(key)) { // We have already inserted this list
                ((List<Object>) elements.get(key)).add(toAdd);
            } else { // First time inserting to a list element
                elements.put(key, new ArrayList<Object>(Arrays.asList(toAdd)));
            }
        } else {
            elements.put(key, toAdd);
        }
    }

    /**
     * Reads individual elements inside of a XML Document
     * @param reader xml reader
     * @return a pair representing the Object value of the element (Left) as well as
     *         a boolean value representing either true (part of a list) or false
     *         (single value)
     * @throws XMLStreamException on malformed XML
     */
    private static final Pair<Object, Boolean> readElement(final XMLStreamReader reader) throws XMLStreamException {
        final QName elementName = reader.getName();
        final StringBuilder sb = new StringBuilder();
        final Map<String, Object> data = new HashMap<String, Object>();
        final Boolean member = isMember(reader);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final QName key = reader.getName();
                    final Pair<Object, Boolean> elem = readElement(reader);
                    addToElements(key.getLocalPart(), elem, data);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (elementName.equals(reader.getName())) {
                        if (data.size() > 0) {
                            return new ImmutablePair<Object, Boolean>(data, member);
                        } else {
                            return new ImmutablePair<Object, Boolean>(sb.toString(), member);
                        }
                    } else {
                        throw new XMLStreamException(reader.getName().getLocalPart());
                    }
                }
                case XMLStreamConstants.CHARACTERS: {
                    sb.append(reader.getText());
                    break;
                }
                default: {
                    throw new XMLStreamException();
                }
            }
        }
        throw new XMLStreamException();
    }

    /**
     * Helper method for determining if the element we are reading is unbounded or singular
     * @param reader xml reader
     * @return True if part of a list, false if singular
     */
    private static Boolean isMember(final XMLStreamReader reader) {
        final String member = reader.getAttributeValue(NS, LIST_ELEM);
        return (member != null && (member.equals("true") || member.equals("1")));
    }

}
