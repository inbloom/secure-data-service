package org.slc.sli.api.service;

import java.util.ArrayList;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.ValidationError;

/**
 * Mock validator for the dal
 */
@Component
@Primary
public class MockValidator implements EntityValidator {
    
    @Override
    public boolean validate(Entity entity) throws EntityValidationException {
        if (entity.getBody().containsKey("bad-entity")) {
            throw new EntityValidationException(entity.getEntityId(), entity.getType(),
                    new ArrayList<ValidationError>());
        }
        return true;
    }
}
