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
package org.slc.sli.ingestion.parser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.parser.EdfiRecordParser;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.parser.TypeProvider;
import org.slc.sli.ingestion.parser.XmlParseException;

/**
 * A reader delegate that will intercept an XML Validator's calls to nextEvent() and build the
 * document into a Map of Maps data structure.
 *
 * Additionally, the class implements ErrorHandler so
 * that the parsing of a specific entity can be aware of validation errors.
 *
 * @author dduran
 *
 */
public class EdfiRecordParserImpl extends EventReaderDelegate implements EdfiRecordParser {

    private static final Logger LOG = LoggerFactory.getLogger(EdfiRecordParserImpl.class);

    private TypeProvider typeProvider;

    Stack<Pair<RecordMeta, Map<String, Object>>> complexTypeStack = new Stack<Pair<RecordMeta, Map<String, Object>>>();
    String currentEntityName = null;
    boolean currentEntityValid = false;
    private String interchange;

    private List<RecordVisitor> recordVisitors = new ArrayList<RecordVisitor>();

    public static void parse(XMLEventReader reader, Resource schemaResource, TypeProvider typeProvider,
            RecordVisitor visitor) throws XmlParseException {

        EdfiRecordParserImpl parser = new EdfiRecordParserImpl();
        parser.setParent(reader);
        parser.addVisitor(visitor);
        parser.typeProvider = typeProvider;

        Schema schema = initializeSchema(schemaResource);

        parseAndValidate(parser, schema);
    }

    private static Schema initializeSchema(Resource schemaResource) throws XmlParseException {
        Schema schema;
        try {
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaResource.getURL());
        } catch (SAXException e) {
            throw new XmlParseException("Exception while initializing XSD schema", e);
        } catch (IOException e) {
            throw new XmlParseException("Exception while accessing XSD schema file", e);
        }
        return schema;
    }

    private static void parseAndValidate(EdfiRecordParserImpl parser, Schema schema) throws XmlParseException {

        Validator validator = schema.newValidator();
        validator.setErrorHandler(parser);

        try {
            validator.validate(new StAXSource(parser));
        } catch (SAXException e) {
            throw new XmlParseException("Exception while processing the xml file", e);
        } catch (IOException e) {
            throw new XmlParseException("Exception while accessing the xml file", e);
        } catch (XMLStreamException e) {
            throw new XmlParseException("Exception while processing the xml file", e);
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent event = super.nextEvent();

        parseXmlEvent(event);

        return event;
    }

    private void parseXmlEvent(XMLEvent event) {
        try {
            String eventName = extractTagName(event);
            if (interchange != null) {
                parseInterchangeEvent(event, eventName);

            } else if (eventName.startsWith("Interchange")) {
                interchange = eventName;
            }
        } catch (XMLStreamException e) {
            LOG.error("Error parsing.", e);
        }
    }

    private void parseInterchangeEvent(XMLEvent event, String eventName) throws XMLStreamException {

        if (!complexTypeStack.isEmpty()) {

            parseEntityEvent(event, eventName);

        } else if (event.isStartElement()) {

            initCurrentEntity(event, eventName);
        }
    }

    private void parseEntityEvent(XMLEvent e, String eventName) throws XMLStreamException {

        if (e.isStartElement()) {
            parseStartElement(e.asStartElement(), eventName);

        } else if (e.isCharacters()) {
            parseCharacters(e.asCharacters());

        } else if (e.isEndElement() && eventName.equals(complexTypeStack.peek().getLeft().getName())) {
            parseEndElement(e);
        }
    }

    private void initCurrentEntity(XMLEvent event, String eventName) {
        String xsdType = typeProvider.getTypeFromInterchange(interchange, eventName);

        RecordMeta recordMeta = new RecordMetaImpl(eventName, xsdType);
        ((RecordMetaImpl) recordMeta).setSourceStartLocation(event.getLocation());

        complexTypeStack.push(createElementEntry(recordMeta));
        currentEntityName = eventName;
        currentEntityValid = true;

        parseEventAttributes(event.asStartElement());
    }

    private void parseStartElement(StartElement startElement, String eventName) {

        newEventToStack(eventName);

        parseEventAttributes(startElement);
    }

    private void newEventToStack(String eventName) {
        RecordMeta typeMeta = typeProvider
                .getTypeFromParentType(complexTypeStack.peek().getLeft().getType(), eventName);

        Pair<RecordMeta, Map<String, Object>> subElement = createElementEntry(typeMeta);

        Object mapValue = subElement.getRight();
        if (typeMeta.isList() && complexTypeStack.peek().getRight().get(eventName) == null) {
            mapValue = new ArrayList<Object>(Arrays.asList(mapValue));
        }

        complexTypeStack.peek().getRight().put(eventName, mapValue);
        complexTypeStack.push(subElement);
    }

    @SuppressWarnings("unchecked")
    private void parseEventAttributes(StartElement startElement) {
        Iterator<Attribute> it = startElement.getAttributes();
        while (it.hasNext()) {
            Attribute a = it.next();
            complexTypeStack.peek().getRight().put("a_" + a.getName().getLocalPart(), a.getValue());
        }
    }

    private void parseCharacters(Characters characters) {
        if (!characters.isIgnorableWhiteSpace() && !characters.isWhiteSpace()) {
            String text = characters.getData();
            Object convertedValue = typeProvider.convertType(complexTypeStack.peek().getLeft().getType(), text);
            complexTypeStack.peek().getRight().put("_value", convertedValue);
        }
    }

    private void parseEndElement(XMLEvent event) {
        if (complexTypeStack.size() > 1) {
            complexTypeStack.pop();
        } else if (complexTypeStack.size() == 1) {

            recordParsingComplete(event);
        }
    }

    private void recordParsingComplete(XMLEvent event) {
        currentEntityName = null;

        Pair<RecordMeta, Map<String, Object>> pair = complexTypeStack.pop();
        LOG.debug("Parsed record: {}", pair);

        if (currentEntityValid) {

            ((RecordMetaImpl) pair.getLeft()).setSourceEndLocation(event.getLocation());

            for (RecordVisitor visitor : recordVisitors) {
                visitor.visit(pair.getLeft(), pair.getRight());
            }
        }
    }

    private Pair<RecordMeta, Map<String, Object>> createElementEntry(RecordMeta edfiType) {
        return new ImmutablePair<RecordMeta, Map<String, Object>>(edfiType, new InnerMap());
    }

    private static String extractTagName(XMLEvent e) {
        String result = "";
        if (e.isEndElement()) {
            result = e.asEndElement().getName().getLocalPart();
        } else if (e.isStartElement()) {
            result = e.asStartElement().getName().getLocalPart();
        }
        return result;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        LOG.warn("Warning: {}", exception.getMessage());
        currentEntityValid = false;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        LOG.error("Error: {}", exception.getMessage());
        currentEntityValid = false;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        LOG.error("FatalError: {}", exception.getMessage());
        currentEntityValid = false;
    }

    @Override
    public void addVisitor(RecordVisitor recordVisitor) {
        recordVisitors.add(recordVisitor);
    }

    @SuppressWarnings({ "unchecked", "serial" })
    private static class InnerMap extends HashMap<String, Object> {
        @Override
        public Object put(String key, Object value) {
            Object result;
            Object stored = this.get(key);
            if (stored != null) {
                if (List.class.isAssignableFrom(stored.getClass())) {
                    List<Object> storage = (List<Object>) stored;
                    storage.add(value);
                    result = storage;
                } else {
                    result = super.put(key, new ArrayList<Object>(Arrays.asList(stored, value)));
                }
            } else {
                result = super.put(key, value);
            }
            return result;
        }
    }

    public void audit(SecurityEvent event) {
        // TODO Auto-generated method stub

    }

}
