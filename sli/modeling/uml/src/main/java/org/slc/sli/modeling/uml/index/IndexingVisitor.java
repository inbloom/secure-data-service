package org.slc.sli.modeling.uml.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlModelElement;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.Visitor;

public class IndexingVisitor implements Visitor {
    
    private static final void visitTaggedValues(final Taggable taggable, final Visitor visitor) {
        for (final TaggedValue taggedValue : taggable.getTaggedValues()) {
            taggedValue.accept(visitor);
        }
    }
    
    final Map<Identifier, UmlModelElement> elementMap = new HashMap<Identifier, UmlModelElement>();
    
    public final Map<Identifier, UmlModelElement> getModelElementMap() {
        return Collections.unmodifiableMap(new HashMap<Identifier, UmlModelElement>(elementMap));
    }
    
    @Override
    public void visit(final Association association) {
        elementMap.put(association.getId(), association);
        visitTaggedValues(association, this);
    }
    
    @Override
    public void visit(final AssociationEnd associationEnd) {
        elementMap.put(associationEnd.getId(), associationEnd);
        visitTaggedValues(associationEnd, this);
    }
    
    @Override
    public void visit(final Attribute attribute) {
        elementMap.put(attribute.getId(), attribute);
        visitTaggedValues(attribute, this);
    }
    
    @Override
    public void visit(final ClassType classType) {
        elementMap.put(classType.getId(), classType);
        for (final Attribute attribute : classType.getAttributes()) {
            attribute.accept(this);
        }
        visitTaggedValues(classType, this);
    }
    
    @Override
    public void visit(final DataType dataType) {
        elementMap.put(dataType.getId(), dataType);
        visitTaggedValues(dataType, this);
    }
    
    @Override
    public void visit(final EnumLiteral enumLiteral) {
        elementMap.put(enumLiteral.getId(), enumLiteral);
        visitTaggedValues(enumLiteral, this);
    }
    
    @Override
    public void visit(final EnumType enumType) {
        elementMap.put(enumType.getId(), enumType);
        visitTaggedValues(enumType, this);
    }
    
    @Override
    public void visit(final Generalization generalization) {
        elementMap.put(generalization.getId(), generalization);
        visitTaggedValues(generalization, this);
    }
    
    @Override
    public void visit(final Model model) {
        elementMap.put(model.getId(), model);
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            ownedElement.accept(this);
        }
        visitTaggedValues(model, this);
    }
    
    @Override
    public void visit(final Multiplicity multiplicity) {
        elementMap.put(multiplicity.getId(), multiplicity);
        visitTaggedValues(multiplicity, this);
    }
    
    @Override
    public void visit(final Range range) {
        elementMap.put(range.getId(), range);
        visitTaggedValues(range, this);
    }
    
    @Override
    public void visit(final TagDefinition tagDefinition) {
        elementMap.put(tagDefinition.getId(), tagDefinition);
        visitTaggedValues(tagDefinition, this);
    }
    
    @Override
    public void visit(final TaggedValue taggedValue) {
        elementMap.put(taggedValue.getId(), taggedValue);
        visitTaggedValues(taggedValue, this);
    }
    
    @Override
    public void visit(final UmlPackage pkg) {
        elementMap.put(pkg.getId(), pkg);
        for (final NamespaceOwnedElement ownedElement : pkg.getOwnedElements()) {
            ownedElement.accept(this);
        }
        visitTaggedValues(pkg, this);
    }
}
