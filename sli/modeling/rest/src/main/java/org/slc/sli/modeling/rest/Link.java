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
 * Used to identify links to resources within representations.
 */
public class Link extends WadlElement {
    private final String resourceType;
    private final String rel;
    private final String rev;

    public Link(final String resourceType, final String rel, final String rev, final List<Documentation> doc) {
        super(doc);
        if (null == resourceType) {
            throw new IllegalArgumentException("resourceType");
        }
        if (null == rel) {
            throw new IllegalArgumentException("rel");
        }
        if (null == rev) {
            throw new IllegalArgumentException("rev");
        }
        if (null == doc) {
            throw new IllegalArgumentException("doc");
        }
        this.resourceType = resourceType;
        this.rel = rel;
        this.rev = rev;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getRel() {
        return rel;
    }

    public String getRev() {
        return rev;
    }
}
