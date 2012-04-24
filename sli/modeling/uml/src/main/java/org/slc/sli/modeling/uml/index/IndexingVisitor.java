package org.slc.sli.modeling.uml.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.Visitor;

public class IndexingVisitor implements Visitor {

    private static final <K, V> void record(final K key, final V value, final Map<K, Set<V>> map) {
        if (map.containsKey(key)) {
            final Set<V> usages = map.get(key);
            usages.add(value);
        } else {
            final Set<V> usages = new HashSet<V>();
            usages.add(value);
            map.put(key, usages);
        }
    }

    private static final void visitTaggedValues(final Taggable taggable, final Visitor visitor) {
        for (final TaggedValue taggedValue : taggable.getTaggedValues()) {
            taggedValue.accept(visitor);
        }
    }

    /**
     * A name maps to one or more model elements.
     */
    final Map<String, Set<ModelElement>> nameMap = new HashMap<String, Set<ModelElement>>();
    /**
     * An identifier uniquely identifies a model element.
     */
    final Map<Identifier, ModelElement> elementMap = new HashMap<Identifier, ModelElement>();

    /**
     * An identifier is referenced by zero or more elements.
     */
    final Map<Identifier, Set<ModelElement>> whereUsed = new HashMap<Identifier, Set<ModelElement>>();

    public final Map<Identifier, ModelElement> getModelElementMap() {
        return Collections.unmodifiableMap(new HashMap<Identifier, ModelElement>(elementMap));
    }

    public final Map<String, Set<ModelElement>> getNameMap() {
        return Collections.unmodifiableMap(new HashMap<String, Set<ModelElement>>(nameMap));
    }

    public final Map<Identifier, Set<ModelElement>> getWhereUsed() {
        return Collections.unmodifiableMap(new HashMap<Identifier, Set<ModelElement>>(whereUsed));
    }

    @Override
    public void visit(final Association association) {
        record(association.getName(), association, nameMap);
        elementMap.put(association.getId(), association);
        visitTaggedValues(association, this);
    }

    @Override
    public void visit(final AssociationEnd associationEnd) {
        record(associationEnd.getName(), associationEnd, nameMap);
        elementMap.put(associationEnd.getId(), associationEnd);
        visitTaggedValues(associationEnd, this);
    }

    @Override
    public void visit(final Attribute attribute) {
        record(attribute.getName(), attribute, nameMap);
        elementMap.put(attribute.getId(), attribute);
        final Identifier typeId = attribute.getType();
        record(typeId, attribute, whereUsed);
        visitTaggedValues(attribute, this);
    }

    @Override
    public void visit(final ClassType classType) {
        record(classType.getName(), classType, nameMap);
        elementMap.put(classType.getId(), classType);
        for (final Attribute attribute : classType.getAttributes()) {
            attribute.accept(this);
            record(attribute.getId(), classType, whereUsed);
        }
        visitTaggedValues(classType, this);
    }

    @Override
    public void visit(final DataType dataType) {
        record(dataType.getName(), dataType, nameMap);
        elementMap.put(dataType.getId(), dataType);
        visitTaggedValues(dataType, this);
    }

    @Override
    public void visit(final EnumLiteral enumLiteral) {
        record(enumLiteral.getName(), enumLiteral, nameMap);
        elementMap.put(enumLiteral.getId(), enumLiteral);
        visitTaggedValues(enumLiteral, this);
    }

    @Override
    public void visit(final EnumType enumType) {
        record(enumType.getName(), enumType, nameMap);
        elementMap.put(enumType.getId(), enumType);
        visitTaggedValues(enumType, this);
    }

    @Override
    public void visit(final Generalization generalization) {
        record(generalization.getName(), generalization, nameMap);
        elementMap.put(generalization.getId(), generalization);
        visitTaggedValues(generalization, this);
    }

    @Override
    public void visit(final Model model) {
        record(model.getName(), model, nameMap);
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
        record(tagDefinition.getName(), tagDefinition, nameMap);
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
        record(pkg.getName(), pkg, nameMap);
        elementMap.put(pkg.getId(), pkg);
        for (final NamespaceOwnedElement ownedElement : pkg.getOwnedElements()) {
            ownedElement.accept(this);
        }
        visitTaggedValues(pkg, this);
    }
}
