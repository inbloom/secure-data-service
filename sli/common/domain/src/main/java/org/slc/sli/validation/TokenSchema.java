package org.slc.sli.validation;

import java.util.List;

import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Token Schema which validates string token (enumeration) entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class TokenSchema extends NeutralSchema {
    
    // Constants
    public static final String TOKENS = "tokens";
    
    // Constructors
    public TokenSchema() {
        this(NeutralSchemaType.TOKEN.getName());
    }
    
    public TokenSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    public boolean isPrimitive() {
        return false;
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
     * @return true if valid
     */
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors) {
        return addError(this.matchesToken(entity), fieldName, entity, getTokensArray(), ErrorType.ENUMERATION_MISMATCH,
                errors);
    }
    
    protected boolean matchesToken(Object entity) {
        if (this.getTokens() != null) {
            for (String token : this.getTokens()) {
                if (token.equals(entity.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    protected List<String> getTokens() {
        return (List<String>) this.getProperties().get(TOKENS);
    }
    
    protected String[] getTokensArray() {
        String[] tokensArray = new String[0];
        if (this.getTokens() != null) {
            tokensArray = new String[this.getTokens().size()];
            for (int index = 0; index < this.getTokens().size(); index++) {
                tokensArray[index] = this.getTokens().get(index);
            }
        }
        return tokensArray;
    }
}
