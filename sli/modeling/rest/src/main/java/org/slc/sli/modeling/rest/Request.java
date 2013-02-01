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
 * A <code>request</code> element describes the input to be included when applying an HTTP method to
 * a resource.
 */
public class Request extends WadlElement {
    private final List<Param> params;
    private final List<Representation> representations;

    public Request(final List<Documentation> doc, final List<Param> params, final List<Representation> representations) {
        super(doc);
        if (null == params) {
            throw new IllegalArgumentException("params");
        }
        if (null == representations) {
            throw new IllegalArgumentException("representations");
        }
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
        this.representations = Collections.unmodifiableList(new ArrayList<Representation>(representations));
    }

    public List<Param> getParams() {
        return params;
    }

    /**
     * Note that the use of
     * <code>representation/code> elements is confined to HTTP methods that accept an entity body in the request (e.g. PUT or POST). Sibling <code>representation</code>
     * elements represent logically equivalent alternatives.
     */
    public List<Representation> getRepresentations() {
        return representations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("params").append(" : ").append(params);
        sb.append(", ");
        sb.append("representations").append(" : ").append(representations);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
