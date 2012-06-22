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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The meta-data for a class.
 */
public final class ClassType extends NamespaceOwnedElement implements Type {
    /**
     * The attributes of this class.
     */
    private final List<Attribute> attributes;
    /**
     * Determines whether the class can be instantiated.
     */
    private final boolean isAbstract;

    public ClassType(final Identifier id, final String name, final boolean isAbstract,
            final List<Attribute> attributes, final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (attributes == null) {
            throw new NullPointerException("attributes");
        }
        this.isAbstract = isAbstract;
        this.attributes = Collections.unmodifiableList(new ArrayList<Attribute>(attributes));
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }

    public List<Attribute> getAttributes() {
        // We've already made defensive copy in initializer, and have made
        // immutable.
        return attributes;
    }

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    @Override
    public boolean isClassType() {
        return true;
    }

    @Override
    public boolean isDataType() {
        return false;
    }

    @Override
    public boolean isEnumType() {
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: " + getName()).append(", ");
        sb.append("attributes: " + attributes);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
