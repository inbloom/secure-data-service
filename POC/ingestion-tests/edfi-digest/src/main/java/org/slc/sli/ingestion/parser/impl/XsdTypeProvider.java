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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;
import org.springframework.core.io.Resource;

import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.TypeProvider;

/**
 * Provides xsd-based typification services to the parser
 *
 * @author dkornishev
 * @author dduran
 *
 */
public class XsdTypeProvider implements TypeProvider {

    private static final Namespace XS_NAMESPACE = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");
    private static final String XS_DATE = "xs:date";
    private static final String XS_BOOLEAN = "xs:boolean";
    private static final String XS_DOUBLE = "xs:double";
    private static final String XS_INT = "xs:int";

    private static final String INTERCHANGE = "interchange";
    private static final String INCLUDE = "include";
    private static final String SCHEMA_LOCATION = "schemaLocation";
    private static final String COMPLEX_TYPE = "complexType";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String ELEMENT = "element";
    private static final String SIMPLETYPE = "simpleType";
    private static final String RESTRICTION = "restriction";
    private static final String BASE = "base";
    private static final String SCHEMA = "schema";
    private static final String UNBOUNDED = "unbounded";
    private static final String MAX_OCCURS = "maxOccurs";
    private static final String EXTENSION = "extension";

    private Resource[] schemaFiles;

    private Map<String, Element> complexTypes = new HashMap<String, Element>();
    private Map<String, String> typeMap = new HashMap<String, String>();

    private Map<String, Map<String, String>> interchangeMap = new HashMap<String, Map<String, String>>();

    private void init() throws IOException, JDOMException {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

        SAXBuilder b = new SAXBuilder();

        for (Resource schemaFile : schemaFiles) {
            parseSchema(b, schemaFile);
        }
    }

    /**
     * @param b
     * @param schemaFile
     * @throws JDOMException
     * @throws IOException
     */
    private void parseSchema(SAXBuilder b, Resource schemaFile) throws JDOMException, IOException {
        if (schemaFile.getFilename().toLowerCase().indexOf(INTERCHANGE) != -1) {
            parseInterchangeSchemas(schemaFile, b);
        } else {
            parseEdfiSchema(schemaFile, b);
        }
    }

    private void parseEdfiSchema(Resource schemaFile, SAXBuilder b) throws JDOMException, IOException {
        Document doc = b.build(schemaFile.getURL());

        for (Element xsInclude : doc.getDescendants(Filters.element(INCLUDE, XS_NAMESPACE))) {
            String inclSchemaLocation = xsInclude.getAttributeValue(SCHEMA_LOCATION);
            parseEdfiSchema(schemaFile.createRelative(inclSchemaLocation), b);
        }

        parseComplexTypes(doc);
    }

    private void parseComplexTypes(Document doc) {
        Iterable<Element> complexTypes = doc.getDescendants(Filters.element(COMPLEX_TYPE, XS_NAMESPACE));
        for (Element e : complexTypes) {
            this.complexTypes.put(e.getAttributeValue(NAME), e);
        }
        buildXsdElementsMap(doc, typeMap);
    }

    private void buildXsdElementsMap(Document doc, Map<String, String> map) {
        Iterable<Element> elements = doc.getDescendants(Filters.element(ELEMENT, XS_NAMESPACE));
        for (Element e : elements) {
            String type = getType(e);
            map.put(e.getAttributeValue(NAME), type);
        }
    }

    private void parseInterchangeSchemas(Resource schemaFile, SAXBuilder b) throws JDOMException, IOException {
        Document doc = b.build(schemaFile.getURL());

        // get interchange element name and build map for it

        Iterator<Element> schemaIter = doc.getDescendants(Filters.element(SCHEMA, XS_NAMESPACE)).iterator();
        if (schemaIter.hasNext()) {
            Element interchangeElement = schemaIter.next().getChild(ELEMENT, XS_NAMESPACE);

            Map<String, String> interchangeElementMap = new HashMap<String, String>();
            interchangeMap.put(interchangeElement.getAttributeValue(NAME), interchangeElementMap);

            buildXsdElementsMap(doc, interchangeElementMap);
        }
    }

    @Override
    public String getTypeFromInterchange(String interchange, String eventName) {
        return interchangeMap.get(interchange).get(eventName);
    }

    @Override
    public RecordMeta getTypeFromParentType(String type, String eventName) {
        Element parentElement = getComplexElement(type);

        while (parentElement != null && eventName != null) {
            for (Element e : parentElement.getDescendants(Filters.element(ELEMENT, XS_NAMESPACE))) {
                if (e.getAttributeValue(NAME).equals(eventName)) {
                    return new RecordMetaImpl(eventName, e.getAttributeValue(TYPE), shouldBeList(e, parentElement));
                }
            }

            IteratorIterable<Element> extensions = parentElement.getDescendants(Filters.element(EXTENSION, XS_NAMESPACE));

            if (extensions.hasNext()) {
                parentElement = extensions.next();
            } else {
                parentElement = null;
            }
        }

        return null;
    }



    private Element getComplexElement(String parentName) {
        Element parent = complexTypes.get(parentName);
        if (parent == null) {
            parent = complexTypes.get(typeMap.get(parentName));
        }
        return parent;
    }

    private boolean shouldBeList(Element e, Element parentElement) {
        if (UNBOUNDED.equals(e.getAttributeValue(MAX_OCCURS))) {
            return true;
        }
        return isContainedByUnboundedElement(e, parentElement);
    }

    private boolean isContainedByUnboundedElement(Element e, Element parentElement) {
        // we may be able to remove this method/logic as the SLI-Edfi schema overrides all types
        // that contain unbounded choice to remove them.
        Element immediateParent = e.getParentElement();
        while (!immediateParent.equals(parentElement)) {
            if (UNBOUNDED.equals(immediateParent.getAttributeValue(MAX_OCCURS))) {
                return true;
            }
            immediateParent = immediateParent.getParentElement();
        }
        return false;
    }

    @Override
    public Object convertType(String type, String value) {
        Object result = value;
        if (type != null) {
            if (type.equals(XS_DATE)) {
                result = value;
            } else if (type.equals(XS_BOOLEAN)) {
                result = Boolean.parseBoolean(value);
            } else if (type.equals(XS_DOUBLE)) {
                result = Double.parseDouble(value);
            } else if (type.equals(XS_INT)) {
                result = Integer.parseInt(value);
            }
        }
        return result;
    }

    /**
     * Figures out xsd type of the element Normally taken from the 'type' attribute, in other cases,
     * needs to dig deeper
     *
     * @param e
     *            node in the tree
     * @return variable type if available
     */
    private String getType(Element e) {
        String type = e.getAttributeValue(TYPE);

        if (type == null) {
            Element simple = e.getChild(SIMPLETYPE, XS_NAMESPACE);

            if (simple != null) {
                Element restriction = simple.getChild(RESTRICTION, XS_NAMESPACE);

                if (restriction != null) {
                    type = restriction.getAttributeValue(BASE);
                }
            }
        }
        return type;
    }

    public void setSchemaFiles(Resource[] schemaFiles) throws Exception {
        this.schemaFiles = Arrays.copyOf(schemaFiles, schemaFiles.length);

        init();
    }

}
