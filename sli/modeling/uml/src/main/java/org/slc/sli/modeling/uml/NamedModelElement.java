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
 * Named UML element.
 */
public abstract class NamedModelElement extends ModelElement implements HasName {
    private final String name;
    
    /**
     * Convenience initializer with no tags.
     */
    public NamedModelElement(final Identifier id, final String name) {
        this(id, name, EMPTY_TAGGED_VALUES);
    }
    
    /**
     * Canonical initializer for this abstract class.
     * 
     * @param id
     *            The identifier.
     * @param name
     *            The name of this class.
     * @param taggedValues
     *            The tags for this class.
     */
    public NamedModelElement(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        this.name = name;
    }
    
    /**
     * Convenience initializer with a randomly generated identifier and no tags.
     */
    public NamedModelElement(final String name) {
        this(Identifier.random(), name, EMPTY_TAGGED_VALUES);
    }
    
    /**
     * Convenience initializer with a randomly generated identifier.
     */
    public NamedModelElement(final String name, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, taggedValues);
    }
    
    @Override
    public String getName() {
        return name;
    }
}
