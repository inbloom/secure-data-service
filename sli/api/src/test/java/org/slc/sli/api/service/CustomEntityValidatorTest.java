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

package org.slc.sli.api.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.validation.ValidationError;

/**
 * Unit test for CustomEntityValidator class.
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class CustomEntityValidatorTest {

    @Autowired
    private CustomEntityValidator customEntityValidator;

    @Test
    public void testEmptyEntity() {
        EntityBody emptyCustomEntity = new EntityBody();

        List<ValidationError> validationErrors = customEntityValidator.validate(emptyCustomEntity);
        Assert.assertTrue("There should be no validation errors", validationErrors.isEmpty());
    }

    @Test
    public void testValidFieldName() {
        String validCustomEntityId = "validCustomEntity1";
        EntityBody validCustomEntity = new EntityBody();
        validCustomEntity.put("ID", validCustomEntityId);
        validCustomEntity.put("goodFieldName", "goodFieldValue");

        List<ValidationError> validationErrors = customEntityValidator.validate(validCustomEntity);
        Assert.assertTrue("There should be no validation errors", validationErrors.isEmpty());
    }

    @SuppressWarnings("serial")
    @Test
    public void testMultipleNestedValidFieldNames() {
        final String validCustomEntityId = "validCustomEntity2";
        EntityBody validCustomEntity = new EntityBody() {
            {
                put("ID", validCustomEntityId);
                put("goodFieldName1", "goodFieldValue");
                put("goodFieldName2", new HashMap<String, Object>() {
                    {
                        put("goodSubFieldName1", "goodFieldValue");
                        put("goodSubFieldName2", "goodFieldValue");
                        put("goodSubFieldName3", new LinkedList<HashMap<String, Object>>() {
                            {
                                add(new HashMap<String, Object>() {
                                    {
                                        put("goodSubFieldName1", "goodFieldValue");
                                    }
                                });
                                add(new HashMap<String, Object>() {
                                    {
                                        put("goodSubFieldName1", "goodFieldValue");
                                        put("goodSubFieldName2", new HashMap[] { new HashMap<String, Object>() {
                                            {
                                                put("goodSubFieldName1", "goodFieldValue");
                                            }
                                        }, new HashMap<String, Object>() {
                                            {
                                                put("goodSubFieldName1", "goodFieldValue");
                                                put("goodSubFieldName2", "goodFieldValue");
                                            }
                                        } });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };

        List<ValidationError> validationErrors = customEntityValidator.validate(validCustomEntity);
        Assert.assertTrue("There should be no validation errors", validationErrors.isEmpty());
    }

    @Test
    public void testInvalidFieldNameConsistingOfNullChar() {
        String invalidCustomEntityId = "invalidCustomEntity1";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("\u0000", "goodFieldValue");

        List<ValidationError> validationErrors = customEntityValidator.validate(invalidCustomEntity);
        Assert.assertEquals("Should be 1 validation error", 1, validationErrors.size());
        ValidationError ve = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "\u0000", "\u0000",
                null);
        Assert.assertEquals("Mismatched validation error", ve.toString(), validationErrors.get(0).toString());
    }

    @Test
    public void testInvalidFieldNameConsistingOfNullString() {
        String invalidCustomEntityId = "invalidCustomEntity2";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("\\x00", "goodFieldValue");

        List<ValidationError> validationErrors = customEntityValidator.validate(invalidCustomEntity);
        Assert.assertEquals("Should be 1 validation error", 1, validationErrors.size());
        ValidationError ve = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "\\x00", "\\x00",
                null);
        Assert.assertEquals("Mismatched validation error", ve.toString(), validationErrors.get(0).toString());
    }

    @Test
    public void testInvalidFieldNameContainingNullChar() {
        String invalidCustomEntityId = "invalidCustomEntity3";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("bad\u0000FieldName", "goodFieldValue");

        List<ValidationError> validationErrors = customEntityValidator.validate(invalidCustomEntity);
        Assert.assertEquals("Should be 1 validation error", 1, validationErrors.size());
        ValidationError ve = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "bad\u0000FieldName",
                "bad\u0000FieldName", null);
        Assert.assertEquals("Mismatched validation error", ve.toString(), validationErrors.get(0).toString());
    }

    @Test
    public void testInvalidFieldNameContainingNullString() {
        String invalidCustomEntityId = "invalidCustomEntity4";
        EntityBody invalidCustomEntity = new EntityBody();
        invalidCustomEntity.put("ID", invalidCustomEntityId);
        invalidCustomEntity.put("bad\\x00FieldName", "goodFieldValue");

        List<ValidationError> validationErrors = customEntityValidator.validate(invalidCustomEntity);
        Assert.assertEquals("Should be 1 validation error", 1, validationErrors.size());
        ValidationError ve = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "bad\\x00FieldName",
                "bad\\x00FieldName", null);
        Assert.assertEquals("Mismatched validation error", ve.toString(), validationErrors.get(0).toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void testInvalidNestedFieldNameInMap() {
        final String invalidCustomEntityId = "invalidCustomEntity5";
        EntityBody invalidCustomEntity = new EntityBody() {
            {
                put("ID", invalidCustomEntityId);
                put("nestedFieldName", new HashMap<String, Object>() {
                    {
                        put("goodFieldName", "goodFieldValue");
                        put("bad\u0011FieldName", "goodFieldValue");
                    }
                });
            }

        };

        List<ValidationError> validationErrors = customEntityValidator.validate(invalidCustomEntity);
        Assert.assertEquals("Should be 1 validation error", 1, validationErrors.size());
        ValidationError ve = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "bad\u0011FieldName",
                "bad\u0011FieldName", null);
        Assert.assertEquals("Mismatched validation error", ve.toString(), validationErrors.get(0).toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void testMultipleNestedInvalidFieldNames() {
        final String invalidCustomEntityId = "invalidCustomEntity6";
        EntityBody invalidCustomEntity = new EntityBody() {
            {
                put("ID", invalidCustomEntityId);
                put("FSCommand = DEL", "goodFieldValue");
                put("nestedField", new HashMap<String, Object>() {
                    {
                        put("goodFieldName1", "goodFieldValue");
                        put("bad\u0000FieldName2", "goodFieldValue");
                        put("goodFieldName2", new LinkedList<HashMap<String, Object>>() {
                            {
                                add(new HashMap<String, Object>() {
                                    {
                                        put("goodSubFieldName1", "goodFieldValue");
                                    }
                                });
                                add(new HashMap<String, Object>() {
                                    {
                                        put("goodSubFieldName2", "goodFieldValue");
                                        put("bad\\x00FieldName3", new HashMap[] {
                                            new HashMap<String, Object>() {
                                                {
                                                    put("goodSubFieldName", "goodFieldValue");
                                                }
                                            }, new HashMap<String, Object>() {
                                                {
                                                    put("goodSubFieldName3", "goodFieldValue");
                                                    put("bad\u007FFieldName4", "goodFieldValue");
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };

        List<ValidationError> validationErrors = customEntityValidator.validate(invalidCustomEntity);
        Assert.assertEquals("Should be 4 validation errors", 4, validationErrors.size());
        ValidationError ve1 = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "FSCommand = DEL",
                "FSCommand = DEL", null);
        ValidationError ve2 = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME,
                "bad\u0000FieldName2", "bad\u0000FieldName2", null);
        ValidationError ve3 = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, "bad\\x00FieldName3",
                "bad\\x00FieldName3", null);
        ValidationError ve4 = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME,
                "bad\u007FFieldName4", "bad\u007FFieldName4", null);
        assertValidationErrorIsInList(validationErrors, ve1);
        assertValidationErrorIsInList(validationErrors, ve2);
        assertValidationErrorIsInList(validationErrors, ve3);
        assertValidationErrorIsInList(validationErrors, ve4);
    }

    private void assertValidationErrorIsInList(List<ValidationError> validationErrors, ValidationError validationError) {
        for (ValidationError ve : validationErrors) {
            if (validationError.toString().equals(ve.toString())) {
                return;
            }
        }
        Assert.fail("Mismatched validation error");
    }
}
