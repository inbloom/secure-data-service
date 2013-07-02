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

import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ComplexType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class takes an incoming UML {@link Model} and converts attributes to
 * associations based upon heuristics provided by plug-ins.
 * <p/>
 * Intentionally package protected.
 */
final class Xsd2UmlLinker {

    private static final Logger LOG = LoggerFactory.getLogger(Xsd2UmlLinker.class);

    private static final String SUFFIX_REFERENCES = "References";
    private static final String SUFFIX_REFERENCE = "Reference";
    private static final String SUFFIX_IDS = "Ids";
    private static final String SUFFIX_ID = "Id";

    private static final List<Attribute> splitClassFeatures(final ClassType classType,
                                                            final List<Attribute> attributes, final Xsd2UmlHostedPlugin plugin, final ModelIndex lookup,
                                                            final Map<String, Identifier> nameToClassTypeId, final Map<String, Attribute> classAttributes,
                                                            final Map<String, AssociationEnd> classAssociationEnds) {
        final List<Attribute> result = new LinkedList<Attribute>();
        for (final Attribute attribute : attributes) {
            final Attribute cleanUp = splitClassFeature(classType, attribute, plugin, lookup, nameToClassTypeId,
                    classAssociationEnds);
            if (cleanUp != null) {
                result.add(cleanUp);
            }
        }
        return result;
    }

    private static final Attribute splitClassFeature(final ClassType classType, final Attribute attribute,
                                                     final Xsd2UmlHostedPlugin plugin, final ModelIndex indexedModel, final Map<String, Identifier> classTypeMap,
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

    private static final ComplexType cleanUpClassType(final ClassType classType, final Xsd2UmlHostedPlugin plugin,
                                                      final ModelIndex lookup, final Map<String, Identifier> nameToClassTypeId,
                                                      final Map<Type, Map<String, AssociationEnd>> navigations) {
        final Identifier id = classType.getId();
        final String name = classType.getName();
        boolean isAbstract = classType.isAbstract();
        final HashMap<String, Attribute> classAttributes = new HashMap<String, Attribute>();
        final HashMap<String, AssociationEnd> classAssociationEnds = new HashMap<String, AssociationEnd>();
        final List<Attribute> attributes = splitClassFeatures(classType, classType.getAttributes(), plugin, lookup,
                nameToClassTypeId, classAttributes, classAssociationEnds);
        if (classAssociationEnds.size() > 0) {
            navigations.put(classType, classAssociationEnds);
        }
        final List<TaggedValue> taggedValues = classType.getTaggedValues();
        return new ClassType(id, name, isAbstract, attributes, taggedValues);
    }

    private static final AssociationEnd toAssociationEnd(final ClassType classType, final Attribute attribute,
                                                         final Xsd2UmlHostedPlugin plugin, final Xsd2UmlPluginHost lookup, final Map<String, Identifier> nameToClassTypeId) {
        final String referenceType = plugin.getAssociationEndTypeName(classType, attribute, lookup);
        if (nameToClassTypeId.containsKey(referenceType)) {
            final Identifier reference = nameToClassTypeId.get(referenceType);
            // Reuse the attribute parts because attribute is no longer needed.
            final Identifier id = attribute.getId();
            final Multiplicity multiplicity = attribute.getMultiplicity();
            final String oldName = attribute.getName();
            final String newName = suggestAssociationEndName(classType, attribute,
                    multiplicity.getRange().getUpper() == Occurs.UNBOUNDED);
            final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>(attribute.getTaggedValues());
            final Identifier navigation = lookup.ensureTagDefinitionId(TagName.MONGO_NAVIGABLE);
            final TaggedValue tag = new TaggedValue(Boolean.toString(true), navigation);
            taggedValues.add(tag);
            if (!oldName.equals(newName)) {
                final Identifier nameTag = lookup.ensureTagDefinitionId(TagName.MONGO_NAME);
                taggedValues.add(new TaggedValue(oldName, nameTag));
            }
            // If it's navigable at the database level then we assume this is also true logically.
            return new AssociationEnd(multiplicity, newName, true, id, taggedValues, reference, oldName);
        } else {
            throw new IllegalStateException(referenceType + " " + nameToClassTypeId);
        }
    }

    private static final Map<String, Integer> degeneracies(final Map<String, AssociationEnd> sourceAttributes) {
        final Map<Identifier, Integer> typeCounts = new HashMap<Identifier, Integer>();
        for (final Map.Entry<String, AssociationEnd> entry : sourceAttributes.entrySet()) {
            final AssociationEnd associationEnd = entry.getValue();
            final Identifier typeId = associationEnd.getType();
            if (typeCounts.containsKey(typeId)) {
                typeCounts.put(typeId, typeCounts.get(typeId) + 1);
            } else {
                typeCounts.put(typeId, Integer.valueOf(1));
            }
        }
        final Map<String, Integer> degeneracies = new HashMap<String, Integer>();
        for (final Map.Entry<String, AssociationEnd> entry : sourceAttributes.entrySet()) {
            final AssociationEnd associationEnd = entry.getValue();
            final Identifier typeId = associationEnd.getType();
            degeneracies.put(entry.getKey(), typeCounts.get(typeId));
        }
        return Collections.unmodifiableMap(degeneracies);
    }

    private static final boolean hasNavigation(final Type source, final Type target,
                                               final Map<Type, Map<String, AssociationEnd>> navigations, final AssociationEnd excludeEnd) {
        return false;
        /* there's some type of issue if navigations exist and are returned to makeAssociations.
           If a navigation exists then you will get duplicate IDs in the xmi, making it unusable.  

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
        */
    }

    private static final AssociationEnd getNavigation(final Type source, final Type target,
                                                      final Map<Type, Map<String, AssociationEnd>> navigations, final AssociationEnd excludeEnd) {
        if (navigations.containsKey(source)) {
            final Map<String, AssociationEnd> attributes = navigations.get(source);
            for (final Map.Entry<String, AssociationEnd> entry : attributes.entrySet()) {
                final AssociationEnd end = entry.getValue();
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

    public static Model link(final Model model, final Xsd2UmlHostedPlugin plugin) {

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
                                                           final AssociationEnd rhsEnd, final Xsd2UmlHostedPlugin plugin, final Xsd2UmlPluginHost host
    ) {
        final Range sourceRange = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        final Multiplicity sourceMultiplicity = new Multiplicity(sourceRange);
        // All relationships are logically navigable in both directions.
        // But a relationship may only be physically navigable based on the database.
        final boolean navigable = true;
        return new AssociationEnd(sourceMultiplicity, lhsName, navigable, lhsType.getId(), rhsEnd.getAssociatedAttributeName());
    }

    private static final List<ClassType> makeAssociations(final Map<Type, Map<String, AssociationEnd>> navigations,
                                                          final ModelIndex lookup, final Xsd2UmlHostedPlugin plugin, final Xsd2UmlPluginHost host) {
        final List<ClassType> associations = new LinkedList<ClassType>();
        // Make sure that every navigation has a reverse navigation.
        for (final Map.Entry<Type, Map<String, AssociationEnd>> navEntry : navigations.entrySet()) {
            Type lhsType = navEntry.getKey();
            final Map<String, AssociationEnd> sourceAttributes = navEntry.getValue();
            final Map<String, Integer> degeneracies = degeneracies(sourceAttributes);

            for (final Map.Entry<String, AssociationEnd> saEntry : sourceAttributes.entrySet()) {
                String rhsEndName = saEntry.getKey();
                final AssociationEnd rhsEnd = saEntry.getValue();
                final Type rhsType = lookup.getType(rhsEnd.getType());
                // Notice roles of source and target are switched to find a reverse navigation.
                if (!hasNavigation(rhsType, lhsType, navigations, rhsEnd)) {
                    final String rhsTypeName = rhsType.getName();
                    final String lhsTypeName = lhsType.getName();
                    final Integer degs = degeneracies.get(rhsEndName);

                    final String lhsEndName = Xsd2UmlHelper.makeAssociationEndName(rhsTypeName, rhsEndName, degs,
                            lhsTypeName);

                    final AssociationEnd lhsEnd = makeAssociationEnd(lhsEndName, lhsType, rhsEnd, plugin, host);
                    final String name = plugin.nameAssociation(lhsEnd, rhsEnd, host.getPlugin());
                    associations.add(new ClassType(name, lhsEnd, rhsEnd));

                } else {
                    final AssociationEnd lhsEnd = getNavigation(rhsType, lhsType, navigations, rhsEnd);
                    final String name = plugin.nameAssociation(lhsEnd, rhsEnd, host.getPlugin());
                    associations.add(new ClassType(name, lhsEnd, rhsEnd));
                }
            }
        }
        return associations;
    }

    private static final Map<String, Identifier> makeNameToClassTypeId(final Iterable<ClassType> classTypes) {
        final Map<String, Identifier> map = new HashMap<String, Identifier>();
        for (final ClassType classType : classTypes) {
            map.put(classType.getName(), classType.getId());
        }
        return map;
    }

    private static final String suggestAssociationEndName(final ClassType classType, final Attribute attribute,
                                                          final boolean pluralize) {
        final String stem = Xsd2UmlHelper.camelCase(suggestStem(classType, attribute));
        return pluralize ? Xsd2UmlHelper.pluralize(stem) : stem;
    }

    private static final String suggestStem(final ClassType classType, final Attribute attribute) {
        final String name = attribute.getName();
        if (name.endsWith(SUFFIX_ID)) {
            return name.substring(0, name.length() - SUFFIX_ID.length());
        } else if (name.endsWith(SUFFIX_IDS)) {
            return name.substring(0, name.length() - SUFFIX_IDS.length());
        } else if (name.endsWith(SUFFIX_REFERENCE)) {
            // reportIllegalSuffix(classType, attribute);
            return name.substring(0, name.length() - SUFFIX_REFERENCE.length());
        } else if (name.endsWith(SUFFIX_REFERENCES)) {
            // reportIllegalSuffix(classType, attribute);
            return name.substring(0, name.length() - SUFFIX_REFERENCES.length());
        } else {
            // reportIllegalSuffix(classType, attribute);
            return name;
        }
    }

    private static final void reportIllegalSuffix(final ClassType classType, final Attribute attribute) {
        LOG.warn("Illegal suffix in " + classType.getName() + "." + attribute.getName());
    }
}
