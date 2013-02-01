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

import java.util.ArrayList;
import java.util.List;

/**
 * Exception for Natural Key Validations
 *
 * @author srupasinghe
 */
public class NaturalKeyValidationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    final String entityType;
    final List<String> naturalKeys;

    public NaturalKeyValidationException(Exception ex) {
        super(ex);
        this.naturalKeys = null;
        this.entityType = null;
    }

    public NaturalKeyValidationException(String msg) {
        super(msg);
        this.naturalKeys = null;
        this.entityType = null;
    }

    public NaturalKeyValidationException(String entityType, List<String> naturalKeys) {
        super(entityType + " is missing the following required key fields: " + naturalKeys);
        this.entityType = entityType;
        this.naturalKeys = naturalKeys;
    }

    public NaturalKeyValidationException(NoNaturalKeysDefinedException e,
            String entityType, ArrayList<String> naturalKeys) {
        super(e);
        this.entityType = entityType;
        this.naturalKeys = naturalKeys;
    }

    public String getEntityType() {
        return entityType;
    }

    public List<String> getNaturalKeys() {
        return this.naturalKeys;
    }
}
