/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.modeling.xmigen;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * This class takes an incoming UML {@link Model} and converts attributes to
 * associations based upon heuristics provided by plug-ins.
 * 
 * Intentionally package protected.
 */
final class Xsd2UmlLinker {
    
    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
    
    private static final List<Attribute> cleanUp(final ClassType classType, final List<Attribute> attributes,
            final Xsd2UmlPlugin plugin, final ModelIndex lookup, final Map<String, Identifier> nameToClassTypeId,
            final Map<String, AssociationEnd> classNavigations) {
        final List<Attribute> result = new LinkedList<Attribute>();
        for (final Attribute attribute : attributes) {
            final Attribute cleanUp = cleanUpAttribute(classType, attribute, plugin, lookup, nameToClassTypeId,
                    classNavigations);
            if (cleanUp != null) {
                result.add(cleanUp);
            }
        }
        return result;
    }
    
    private static final Attribute cleanUpAttribute(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPlugin plugin, final ModelIndex indexedModel, final Map<String, Identifier> classTypeMap,
            final Map<String, AssociationEnd> associationEnds) {
        final Xsd2UmlPluginHost host = new Xsd2UmlPluginHostAdapter(indexedModel);
        if (plugin.isAssociationEnd(classType, attribute, host)) {
            final AssociationEnd associationEnd = toAssociationEnd(classType, attribute, plugin, host, classTypeMap);
            associationEnds.put(associationEnd.getName(), associationEnd);
            return null;
        } else {
            return attribute;
        }
    }
    
    private static final ClassType cleanUpClassType(final ClassType classType, final Xsd2UmlPlugin plugin,
            final ModelIndex lookup, final Map<String, Identifier> nameToClassTypeId,
            final Map<Type, Map<String, AssociationEnd>> navigations) {
        final Identifier id = classType.getId();
        final String name = classType.getName();
        boolean isAbstract = classType.isAbstract();
        final HashMap<String, AssociationEnd> classNavigations = new HashMap<String, AssociationEnd>();
        final List<Attribute> attributes = cleanUp(classType, classType.getAttributes(), plugin, lookup,
                nameToClassTypeId, classNavigations);
        if (classNavigations.size() > 0) {
            navigations.put(classType, classNavigations);
        }
        final List<TaggedValue> taggedValues = classType.getTaggedValues();
        return new ClassType(id, name, isAbstract, attributes, taggedValues);
    }
    
    private static final AssociationEnd toAssociationEnd(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPlugin plugin, final Xsd2UmlPluginHost lookup, final Map<String, Identifier> nameToClassTypeId) {
        final String referenceType = plugin.getAssociationEndTypeName(classType, attribute, lookup);
        if (nameToClassTypeId.containsKey(referenceType)) {
            final Identifier reference = nameToClassTypeId.get(referenceType);
            // Reuse the attribute parts because attribute is no longer needed.
            final Identifier id = attribute.getId();
            final Multiplicity multiplicity = attribute.getMultiplicity();
            final String oldName = attribute.getName();
            final String newName = suggestAssociationEndName(attribute.getName(),
                    multiplicity.getRange().getUpper() == Occurs.UNBOUNDED);
            final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>(attribute.getTaggedValues());
            {
                final Identifier navigation = lookup.ensureTagDefinitionId(TagName.MONGO_NAVIGABLE);
                final TaggedValue tag = new TaggedValue(Boolean.toString(true), navigation);
                taggedValues.add(tag);
            }
            if (!oldName.equals(newName)) {
                final Identifier nameTag = lookup.ensureTagDefinitionId(TagName.MONGO_NAME);
                final TaggedValue tag = new TaggedValue(oldName, nameTag);
                taggedValues.add(tag);
            }
            // If it's navigable at the database level then we assume this is also true logically.
            return new AssociationEnd(multiplicity, newName, true, id, taggedValues, reference);
        } else {
            throw new IllegalStateException(referenceType + " " + nameToClassTypeId);
        }
    }
    
    private static final Map<String, Integer> degeneracies(final Map<String, AssociationEnd> sourceAttributes) {
        final Map<Identifier, Integer> typeCounts = new HashMap<Identifier, Integer>();
        for (final String name : sourceAttributes.keySet()) {
            final AssociationEnd associationEnd = sourceAttributes.get(name);
            final Identifier typeId = associationEnd.getType();
            if (typeCounts.containsKey(typeId)) {
                typeCounts.put(typeId, typeCounts.get(typeId) + 1);
            } else {
                typeCounts.put(typeId, Integer.valueOf(1));
            }
        }
        final Map<String, Integer> degeneracies = new HashMap<String, Integer>();
        for (final String name : sourceAttributes.keySet()) {
            final AssociationEnd associationEnd = sourceAttributes.get(name);
            final Identifier typeId = associationEnd.getType();
            degeneracies.put(name, typeCounts.get(typeId));
        }
        return Collections.unmodifiableMap(degeneracies);
    }
    
    private static final boolean hasNavigation(final Type source, final Type target,
            final Map<Type, Map<String, AssociationEnd>> navigations, final AssociationEnd excludeEnd) {
        if (navigations.containsKey(source)) {
            final Map<String, AssociationEnd> attributes = navigations.get(source);
            for (final String name : attributes.keySet()) {
                final AssociationEnd end = attributes.get(name);
                if (!end.equals(excludeEnd)) {
                    final Identifier endTypeId = end.getType();
                    if (endTypeId.equals(target.getId())) {
                        return true;
                    }
                }
            }
            // The source type has navigations, but none to the target type.
            return false;
        } else {
            // There aren't even any navigations from the source type.
            return false;
        }
    }
    
    private static final AssociationEnd getNavigation(final Type source, final Type target,
            final Map<Type, Map<String, AssociationEnd>> navigations, final AssociationEnd excludeEnd) {
        if (navigations.containsKey(source)) {
            final Map<String, AssociationEnd> attributes = navigations.get(source);
            for (final String name : attributes.keySet()) {
                final AssociationEnd end = attributes.get(name);
                if (!end.equals(excludeEnd)) {
                    final Identifier endTypeId = end.getType();
                    if (endTypeId.equals(target.getId())) {
                        return end;
                    }
                }
            }
            throw new AssertionError();
        } else {
            throw new AssertionError();
        }
    }
    
    public static Model link(final Model model, final Xsd2UmlPlugin plugin) {
        
        final ModelIndex indexedModel = new DefaultModelIndex(model);
        final Map<String, Identifier> nameToClassTypeId = makeNameToClassTypeId(indexedModel.getClassTypes().values());
        final Map<Type, Map<String, AssociationEnd>> navigations = new HashMap<Type, Map<String, AssociationEnd>>();
        final List<NamespaceOwnedElement> ownedElements = new LinkedList<NamespaceOwnedElement>();
        
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            if (ownedElement instanceof ClassType) {
                final ClassType classType = (ClassType) ownedElement;
                ownedElements.add(cleanUpClassType(classType, plugin, indexedModel, nameToClassTypeId, navigations));
            } else {
                ownedElements.add(ownedElement);
            }
        }
        final Xsd2UmlPluginHost host = new Xsd2UmlPluginHostAdapter(indexedModel);
        
        ownedElements.addAll(makeAssociations(navigations, indexedModel, plugin, host));
        return new Model(Identifier.random(), model.getName(), model.getTaggedValues(), ownedElements);
    }
    
    private static final AssociationEnd makeAssociationEnd(final String lhsName, final Type lhsType,
            final AssociationEnd rhsEnd, final Xsd2UmlPlugin plugin, final Xsd2UmlPluginHost host) {
        final Range sourceRange = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        final Multiplicity sourceMultiplicity = new Multiplicity(sourceRange);
        // All relationships are logically navigable in both directions.
        // But a relationship may only be physically navigable based on the database.
        final boolean navigable = true;
        return new AssociationEnd(sourceMultiplicity, lhsName, navigable, lhsType.getId());
    }
    
    private static final List<Association> makeAssociations(final Map<Type, Map<String, AssociationEnd>> navigations,
            final ModelIndex lookup, final Xsd2UmlPlugin plugin, final Xsd2UmlPluginHost host) {
        final List<Association> associations = new LinkedList<Association>();
        // Make sure that every navigation has a reverse navigation.
        for (final Type lhsType : navigations.keySet()) {
            final Map<String, AssociationEnd> sourceAttributes = navigations.get(lhsType);
            final Map<String, Integer> degeneracies = degeneracies(sourceAttributes);
            
            for (final String rhsEndName : sourceAttributes.keySet()) {
                final AssociationEnd rhsEnd = sourceAttributes.get(rhsEndName);
                final Type rhsType = lookup.getType(rhsEnd.getType());
                // Notice roles of source and target are switched to find a reverse navigation.
                if (!hasNavigation(rhsType, lhsType, navigations, rhsEnd)) {
                    final String rhsTypeName = rhsType.getName();
                    final String lhsTypeName = lhsType.getName();
                    final Integer degs = degeneracies.get(rhsEndName);
                    final String lhsEndName = makeEndName(rhsTypeName, rhsEndName, degs, lhsTypeName);
                    final AssociationEnd lhsEnd = makeAssociationEnd(lhsEndName, lhsType, rhsEnd, plugin, host);
                    final String name = plugin.nameAssociation(lhsEnd, rhsEnd, host);
                    associations.add(new Association(name, lhsEnd, rhsEnd));
                    
                } else {
                    final AssociationEnd lhsEnd = getNavigation(rhsType, lhsType, navigations, rhsEnd);
                    final String name = plugin.nameAssociation(lhsEnd, rhsEnd, host);
                    associations.add(new Association(name, lhsEnd, rhsEnd));
                }
            }
        }
        return associations;
    }
    
    /**
     * Compute a sensible name for the reverse (physical) navigation direction.
     */
    private static final String makeEndName(final String sourceTypeName, final String sourceName, final int degeneracy,
            final String targetTypeName) {
        // Our first guess is the pluralization of the target type name.
        final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
        if (degeneracy > 1) {
            return sourceName.concat(titleCase(targetName));
        } else {
            if (targetTypeName.equals(sourceTypeName)) {
                // Reference to self. Avoid absurdity. See AssessmentFamily.
                return camelCase(targetName);
            } else if (targetName.toLowerCase().contains(sourceTypeName.toLowerCase())) {
                return camelCase(Xsd2UmlHelper.replaceAllIgnoreCase(targetName, sourceTypeName, ""));
            } else {
                return camelCase(targetName);
            }
        }
    }
    
    private static final Map<String, Identifier> makeNameToClassTypeId(final Iterable<ClassType> classTypes) {
        final Map<String, Identifier> map = new HashMap<String, Identifier>();
        for (final ClassType classType : classTypes) {
            map.put(classType.getName(), classType.getId());
        }
        return map;
    }
    
    private static final String suggestAssociationEndName(final String name, final boolean pluralize) {
        final String stem = camelCase(suggestStem(name));
        return pluralize ? stem.concat("s") : stem;
    }
    
    private static final String suggestStem(final String name) {
        if (name.endsWith("Id")) {
            return name.substring(0, name.length() - 2);
        } else if (name.endsWith("Ids")) {
            return name.substring(0, name.length() - 3);
        } else if (name.endsWith("Reference")) {
            return name.substring(0, name.length() - 9);
        } else {
            return name;
        }
    }
    
    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
}
