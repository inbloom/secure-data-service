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


package org.slc.sli.modeling.psm;

public final class PsmDocument<TYPE> {
    private final TYPE type;
    private final PsmResource pluralResourceName;
    private final PsmCollection singularResourceName;

    public PsmDocument(final TYPE type, final PsmResource pluralResourceName, final PsmCollection singularResourceName) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (pluralResourceName == null) {
            throw new NullPointerException("resource");
        }
        if (singularResourceName == null) {
            throw new NullPointerException("collection");
        }
        this.type = type;
        this.pluralResourceName = pluralResourceName;
        this.singularResourceName = singularResourceName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type : \"").append(type).append("\"");
        sb.append(", ");
        sb.append("pluralResourceName : \"").append(pluralResourceName).append("\"");
        sb.append(", ");
        sb.append("singularResourceName : \"").append(singularResourceName).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public TYPE getType() {
        return type;
    }

    public PsmResource getPluralResourceName() {
        return pluralResourceName;
    }

    public PsmCollection getSingularResourceName() {
        return singularResourceName;
    }
}
