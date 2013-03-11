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
package org.slc.sli.validation.schema;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.SchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author sashton
 */
@Component
public class NaturalKeyExtractor implements INaturalKeyExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(NaturalKeyExtractor.class);

    @Autowired
    protected SchemaRepository entitySchemaRegistry;

    @Qualifier("deterministicUUIDGeneratorStrategy")
    @Autowired
    protected UUIDGeneratorStrategy generatorStrategy;

    @Autowired
    private ContainerDocumentHolder containerDocumentHolder;
    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.validation.schema.INaturalKeyExtractor#getNaturalKeys(org.slc.sli.domain.Entity)
     */
    @Override
    public Map<String, String> getNaturalKeys(Entity entity) throws NoNaturalKeysDefinedException {
        Map<String, String> map = new HashMap<String, String>();

        List<String> missingKeys = new ArrayList<String>();

        Map<String, Boolean> naturalKeyFields = getNaturalKeyFields(entity);
        if (naturalKeyFields == null) {
            // natural keys don't apply to this entity
            return null;
        }

        for (Entry<String, Boolean> keyField : naturalKeyFields.entrySet()) {
            // instead use x-paths here?
            // Object value = entity.getBody().get(keyField.getKey());
            Object value = null;
            try {
                value = PropertyUtils.getProperty(entity.getBody(), keyField.getKey());
            } catch (IllegalAccessException e) {
                handleFieldAccessException(keyField.getKey(), entity);
            } catch (InvocationTargetException e) {
                handleFieldAccessException(keyField.getKey(), entity);
            } catch (NoSuchMethodException e) {
                handleFieldAccessException(keyField.getKey(), entity);
            }
            if (value == null) {
                if (keyField.getValue().booleanValue()) {
                    map.put(keyField.getKey(), "");
                } else {
                    // if the required key field is not found, there's a problem
                    missingKeys.add(keyField.getKey());
                }
            } else {
                String strValue = value.toString();
                map.put(keyField.getKey(), strValue);
            }
        }
        if (!missingKeys.isEmpty()) {
            throw new NaturalKeyValidationException(entity.getType(), missingKeys);
        }

        return map;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.validation.schema.INaturalKeyExtractor#getNaturalKeyFields(org.slc.sli.domain
     * .Entity)
     */
    @Override
    public Map<String, Boolean> getNaturalKeyFields(Entity entity) throws NoNaturalKeysDefinedException {

        Map<String, Boolean> naturalKeyFields = null;

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema != null) {

            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                if (appInfo.applyNaturalKeys()) {
                    naturalKeyFields = new HashMap<String, Boolean>();
                    // recursive call to get natural fields
                    getNaturalKeyFields(naturalKeyFields, schema, false, "");

                    if (naturalKeyFields.isEmpty()) {
                        // if no fields are found, there is a problem
                        LOG.error("Failed to find natural key definitions for the " + entity.getType() + " entity");
                        throw new NoNaturalKeysDefinedException(entity.getType());
                    }
                }
            }
        }

        return naturalKeyFields;
    }

    /**
     * Recursive method to traverse down to the leaf nodes of a neutral schema and extract annotated
     * key fields
     */
    private void getNaturalKeyFields(Map<String, Boolean> naturalKeyFields, NeutralSchema schema, boolean fieldSchemaChoice, String baseXPath) {
        Map<String, NeutralSchema> fields = schema.getFields();
        for (Entry<String, NeutralSchema> fieldEntry : fields.entrySet()) {
            String fieldXPath = baseXPath + fieldEntry.getKey();

            NeutralSchema fieldSchema = fieldEntry.getValue();

            AppInfo fieldsAppInfo = fieldSchema.getAppInfo();
            if (fieldsAppInfo != null) {
                boolean isNaturalKey = fieldsAppInfo.isNaturalKey();
                if (isNaturalKey) {
                    if (fieldSchema instanceof ComplexSchema) {
                        getNaturalKeyFields(naturalKeyFields, fieldSchema, fieldSchemaChoice, fieldXPath + ".");
                    } else {
                        Boolean isOptional = !fieldsAppInfo.isRequired() || fieldSchemaChoice;
                        naturalKeyFields.put(fieldXPath, isOptional);
                    }
                }
                else {
                    String schemaClass = fieldSchema.getValidatorClass();
                    if (schemaClass.equals ("org.slc.sli.validation.schema.ChoiceSchema")) {
                        getNaturalKeyFields(naturalKeyFields, fieldSchema, true, fieldXPath + ".");
                    }
                }
            }

        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.validation.schema.INaturalKeyExtractor#getNaturalKeyDescriptor(org.slc.sli.domain
     * .Entity)
     * .Entity)
     */
    @Override
    public NaturalKeyDescriptor getNaturalKeyDescriptor(Entity entity) throws NoNaturalKeysDefinedException {

        Map<String, String> map = getNaturalKeys(entity);
        if (map == null) {
            NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
            naturalKeyDescriptor.setNaturalKeysNotNeeded(true);
            return naturalKeyDescriptor;
        }

        String entityType = getCollectionName(entity);
        String tenantId = TenantContext.getTenantId();
        String parentId = retrieveParentId(entity);
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(map, tenantId, entityType, parentId);
        return naturalKeyDescriptor;
    }

    private String retrieveParentId(Entity entity) {
        if (EmbeddedDocumentRelations.getSubDocuments().contains(entity.getType())) {
            String parentKey = EmbeddedDocumentRelations.getParentFieldReference(entity.getType());
            String parentId = (String) entity.getBody().get(parentKey);
            return parentId;
        } else if (containerDocumentHolder.isContainerDocument(entity.getType())) {
            ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
            if (containerDocument.isContainerSubdoc()) {
                final List<String> parentKeys = containerDocument.getParentNaturalKeys();
                final Map<String, String> naturalKeyMap = new HashMap<String, String>();
                for (final String key : parentKeys) {
                    String value = (String) entity.getBody().get(key);
                    naturalKeyMap.put(key, value);
                }
                return generatorStrategy.generateId(new NaturalKeyDescriptor(naturalKeyMap));

            }
        }

        return null;
    }

    private void handleFieldAccessException(String keyField, Entity entity) {
        LOG.error("Failed to extract field " + keyField + " from " + entity.getType() + "entity");
    }

    /*
     *
     */
    public String getCollectionName(Entity entity) {
        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema != null) {
            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                return appInfo.getCollectionType();
            }
        }
        LOG.error("No collectionType found in schema for entity: " + entity.getType());
        return null;
    }
}
