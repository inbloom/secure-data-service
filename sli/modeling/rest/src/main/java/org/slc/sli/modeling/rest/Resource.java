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
 * Describes a set of {@link Resource}, each identified by a URI that follows a common pattern.
 */
public class Resource extends WadlElement {
    private final String id;
    private final List<String> type;
    private final String queryType;
    private final String path;
    private final String resourceClass;
    private final List<Param> params;
    private final List<Method> methods;
    private final List<Resource> resources;

    public Resource(final String id, final List<String> type, final String queryType, final String path,
            final List<Documentation> doc, final List<Param> params, final List<Method> methods,
            final List<Resource> resources, final String resourceClass) {
        super(doc);
        if (null == type) {
            throw new IllegalArgumentException("type");
        }
        if (null == queryType) {
            throw new IllegalArgumentException("queryType");
        }
        if (null == params) {
            throw new IllegalArgumentException("params");
        }
        if (null == methods) {
            throw new IllegalArgumentException("methods");
        }
        if (null == resources) {
            throw new IllegalArgumentException("resources");
        }
        this.id = id;
        this.type = Collections.unmodifiableList(new ArrayList<String>(type));
        this.queryType = queryType;
        this.path = path;
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
        this.methods = Collections.unmodifiableList(new ArrayList<Method>(methods));
        this.resources = Collections.unmodifiableList(new ArrayList<Resource>(resources));
        this.resourceClass = resourceClass;
    }

    /**
     * An optional attribute that identifies the <code>resource</code> element.
     */
    public String getId() {
        return id;
    }

    /**
     * An optional attribute whose type is a list of <code>xsd:anyURI</code>. Each value in the list
     * identifies a resource type element that defines a set of methods supported by the resource.
     */
    public List<String> getType() {
        return type;
    }

    /**
     * Defines the media type for the query component of the resource URI. Defaults to
     * <code>application/x-www-form-urlencoded</code> if not specified which results in query
     * strings being formatted as specified in section 17.13 of HTML 4.01.
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * An optional attribute that provides a relative URI template for the identifier of the
     * resource. The resource's base URI is given by the <code>resource</code> element's parent
     * <code>resource</code> or <code>resources</code> element.
     */
    public String getPath() {
        return path;
    }

    public List<Param> getParams() {
        return params;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public List<Resource> getResources() {
        return resources;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id").append(" : ").append(id);
        sb.append(", ");
        sb.append("type").append(" : ").append(type);
        sb.append(", ");
        sb.append("queryType").append(" : ").append(queryType);
        sb.append(", ");
        sb.append("path").append(" : ").append(path);
        sb.append(", ");
        sb.append("params").append(" : ").append(params);
        sb.append(", ");
        sb.append("methods").append(" : ").append(methods);
        sb.append(", ");
        sb.append("resources").append(" : ").append(resources);
        sb.append("}");
        return sb.toString();
    }

    public String getResourceClass() {
        return resourceClass;
    }
}
