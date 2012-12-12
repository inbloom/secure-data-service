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

import org.slc.sli.domain.Entity;

/**
 * Interface that can validate an Entity.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 */
public interface EntityValidator {

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param entity
     */
    public boolean validate(Entity entity) throws EntityValidationException;

    /**
     * Validates the fields that are present in the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * This method is different from "validate" in that it does not check for missing required
     * fields.
     *
     * @param entity
     */
    public boolean validatePresent(Entity entity) throws EntityValidationException;

    /**
     * Enable / disable reference checking as a part of entity validation
     *
     * @param referenceCheck
     */
    public void setReferenceCheck(String referenceCheck);
    
    /**
     * 
     * @param entity
     * @param fullOverwrite
     */
    public void validateNaturalKeys(final Entity entity, boolean fullOverwrite);

}
