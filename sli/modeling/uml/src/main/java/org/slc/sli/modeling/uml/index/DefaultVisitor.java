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

package org.slc.sli.modeling.uml.index;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.Visitor;

/**
 * UML Visitor Implementation
 */
public class DefaultVisitor implements Visitor {
    
    private static final void visitTaggedValues(final Taggable taggable, final Visitor visitor) {
        for (final TaggedValue taggedValue : taggable.getTaggedValues()) {
            taggedValue.accept(visitor);
        }
    }
    
    @Override
    public void beginPackage(UmlPackage pkg) {
        for (final NamespaceOwnedElement ownedElement : pkg.getOwnedElements()) {
            ownedElement.accept(this);
        }
        visitTaggedValues(pkg, this);
    }
    
    @Override
    public void endPackage(UmlPackage pkg) {
        // No Op
    }
    
    @Override
    public void visit(final AssociationEnd associationEnd) {
        visitTaggedValues(associationEnd, this);
    }
    
    @Override
    public void visit(final Attribute attribute) {
        visitTaggedValues(attribute, this);
    }
    
    @Override
    public void visit(final ClassType classType) {
        visitTaggedValues(classType, this);
    }
    
    @Override
    public void visit(final DataType dataType) {
        visitTaggedValues(dataType, this);
    }
    
    @Override
    public void visit(final EnumLiteral enumLiteral) {
        visitTaggedValues(enumLiteral, this);
    }
    
    @Override
    public void visit(final EnumType enumType) {
        visitTaggedValues(enumType, this);
    }
    
    @Override
    public void visit(final Generalization generalization) {
        visitTaggedValues(generalization, this);
    }
    
    @Override
    public void visit(final Model model) {
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            ownedElement.accept(this);
        }
        visitTaggedValues(model, this);
    }
    
    @Override
    public void visit(final Multiplicity multiplicity) {
        visitTaggedValues(multiplicity, this);
    }
    
    @Override
    public void visit(final Range range) {
        visitTaggedValues(range, this);
    }
    
    @Override
    public void visit(final TagDefinition tagDefinition) {
        visitTaggedValues(tagDefinition, this);
    }
    
    @Override
    public void visit(final TaggedValue taggedValue) {
        // No Op
    }
}
