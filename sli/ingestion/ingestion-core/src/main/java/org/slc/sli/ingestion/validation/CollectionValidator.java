package org.slc.sli.ingestion.validation;

import java.util.Collection;
import java.util.List;

/**
 * Collection Validator, can iterate through collections to validate individual items.
 *
 * @author okrook
 *
 * @param <T>
 */
public abstract class CollectionValidator<T> extends SimpleValidator<Collection<T>> {

    private List<Validator<T>> itemValidators;
    private boolean failOnFirst;

    @Override
    public boolean isValid(Collection<T> object, ErrorReport callback) {
        boolean valid = true;

        if (this.itemValidators != null) {
            for (T item : object) {
                if (!isItemValid(item, callback)) {
                    valid = false;

                    if (this.failOnFirst) {
                        break;
                    }
                }
            }
        }

        return valid;
    }

    /**
     * Validates item of a collection.
     *
     * @param object Item to validate
     * @param callback validation report callback
     * @return true if item is valid; false otherwise
     */
    protected boolean isItemValid(T object, ErrorReport callback) {
        for (Validator<T> validator : this.itemValidators) {
            if (!validator.isValid(object, callback)) {
                return false;
            }
        }

        return true;
    }

    public List<Validator<T>> getItemValidators() {
        return itemValidators;
    }

    public void setItemValidators(List<Validator<T>> itemValidators) {
        this.itemValidators = itemValidators;
    }

    public boolean isFailOnFirst() {
        return failOnFirst;
    }

    public void setFailOnFirst(boolean failOnFirst) {
        this.failOnFirst = failOnFirst;
    }
}
