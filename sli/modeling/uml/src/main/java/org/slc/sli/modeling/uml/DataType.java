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

import java.util.Collections;
import java.util.List;

/**
 * A data type is a type that typically has no references to other types.
 */
public class DataType extends NamespaceOwnedElement implements SimpleType {
    private final boolean isAbstract;
    
    public DataType(final Identifier id, final String name) {
        this(id, name, false, EMPTY_TAGGED_VALUES);
    }
    
    public DataType(final Identifier id, final String name, final boolean isAbstract) {
        this(id, name, isAbstract, EMPTY_TAGGED_VALUES);
    }
    
    public DataType(final Identifier id, final String name, final boolean isAbstract,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        this.isAbstract = isAbstract;
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public List<EnumLiteral> getLiterals() {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    @Override
    public boolean isClassType() {
        return false;
    }
    
    @Override
    public boolean isDataType() {
        return true;
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
        sb.append("name: \"" + getName() + "\"").append(", ");
        sb.append("isAbstract: " + isAbstract);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
