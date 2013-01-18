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
package org.slc.sli.validation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * Validates whether an entity has a field which references itself
 * @author ablum
 */
@Component
public class SelfReferenceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(SelfReferenceValidator.class);

    @Autowired
    private SelfReferenceExtractor selfReferenceExtractor;

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Autowired
    private INaturalKeyExtractor naturalKeyExtractor;

    public boolean validate(Entity entity, List<ValidationError> errors) {
        String selfReferencePath = selfReferenceExtractor.getSelfReferenceFields(entity);

        if (selfReferencePath != null) {
            Map<String, Object> body = entity.getBody();
            if (body != null) {
                String property = (String) body.get(selfReferencePath);
                String uuid = entity.getEntityId();
                if (uuid == null) {
                    uuid = getDeterministicId(entity);
                }
                if (property != null && property.equals(uuid)) {
                    Map<String, String> naturalKeys = null;
                    try {
                        naturalKeys = naturalKeyExtractor.getNaturalKeys(entity);
                        if (naturalKeys != null && naturalKeys.size() > 0) {
                            property = naturalKeys.toString();
                        }
                    } catch (NoNaturalKeysDefinedException e) {
                        // Nothing can be done with the entity at this point,
                        LOG.error(e.getMessage(), e);
                    }
                    errors.add(new ValidationError(ErrorType.SELF_REFERENCING_DATA, selfReferencePath,
                            property, new String[] { "Reference to a separate entity" }));
                    return false;
                }
                return true;
            }
        }

        return true;
    }


    public String getDeterministicId(Entity entity) {

        final String uid;
        NaturalKeyDescriptor naturalKeyDescriptor;
        try {
            naturalKeyDescriptor = naturalKeyExtractor.getNaturalKeyDescriptor(entity);
        } catch (NoNaturalKeysDefinedException e) {
            // Nothing can be done with the entity at this point,
            // it is supposed to have natural keys, but none were defined.
            // Picking a random UUID would be undesired behavior
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        if (uuidGeneratorStrategy == null) {
            LOG.warn("Generating Type 4 UUID by default because the UUID generator strategy is null.  This will cause issues if this value is being used in a Mongo indexed field (like _id)");
            uid = UUID.randomUUID().toString();
        } else {
            if (naturalKeyDescriptor.isNaturalKeysNotNeeded()) {
                // generate a truly random id
                uid = uuidGeneratorStrategy.generateId();
            } else {
                uid = uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
            }
        }

        return uid.toString();
    }
}
