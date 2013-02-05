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
 * The representation of an enumeration.
 */
public class EnumType extends NamespaceOwnedElement implements SimpleType {
    /**
     * The literals that are part of the enumeration.
     */
    private final List<EnumLiteral> literals;
    
    public EnumType(final Identifier id, final String name, final List<EnumLiteral> literals,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        this.literals = Collections.unmodifiableList(new ArrayList<EnumLiteral>(literals));
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public List<EnumLiteral> getLiterals() {
        return literals;
    }
    
    @Override
    public boolean isAbstract() {
        return false;
    }
    
    @Override
    public boolean isClassType() {
        return false;
    }
    
    @Override
    public boolean isDataType() {
        return false;
    }
    
    @Override
    public boolean isEnumType() {
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
        sb.append(", ");
        sb.append("literals: " + literals);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
