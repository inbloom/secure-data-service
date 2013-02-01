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
package org.slc.sli.ingestion.parser.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.parser.TypeProvider;

/**
 * Provides xsd-based typification services to the parser
 *
 * @author dkornishev
 *
 */
@Component
public class XsdTypeProvider implements TypeProvider {

    private static final Namespace XS_NAMESPACE = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

    @Value("file:${sli.conf}")
    private Resource sliPropsFile;

    private Map<String, Element> complexTypes = new HashMap<String, Element>();
    private Map<String, String> typeMap = new HashMap<String, String>();

    private Map<String, Map<String, String>> interchangeMap = new HashMap<String, Map<String, String>>();

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() throws Exception {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

        Properties sliProps = PropertiesLoaderUtils.loadProperties(sliPropsFile);
        if (null == sliProps) {
            throw new Exception("Cannot load properties from props file '" + sliPropsFile + "' == ${sli.conf}"); // NOPMD
        }

        String curdir = System.getProperty("user.dir");

        String schemaLocation = sliProps.getProperty("sli.poc.atma.schema");
        parseEdfiSchema(schemaLocation);

        parseInterchangeSchemas(sliProps);
    }

    private void parseEdfiSchema(String schemaLocation) throws JDOMException, IOException {
        SAXBuilder b = new SAXBuilder();
        Document doc = b.build(new FileInputStream(schemaLocation));
        for (Element xsInclude : doc.getDescendants(Filters.element("include", XS_NAMESPACE))) {
            parseEdfiSchema(xsInclude.getAttributeValue("schemaLocation"));
        }

        parseComplexTypes(doc);
    }

    private void parseComplexTypes(Document doc) {
        Iterable<Element> complexTypes = doc.getDescendants(Filters.element("complexType", XS_NAMESPACE));
        for (Element e : complexTypes) {
            this.complexTypes.put(e.getAttributeValue("name"), e);
        }
        buildXsdElementsMap(doc, typeMap);
    }

    private void buildXsdElementsMap(Document doc, Map<String, String> map) {
        Iterable<Element> elements = doc.getDescendants(Filters.element("element", XS_NAMESPACE));
        for (Element e : elements) {
            String type = getType(e);
            map.put(e.getAttributeValue("name"), type);
        }
    }

    private void parseInterchangeSchemas(Properties sliProps) throws JDOMException, IOException {
        FileInputStream interchangeStream = new FileInputStream(sliProps.getProperty("sli.poc.atma.assessment"));
        interchangeMap.put("InterchangeAssessmentMetadata", new HashMap<String, String>());
        SAXBuilder b = new SAXBuilder();
        buildXsdElementsMap(b.build(interchangeStream), interchangeMap.get("InterchangeAssessmentMetadata"));
    }

    @Override
    public String getTypeFromInterchange(String interchange, String eventName) {
        return interchangeMap.get(interchange).get(eventName);
    }

    @Override
    public String getTypeFromParentType(String xsdType, String eventName) {
        return getType(getComplexElement(xsdType).getChild(eventName));
    }

    @Override
    public boolean isComplexType(String elementName) {
        return this.complexTypes.containsKey(elementName)
                || this.complexTypes.containsKey(this.typeMap.get(elementName));
    }

    @Override
    public boolean existsInSchema(String parentName, String name) {
        Element parent = getComplexElement(parentName);
        return parent.getChild(name) != null;
    }

    private Element getComplexElement(String parentName) {
        Element parent = this.complexTypes.get(parentName);
        if (parent == null) {
            parent = this.complexTypes.get(this.typeMap.get(parentName));
        }
        return parent;
    }

    @Override
    public Object convertType(String typeName, String value) {
        String type = this.typeMap.get(typeName);

        Object result = value;
        if (type.equals("xs:date")) {
            result = value;
        } else if (type.equals("xs:boolean")) {
            result = Boolean.parseBoolean(value);
        } else if (type.equals("xs:double")) {
            result = Double.parseDouble(value);
        } else if (type.equals("xs:int")) {
            result = Integer.parseInt(value);
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
        String type = e.getAttributeValue("type");

        if (type == null) {
            Element simple = e.getChild("simpleType", XS_NAMESPACE);

            if (simple != null) {
                Element restriction = simple.getChild("restriction", XS_NAMESPACE);

                if (restriction != null) {
                    type = restriction.getAttributeValue("base");
                }
            }
        }
        return type;
    }

    public void audit(SecurityEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isReference(String elementName) {
        // TODO Auto-generated method stub
        return false;
    }

}
