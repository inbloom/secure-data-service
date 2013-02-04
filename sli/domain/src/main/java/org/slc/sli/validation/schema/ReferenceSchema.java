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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * SLI reference schema that validates entity references
 *
 * @author srupasinghe
 *
 */
public class ReferenceSchema extends NeutralSchema {
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceSchema.class);

    private SchemaRepository schemaRepository;

    public ReferenceSchema(String xsdType, SchemaRepository schemaRepository) {
        super(xsdType);
        this.schemaRepository = schemaRepository;
    }

    // Methods
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.REFERENCE;
    }

    /**
     * Identifies what entity type this schema references.
     *
     * @return a entity type/collection name this reference refers to
     */
    public String getEntityType() {
        String collectionName = getAppInfo().getReferenceType();

        if (schemaRepository != null) {
            if (schemaRepository.getSchema(collectionName).getAppInfo() != null
                    && schemaRepository.getSchema(collectionName).getAppInfo().getCollectionType() != null) {
                collectionName = schemaRepository.getSchema(collectionName).getAppInfo().getCollectionType();
            }
        }

        return collectionName;
    }

    @Override
    public Object convert(Object value) {
        return value;
    }

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param fieldName
     *            name of entity field being validated
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        boolean isValidReference = isValidReference(entity);
        if (!addError(isValidReference, fieldName, entity, "String", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }

        boolean found = false;
        String collectionType = getEntityType();

        try {
            // try to find an entity with the given id
            found = repo.exists(collectionType, (String) entity);
        } catch (Exception e) {
            // repo.find is currently throwing multiple kinds of exceptions so we will catch all for
            // now, as we sort out what is thrown and why
            LOG.debug("Exception when looking up reference in repository. ", e);
        }

        // if not found add the appropriate error
        if (!found) {
            LOG.debug("Broken reference in {}, {}, {}", new Object[] { entity, fieldName, errors });

            addError(false, fieldName, entity, "Valid reference", ErrorType.REFERENTIAL_INFO_MISSING, errors);
            return false;
        }

        return true;
    }

    private boolean isValidReference(Object reference) {
        // reference must be a non-null, non-empty String
        if (reference != null && String.class.isInstance(reference)) {
            String referenceString  = (String) reference;
            return (!referenceString.isEmpty());
        }

        return false;
    }

}
