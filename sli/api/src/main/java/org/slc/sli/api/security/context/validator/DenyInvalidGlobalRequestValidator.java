package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;

/**
 * This class is to catch any exceptional request that got through the global
 * entity validator filters. These include such requests that go through
 * entities with no context (Assessment, Learning Objective, Learning Standard)
 * that resolve to a non-global entity, or other global entities which currently
 * do not have endpoints on them that resolve to non-global entities
 * 
 */
public class DenyInvalidGlobalRequestValidator extends AbstractContextValidator {

	protected static final Set<String> OTHER_INVALID_RESOURCES = new HashSet<String>(
			Arrays.asList(
					EntityNames.COURSE,
					EntityNames.COURSE_OFFERING));

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
