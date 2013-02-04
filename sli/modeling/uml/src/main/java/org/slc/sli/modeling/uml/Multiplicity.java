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
 * This multiplicity element is supported distinct from {@link Range} to maintain round-trip
 * fidelity.
 */
public class Multiplicity extends ModelElement {
    /**
     * The lower and upper bound range.
     */
    private final Range range;
    
    public Multiplicity(final Identifier id, final List<TaggedValue> taggedValues, final Range range) {
        super(id, taggedValues);
        if (range == null) {
            throw new IllegalArgumentException("range");
        }
        this.range = range;
    }
    
    public Multiplicity(final List<TaggedValue> taggedValues, final Range range) {
        this(Identifier.random(), taggedValues, range);
    }
    
    public Multiplicity(final Range range) {
        this(Identifier.random(), EMPTY_TAGGED_VALUES, range);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Range getRange() {
        return range;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("range: " + range);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
