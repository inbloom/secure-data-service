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

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.SelfReferenceValidator;
import org.slc.sli.validation.ValidationError;

/**
 * Validates an Entity body against the appropriate SLI neutral schema.
 *
 * @author Sean Melody <smelody@wgen.net>
 * @author Ryan Farris <rfarris@wgen.net>
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Component
public class NeutralSchemaValidator implements EntityValidator {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(NeutralSchemaValidator.class);

    // Attributes
    @Autowired
    protected SchemaRepository entitySchemaRegistry;

    @Autowired
    @Qualifier("validationRepo")
    protected Repository<Entity> validationRepo;

    @Autowired
    @Qualifier("simpleValidationRepo")
    private Repository<Entity> simpleValidationRepo;

    @Autowired
    private SelfReferenceValidator selfReferenceValidator;

	// Constructors
    public NeutralSchemaValidator() {
        // necessary for instantiation via Spring 
    }

    public NeutralSchemaValidator(SchemaRepository entitySchemaRegistry) {
        this.entitySchemaRegistry = entitySchemaRegistry;
    }

    // Methods

    /**
     * Validates the given entity using its SLI Neutral Schema.
     */
    @Override
    public boolean validate(Entity entity) throws EntityValidationException {

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema == null) {
            LOG.warn("No schema associatiated for type {}", entity.getType());
            return true;
        }

        List<ValidationError> errors = new LinkedList<ValidationError>();
        boolean valid = schema.validate("", entity.getBody(), errors, validationRepo);
        if (!valid) {
            LOG.debug("Errors detected in {}, {}", new Object[]{entity.getEntityId(), errors});
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), errors);
        }

        valid = selfReferenceValidator.validate(entity, errors);
        if (!valid) {
            LOG.debug("Errors detected in {}, {}", new Object[]{entity.getEntityId(), errors});
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), errors);
        }

        return true;
    }

    public void setSchemaRegistry(SchemaRepository schemaRegistry) {
        entitySchemaRegistry = schemaRegistry;
    }

    public void setEntityRepository(Repository<Entity> entityRepo) {
        this.validationRepo = entityRepo;
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        if ("false".equals(referenceCheck)) {
            LOG.info("Turning off reference checking");

            setEntityRepository(simpleValidationRepo);
        }

    }
    
    @Override
    public boolean validateNaturalKeys(final Entity entity, boolean clearOriginal) {
        return true;
    }

    @Override
    public boolean validatePresent(Entity entity) throws EntityValidationException {

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema == null) {
            LOG.warn("No schema associatiated for type {}", entity.getType());
            return true;
        }

        List<ValidationError> errors = new LinkedList<ValidationError>();
        boolean valid = schema.validatePresent(entity.getBody(), errors, validationRepo);
        if (!valid) {
            LOG.debug("Errors detected in {}, {}", new Object[]{entity.getEntityId(), errors});
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), errors);
        }

        return true;
    }

    public void setSelfReferenceValidator(
			SelfReferenceValidator selfReferenceValidator) {
		this.selfReferenceValidator = selfReferenceValidator;
	}

}
