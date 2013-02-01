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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.Visitor;

/**
 * {@link Visitor} implementation for indexing the UML model.
 */
class IndexingVisitor implements Visitor {
    
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
     * A stack of scopes.
     */
    private final LinkedList<String> scope = new LinkedList<String>();
    
    @Deprecated
    private final Map<QName, Set<ModelElement>> nameMap = new HashMap<QName, Set<ModelElement>>();
    private final Map<QName, TagDefinition> tagDefinitionsByName = new HashMap<QName, TagDefinition>();
    private final Map<String, ClassType> classTypesByName = new HashMap<String, ClassType>();
    private final Map<QName, DataType> dataTypesByName = new HashMap<QName, DataType>();
    /**
     * An identifier uniquely identifies a model element.
     */
    private final Map<Identifier, ModelElement> elementMap = new HashMap<Identifier, ModelElement>();
    /**
     * An identifier uniquely identifies a model element.
     */
    private final Map<Identifier, String> namespaceMap = new HashMap<Identifier, String>();
    
    /**
     * An identifier is referenced by zero or more elements.
     */
    private final Map<Identifier, Set<ModelElement>> whereUsed = new HashMap<Identifier, Set<ModelElement>>();
    
    @Override
    public void beginPackage(final UmlPackage pkg) {
        record(new QName(getNamespaceURI(), pkg.getName()), pkg, nameMap);
        scope.addFirst(pkg.getName());
        elementMap.put(pkg.getId(), pkg);
        for (final NamespaceOwnedElement ownedElement : pkg.getOwnedElements()) {
            record(ownedElement.getId(), pkg, whereUsed);
            ownedElement.accept(this);
        }
        visitTaggedValues(pkg, this);
    }
    
    @Override
    public void endPackage(final UmlPackage pkg) {
        scope.removeFirst();
    }
    
    public final Map<QName, DataType> getDataTypesByName() {
        return Collections.unmodifiableMap(new HashMap<QName, DataType>(dataTypesByName));
    }
    
    public final Map<String, ClassType> getClassTypesByName() {
        return Collections.unmodifiableMap(new HashMap<String, ClassType>(classTypesByName));
    }
    
    public final Map<Identifier, ModelElement> getModelElementMap() {
        return Collections.unmodifiableMap(new HashMap<Identifier, ModelElement>(elementMap));
    }
    
    public final Map<QName, Set<ModelElement>> getNameMap() {
        return Collections.unmodifiableMap(new HashMap<QName, Set<ModelElement>>(nameMap));
    }
    
    public final Map<Identifier, String> getNamespaceMap() {
        return Collections.unmodifiableMap(new HashMap<Identifier, String>(namespaceMap));
    }
    
    /**
     * Converts the current stack of packages into a linear package specification.
     */
    private String getNamespaceURI() {
        // Simple implementation right now because we only go one level deep.
        if (scope.isEmpty()) {
            return "";
        } else {
            return scope.peek();
        }
    }
    
    public final Map<QName, TagDefinition> getTagDefinitionsByName() {
        return Collections.unmodifiableMap(new HashMap<QName, TagDefinition>(tagDefinitionsByName));
    }
    
    public Map<Identifier, Set<ModelElement>> getWhereUsed() {
        return Collections.unmodifiableMap(new HashMap<Identifier, Set<ModelElement>>(whereUsed));
    }
    
    @Override
    public void visit(final AssociationEnd associationEnd) {
        record(new QName(getNamespaceURI(), associationEnd.getName()), associationEnd, nameMap);
        elementMap.put(associationEnd.getId(), associationEnd);
        visitTaggedValues(associationEnd, this);
    }
    
    @Override
    public void visit(final Attribute attribute) {
        record(new QName(getNamespaceURI(), attribute.getName()), attribute, nameMap);
        elementMap.put(attribute.getId(), attribute);
        final Identifier typeId = attribute.getType();
        record(typeId, attribute, whereUsed);
        visitTaggedValues(attribute, this);
    }
    
    @Override
    public void visit(final ClassType classType) {
        record(new QName(getNamespaceURI(), classType.getName()), classType, nameMap);
        namespaceMap.put(classType.getId(), getNamespaceURI());
        classTypesByName.put(classType.getName(), classType);
        elementMap.put(classType.getId(), classType);
        for (final Attribute attribute : classType.getAttributes()) {
            attribute.accept(this);
            record(attribute.getId(), classType, whereUsed);
        }

        if (classType.isAssociation()) {
            record(classType.getLHS().getId(), classType, whereUsed);
            record(classType.getRHS().getId(), classType, whereUsed);
        }

        visitTaggedValues(classType, this);
    }
    
    @Override
    public void visit(final DataType dataType) {
        namespaceMap.put(dataType.getId(), getNamespaceURI());
        dataTypesByName.put(new QName(getNamespaceURI(), dataType.getName()), dataType);
        elementMap.put(dataType.getId(), dataType);
        visitTaggedValues(dataType, this);
    }
    
    @Override
    public void visit(final EnumLiteral enumLiteral) {
        record(new QName(getNamespaceURI(), enumLiteral.getName()), enumLiteral, nameMap);
        elementMap.put(enumLiteral.getId(), enumLiteral);
        visitTaggedValues(enumLiteral, this);
    }
    
    @Override
    public void visit(final EnumType enumType) {
        record(new QName(getNamespaceURI(), enumType.getName()), enumType, nameMap);
        namespaceMap.put(enumType.getId(), getNamespaceURI());
        elementMap.put(enumType.getId(), enumType);
        visitTaggedValues(enumType, this);
    }
    
    @Override
    public void visit(final Generalization generalization) {
        record(new QName(getNamespaceURI(), generalization.getName()), generalization, nameMap);
        elementMap.put(generalization.getId(), generalization);
        visitTaggedValues(generalization, this);
    }
    
    @Override
    public void visit(final Model model) {
        record(new QName(getNamespaceURI(), model.getName()), model, nameMap);
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
        tagDefinitionsByName.put(new QName(getNamespaceURI(), tagDefinition.getName()), tagDefinition);
        elementMap.put(tagDefinition.getId(), tagDefinition);
        visitTaggedValues(tagDefinition, this);
    }
    
    @Override
    public void visit(final TaggedValue taggedValue) {
        elementMap.put(taggedValue.getId(), taggedValue);
        visitTaggedValues(taggedValue, this);
    }
}
