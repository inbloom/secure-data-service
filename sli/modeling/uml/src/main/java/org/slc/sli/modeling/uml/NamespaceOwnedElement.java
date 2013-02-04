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
 * A model element that can be found in a name-space.
 */
public abstract class NamespaceOwnedElement extends NamedModelElement {
    
    public NamespaceOwnedElement(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
    }
    
    public NamespaceOwnedElement(final String name, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, taggedValues);
    }
    
    public NamespaceOwnedElement(final Identifier id, final String name) {
        this(id, name, EMPTY_TAGGED_VALUES);
    }
    
    public NamespaceOwnedElement(final String name) {
        this(Identifier.random(), name, EMPTY_TAGGED_VALUES);
    }
}
