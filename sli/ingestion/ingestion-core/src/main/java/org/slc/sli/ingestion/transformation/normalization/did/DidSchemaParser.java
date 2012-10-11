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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Parser for the Ed-Fi XSD and Ed-Fi extension XSD.
 * Responsible for creating deterministic id reference resolution
 * configuration objects, based on XSD annotations.
 *
 * @author jtully
 *
 */
public class DidSchemaParser implements ResourceLoaderAware {

    ResourceLoader resourceLoader;

    //cache for complex types
    Map<String, XmlSchemaComplexType> complexTypes;
    //cache for reference types
    Map<String, XmlSchemaComplexType> referenceTypes;

    //schema type constants
    private static final String REFERENCE_TYPE = "ReferenceType";
    private static final String IDENTITY_TYPE = "IdentityType";

    //Did annotation constants
    private static final String APPLY_KEY_FIELDS = "applyKeyFields";
    private static final String REF_TYPE = "refType";
    private static final String PARENT_REF_TYPE = "parentRefType";
    private static final String KEY_FIELD_NAME = "keyFieldName";
    private static final String PARENT_KEY_FIELD_NAME = "parentKeyFieldName";
    private static final String VALUE_SOURCE = "valueSource";
    private static final String XPATH_PREFIX = "body.";

    private static final Logger LOG = LoggerFactory.getLogger(DidSchemaParser.class);

    String xsdLocation;

    String extensionXsdLocation;

    public String getExtensionXsdLocation() {
        return extensionXsdLocation;
    }

    public void setExtensionXsdLocation(String entensionXsdLocation) {
        this.extensionXsdLocation = entensionXsdLocation;
    }

    public String getXsdLocation() {
        return xsdLocation;
    }

    public void setXsdLocation(String xsdLocation) {
        this.xsdLocation = xsdLocation;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Initialization method, parses XSD for complexTypes and referenceTypes
     */
    @PostConstruct
    public void setup() {
        complexTypes = new HashMap<String, XmlSchemaComplexType>();

        Resource xsdResource = resourceLoader.getResource(xsdLocation);
        Resource extensionXsdResource = resourceLoader.getResource(extensionXsdLocation);

        //extract complex types from base schema
        cacheComplexTypesFromResource(xsdResource);
        //extract complex types from extension schema
        cacheComplexTypesFromResource(extensionXsdResource);

        //extract the reference types from the complexTypes
        cacheReferenceTypes();

        //remove parent types from cache
        removeParentTypesFromCache();
    }

    /**
     * Extract entity configs
     */
    public Map<String, DidEntityConfig> extractEntityConfigs() {
        Map<String, DidEntityConfig> entityConfigs = new HashMap<String, DidEntityConfig>();

        // Iterate XML Schema items
        for (Entry<String, XmlSchemaComplexType> complexType : complexTypes.entrySet()) {

            DidEntityConfig entityConfig = extractEntityConfig(complexType.getValue());
            if (entityConfig != null) {
                entityConfigs.put(complexType.getKey(), entityConfig);
            }
        }

        return entityConfigs;
    }

    /**
     * Extract ref configs
     */
    public Map<String, DidRefConfig> extractRefConfigs() {
        Map<String, DidRefConfig> refConfigs = new HashMap<String, DidRefConfig>();

        // Iterate XML Schema items
        for (Entry<String, XmlSchemaComplexType> refType : referenceTypes.entrySet()) {
            DidRefConfig refConfig = extractRefConfig(refType.getValue());
            if (refConfig != null) {
                refConfigs.put(refConfig.getEntityType(), refConfig);
            }
        }

        return refConfigs;
    }

    /**
     * extract complex types from a schema resource and cache in complexTypes
     */
    private void cacheComplexTypesFromResource(Resource schemaResource) {
        try {
            // get the path to the xsd directory
            URI xsdUri = schemaResource.getURI();
            File file = new File(xsdUri.getPath());
            File parentFile = file.getParentFile();

            // parse the xsd schema and pull out complex types
            XmlSchema xmlSchema = parseXmlSchema(schemaResource.getInputStream(), parentFile.getPath());
            cacheComplexTypes(xmlSchema);
        } catch (IOException e) {
            LOG.error("Failed parse schema " + schemaResource.getFilename(), e);
        }
    }

    private XmlSchema parseXmlSchema(final InputStream is, String baseUri) {
        try {
            XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
            schemaCollection.setBaseUri(baseUri);
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

    /**
     * extract all complex types from a schema and cache into a map
     */
    private void cacheComplexTypes(XmlSchema schema) {
        XmlSchemaObjectCollection schemaItems = schema.getItems();

        int numElements = schemaItems.getCount();

        // Iterate XML Schema items
        for (int i = 0; i < numElements; i++) {
            XmlSchemaObject schemaObject = schemaItems.getItem(i);
            if (schemaObject instanceof XmlSchemaComplexType) {
                XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaObject;
                String elementTypeName = complexType.getName();
                complexTypes.put(elementTypeName, complexType);
            }
        }
    }

    /**
     * Remove parent types from the complexTypes cache.
     * We are only interested in the leaf node extended types.
     */
    private void removeParentTypesFromCache() {
        //find all the parent types
        Set<String> parentTypeSet = new HashSet<String>();

        for (XmlSchemaComplexType complexType : complexTypes.values()) {
            //this needs to also respect restriction
            String baseName = extractBaseTypeName(complexType);
            if (baseName != null) {
                parentTypeSet.add(baseName);
            }
        }

        //remove all the parentTypes from cache
        for (String parentType : parentTypeSet) {
            complexTypes.remove(parentType);
            referenceTypes.remove(parentType);
        }
    }

    /**
     * Extract and cache all reference types from the complexTypes map.
     */
    private void cacheReferenceTypes() {
        referenceTypes = new HashMap<String, XmlSchemaComplexType>();

        // extract referenceTypes from the complexTypes
        for (Entry<String, XmlSchemaComplexType> complexTypeEntry : complexTypes.entrySet()) {
            if (isReferenceType(complexTypeEntry.getValue())) {
                referenceTypes.put(complexTypeEntry.getKey(), complexTypeEntry.getValue());
            }
        }

        // remove the refTypes from the complex types map
        //for (String refTypeName : referenceTypes.keySet()) {
        //    complexTypes.remove(refTypeName);
        //}
    }

    /**
     * determine whether a given complexType is a referenceType
     * by traversing through all baseSchemas looking for ReferenceType
     */
    private boolean isReferenceType(XmlSchemaComplexType complexType) {
        boolean isRef = false;

        String baseName = extractBaseTypeName(complexType);
        while (baseName != null) {
            if (baseName.equals(REFERENCE_TYPE)) {
                isRef = true;
                baseName = null;
            } else if (complexTypes.containsKey(baseName)) {
                XmlSchemaComplexType baseType = complexTypes.get(baseName);
                if (baseType != null) {
                    baseName = extractBaseTypeName(baseType);
                } else {
                    baseName = null;
                }
            } else {
                baseName = null;
            }
        }

        return isRef;
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
            }
        }

        return particle;
    }

    /**
     * Extract a particle from a complex type, respecting both extensions and restrictions
     * returns null if there isn't one.
     */
    private String extractBaseTypeName(XmlSchemaComplexType complexType) {
        String baseTypeName = null;

        if (complexType.getBaseSchemaTypeName() != null) {
            baseTypeName = complexType.getBaseSchemaTypeName().getLocalPart();
        } else if (complexType.getContentModel() != null
                && complexType.getContentModel().getContent() != null) {
            XmlSchemaContent content = complexType.getContentModel().getContent();
            if (content instanceof XmlSchemaComplexContentRestriction) {
                XmlSchemaComplexContentRestriction contentRestriction = (XmlSchemaComplexContentRestriction) content;
                if (contentRestriction.getBaseTypeName() != null) {
                    baseTypeName = contentRestriction.getBaseTypeName().getLocalPart();
                }
            }
        }

        return baseTypeName;
    }

    /**
     * Extract refConfig for a refType
     */
    private DidRefConfig extractRefConfig(XmlSchemaComplexType refType) {
        // get the identityType out of the refType
        DidRefConfig refConfig = null;

        // find the identity type element
        XmlSchemaElement identityTypeElement = null;

        identityTypeElement = parseParticleForIdentityType(extractParticle(refType));

        if (identityTypeElement == null) {
            LOG.error("Failed to extract IdentityType for referenceType " + refType.getName());
            return null;
        }

        XmlSchemaComplexType identityType = null;
        identityType = complexTypes.get(identityTypeElement.getSchemaTypeName().getLocalPart());
        String baseXPath = identityTypeElement.getName() + ".";
        List<Map<String, String>> keyFieldMaps = new ArrayList<Map<String, String>>();

        parseParticleForKeyFields(extractParticle(identityType), keyFieldMaps, baseXPath);

        if (keyFieldMaps.isEmpty()) {
            return null;
        }

        try {
            refConfig = createRefConfigFromMap(keyFieldMaps);
        } catch (Exception e) {
            LOG.error("Failed to create a DidRefConfig from keyFieldMaps " + keyFieldMaps, e);
        }

        return refConfig;
    }

    /**
     * create a nested ref config from a keyFieldsMap
     */
    private DidRefConfig createRefConfigFromMap(List<Map<String, String>> keyFieldMaps) {
        DidRefConfig refConfig = new DidRefConfig();
        refConfig.setKeyFields(new ArrayList<KeyFieldDef>());
        List<Map<String, String>> nestedMaps = new ArrayList<Map<String, String>>();

        // extract the base level key fields
        for (Map<String, String> keyFieldMap : keyFieldMaps) {
            if (keyFieldMap.containsKey(PARENT_KEY_FIELD_NAME)) {
                nestedMaps.add(keyFieldMap);
            } else {
                refConfig.setEntityType(keyFieldMap.get(REF_TYPE));
                KeyFieldDef keyField = new KeyFieldDef();
                keyField.setKeyFieldName(keyFieldMap.get(KEY_FIELD_NAME));
                keyField.setValueSource(keyFieldMap.get(VALUE_SOURCE));
                refConfig.getKeyFields().add(keyField);
            }
        }

        Set<String> refConfigTypes = new HashSet<String>();
        refConfigTypes.add(refConfig.getEntityType());

        // add nested keyFields
        while (!nestedMaps.isEmpty()) {
            List<Map<String, String>> toRemove = new ArrayList<Map<String, String>>();
            for (Map<String, String> keyFieldMap : nestedMaps) {
                String parentRefType = keyFieldMap.get(PARENT_REF_TYPE);

                // don't try to add a nested refConfig until the parent refConfig has been added
                if (!refConfigTypes.contains(parentRefType)) {
                    continue;
                }

                String parentKeyFieldName = keyFieldMap.get(PARENT_KEY_FIELD_NAME);

                // create the nested keyField
                KeyFieldDef keyField = new KeyFieldDef();
                keyField.setKeyFieldName(keyFieldMap.get(KEY_FIELD_NAME));
                keyField.setValueSource(keyFieldMap.get(VALUE_SOURCE));

                // find the parent node
                KeyFieldDef parentNode = findParentRefNode(refConfig, parentRefType, parentKeyFieldName);
                if (parentNode == null) {
                    LOG.error("Failed to find parent node for keyField " + keyFieldMap);
                    return null;
                }

                // create the nested refConfig if required
                if (parentNode.getRefConfig() == null) {
                    parentNode.setValueSource(null);
                    DidRefConfig nestedRefConfig = new DidRefConfig();
                    nestedRefConfig.setKeyFields(new ArrayList<KeyFieldDef>());
                    nestedRefConfig.setEntityType(keyFieldMap.get(REF_TYPE));
                    parentNode.setRefConfig(nestedRefConfig);

                    // add to the refConfigTypes lookup set
                    refConfigTypes.add(parentRefType);
                }

                // add the keyField to the parentRefConfig
                parentNode.getRefConfig().getKeyFields().add(keyField);

                toRemove.add(keyFieldMap);
            }
            // check for infinite loop
            if (toRemove.isEmpty() && !nestedMaps.isEmpty()) {
                LOG.error("Failed in adding nested refConfigs");
                break;
            }

            // remove all the nested maps that have been added
            for (Map<String, String> keyFieldMap : toRemove) {
                nestedMaps.remove(keyFieldMap);
            }
        }

        return refConfig;
    }

    /**
     * Traverse through nested refConfigs until the parent KeyFieldDef node is found
     */
    private KeyFieldDef findParentRefNode(DidRefConfig baseRefConfig, String parentRefType, String parentKeyFieldName) {
        // List to hold the refConfigs at each level
        List<DidRefConfig> refConfigList = new ArrayList<DidRefConfig>();
        refConfigList.add(baseRefConfig);

        while (!refConfigList.isEmpty()) {
            // keep track of the refConfigs in the next level
            List<DidRefConfig> nextRefConfigList = new ArrayList<DidRefConfig>();

            for (DidRefConfig refConfig : refConfigList) {
                String refType = refConfig.getEntityType();
                if (!refType.equals(parentRefType)) {
                    continue;
                }

                KeyFieldDef parentKeyField = null;
                // try to find the parentKeyField
                for (KeyFieldDef keyField : refConfig.getKeyFields()) {
                    // check for a match
                    if (keyField.getKeyFieldName().equals(parentKeyFieldName)) {
                        parentKeyField = keyField;
                        break;
                    }

                    // add any nested refConfigs to the
                    if (keyField.getRefConfig() != null) {
                        nextRefConfigList.add(keyField.getRefConfig());
                    }
                }
                // if a parentKeyField wasn't found, create one
                if (parentKeyField == null) {
                    parentKeyField = new KeyFieldDef();
                    parentKeyField.setKeyFieldName(parentKeyFieldName);
                    refConfig.getKeyFields().add(parentKeyField);
                }
                return parentKeyField;
            }
            refConfigList = nextRefConfigList;
        }

        return null;
    }

    /**
     * Extract a DidEntityConfig for a ComplexType.
     * Returns null if the reference contains no DID references.
     */
    private DidEntityConfig extractEntityConfig(XmlSchemaComplexType complexType) {
        DidEntityConfig entityConfig = null;

        List<DidRefSource> refSources = new ArrayList<DidRefSource>();
        parseParticleForRef(extractParticle(complexType), refSources);

        // if any DidRefSources were found for this complex type, create a DidEntityConfig
        if (refSources.size() > 0) {
            entityConfig = new DidEntityConfig();
            entityConfig.setReferenceSources(refSources);
        }

        return entityConfig;
    }

    /**
     * Recursively parse through an XmlSchemaPatricle to the elements
     * collecting all DidRefSources
     */
    private XmlSchemaElement parseParticleForIdentityType(XmlSchemaParticle particle) {
        XmlSchemaElement identityType = null;
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                XmlSchemaElement element = (XmlSchemaElement) particle;
                String elementName = element.getSchemaTypeName().getLocalPart();
                if (elementName.contains(IDENTITY_TYPE)) {
                    identityType = element;
                }
            } else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        identityType = parseParticleForIdentityType((XmlSchemaParticle) item);
                    }
                }
            }
        }
        return identityType;
    }

    /**
     * Recursively parse through an XmlSchemaPatricle to the elements
     * collecting all KeyFieldDefs
     */
    private void parseParticleForKeyFields(XmlSchemaParticle particle, List<Map<String, String>> keyFields,
            String baseXPath) {
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                XmlSchemaElement element = (XmlSchemaElement) particle;
                String elementName = element.getName();

                Map<String, String> keyFieldMap = parseAnnotationForKeyField(element.getAnnotation());
                if (!keyFieldMap.isEmpty()) {
                    keyFieldMap.put(VALUE_SOURCE, baseXPath + elementName);
                    // add the base key fields
                    keyFields.add(keyFieldMap);
                }

            } else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticleForKeyFields((XmlSchemaParticle) item, keyFields, baseXPath);
                    }
                }
            } else if (particle instanceof XmlSchemaChoice) {
                XmlSchemaChoice xmlSchemaChoice = (XmlSchemaChoice) particle;
                XmlSchemaObjectCollection choices = xmlSchemaChoice.getItems();
                for (int i = 0; i < choices.getCount(); ++i) {
                    XmlSchemaObject item = xmlSchemaChoice.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticleForKeyFields((XmlSchemaParticle) item, keyFields, baseXPath);
                    }
                }
            }
        }
    }

    /**
     * Recursively parse through an XmlSchemaPatricle to the elements
     * collecting all DidRefSources
     */
    private void parseParticleForRef(XmlSchemaParticle particle, List<DidRefSource> refs) {
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                XmlSchemaElement element = (XmlSchemaElement) particle;
                String elementName = element.getName();
                QName elementType = element.getSchemaTypeName();

                if (elementType != null && referenceTypes.containsKey(elementType.getLocalPart())) {

                    // TODO, this could be pre-computed for all refTypes to avoid some repetition
                    XmlSchemaComplexType refSchema = referenceTypes.get(elementType.getLocalPart());

                    DidRefSource refSource = parseAnnotationForRef(refSchema.getAnnotation());
                    if (refSource != null) {
                        refSource.setSourceRefPath(XPATH_PREFIX + elementName);
                        refs.add(refSource);
                    }
                }
            } else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticleForRef((XmlSchemaParticle) item, refs);
                    }
                }
            } else if (particle instanceof XmlSchemaChoice) {
                XmlSchemaChoice xmlSchemaChoice = (XmlSchemaChoice) particle;
                XmlSchemaObjectCollection choices = xmlSchemaChoice.getItems();
                for (int i = 0; i < choices.getCount(); ++i) {
                    XmlSchemaObject item = xmlSchemaChoice.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticleForRef((XmlSchemaParticle) item, refs);
                    }
                }
            }
        }
    }

    /**
     * Get SLI appInfor from an annotation
     */
    private XmlSchemaAppInfo getAppInfo(XmlSchemaAnnotation annotation) {
        XmlSchemaAppInfo appInfo = null;
        XmlSchemaObjectCollection items = annotation.getItems();

        for (int annotationIdx = 0; annotationIdx < items.getCount(); ++annotationIdx) {

            XmlSchemaObject item = items.getItem(annotationIdx);
            if (item instanceof XmlSchemaAppInfo) {
                appInfo = (XmlSchemaAppInfo) item;
                break;
            }
        }

        return appInfo;
    }

    /**
     * Parse an annotation for keyfields and add to a map
     */
    private Map<String, String> parseAnnotationForKeyField(XmlSchemaAnnotation annotation) {
        Map<String, String> keyField = new HashMap<String, String>();

        XmlSchemaAppInfo appInfo = getAppInfo(annotation);
        if (appInfo != null) {
            NodeList nodes = appInfo.getMarkup();
            for (int nodeIdx = 0; nodeIdx < nodes.getLength(); nodeIdx++) {
                Node node = nodes.item(nodeIdx);
                if (node instanceof Element) {
                    String key = node.getLocalName().trim();
                    String value = node.getFirstChild().getNodeValue().trim();

                    if (key.equals(REF_TYPE) || key.equals(KEY_FIELD_NAME) || key.equals(PARENT_REF_TYPE)
                            || key.equals(PARENT_KEY_FIELD_NAME)) {
                        keyField.put(key, value);
                    }
                }
            }
        }
        return keyField;
    }

    /**
     * Parse an annotation for DidRefSource data
     */
    private DidRefSource parseAnnotationForRef(XmlSchemaAnnotation annotation) {
        DidRefSource refSource = null;

        boolean applyKeyFields = false;
        String refType = null;

        XmlSchemaAppInfo appInfo = getAppInfo(annotation);
        if (appInfo != null) {
            // get applyKeyFields and refType from appInfo
            NodeList nodes = appInfo.getMarkup();
            for (int nodeIdx = 0; nodeIdx < nodes.getLength(); nodeIdx++) {
                Node node = nodes.item(nodeIdx);
                if (node instanceof Element) {

                    String key = node.getLocalName().trim();
                    String value = node.getFirstChild().getNodeValue().trim();

                    if (key.equals(APPLY_KEY_FIELDS)) {
                        if (value.equals("true")) {
                            applyKeyFields = true;
                        }
                    } else if (key.equals(REF_TYPE)) {
                        refType = value;
                    }
                }
            }
            if (applyKeyFields && refType != null) {
                refSource = new DidRefSource();
                refSource.setEntityType(refType);
            }
        }

        return refSource;
    }
}
