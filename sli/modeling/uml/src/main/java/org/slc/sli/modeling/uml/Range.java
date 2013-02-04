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
 * Provides the lower and upper bounds for a type.
 */
public class Range extends ModelElement {
    /**
     * The lower bound of the attribute. Typically 0 or 1. Never <code>null</code>.
     */
    private final Occurs lowerBound;
    /**
     * The upper bound of the attribute. Typically 1 or unbounded. Never <code>null</code>.
     */
    private final Occurs upperBound;
    
    public Range(final Identifier id, final Occurs lower, final Occurs upper, final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (lower == null) {
            throw new IllegalArgumentException("lower");
        }
        if (upper == null) {
            throw new IllegalArgumentException("upper");
        }
        this.lowerBound = lower;
        this.upperBound = upper;
    }
    
    public Range(final Occurs lower, final Occurs upper, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), lower, upper, taggedValues);
    }
    
    public Range(final Occurs lower, final Occurs upper) {
        this(Identifier.random(), lower, upper, EMPTY_TAGGED_VALUES);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Occurs getLower() {
        return lowerBound;
    }
    
    public Occurs getUpper() {
        return upperBound;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("lower: " + lowerBound).append(", ");
        sb.append("upper: " + upperBound);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
