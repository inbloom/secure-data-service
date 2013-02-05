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
 * @author dholmes
 */
public class ResourceType extends WadlElement {
    private final String id;
    private final List<Param> params;
    private final Method method;
    private final Resource resource;

    public ResourceType(final String id, final List<Documentation> doc, final List<Param> params,
            final Method method, final Resource resource) {
        super(doc);
        if (null == id) {
            throw new IllegalArgumentException("id");
        }
        if (null == params) {
            throw new IllegalArgumentException("params");
        }
        if (null == method) {
            throw new IllegalArgumentException("method");
        }
        if (null == resource) {
            throw new IllegalArgumentException("resource");
        }
        this.id = id;
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
        this.method = method;
        this.resource = resource;
    }

    public String getId() {
        return id;
    }

    public List<Param> getParams() {
        return params;
    }

    public Method getMethod() {
        return method;
    }

    public Resource getResource() {
        return resource;
    }
}
