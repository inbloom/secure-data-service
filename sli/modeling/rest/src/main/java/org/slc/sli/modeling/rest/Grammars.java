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
 * The <code>grammars</code> element acts as a container for definitions of the format of data
 * exchanged during execution of the protocol described by the WADL document. Such definitions may
 * be included in-line or by reference using the <code>include</code> element. No particular data
 * format definition language is mandated.
 */
public class Grammars extends WadlElement {
    private final List<Include> includes;

    public Grammars(final List<Documentation> doc, final List<Include> includes) {
        super(doc);
        if (null == includes) {
            throw new IllegalArgumentException("includes");
        }
        this.includes = Collections.unmodifiableList(new ArrayList<Include>(includes));
    }

    public List<Include> getIncludes() {
        return includes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("includes").append(" : ").append(includes);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
