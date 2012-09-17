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
package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A feature is an abstract part of a class such as an attribute or association end.
 */
public abstract class Feature extends NamedModelElement implements HasType, HasMultiplicity {
    /**
     * The lower bound of the attribute. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    /**
     * The type of the attribute. Never <code>null</code>.
     */
    private final Identifier type;
    
    public Feature(final Identifier id, final String name, final Identifier type, final Multiplicity multiplicity,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        this.type = type;
        this.multiplicity = multiplicity;
    }
    
    public abstract boolean isAttribute();
    
    public abstract boolean isAssociationEnd();
    
    @Override
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    @Override
    public Identifier getType() {
        return type;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: " + getName());
        sb.append(", ");
        sb.append("type: " + type);
        sb.append(", ");
        sb.append("multiplicity: " + multiplicity);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}