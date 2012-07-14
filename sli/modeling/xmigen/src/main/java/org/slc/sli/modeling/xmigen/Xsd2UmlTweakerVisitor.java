package org.slc.sli.modeling.xmigen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import org.slc.sli.modeling.uml.index.ModelIndex;

public final class Xsd2UmlTweakerVisitor implements Visitor {
    
    private final boolean tweakingEnabled = true;
    
    private static final boolean isExactlyOne(final AssociationEnd end) {
        final Multiplicity multiplicity = end.getMultiplicity();
        final Range range = multiplicity.getRange();
        final Occurs lower = range.getLower();
        final Occurs upper = range.getUpper();
        return (upper == Occurs.ONE && lower == Occurs.ONE);
    }
    
    private final ModelIndex model;
    
    private final List<NamespaceOwnedElement> ownedElements = new LinkedList<NamespaceOwnedElement>();
    
    public Xsd2UmlTweakerVisitor(final ModelIndex model) {
        if (model == null) {
            throw new NullPointerException("model");
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
    }
    
    @Override
    public void visit(final Attribute attribute) {
        // Ignore
    }
    
    @Override
    public void visit(final ClassType classType) {
        if (classType.isClassType()) {
            if (classType.isAssociation()) {
                // How can that be?
                throw new AssertionError();
            } else {
                // It's a candidate for being turned from Class into an AssociationClass.
                if (tweakingEnabled) {
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
            throw new NullPointerException();
        }
        final List<AssociationEnd> ends = model.getAssociationEnds(classType.getId());
        if (ends.size() == 2) {
            final List<AssociationEnd> singletonEnds = new ArrayList<AssociationEnd>(2);
            for (final AssociationEnd end : ends) {
                if (isExactlyOne(end)) {
                    singletonEnds.add(end);
                }
            }
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
        } else {
            return classType;
        }
    }
    
    private static final AssociationEnd compute(final AssociationEnd end) {
        final Range r = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        final Multiplicity m = new Multiplicity(r);
        return new AssociationEnd(m, Xsd2UmlHelper.pluralize(end.getName()), true, end.getType());
    }
    
    @Override
    public void visit(final DataType dataType) {
        ownedElements.add(dataType);
    }
    
    @Override
    public void visit(final EnumLiteral enumLiteral) {
        // Ignore
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
    }
    
    @Override
    public void visit(final Range range) {
        // Ignore
    }
    
    @Override
    public void visit(final TagDefinition tagDefinition) {
        ownedElements.add(tagDefinition);
    }
    
    @Override
    public void visit(final TaggedValue taggedValue) {
        // Ignore
    }
}
