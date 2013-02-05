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


package org.slc.sli.modeling.psm;

/**
 * Model document.
 *
 * @param <TYPE>
 */
public class PsmDocument<TYPE> {
    private final TYPE type;
    private final PsmResource graphResourceName;
    private final PsmCollection singularResourceName;

    public PsmDocument(final TYPE type, final PsmResource graphResourceName, final PsmCollection singularResourceName) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (graphResourceName == null) {
            throw new IllegalArgumentException("graphResourceName");
        }
        if (singularResourceName == null) {
            throw new IllegalArgumentException("collection");
        }
        this.type = type;
        this.graphResourceName = graphResourceName;
        this.singularResourceName = singularResourceName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type : \"").append(type).append("\"");
        sb.append(", ");
        sb.append("graphResourceName : \"").append(graphResourceName).append("\"");
        sb.append(", ");
        sb.append("singularResourceName : \"").append(singularResourceName).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public TYPE getType() {
        return type;
    }

    /**
     * This is the name that should be used in a graph as an association-end from a reference node
     * to the collection. This is appropriate for the top-level arcs in a REST API but is not
     * appropriate for the name of an element in a schema file.
     */
    public PsmResource getGraphAssociationEndName() {
        return graphResourceName;
    }

    /**
     * This is the name that should be used as the stem in the WXS (W3C XML Schema) file.
     */
    public PsmCollection getSingularResourceName() {
        return singularResourceName;
    }
}
