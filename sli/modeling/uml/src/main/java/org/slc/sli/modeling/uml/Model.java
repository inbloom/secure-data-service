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
package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The collection of all entities making up the unified model
 */
public class Model extends ModelElement implements Visitable {
    
    private final String name;
    private final List<NamespaceOwnedElement> ownedElements;
    
    public Model(final Identifier id, final String name, final List<TaggedValue> taggedValues,
            final List<NamespaceOwnedElement> ownedElements) {
        super(id, taggedValues);
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        this.name = name;
        this.ownedElements = Collections.unmodifiableList(new ArrayList<NamespaceOwnedElement>(ownedElements));
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public String getName() {
        return name;
    }
    
    public List<NamespaceOwnedElement> getOwnedElements() {
        return ownedElements;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
        sb.append(", ");
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
