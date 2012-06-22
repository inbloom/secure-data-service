/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
