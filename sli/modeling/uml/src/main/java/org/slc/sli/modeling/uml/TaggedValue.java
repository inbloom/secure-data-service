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
 * A tagged value is a tag value that is placed on a model element and conforms to a tag definition.
 */
public class TaggedValue extends ModelElement {
    /**
     * The tag definition of the tagged value. Never <code>null</code>.
     */
    private final Identifier tagDefinition;
    /**
     * The value of the tagged value. Never <code>null</code>.
     */
    private final String value;
    
    public TaggedValue(final Identifier id, final List<TaggedValue> taggedValues, final String value,
            final Identifier tagDefinition) {
        super(id, taggedValues);
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        if (tagDefinition == null) {
            throw new IllegalArgumentException("tagDefinition");
        }
        this.value = value;
        this.tagDefinition = tagDefinition;
    }
    
    public TaggedValue(final List<TaggedValue> taggedValues, final String value, final Identifier tagDefinition) {
        this(Identifier.random(), taggedValues, value, tagDefinition);
    }
    
    public TaggedValue(final String value, final Identifier tagDefinition) {
        this(Identifier.random(), EMPTY_TAGGED_VALUES, value, tagDefinition);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Identifier getTagDefinition() {
        return tagDefinition;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("value: \"" + value + "\"");
        sb.append(", ");
        sb.append("tagDefinition: " + tagDefinition);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
