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


package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>method</code> element describes the input to and output from an HTTP protocol method that
 * may be applied to a resource.
 */
public final class Method extends WadlElement {
    private final String id;
    private final String name;
    private final Request request;
    private final List<Response> responses;

    public Method(final String id, final String name, final List<Documentation> doc, final Request request,
            final List<Response> responses) {
        super(doc);
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == responses) {
            throw new NullPointerException("responses");
        }
        this.id = id;
        this.name = name;
        this.request = request;
        this.responses = Collections.unmodifiableList(new ArrayList<Response>(responses));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Request getRequest() {
        return request;
    }

    public List<Response> getResponses() {
        return responses;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id").append(" : ").append(id);
        sb.append(", ");
        sb.append("name").append(" : ").append(name);
        sb.append(", ");
        sb.append("request").append(" : ").append(request);
        sb.append(", ");
        sb.append("responses").append(" : ").append(responses);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}