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

import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Validates the context of a staff/teacher member to see the requested set of
 * non-transitive public entities. Returns true if the staff member can see ALL
 * of the entities, and false otherwise.
 */
@Component
public class GlobalEntityValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return isTransitive && isGlobalWrite(entityType);
	}

	@Override
	public boolean validate(String entityType, Set<String> entityIds) throws IllegalStateException {
		return areParametersValid(GLOBAL_WRITE_RESOURCE, entityType, entityIds);
	}

}
