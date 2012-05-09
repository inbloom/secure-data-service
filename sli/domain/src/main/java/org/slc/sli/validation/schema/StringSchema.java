package org.slc.sli.validation.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.owasp.esapi.reference.validation.BaseValidationRule;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 *
 * SLI String Schema which validates string entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class StringSchema extends NeutralSchema {

	private List<BaseValidationRule> validationRuleList;

    // Constructors
    public StringSchema() {
        this(NeutralSchemaType.STRING.getName(), new ArrayList<BaseValidationRule>());
    }

    public StringSchema(String xsdType) {
    	this(xsdType, new ArrayList<BaseValidationRule>());
    }

    public StringSchema(List<BaseValidationRule> validationRuleList) {
    	this(NeutralSchemaType.STRING.getName(), validationRuleList);
    }

    public StringSchema(String xsdType, List<BaseValidationRule> validationRuleList) {
    	super(xsdType);
    	this.validationRuleList = validationRuleList;
    }

//    private void initializeBlacklistPatterns(List<String> validationBlacklist) {
//    	blacklistPatterns = new ArrayList<Pattern>();
//
//    	if (validationBlacklist == null) {
//    		return;
//    	}
//
//    	for (String patternStr : validationBlacklist) {
//    		Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
//    		blacklistPatterns.add(p);
//    	}
//    }

    // Methods

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.STRING;
    }




    @Override
    public Object convert(Object value) {
        return value;
    }



    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param fieldName
     *            name of entity field being validated
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    @Override
	protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        if (!addError(String.class.isInstance(entity), fieldName, entity, "String", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }
        String data = (String) entity;
        if (this.getProperties() != null) {
            for (Entry<String, Object> entry : this.getProperties().entrySet()) {
                if (Restriction.isRestriction(entry.getKey())) {
                    String restrictionValue = (String) entry.getValue();
                    switch (Restriction.fromValue(entry.getKey())) {
                    case PATTERN:
                        if (!addError(data.matches(restrictionValue), fieldName, entity, "pattern=" + restrictionValue,
                                ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case LENGTH:
                        if (!addError(data.length() == Integer.parseInt(restrictionValue), fieldName, entity, "length="
                                + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MIN_LENGTH:
                        if (!addError(data.length() >= Integer.parseInt(restrictionValue), fieldName, entity,
                                "min-length=" + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MAX_LENGTH:
                        if (!addError(data.length() <= Integer.parseInt(restrictionValue), fieldName, entity,
                                "max-length=" + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        if (!isWhitelisted()) {
            for (BaseValidationRule validationRule : validationRuleList) {
            	if (!validationRule.isValid("StringSchemaContext", data)) {
            		return false;
            	}
            }
        } else {
            int i = 0;
            ++i;

        }

        return true;
    }

}
