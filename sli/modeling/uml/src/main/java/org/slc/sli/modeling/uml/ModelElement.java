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
 * A model element is something that has an identifier, can be tagged, and can
 * be visited.
 */
public abstract class ModelElement implements HasIdentity, Taggable, Visitable {
    
    protected static final List<TaggedValue> EMPTY_TAGGED_VALUES = Collections.emptyList();
    
    private final Identifier id;
    private final List<TaggedValue> taggedValues;
    
    public ModelElement() {
        this(Identifier.random(), EMPTY_TAGGED_VALUES);
    }
    
    public ModelElement(final Identifier id) {
        this(id, EMPTY_TAGGED_VALUES);
    }
    
    public ModelElement(final Identifier id, final List<TaggedValue> taggedValues) {
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        if (taggedValues == null) {
            throw new IllegalArgumentException("taggedValues");
        }
        this.id = id;
        this.taggedValues = Collections.unmodifiableList(new ArrayList<TaggedValue>(taggedValues));
    }
    
    public ModelElement(final List<TaggedValue> taggedValues) {
        this(Identifier.random(), taggedValues);
    }
    
    @Override
    public Identifier getId() {
        return id;
    }
    
    @Override
    public List<TaggedValue> getTaggedValues() {
        return taggedValues;
    }
    
    @Override
    public  boolean equals(final Object obj) {
        if (obj instanceof ModelElement) {
            final ModelElement other = (ModelElement) obj;
            return id.equals(other.id);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
