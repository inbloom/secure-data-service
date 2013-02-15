package org.slc.sli.api.security.context.validator;

import java.util.Set;

/**
 * This class is to catch any exceptional request that got through the global
 * entity validator filters
 * 
 */
public class DenyInvalidGlobalRequestValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return !isTransitive && isGlobalWrite(entityType);
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

}
