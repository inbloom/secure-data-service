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
 * The meta-data for a class.
 */
public class ClassType extends ComplexType implements Navigable {
    private static final List<Attribute> EMPTY_ATTRIBUTE_LIST = Collections.emptyList();
    private static final String BEGIN_DATE = "dataStore.beginDate";
    private static final String END_DATE = "dataStore.endDate";
    /**
     * Checks the invariant that either both ends are specified or both omitted.
     */
    private static boolean checkAssociationEnds(final AssociationEnd lhs, final AssociationEnd rhs,
            final boolean required) {
        if (lhs != null) {
            if (rhs != null) {
                return true;
            } else {
                if (required) {
                    throw new IllegalArgumentException("rhs");
                } else {
                    throw new IllegalArgumentException("lhs is specified, but rhs is null.");
                }
            }
        } else {
            if (required) {
                throw new IllegalArgumentException("lhs");
            } else {
                if (rhs == null) {
                    return false;
                } else {
                    throw new IllegalArgumentException("rhs is specified, but lhs is null.");
                }
            }
        }
    }
    
    /**
     * The attributes of this class.
     */
    private final List<Attribute> attributes;
    /**
     * Determines whether the class can be instantiated.
     */
    private final boolean isAbstract;
    private final boolean isClassType;
    private final AssociationEnd lhs;
    
    private final AssociationEnd rhs;
    private Attribute beginDateAttribute;

    private Attribute endDateAttribute;


    public void setAssociatedDatedCollectionStore(List<String> associatedDatedCollectionStore) {
        this.associatedDatedCollectionStore = associatedDatedCollectionStore;
    }

    private List<String> associatedDatedCollectionStore ;

    public Attribute getBeginDateAttribute() {
        return beginDateAttribute;
    }

    public Attribute getEndDateAttribute() {
        return endDateAttribute;
    }

    public List<String> getAssociatedDatedCollectionStore() {
        return associatedDatedCollectionStore;
    }

    public ClassType(final AssociationEnd lhs, final AssociationEnd rhs) {
        this(Identifier.random(), "", lhs, rhs, EMPTY_TAGGED_VALUES);
    }
    
    /**
     * Canonical Initializer (but should not be accessible).
     */
    private ClassType(final boolean isClassType, final Identifier id, final String name, final boolean isAbstract,
            final List<Attribute> attributes, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("attributes");
        }

        checkAssociationEnds(lhs, rhs, !isClassType);
        this.isAbstract = isAbstract;
        this.attributes = Collections.unmodifiableList(new ArrayList<Attribute>(attributes));
        this.lhs = lhs;
        this.rhs = rhs;
        this.isClassType = isClassType;
        for (Attribute attribute: this.attributes) {
            List<TaggedValue> taggedValueList = attribute.getTaggedValues();
            for (TaggedValue taggedValue: taggedValueList) {
                if(taggedValue.getValue().equals(BEGIN_DATE)) {
                    this.beginDateAttribute = attribute;
                }
                if(taggedValue.getValue().equals(END_DATE)) {
                    this.endDateAttribute = attribute;
                }
            }
        }
    }
    
    public ClassType(final Identifier id, final String name, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        this(false, id, name, false, EMPTY_ATTRIBUTE_LIST, lhs, rhs, taggedValues);
        checkAssociationEnds(lhs, rhs, true);
        if (lhs.getId().equals(rhs.getId())) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * UML AssociationClass initializer.
     */
    public ClassType(final Identifier id, final String name, final boolean isAbstract,
            final List<Attribute> attributes, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        this(true, id, name, isAbstract, attributes, lhs, rhs, taggedValues);
        checkAssociationEnds(lhs, rhs, true);
    }
    
    public ClassType(final Identifier id, final String name, final boolean isAbstract,
            final List<Attribute> attributes, final List<TaggedValue> taggedValues) {
        this(true, id, name, isAbstract, attributes, null, null, taggedValues);
    }
    
    public ClassType(final String name, final AssociationEnd lhs, final AssociationEnd rhs) {
        this(Identifier.random(), name, lhs, rhs, EMPTY_TAGGED_VALUES);
    }
    
    public ClassType(final String name, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, lhs, rhs, taggedValues);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    @Override
    public AssociationEnd getLHS() {
        return lhs;
    }
    
    @Override
    public AssociationEnd getRHS() {
        return rhs;
    }
    
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    @Override
    public boolean isAssociation() {
        return checkAssociationEnds(lhs, rhs, false);
    }
    
    @Override
    public boolean isClassType() {
        return isClassType;
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
