package org.slc.sli.api.security.context.validator;

import java.util.Set;

public interface IContextValidator {

    public abstract boolean canValidate(String entityType);
    
    public abstract boolean validate(Set<String> ids);
    
}
