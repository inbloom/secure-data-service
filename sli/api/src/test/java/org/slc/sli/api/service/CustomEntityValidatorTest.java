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

package org.slc.sli.api.service;

import java.util.HashMap;
import java.util.ListIterator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

/**
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class CustomEntityValidatorTest {

    @Autowired
    private CustomEntityValidator customEntityValidator;

    private static final String CUSTOM_ENTITY_COLLECTION = "custom_entities";

    @Test(expected = EntityValidationException.class)
    public void testInvalidFieldNameConsistingOfNullChar() {
        String invalidCustomEntityId = "invalidCustomEntity1";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("\u0000", "goodFieldValue");

        customEntityValidator.validate(invalidCustomEntityId, CUSTOM_ENTITY_COLLECTION, invalidCustomEntity);
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidFieldNameConsistingOfNullString() {
        String invalidCustomEntityId = "invalidCustomEntity1";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("\\x00", "goodFieldValue");

        customEntityValidator.validate(invalidCustomEntityId, CUSTOM_ENTITY_COLLECTION, invalidCustomEntity);
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidFieldNameContainingNullChar() {
        String invalidCustomEntityId = "invalidCustomEntity2";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("bad\u0000FieldName", "goodFieldValue");

        customEntityValidator.validate(invalidCustomEntityId, CUSTOM_ENTITY_COLLECTION, invalidCustomEntity);
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidFieldNameContainingNullString() {
        String invalidCustomEntityId = "invalidCustomEntity2";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("bad\\x00FieldName", "goodFieldValue");

        customEntityValidator.validate(invalidCustomEntityId, CUSTOM_ENTITY_COLLECTION, invalidCustomEntity);
    }

    @SuppressWarnings("serial")
    @Test(expected = EntityValidationException.class)
    public void testInvalidNestedFieldNameContainingNullChar() {
        final String invalidCustomEntityId = "invalidCustomEntity3";
        EntityBody invalidCustomEntity = new EntityBody() {
            {
                put("ID", invalidCustomEntityId);
                put("nestedField", new HashMap<String, Object>() {
                    {
                        put("fieldId", "goodIdValue");
                        put("bad\u0011FieldName", "goodFieldValue");
                    }
                });
            }

        };

        customEntityValidator.validate(invalidCustomEntityId, CUSTOM_ENTITY_COLLECTION, invalidCustomEntity);
    }

    @SuppressWarnings("serial")
    @Test
    public void testMultipleInvalidFieldNamesAndErrors() {
        final String invalidCustomEntityId = "invalidCustomEntity3";
        EntityBody invalidCustomEntity = new EntityBody() {
            {
                put("ID", invalidCustomEntityId);
                put("FSCommand = DEL", "goodFieldValue");
                put("nestedField", new HashMap<String, Object>() {
                    {
                        put("fieldId", "goodIdValue");
                        put("bad\u0011FieldName", "goodFieldValue");
                    }
                });
            }
        };

        try {
            customEntityValidator.validate(invalidCustomEntityId, CUSTOM_ENTITY_COLLECTION, invalidCustomEntity);
            Assert.fail("Expected EntityValidationException");
        } catch (EntityValidationException eve) {
            Assert.assertEquals("Mismatched entity ID in EntityValidationException", eve.getEntityId(), invalidCustomEntityId);
            Assert.assertEquals("Mismatched entity type in EntityValidationException", eve.getEntityType(), CUSTOM_ENTITY_COLLECTION);
            ListIterator<ValidationError> validationErrors = eve.getValidationErrors().listIterator();
            Assert.assertEquals("Should be 2 validation errors", 2, eve.getValidationErrors().size());
            ValidationError ve1 = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_CONTENT,
                    "FSCommand = DEL", "FSCommand = DEL", null);
            Assert.assertEquals("Mismatched validation error", validationErrors.next().toString(), ve1.toString());
            ValidationError ve2 = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_CONTENT,
                    "bad\u0011FieldName", "bad\u0011FieldName", null);
            Assert.assertEquals("Mismatched validation error", validationErrors.next().toString(), ve2.toString());
        }
    }

    @Test
    public void testValidFieldNameWithoutBrackets() {
        String validCustomEntityId = "validCustomEntity1";
        EntityBody validCustomEntity = new EntityBody();
        validCustomEntity.put("ID", validCustomEntityId);
        validCustomEntity.put("goodFieldName", "goodFieldValue");

        customEntityValidator.validate(validCustomEntityId, CUSTOM_ENTITY_COLLECTION, validCustomEntity);
    }

    @Test
    public void testValidFieldNameWithBrackets() {
        String validCustomEntityId = "validCustomEntity2";
        EntityBody validCustomEntity = new EntityBody();
        validCustomEntity.put("ID", validCustomEntityId);
        validCustomEntity.put("<goodFieldName>", "goodFieldValue");

        customEntityValidator.validate(validCustomEntityId, CUSTOM_ENTITY_COLLECTION, validCustomEntity);
    }
}
