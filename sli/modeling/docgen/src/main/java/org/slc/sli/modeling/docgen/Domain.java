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


package org.slc.sli.modeling.docgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @param <TYPE> Feature type.
 */
public final class Domain<TYPE> {
    private final String title;
    private final String description;
    private final List<Entity<TYPE>> entities;
    private final List<Diagram> diagrams;
    
    public Domain(final String title, final String description, final List<Entity<TYPE>> entities,
            final List<Diagram> diagrams) {
        if (title == null) {
            throw new IllegalArgumentException("title");
        }
        if (description == null) {
            throw new IllegalArgumentException("description");
        }
        if (entities == null) {
            throw new IllegalArgumentException("entities");
        }
        if (diagrams == null) {
            throw new IllegalArgumentException("diagrams");
        }
        this.title = title;
        this.description = description;
        this.entities = Collections.unmodifiableList(new ArrayList<Entity<TYPE>>(entities));
        this.diagrams = Collections.unmodifiableList(new ArrayList<Diagram>(diagrams));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("description : \"").append(description).append("\"");
        sb.append(", ");
        sb.append("entities : \"").append(entities).append("\"");
        sb.append(", ");
        sb.append("diagrams : \"").append(diagrams).append("\"");
        sb.append("}");
        return sb.toString();
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Entity<TYPE>> getEntities() {
        return entities;
    }
    
    public List<Diagram> getDiagrams() {
        return diagrams;
    }
}
