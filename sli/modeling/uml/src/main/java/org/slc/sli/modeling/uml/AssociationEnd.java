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

import java.util.List;

/**
 * The end of an association between two classes.
 */
public class AssociationEnd extends Feature {
    /**
     * Determines whether the association is navigable in this direction.
     */
    private final boolean isNavigable;

    private final String associatedAttributeName;
    
    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final Identifier type, final String associatedAttributeName) {
        this(multiplicity, name, isNavigable, Identifier.random(), EMPTY_TAGGED_VALUES, type, associatedAttributeName);
    }
    
    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final Identifier id, final List<TaggedValue> taggedValues, final Identifier type, final String associatedAttributeName) {
        super(id, name, type, multiplicity, taggedValues);
        this.isNavigable = isNavigable;
        this.associatedAttributeName = associatedAttributeName;
    }
    
    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final List<TaggedValue> taggedValues, final Identifier type, final String associatedAttributeName) {
        this(multiplicity, name, isNavigable, Identifier.random(), taggedValues, type, associatedAttributeName);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean isAssociationEnd() {
        return true;
    }
    
    @Override
    public boolean isAttribute() {
        return false;
    }
    
    public boolean isNavigable() {
        return isNavigable;
    }
    public String getAssociatedAttributeName() {
        return associatedAttributeName;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId());
        sb.append(", ");
        sb.append("name: " + getName());
        sb.append(", ");
        sb.append("type: " + getType());
        sb.append(", ");
        sb.append("multiplicity: " + getMultiplicity());
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
