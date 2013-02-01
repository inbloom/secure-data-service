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
 * The <code>application</code> element forms the root of a WADL description.
 */
public class Application extends WadlElement {
    private final Grammars grammars;
    private final Resources resources;
    private final List<ResourceType> resourceTypes;
    private final List<Method> methods;
    private final List<Representation> representations;
    private final List<Representation> faults;

    public Application(final List<Documentation> doc, final Grammars grammars, final Resources resources,
            final List<ResourceType> resourceTypes, final List<Method> methods,
            final List<Representation> representations, final List<Representation> faults) {
        super(doc);
        if (null == resourceTypes) {
            throw new IllegalArgumentException("resourceTypes");
        }
        if (null == methods) {
            throw new IllegalArgumentException("methods");
        }
        if (null == representations) {
            throw new IllegalArgumentException("representations");
        }
        if (null == faults) {
            throw new IllegalArgumentException("faults");
        }
        this.grammars = grammars;
        this.resources = resources;
        this.resourceTypes = Collections.unmodifiableList(new ArrayList<ResourceType>(resourceTypes));
        this.methods = Collections.unmodifiableList(new ArrayList<Method>(methods));
        this.representations = Collections.unmodifiableList(new ArrayList<Representation>(representations));
        this.faults = Collections.unmodifiableList(new ArrayList<Representation>(faults));
    }

    public Grammars getGrammars() {
        return grammars;
    }

    public Resources getResources() {
        return resources;
    }

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public List<Representation> getRepresentations() {
        return representations;
    }

    public List<Representation> getFaults() {
        return faults;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("grammars").append(" : ").append(grammars);
        sb.append(", ");
        sb.append("resources").append(" : ").append(resources);
        sb.append(", ");
        sb.append("resourceTypes").append(" : ").append(resourceTypes);
        sb.append(", ");
        sb.append("methods").append(" : ").append(methods);
        sb.append(", ");
        sb.append("representations").append(" : ").append(representations);
        sb.append(", ");
        sb.append("faults").append(" : ").append(faults);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
