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


package test.camel.support.stax;

import java.io.IOException;
import java.util.ArrayList;
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

import org.apache.camel.util.AntPathMatcher;
import org.springframework.core.io.Resource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author okrook
 *
 */
public class XmlParser extends EventReaderDelegate implements ErrorHandler {
    private String antPath = "/Interchange*/*/**";
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    private ObjectReadyNotifier notifier;

    // States
    private Stack<StartElement> parents = new Stack<StartElement>();
    private Stack<Map<String, Object>> currentObjects = new Stack<Map<String,Object>>();
    boolean popRequest = false;

    private XmlParser(XMLEventReader reader, ObjectReadyNotifier notifier) {
        super(reader);
        this.notifier = notifier;
    }

    public static void parse(XMLEventReader reader, Resource schemaResource, ObjectReadyNotifier notifier) throws SAXException, IOException, XMLStreamException {
        XmlParser parser = new XmlParser(reader, notifier);

        SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = SCHEMA_FACTORY.newSchema(schemaResource.getURL());
        Validator validator = schema.newValidator();
        validator.setErrorHandler(parser);
        validator.validate(new StAXSource(parser));
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        currentObjects.clear();
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        currentObjects.clear();
    }

    private void notifyObjectIsReady() {
        if (currentObjects.size() == 0) {
            // nothing to report as there was a validation error.
            return;
        }

        notifier.objectIsReady(currentObjects.peek());
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent nextEvent = super.nextEvent();

        if (popRequest && currentObjects.size() > 0) {
            if (parents.size() == 1) {
                notifyObjectIsReady();
            } else if (parents.size() > 1) {
                currentObjects.pop();
                popRequest = false;
            }
        }

        if (nextEvent.isStartElement()) {
            parents.push(nextEvent.asStartElement());

            if (isTargeted()) {
                collectEventInfo(nextEvent.asStartElement());
            }
        } else if (nextEvent.isCharacters()) {
            collectEventInfo(nextEvent.asCharacters());
        } else if (nextEvent.isEndElement()) {
            if (isTargeted()) {
                popRequest = true;
            }

            parents.pop();
        }

        return nextEvent;
    }

    private void collectEventInfo(Characters characters) {
        if (currentObjects.size() == 0) {
            return;
        }

        if (characters.isIgnorableWhiteSpace() || characters.isWhiteSpace()) {
            return;
        }

        String text = characters.getData();

        if (currentObjects.peek().containsKey(parents.peek().getName().getLocalPart() + "_value")) {
            String oldText = (String) currentObjects.peek().get(parents.peek().getName().getLocalPart() + "_value");
            text = oldText + text;
        }

        currentObjects.peek().put(parents.peek().getName().getLocalPart() + "_value", text);
    }

    @SuppressWarnings("unchecked")
    private void collectEventInfo(StartElement event) {
        if (parents.size() > 2 && currentObjects.size() == 0) {
            return;
        }

        Map<String, Object> object = generateObject(event);
        String element = event.getName().getLocalPart();

        if (parents.size() == 2) {
            object.put("element", element);
            object.put("line", event.getLocation().getLineNumber());
            object.put("column", event.getLocation().getColumnNumber());
        } else {
            Map<String, Object> currentObject = currentObjects.peek();

            if (currentObject.containsKey(element)) {

                List<Object> collection;
                Object elementObj = currentObject.get(element);

                if (elementObj instanceof List) {
                    collection = (List<Object>) elementObj;
                    collection.add(object);
                } else {
                    collection = new ArrayList<Object>();
                    collection.add(elementObj);
                    collection.add(object);

                    currentObject.put(element, collection);
                }
            } else {
                currentObject.put(element, object);
            }
        }

        currentObjects.push(object);
    }

    private static Map<String, Object> generateObject(StartElement event) {
        Map<String, Object> object = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Iterator<Attribute> attrs = event.getAttributes();

        while (attrs.hasNext()) {
            Attribute attr = attrs.next();

            object.put(attr.getName().getLocalPart(), attr.getValue());
        }

        return object;
    }

    private final boolean isTargeted() {
        return parents.size() > 1;
        //return antPathMatcher.match(antPath, getCurrentXPath());
    }

    private final String getCurrentXPath() {
        StringBuffer sb = new StringBuffer();

        for (StartElement start : this.parents) {
            sb.append('/').append(start.getName().getLocalPart());
        }

        return sb.toString();
    }
}
