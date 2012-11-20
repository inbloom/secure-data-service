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

package org.slc.sli.modeling.sdkgen;

import org.slc.sli.modeling.rest.Application;

/**
 * Maintains the relationship between a model and where it was located in order to resolve
 * references.
 */
public final class Wadl<T> {

    private final Application application;
    private final T source;

    public Wadl(final Application application, final T source) {
        if (application == null) {
            throw new IllegalArgumentException("application");
        }
        if (source == null) {
            throw new IllegalArgumentException("source");
        }
        this.application = application;
        this.source = source;

    }

    public Application getApplication() {
        return application;
    }

    public T getSource() {
        return source;
    }
}
