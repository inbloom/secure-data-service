package org.slc.sli.validation.strategy;

import java.util.Collection;

import org.owasp.esapi.reference.validation.BaseValidationRule;

/**
 * Extension of BaseValidationRule to use a Collection of input Strings to
 * build the validation model
 *
 * @author vmcglaughlin
 */
public abstract class AbstractBlacklistStrategy extends BaseValidationRule {

    /**
    * Collection of Strings from which the validation model will be built
    */
    protected Collection<String> inputCollection;

    /**
     * Constructor with specified typeName
     * @param typeName
     */
    public AbstractBlacklistStrategy(String typeName) {
        super(typeName);
    }

    /**
     * Call after construction to build validation model from input collection
     */
    protected abstract void init();

    /**
     * Get the input collection used to construct the validation model
     * @return
     */
    public Collection<String> getInputCollection() {
        return inputCollection;
    }

    /**
     * Set the input collection used to construct the validation model.
     * Note: the validation model will not be rebuilt when this method is called
     * @param inputCollection
     */
    public void setInputCollection(Collection<String> inputCollection) {
        this.inputCollection = inputCollection;
    }
}
