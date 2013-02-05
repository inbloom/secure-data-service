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

package org.slc.sli.modeling.tools.xmigen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
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
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.Visitor;
import org.slc.sli.modeling.uml.helpers.TaggedValueHelper;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Visits endpoints on the model and stores certain endpoints (some slightly modified) for later access.
 * 
 * @author kmyers
 *
 */
public final class Xsd2UmlTweakerVisitor implements Visitor {
    
    private static final boolean TWEAKING_ENABLED = true;
    
    private static final boolean isExactlyOne(final AssociationEnd end, final ModelIndex model) {
        final Multiplicity multiplicity = end.getMultiplicity();
        final Range range = multiplicity.getRange();
        final Occurs lower = range.getLower();
        final Occurs upper = range.getUpper();
        if (upper == Occurs.ONE && lower == Occurs.ONE) {
            return TaggedValueHelper.getBooleanTag(SliUmlConstants.TAGDEF_ASSOCIATION_KEY, end, model, false);
        } else {
            return false;
        }
    }
    
    private final ModelIndex model;
    
    private final List<NamespaceOwnedElement> ownedElements = new LinkedList<NamespaceOwnedElement>();
    
    public Xsd2UmlTweakerVisitor(final ModelIndex model) {
        if (model == null) {
            throw new IllegalArgumentException("model");
        }
        this.model = model;
    }
    
    @Override
    public void beginPackage(final UmlPackage pkg) {
        ownedElements.add(pkg);
    }
    
    @Override
    public void endPackage(final UmlPackage pkg) {
        // Ignore
    }
    
    public final List<NamespaceOwnedElement> getOwnedElements() {
        return Collections.unmodifiableList(ownedElements);
    }
    
    @Override
    public void visit(final AssociationEnd associationEnd) {
        // Ignore
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(final Attribute attribute) {
        // Ignore
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(final ClassType classType) {
        if (classType.isClassType()) {
            if (classType.isAssociation()) {
                // How can that be?
                throw new AssertionError();
            } else {
                // It's a candidate for being turned from Class into an AssociationClass.
                if (TWEAKING_ENABLED) {
                    ownedElements.add(transform(classType, model));
                } else {
                    ownedElements.add(classType);
                }
            }
        } else {
            if (classType.isAssociation()) {
                // It's a pure Association.
                ownedElements.add(classType);
            } else {
                // That's bad. It should be one or the other.
                throw new AssertionError();
            }
        }
    }
    
    private static final ClassType transform(final ClassType classType, final ModelIndex model) {
        if (classType == null) {
            throw new IllegalArgumentException();
        }
//        System.out.println("classType     : " + classType.getName());
        final List<AssociationEnd> ends = model.getAssociationEnds(classType.getId());
//        System.out.println("ends          : " + ends.size());
        final List<AssociationEnd> singletonEnds = new ArrayList<AssociationEnd>(2);
        for (final AssociationEnd end : ends) {
            if (isExactlyOne(end, model)) {
                singletonEnds.add(end);
            }
        }
//        System.out.println("singletonEnds : " + singletonEnds.size());
        if (singletonEnds.size() == 2) {
            if (classType.getName().endsWith("Association")) {
                final AssociationEnd lhsAssociationEnd = singletonEnds.get(0);
                final AssociationEnd rhsAssociationEnd = singletonEnds.get(1);
                final AssociationEnd lhsEnd = compute(lhsAssociationEnd);
                final AssociationEnd rhsEnd = compute(rhsAssociationEnd);
                return new ClassType(classType.getId(), classType.getName(), classType.isAbstract(),
                        classType.getAttributes(), lhsEnd, rhsEnd, classType.getTaggedValues());
            } else {
                return classType;
            }
        } else {
            return classType;
        }
    }
    
    private static final AssociationEnd compute(final AssociationEnd end) {
        final Range r = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        final Multiplicity m = new Multiplicity(r);
        return new AssociationEnd(m, Xsd2UmlHelper.pluralize(end.getName()), true, end.getType(), end.getAssociatedAttributeName());
    }
    
    @Override
    public void visit(final DataType dataType) {
        ownedElements.add(dataType);
    }
    
    @Override
    public void visit(final EnumLiteral enumLiteral) {
        // Ignore
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(final EnumType enumType) {
        ownedElements.add(enumType);
    }
    
    @Override
    public void visit(final Generalization generalization) {
        ownedElements.add(generalization);
    }
    
    @Override
    public void visit(final Model model) {
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            ownedElement.accept(this);
        }
    }
    
    @Override
    public void visit(final Multiplicity multiplicity) {
        // Ignore
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(final Range range) {
        // Ignore
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(final TagDefinition tagDefinition) {
        ownedElements.add(tagDefinition);
    }
    
    @Override
    public void visit(final TaggedValue taggedValue) {
        // Ignore
        throw new UnsupportedOperationException();
    }
}
