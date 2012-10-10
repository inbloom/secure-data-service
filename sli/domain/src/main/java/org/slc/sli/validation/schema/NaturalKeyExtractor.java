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
package org.slc.sli.validation.schema;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.EmbedDocumentRelations;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.SchemaRepository;

/**
 * @author sashton
 */
@Component
public class NaturalKeyExtractor implements INaturalKeyExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(NaturalKeyExtractor.class);

    @Autowired
    protected SchemaRepository entitySchemaRegistry;

    public static boolean useDeterministicIds() {

        String nonDeterministicIds = System.getProperty("nonDeterministicIds");
        return nonDeterministicIds == null;

    }

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
            }
            if (value instanceof String) {
                String strValue = (String) value;
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
                    getNaturalKeyFields(naturalKeyFields, schema, "");

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
    private void getNaturalKeyFields(Map<String, Boolean> naturalKeyFields, NeutralSchema schema, String baseXPath) {
        Map<String, NeutralSchema> fields = schema.getFields();
        for (Entry<String, NeutralSchema> fieldEntry : fields.entrySet()) {
            String fieldXPath = baseXPath + fieldEntry.getKey();

            NeutralSchema fieldSchema = fieldEntry.getValue();

            AppInfo fieldsAppInfo = fieldSchema.getAppInfo();
            if (fieldsAppInfo != null) {
                boolean foo = fieldsAppInfo.isNaturalKey();
                if (foo) {
                    if (fieldSchema instanceof ComplexSchema) {
                        getNaturalKeyFields(naturalKeyFields, fieldSchema, fieldXPath + ".");
                    } else {
                        Boolean isOptional = null;
                        if (fieldsAppInfo.isRequired()) {
                            isOptional = new Boolean(false);
                        } else {
                            isOptional = new Boolean(true);
                        }
                        naturalKeyFields.put(fieldXPath, isOptional);
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
        String tenantId = (String) entity.getMetaData().get("tenantId");
        String parentId = retrieveParentId(entity);
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(map, tenantId, entityType, parentId);
        return naturalKeyDescriptor;
    }

    private String retrieveParentId(Entity entity) {
        if (EmbedDocumentRelations.getSubDocuments().contains(entity.getType())) {
            String parentKey = EmbedDocumentRelations.getParentFieldReference(entity.getType());
            String parentId = (String) entity.getBody().get(parentKey);
            return parentId;
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
