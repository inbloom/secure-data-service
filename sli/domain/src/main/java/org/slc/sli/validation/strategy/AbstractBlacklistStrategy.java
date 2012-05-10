package org.slc.sli.validation.strategy;

import java.util.Collection;

import org.owasp.esapi.reference.validation.BaseValidationRule;


public abstract class AbstractBlacklistStrategy extends BaseValidationRule {

    protected Collection<String> inputCollection;

    public AbstractBlacklistStrategy(String typeName) {
        super(typeName);
    }

    public Collection<String> getInputCollection() {
        return inputCollection;
    }

    public void setInputCollection(Collection<String> inputCollection) {
        this.inputCollection = inputCollection;
    }
}
