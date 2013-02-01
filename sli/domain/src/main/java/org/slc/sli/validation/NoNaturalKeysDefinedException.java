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

/**
 * Exception for Natural Key Lookup
 * 
 * @author sashton
 */
public class NoNaturalKeysDefinedException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    final String entityType;
    
    public NoNaturalKeysDefinedException(String entityType) {
        super(entityType + " should have natural keys defined, but none were found");
        this.entityType = entityType;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
}
