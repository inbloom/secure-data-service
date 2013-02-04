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
 * A <code>response</code> element describes the output that results from performing an HTTP method
 * on a resource.
 */
public class Response extends WadlElement {
    private final List<String> statusCodes;
    private final List<Param> params;
    private final List<Representation> representations;

    public Response(final List<String> statusCodes, final List<Documentation> doc, final List<Param> params,
            final List<Representation> representations) {
        super(doc);
        if (null == statusCodes) {
            throw new IllegalArgumentException("statusCodes");
        }
        if (null == params) {
            throw new IllegalArgumentException("params");
        }
        if (null == representations) {
            throw new IllegalArgumentException("representations");
        }
        this.statusCodes = Collections.unmodifiableList(new ArrayList<String>(statusCodes));
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
        this.representations = Collections.unmodifiableList(new ArrayList<Representation>(representations));
    }

    public List<String> getStatusCodes() {
        return statusCodes;
    }

    public List<Param> getParams() {
        return params;
    }

    public List<Representation> getRepresentations() {
        return representations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("statusCodes").append(" : ").append(statusCodes);
        sb.append(", ");
        sb.append("params").append(" : ").append(params);
        sb.append(", ");
        sb.append("representations").append(" : ").append(representations);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
