package org.slc.sli.validation.strategy;

import java.util.Collection;

/**
 * Use a Collection of input Strings to build the validation model
 *
 * @author vmcglaughlin
 */
public abstract class AbstractBlacklistStrategy {

    /**
    * Collection of Strings from which the validation model will be built
    */
    protected Collection<String> inputCollection;

    /**
     *
     */
    protected String identifier;

    /**
     * Constructor with specified identifier
     * @param typeName
     */
    public AbstractBlacklistStrategy(String identifier) {
        this.identifier = identifier;
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

    /**
     * Get the identifier associated with the strategy
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set the identifier associated with the strategy
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Based on the strategy and context, return true if the input is valid, false otherwise
     * @param context
     * @param input
     * @return
     */
    public abstract boolean isValid(String context, String input);
}
