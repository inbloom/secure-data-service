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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.stream.Location;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xerces.stax.ImmutableLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.slc.sli.common.util.logging.SecurityEvent;
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
public class EdfiRecordParserImpl2 extends DefaultHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EdfiRecordParserImpl2.class);

    private TypeProvider typeProvider;

    private Stack<Pair<RecordMeta, Map<String, Object>>> complexTypeStack = new Stack<Pair<RecordMeta, Map<String, Object>>>();

    private boolean currentEntityValid = false;

    private String interchange;

    private StringBuffer elementValue = new StringBuffer();

    private Locator locator;

    private List<RecordVisitor> recordVisitors = new ArrayList<RecordVisitor>();

    public static void parse(InputStream input, Resource schemaResource, TypeProvider typeProvider,
            RecordVisitor visitor) throws SAXException, IOException, XmlParseException {

        EdfiRecordParserImpl2 parser = new EdfiRecordParserImpl2();

        parser.addVisitor(visitor);
        parser.typeProvider = typeProvider;

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(schemaResource.getURL());

        parser.parseAndValidate(input, schema);
    }

    private void parseAndValidate(InputStream input, Schema schema) throws XmlParseException, IOException {
        ValidatorHandler vHandler = schema.newValidatorHandler();
        vHandler.setContentHandler(this);
        vHandler.setErrorHandler(this);

        InputSource is = new InputSource(new InputStreamReader(input, "UTF-8"));
        is.setEncoding("UTF-8");

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(vHandler);
            parser.setErrorHandler(this);

            vHandler.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);

            parser.setFeature("http://apache.org/xml/features/validation/id-idref-checking", false);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            parser.parse(is);
        } catch (SAXException e) {
            throw new XmlParseException(e.getMessage(), e);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementValue.setLength(0);

        if (interchange != null) {
            parseInterchangeEvent(localName, attributes);
        } else if (localName.startsWith("Interchange")) {
            interchange = localName;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (complexTypeStack.isEmpty()) {
            return;
        }

        String expectedLocalName = complexTypeStack.peek().getLeft().getName();

        if (localName.equals(expectedLocalName)) {
            if (elementValue.length() > 0) {
                String text = StringUtils.trimToEmpty(elementValue.toString());

                if (StringUtils.isNotBlank(text)) {
                    parseCharacters(text);
                }
            }

            if (complexTypeStack.size() > 1) {
                complexTypeStack.pop();
            } else if (complexTypeStack.size() == 1) {
                recordParsingComplete();
            }
        }

        elementValue.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementValue.append(ch, start, length);
    }

    private void parseInterchangeEvent(String localName, Attributes attributes) {
        if (complexTypeStack.isEmpty()) {
            initCurrentEntity(localName, attributes);
        } else {
            parseStartElement(localName, attributes);
        }
    }

    private void initCurrentEntity(String localName, Attributes attributes) {
        String xsdType = typeProvider.getTypeFromInterchange(interchange, localName);

        RecordMetaImpl recordMeta = new RecordMetaImpl(localName, xsdType);

        recordMeta.setSourceStartLocation(getCurrentLocation());

        complexTypeStack.push(createElementEntry(recordMeta));

        currentEntityValid = true;

        parseEventAttributes(attributes);
    }

    private void parseStartElement(String localName, Attributes attributes) {
        newEventToStack(localName);

        parseEventAttributes(attributes);
    }

    private void newEventToStack(String localName) {

        RecordMeta typeMeta = getRecordMetaForEvent(localName);

        Pair<RecordMeta, Map<String, Object>> subElement = createElementEntry(typeMeta);

        Object mapValue = subElement.getRight();
        if (typeMeta.isList() && complexTypeStack.peek().getRight().get(localName) == null) {
            mapValue = new ArrayList<Object>(Arrays.asList(mapValue));
        }

        insertToMap(localName, mapValue, complexTypeStack.peek().getRight());

        complexTypeStack.push(subElement);
    }

    private RecordMeta getRecordMetaForEvent(String eventName) {
        RecordMeta typeMeta = typeProvider
                .getTypeFromParentType(complexTypeStack.peek().getLeft().getType(), eventName);

        if (typeMeta == null) {
            // the parser must go on building the stack
            LOG.warn(
                    "Could not determine type of element: {} with parent of type: {}. Type conversion may not be applied on its value.",
                    eventName, complexTypeStack.peek().getLeft().getType());
            typeMeta = new RecordMetaImpl(eventName, "UNKNOWN");
        }
        return typeMeta;
    }

    private void parseEventAttributes(Attributes attributes) {
        String elementType = complexTypeStack.peek().getLeft().getType();

        for (int i = 0; i < attributes.getLength(); i++) {
            String attributeName = attributes.getLocalName(i);
            Object value = typeProvider.convertAttributeType(elementType, attributeName, attributes.getValue(i));
            complexTypeStack.peek().getRight().put("a_" + attributeName, value);
        }
    }

    private void parseCharacters(String text) {
        Object convertedValue = typeProvider.convertType(complexTypeStack.peek().getLeft().getType(), text);
        complexTypeStack.peek().getRight().put("_value", convertedValue);
    }

    private void recordParsingComplete() {
        Pair<RecordMeta, Map<String, Object>> pair = complexTypeStack.pop();
        LOG.debug("Parsed record: {}", pair);

        if (currentEntityValid) {
            ((RecordMetaImpl) pair.getLeft()).setSourceEndLocation(getCurrentLocation());

            for (RecordVisitor visitor : recordVisitors) {
                visitor.visit(pair.getLeft(), pair.getRight());
            }
        }
    }

    public Location getCurrentLocation() {
        return new ImmutableLocation(0, locator.getColumnNumber(), locator.getLineNumber(), locator.getPublicId(),
                locator.getSystemId());
    }

    private static Pair<RecordMeta, Map<String, Object>> createElementEntry(RecordMeta edfiType) {
        return new ImmutablePair<RecordMeta, Map<String, Object>>(edfiType, new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    private static void insertToMap(String key, Object value, Map<String, Object> map) {
        Object stored = map.get(key);
        if (stored != null && List.class.isAssignableFrom(stored.getClass())) {
            List<Object> storage = (List<Object>) stored;
            storage.add(value);
        } else {
            map.put(key, value);
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        LOG.warn("Warning: {}", exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        LOG.error("Error: {}", exception.getMessage());

        // TODO: Reactivate this statement for strict validation and story US5061 acceptance!!!
//        currentEntityValid = false;

    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        LOG.error("FatalError: {}", exception.getMessage());

        // TODO: Reactivate this statement for strict validation and story US5061 acceptance!!!
//        currentEntityValid = false;
    }

    public void addVisitor(RecordVisitor recordVisitor) {
        recordVisitors.add(recordVisitor);
    }

    public void audit(SecurityEvent event) {
        // Do nothing
    }
}
