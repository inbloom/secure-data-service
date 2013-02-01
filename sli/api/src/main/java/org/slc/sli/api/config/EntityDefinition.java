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

package org.slc.sli.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.ReferenceSchema;

/**
 * Definition of an entity resource
 *
 * @author nbrown
 *
 */
public class EntityDefinition {

    private final String type;
    private final String resourceName;
    private final EntityService service;
    private final String collectionName;
    private final List<EntityDefinition> referencingEntities; // entities that reference this entity
    private static Repository<Entity> defaultRepo;
    private NeutralSchema schema;
    private LinkedHashMap<String, ReferenceSchema> referenceFields; // all fields on this entity
                                                                    // that reference other entities
    private final boolean supportsAggregates;
    private final boolean skipContextValidation;
    private final boolean wrapperEntity;

    protected EntityDefinition(String type, String resourceName, String collectionName, EntityService service,
            boolean supportsAggregates) {
        this(type, resourceName, collectionName, service, supportsAggregates, false, false);
    }

    protected EntityDefinition(String type, String resourceName, String collectionName, EntityService service,
            boolean supportsAggregates, boolean skipContextValidation, boolean pullTypeFromEntity) {
        this.type = type;
        this.resourceName = resourceName;
        this.collectionName = collectionName;
        this.service = service;
        this.referencingEntities = new LinkedList<EntityDefinition>();
        this.supportsAggregates = supportsAggregates;
        this.skipContextValidation = skipContextValidation;
        this.wrapperEntity = pullTypeFromEntity;
    }

    public boolean hasArrayField(String fieldName) {
        if (this.schema == null || this.schema.getFields() == null) {
            return false;
        }

        NeutralSchema fieldSchema = this.schema.getFields().get(fieldName);
        if (fieldSchema == null) {
            return false;
        }

        return fieldSchema.getSchemaType() == NeutralSchemaType.LIST;
    }

    public NeutralSchema getSchema() {
        return this.schema;
    }

    /**
     * Associates a schema to an entity definition. This also has a side effect of scanning the
     * fields for any reference fields and recording them
     * for later access via "getReferenceFields()".
     *
     *
     * @param neutralSchema
     *            schema that can identify a valid instance of this entity type
     */
    public void setSchema(NeutralSchema neutralSchema) {
        // store reference
        this.schema = neutralSchema;

        // create separate map just for reference fields
        this.referenceFields = new LinkedHashMap<String, ReferenceSchema>();

        // confirm schema was loaded
        if (this.schema != null) {
            addRefs("", neutralSchema);
        }
    }

    private void addRefs(String name, NeutralSchema schema) {
        if (schema instanceof ListSchema) {
            for (NeutralSchema schemaInList : ((ListSchema) schema).getList()) {
                addRefs(name, schemaInList);
            }
        } else if (schema instanceof ReferenceSchema) {
            // if field is a reference field
            this.referenceFields.put(name, (ReferenceSchema) schema);
        } else if (schema instanceof ComplexSchema) {
            // loop through all fields
            for (Map.Entry<String, NeutralSchema> entry : schema.getFields().entrySet()) {
                if (!Arrays.asList(name.split("\\.")).contains(entry.getKey())) {
                    String fieldName = (name.equals("") ? "" : name + ".") + entry.getKey();
                    NeutralSchema fieldSchema = entry.getValue();
                    addRefs(fieldName, fieldSchema);
                }
            }
        }
    }

    /**
     * Returns the names of all fields that are reference fields associated to a particular
     * collection.
     *
     * @param resource
     *            the desired collection
     * @return
     */
    public Iterable<String> getReferenceFieldNames(String resource) {
        ArrayList<String> fieldNames = new ArrayList<String>();
        for (Entry<String, ReferenceSchema> referenceField : this.referenceFields.entrySet()) {
            if (referenceField.getValue().getEntityType().equals(resource)) {
                fieldNames.add(referenceField.getKey());
            }
        }
        return fieldNames;
    }

    /**
     * Returns a map of all fields that are references from the field name to the collection
     * referenced.
     *
     * @return map of field names to collections referenced
     */
    public final Map<String, ReferenceSchema> getReferenceFields() {
        return this.referenceFields;
    }

    public final void addReferencingEntity(EntityDefinition entityDefinition) {
        if (!this.referencingEntities.contains(entityDefinition)) {
            this.referencingEntities.add(entityDefinition);
        }
    }

    /**
     * Returns a collection of all entities that reference this entity definition.
     *
     * @return collection of all entities that reference this entity definition
     */
    public final Collection<EntityDefinition> getReferencingEntities() {
        return this.referencingEntities;
    }

    public String getStoredCollectionName() {
        return this.collectionName;
    }

    public String getType() {
        return type;
    }

    /**
     * The name of the resource name in the ReST URI
     *
     * @return
     */
    public String getResourceName() {
        return resourceName;
    }

    public EntityService getService() {
        return service;
    }

    public boolean isOfType(String id) {
        return service.exists(id);
    }

    public boolean isRestrictedForLogging() {
        if (schema != null) {
            return schema.isRestrictedForLogging();
        }
        return false;
    }

    public static void setDefaultRepo(Repository<Entity> defaultRepo) {
        EntityDefinition.defaultRepo = defaultRepo;
    }

    public static Repository<Entity> getDefaultRepo() {
        return defaultRepo;
    }

    /**
     * Whether or not the entity definition supports aggregates
     *
     * @return
     */
    public boolean supportsAggregates() {
        return supportsAggregates;
    }

    /**
     * Skip pre-filter context validation for this entity.
     *
     * @return
     */
    public boolean skipContextValidation() {
        return skipContextValidation;
    }

    /**
     * The entity is a wrapper for other entities
     * For example, search wraps entities like student, section, etc.
     *
     * @return
     */
    public boolean wrapperEntity() {
        return wrapperEntity;
    }
}
