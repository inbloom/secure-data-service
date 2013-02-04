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

package org.slc.sli.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class SmooksGenerator {

    public class ElementData {
        private String elementType;
        private boolean isList;

        public String getElementType() {
            return elementType;
        }

        public void setElementType(String elementType) {
            this.elementType = elementType;
        }

        public boolean isList() {
            return isList;
        }

        public void setIsList(boolean isList) {
            this.isList = isList;
        }

        @Override
        public String toString() {
            return elementType;
        }

    }

    public class ComplexTypeData {
        private Set<String> attributes;
        private Map<String, ElementData> elements;

        public ComplexTypeData() {
            attributes = new HashSet<String>();
            elements = new HashMap<String, ElementData>();
        }

        @Override
        public String toString() {
            return "attributes:\n" + attributes.toString() + "\nelements:\n" + elements.toString();
        }

        public Set<String> getAttributes() {
            return attributes;
        }

        public Map<String, ElementData> getElements() {
            return elements;
        }
    }

    ResourceLoader resourceLoader;
    // cache for complex types
    private Map<String, XmlSchemaComplexType> complexTypes;
    // cache for simple types
    private Map<String, XmlSchemaSimpleType> simpleTypes;

    // cache for data contained in complex types
    private Map<String, ComplexTypeData> complexTypesData;

    private String xsdLocation = "classpath:edfiXsd/Ed-Fi-Core.xsd";
    private String xsdParentLocation = "classpath:edfiXsd";

    private String extensionXsdLocation = "classpath:edfiXsd-SLI/SLI-Ed-Fi-Core.xsd";
    private String extensionXsdParentLocation = "classpath:edfiXsd-SLI";

    /**
     * @param args
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        SmooksGenerator smooksGen = new SmooksGenerator();
        smooksGen.createSmooks("SLC-CalendarDate", "CalendarDate", "calendarDate",
                "InterchangeEducationOrgCalendar");
    }

    public SmooksGenerator() {
        complexTypes = new HashMap<String, XmlSchemaComplexType>();
        simpleTypes = new HashMap<String, XmlSchemaSimpleType>();
        complexTypesData = new HashMap<String, ComplexTypeData>();

        resourceLoader = new DefaultResourceLoader();
        Resource xsdResource = resourceLoader.getResource(xsdLocation);
        Resource extensionXsdResource = resourceLoader.getResource(extensionXsdLocation);

        // extract complex types from base schema
        cacheTypesFromResource(xsdResource, xsdParentLocation);
        // extract complex types from extension schema
        cacheTypesFromResource(extensionXsdResource, extensionXsdParentLocation);

        // extract data from complex data including hierarchy
        cacheComplexTypeData();
    }

    /**
     * Create smooks mapping based on ed-fi xsd for specified entity
     *
     *
     * @param entityType
     *            - the schema type of the entity
     * @param entityName
     *            - the name by which the entity is refered to in the interchange
     * @param entityRecordType
     *            - the record Type that is given to the entity in ingestion
     * @param interchange
     *            - the intechange the entity appears in
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void createSmooks(String entityType, String entityName, String entityRecordType, String interchange)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element smooks = doc.createElement("smooks-resource-list");
        Element recordBean = doc.createElement("jb:bean");
        doc.appendChild(smooks);
        smooks.appendChild(recordBean);
        recordBean.setAttribute("beanId", "record");
        recordBean.setAttribute("class", "org.slc.sli.ingestion.NeutralRecord");
        recordBean.setAttribute("createOnElement", interchange + "/" + entityName);

        Element expressionBean = doc.createElement("jb:expression");
        expressionBean.setAttribute("property", "recordType");
        expressionBean.setTextContent("\'" + entityRecordType + "\'");
        recordBean.appendChild(expressionBean);

        Element wiringBean = doc.createElement("jb:wiring");
        wiringBean.setAttribute("property", "attributes");
        wiringBean.setAttribute("beanIdRef", entityType + "_Attributes");
        recordBean.appendChild(wiringBean);

        String baseXPath = entityName;
        String beanName = entityType + "_Attributes";
        // recurse through creating xml beans from each nested complexTypeData
        createBean(entityType, beanName, baseXPath, interchange, doc, smooks);

        outputXml(doc);
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void createBean(String complexType, String beanName, String baseXPath, String interchange, Document doc,
            Element smooksNode) {
        ComplexTypeData data = complexTypesData.get(complexType);
        if (data == null) {
            System.out.println("no data found for type " + complexType);
            return;
        }

        Element bean = doc.createElement("jb:bean");

        smooksNode.appendChild(bean);

        bean.setAttribute("beanId", beanName);
        bean.setAttribute("class", "java.util.HashMap");
        bean.setAttribute("createOnElement", interchange + "/" + baseXPath);

        for (String attributeName : data.getAttributes()) {
            Element attr = doc.createElement("jb:value");
            bean.appendChild(attr);
            attr.setAttribute("property", attributeName);
            attr.setAttribute("data", baseXPath + "/@" + attributeName);
        }

        for (Entry<String, ElementData> elementEntry : data.getElements().entrySet()) {

            String elemName = elementEntry.getKey();
            ElementData elemData = elementEntry.getValue();
            String elemType = elemData.getElementType();

            if (elemType != null && complexTypesData.containsKey(elemType)) {

                String nestedBeanName = beanName + "_" + elemType;

                if (elemData.isList()) {
                    String nestedListBeanName = nestedBeanName + "Array";
                    // create the list bean
                    Element listbean = doc.createElement("jb:bean");
                    smooksNode.appendChild(listbean);

                    listbean.setAttribute("beanId", nestedListBeanName);
                    listbean.setAttribute("class", "java.util.ArrayList");
                    listbean.setAttribute("createOnElement", baseXPath);

                    // wire the new bean into the listbean
                    Element listElem = doc.createElement("jb:wiring");
                    listElem.setAttribute("beanIdRef", nestedBeanName);
                    listbean.appendChild(listElem);

                    // wire the listbean into the bean
                    Element elem = doc.createElement("jb:wiring");
                    elem.setAttribute("property", elemName);
                    elem.setAttribute("beanIdRef", nestedListBeanName);
                    bean.appendChild(elem);
                } else {
                    Element elem = doc.createElement("jb:wiring");
                    elem.setAttribute("property", elemName);
                    elem.setAttribute("beanIdRef", nestedBeanName);
                    bean.appendChild(elem);
                }

                // recursively add the nested beans
                createBean(elemType, nestedBeanName, baseXPath + "/" + elemName, interchange, doc, smooksNode);
            } else {
                // TODO add decoder here
                Element elem = doc.createElement("jb:value");
                bean.appendChild(elem);
                elem.setAttribute("property", elemName);
                elem.setAttribute("data", baseXPath + "/" + elemName);
            }
        }
    }

    @SuppressWarnings("PMD.AvoidPrintStackTrace")    // smooks support soon to be removed
    private void outputXml(Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Output to console for testing
            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void cacheComplexTypeData() {
        for (Entry<String, XmlSchemaComplexType> complexTypeEntry : complexTypes.entrySet()) {
            ComplexTypeData complexData = new ComplexTypeData();

            // add the top level elements and attributes
            addComplexTypeData(complexTypeEntry.getValue(), complexData);

            // add the base level elements and attributes
            String baseTypeName = extractBaseTypeName(complexTypeEntry.getValue());
            while (baseTypeName != null) {
                XmlSchemaComplexType baseType = complexTypes.get(baseTypeName);
                addComplexTypeData(baseType, complexData);
                baseTypeName = extractBaseTypeName(baseType);
            }

            complexTypesData.put(complexTypeEntry.getKey(), complexData);
        }
    }

    private void addComplexTypeData(XmlSchemaComplexType schemaType, ComplexTypeData complexTypeData) {
        XmlSchemaObjectCollection attributes = schemaType.getAttributes();
        int numElements = attributes.getCount();
        parseParticleForElements(extractParticle(schemaType), complexTypeData);

        // Iterate XML Schema items
        for (int i = 0; i < numElements; i++) {
            XmlSchemaAttribute schemaAttribute = (XmlSchemaAttribute) attributes.getItem(i);
            complexTypeData.getAttributes().add(schemaAttribute.getName());
        }
    }

    /**
     * Recursively parse through an XmlSchemaPatricle to the elements
     * filling in the complexTypeData
     */
    private void parseParticleForElements(XmlSchemaParticle particle, ComplexTypeData complexTypeData) {
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                XmlSchemaElement element = (XmlSchemaElement) particle;
                String elementName = element.getName();

                String elementType = null;
                if (element.getSchemaTypeName() != null) {
                    elementType = element.getSchemaTypeName().getLocalPart();
                }

                if (!complexTypeData.getElements().containsKey(elementName)) {
                    ElementData elementData = new ElementData();
                    // check if it is a list (>1)
                    if (element.getMaxOccurs() > 1) {
                        elementData.setIsList(true);
                    } else {
                        elementData.setIsList(false);
                    }
                    elementData.setElementType(elementType);
                    complexTypeData.getElements().put(elementName, elementData);
                }

            } else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticleForElements((XmlSchemaParticle) item, complexTypeData);
                    }
                }
            } else if (particle instanceof XmlSchemaChoice) {
                XmlSchemaChoice xmlSchemaChoice = (XmlSchemaChoice) particle;
                XmlSchemaObjectCollection choices = xmlSchemaChoice.getItems();
                for (int i = 0; i < choices.getCount(); i++) {
                    XmlSchemaObject item = xmlSchemaChoice.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticleForElements((XmlSchemaParticle) item, complexTypeData);
                    }
                }
            }
        }
    }

    /**
     * Extract a particle from a complex type
     * returns null if it can't be extracted.
     */
    private XmlSchemaParticle extractParticle(XmlSchemaComplexType complexType) {
        XmlSchemaParticle particle = complexType.getParticle();

        // handle case where the complexType is an extension
        if (particle == null && complexType.getContentModel() != null
                && complexType.getContentModel().getContent() != null) {
            XmlSchemaContent content = complexType.getContentModel().getContent();
            if (content instanceof XmlSchemaComplexContentExtension) {
                XmlSchemaComplexContentExtension complexContent = (XmlSchemaComplexContentExtension) content;
                particle = complexContent.getParticle();
            } else if (content instanceof XmlSchemaComplexContentRestriction) {
                XmlSchemaComplexContentRestriction complexContent = (XmlSchemaComplexContentRestriction) content;
                particle = complexContent.getParticle();
            }
        }

        return particle;
    }

    /**
     * extract complex types from a schema resource and cache in complexTypes
     */
    @SuppressWarnings("PMD.AvoidPrintStackTrace")    // smooks support soon to be removed    
    private void cacheTypesFromResource(Resource schemaResource, String baseXsdPath) {
        try {
            // parse the xsd schema and pull out complex types
            XmlSchema xmlSchema = parseXmlSchema(schemaResource.getInputStream(), baseXsdPath);
            cacheTypes(xmlSchema);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("PMD.DoNotThrowExceptionInFinally")    // smooks support soon to be removed
    private XmlSchema parseXmlSchema(final InputStream is, final String baseXsdPath) {
        try {
            XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
            // schemaCollection.setBaseUri(baseUri);
            schemaCollection.setSchemaResolver(new URIResolver() {
                @Override
                public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
                    if (resourceLoader != null) {
                        Resource resource = resourceLoader.getResource(baseXsdPath + "/" + schemaLocation);
                        if (resource.exists()) {
                            try {
                                return new InputSource(resource.getInputStream());
                            } catch (IOException e) {
                                throw new RuntimeException("Exception occurred", e);
                            }
                        }
                    }
                    return new InputSource(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream(baseXsdPath + "/" + schemaLocation));
                }
            });
            return schemaCollection.read(new InputSource(is), null);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            try {
                is.close();
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
    }

    private static final String COMPLEX_OBJECT_TYPE = "ComplexObjectType";
    private static final String REFERENCE_TYPE = "ReferenceType";

    /**
     * Extract a particle from a complex type, respecting both extensions and restrictions
     * returns null if there isn't one.
     */
    private String extractBaseTypeName(XmlSchemaComplexType complexType) {
        String baseTypeName = null;

        if (complexType.getBaseSchemaTypeName() != null) {
            baseTypeName = complexType.getBaseSchemaTypeName().getLocalPart();
            // don't go as far as ComplexObjectType or ReferenceType - we never map these
            if (baseTypeName.equals(COMPLEX_OBJECT_TYPE) || baseTypeName.equals(REFERENCE_TYPE)) {
                baseTypeName = null;
            }
        }

        return baseTypeName;
    }

    /**
     * extract all complex types from a schema and cache into a map
     */
    private void cacheTypes(XmlSchema schema) {
        XmlSchemaObjectCollection schemaItems = schema.getItems();

        int numElements = schemaItems.getCount();

        // Iterate XML Schema items
        for (int i = 0; i < numElements; i++) {
            XmlSchemaObject schemaObject = schemaItems.getItem(i);
            if (schemaObject instanceof XmlSchemaComplexType) {
                XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaObject;
                String elementTypeName = complexType.getName();
                complexTypes.put(elementTypeName, complexType);
            } else if (schemaObject instanceof XmlSchemaSimpleType) {
                XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) schemaObject;
                String elementTypeName = simpleType.getName();
                simpleTypes.put(elementTypeName, simpleType);
            }
        }
    }

}
