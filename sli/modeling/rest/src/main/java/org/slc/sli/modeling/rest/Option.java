/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.modeling.rest;

import java.util.List;

/**
 * An <code>option</code> element defines one of a set of possible values for the parameter
 * represented by its parent <code>param</code> element. An <code>option</code> element has a
 * required <code>value</code> attribute that defines the value.
 */
public class Option extends WadlElement {
    private final String value;

    public Option(final String value, final List<Documentation> doc) {
        super(doc);
        if (null == value) {
            throw new IllegalArgumentException("value");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
