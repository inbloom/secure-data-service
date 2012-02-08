package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.validation.EntityValidationRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * Schema which validates choices
 * 
 * @author nbrown
 * 
 */
public class ChoiceSchema extends NeutralSchema {
    private static final Logger LOG = LoggerFactory.getLogger(ChoiceSchema.class);
    
    private final Map<String, NeutralSchema> options;
    
    public ChoiceSchema(Map<String, NeutralSchema> options) {
        super(NeutralSchemaType.CHOICE.getName());
        this.options = options;
    }
    
    public Map<String, NeutralSchema> getOptions() {
        return options;
    }
    
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityValidationRepository repo) {
        NeutralSchema optionSchema = options.get(fieldName);
        if (optionSchema == null) {
            errors.add(new ValidationError(ErrorType.UNKNOWN_FIELD, fieldName, entity, options.keySet().toArray(
                    new String[options.size()])));
            LOG.debug("No schema found for {}", fieldName);
            return false;
        }
        if (!optionSchema.validate(entity)) {
            errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, fieldName, entity, new String[] { optionSchema
                    .getType() }));
            LOG.debug("{} schema failed validation", optionSchema);
            return false;
        }
        LOG.debug("{} schema passed validation", optionSchema);
        return true;
    }
    
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.CHOICE;
    }
    
}
