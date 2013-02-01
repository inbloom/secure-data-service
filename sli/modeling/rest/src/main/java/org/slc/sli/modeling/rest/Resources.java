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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>resources</code> element acts as a container for the resources provided by the
 * application.
 */
public class Resources extends WadlElement {
    private final String base;
    private final List<Resource> resources;

    public Resources(final String base, final List<Documentation> doc, final List<Resource> resources) {
        super(doc);
        if (null == base) {
            throw new IllegalArgumentException("base");
        }
        if (null == resources) {
            throw new IllegalArgumentException("resources");
        }
        this.base = base;
        this.resources = Collections.unmodifiableList(new ArrayList<Resource>(resources));
    }

    /**
     * Provides the base URI for each child resource identifier.
     */
    public String getBase() {
        return base;
    }

    /**
     * The resources provided by the application.
     */
    public List<Resource> getResources() {
        return resources;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("base").append(" : ").append(base);
        sb.append(", ");
        sb.append("resources").append(" : ").append(resources);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
