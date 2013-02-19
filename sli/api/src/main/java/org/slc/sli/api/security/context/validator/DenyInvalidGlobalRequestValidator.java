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
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;

/**
 * This class is to catch any exceptional request that got through the global
 * entity validator filters. These include such requests that go through
 * entities with no context (Assessment, Learning Objective, Learning Standard)
 * that resolve to a non-global entity, or other global entities which currently
 * do not have endpoints on them that resolve to non-global entities
 *
 */
@Component
public class DenyInvalidGlobalRequestValidator extends AbstractContextValidator {

	protected static final Set<String> OTHER_INVALID_RESOURCES = new HashSet<String>(
			Arrays.asList(
					EntityNames.COURSE,
					EntityNames.COURSE_OFFERING,
					EntityNames.GRADING_PERIOD,
					EntityNames.GRADUATION_PLAN,
					EntityNames.SESSION,
					EntityNames.STUDENT_COMPETENCY_OBJECTIVE));

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return !isTransitive
				&& (isGlobalWrite(entityType) || isOtherInvalidNonTransitiveValidator(entityType));
	}

	@Override
	public boolean validate(String entityType, Set<String> ids)
			throws IllegalStateException {
		/*
		 * We should never reach this in normal operations, throw an exception
		 * to indicate this should never happen in a working system
		 */
		throw new IllegalStateException(
				"The non-transitive validator for Global Write entities should never be called");
	}

	private boolean isOtherInvalidNonTransitiveValidator(String entityType) {
		return OTHER_INVALID_RESOURCES.contains(entityType);
	}

}
