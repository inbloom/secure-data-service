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

package org.slc.sli.validation;

import java.util.Arrays;

/**
 * Describes a validation rule violation.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
public class ValidationError {

    /**
     * Indicates the type of validation condition that was violated.
     *
     * @author Ryan Farris <rfarris@wgen.net>
     *
     */
    public enum ErrorType {
        INVALID_DATE_FORMAT, REQUIRED_FIELD_MISSING, UNKNOWN_FIELD, ENUMERATION_MISMATCH, INVALID_DATATYPE,
        REFERENTIAL_INFO_MISSING, INVALID_VALUE, INVALID_CHOICE_VALUE, TOO_MANY_CHOICES, TOO_FEW_CHOICES, MISSING_CHOICE_TYPE,
        MISSING_CHOICE_VALUE, INVALID_CHOICE_TYPE, INVALID_VALUE_FOR_CHOICE, INVALID_TYPE_FOR_CHOICE, INVALID_FIELD_NAME, SELF_REFERENCING_DATA
    }

    private final ErrorType type;
    private final String fieldName;
    private final Object fieldValue;
    private final String[] expectedTypes;

    public ValidationError(ErrorType type, String fieldName, Object fieldValue, String[] expectedTypes) {
        this.type = type;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.expectedTypes = expectedTypes == null ? new String[] {} : Arrays.copyOf(expectedTypes,
                expectedTypes.length);
    }

    public ErrorType getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public String[] getExpectedTypes() {
        return Arrays.copyOf(expectedTypes, expectedTypes.length);
    }

    @Override
    public String toString() {
        return "ValidationError [type=" + type + ", fieldName=" + fieldName + ", fieldValue=" + fieldValue
                + ", expectedTypes=" + Arrays.toString(expectedTypes) + "]";
    }

}
