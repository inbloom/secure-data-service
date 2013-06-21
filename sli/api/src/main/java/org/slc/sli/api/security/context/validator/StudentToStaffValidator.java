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
package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

/**
 * Validator to deny access to all non transitive access to staff (/staff/id/somethingElse)
 *
 * @author nbrown
 *
 */
@Component
public class StudentToStaffValidator extends BasicValidator {

    public StudentToStaffValidator() {
        super(false, EntityNames.STUDENT, Arrays.asList(EntityNames.STAFF, EntityNames.TEACHER));
    }

    @Override
    protected boolean doValidate(Set<String> ids) {
        return false;
    }

}
