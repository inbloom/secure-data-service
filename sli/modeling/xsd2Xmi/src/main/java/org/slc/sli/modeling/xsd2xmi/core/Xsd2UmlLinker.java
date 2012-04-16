package org.slc.sli.modeling.xsd2xmi.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;

/**
 * This class takes an incoming UML {@link Model} and converts attributes to associations based upon
 * heuristics provided by plug-ins.
 */
public final class Xsd2UmlLinker {
    
    private static final String associationEnd(final String name) {
        if (name.endsWith("Id")) {
            return name.substring(0, name.length() - 2);
        } else {
            return name;
        }
    }
    
    @SuppressWarnings("unused")
    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
    
    private static final List<Attribute> cleanUp(final ClassType classType, final List<Attribute> attributes,
            final Xsd2UmlPlugin plugin, final Mapper lookup, final Map<String, Identifier> nameToClassTypeId,
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
            final Xsd2UmlPlugin plugin, final Mapper mapper, final Map<String, Identifier> nameToClassTypeId,
            final Map<String, AssociationEnd> associationEnds) {
        final Xsd2UmlPluginHost host = new Xsd2UmlPluginHostAdapter(mapper);
        if (plugin.isAssociationEnd(classType, attribute, host)) {
            final AssociationEnd associationEnd = convertAttributeToAssociationEnd(classType, attribute, plugin, host,
                    nameToClassTypeId);
            associationEnds.put(associationEnd.getName(), associationEnd);
            return null;
        } else {
            return attribute;
        }
    }
    
    private static final ClassType cleanUpClassType(final ClassType classType, final Xsd2UmlPlugin plugin,
            final Mapper lookup, final Map<String, Identifier> nameToClassTypeId,
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
    
    private static final AssociationEnd convertAttributeToAssociationEnd(final ClassType classType,
            final Attribute attribute, final Xsd2UmlPlugin plugin, final Xsd2UmlPluginHost lookup,
            final Map<String, Identifier> nameToClassTypeId) {
        final String referenceType = plugin.getAssociationEndTypeName(classType, attribute, lookup);
        if (nameToClassTypeId.containsKey(referenceType)) {
            final Identifier reference = nameToClassTypeId.get(referenceType);
            // TODO: Opportunity to simplify the name of the association end.
            // TODO: Opportunity to supply the aggregation.
            return new AssociationEnd(attribute.getMultiplicity(), associationEnd(attribute.getName()), true,
                    attribute.getId(), attribute.getTaggedValues(), reference);
        } else {
            throw new IllegalStateException(referenceType);
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
            final Map<Type, Map<String, AssociationEnd>> navigations) {
        if (navigations.containsKey(source)) {
            final Map<String, AssociationEnd> attributes = navigations.get(source);
            for (final String name : attributes.keySet()) {
                final AssociationEnd end = attributes.get(name);
                final Identifier endType = end.getType();
                return endType.equals(target.getId());
            }
            return false;
        } else {
            return false;
        }
    }
    
    public static Model link(final Model model, final Xsd2UmlPlugin plugin) {
        
        final Mapper lookup = new DefaultMapper(model);
        final Map<String, Identifier> nameToClassTypeId = makeNameToClassTypeId(lookup.getClassTypes());
        final Map<Type, Map<String, AssociationEnd>> navigations = new HashMap<Type, Map<String, AssociationEnd>>();
        final List<NamespaceOwnedElement> ownedElements = new LinkedList<NamespaceOwnedElement>();
        
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            if (ownedElement instanceof ClassType) {
                final ClassType classType = (ClassType) ownedElement;
                ownedElements.add(cleanUpClassType(classType, plugin, lookup, nameToClassTypeId, navigations));
            } else {
                ownedElements.add(ownedElement);
            }
        }
        
        ownedElements.addAll(makeAssociations(navigations, lookup));
        return new Model(Identifier.random(), model.getName(), model.getTaggedValues(), ownedElements);
    }
    
    private static final Association makeAssociation(final Type source, final AssociationEnd existingEnd,
            final String name, final Mapper lookup) {
        final Range range = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        final Multiplicity multiplicity = new Multiplicity(range);
        final AssociationEnd missingEnd = new AssociationEnd(multiplicity, name, false, source.getId());
        return new Association(missingEnd, existingEnd);
    }
    
    private static final List<Association> makeAssociations(final Map<Type, Map<String, AssociationEnd>> navigations,
            final Mapper lookup) {
        final List<Association> associations = new LinkedList<Association>();
        // Make sure that every target has a reciprocal navigation path.
        for (final Type source : navigations.keySet()) {
            final Map<String, AssociationEnd> sourceAttributes = navigations.get(source);
            final Map<String, Integer> degeneracies = degeneracies(sourceAttributes);
            
            for (final String name : sourceAttributes.keySet()) {
                final AssociationEnd existingEnd = sourceAttributes.get(name);
                final Type target = lookup.getType(existingEnd.getType());
                // Notice how the roles of source and target become switched.
                if (!hasNavigation(target, source, navigations)) {
                    final String sourceName = existingEnd.getName();
                    String targetName = source.getName();
                    final String missingEndName = makeEndName(sourceName, degeneracies.get(name), targetName);
                    associations.add(makeAssociation(source, existingEnd, missingEndName, lookup));
                } else {
                    // FIXME
                }
            }
        }
        return associations;
    }
    
    private static final String makeEndName(final String sourceName, final int degeneracy, final String targetName) {
        if (degeneracy > 1) {
            return sourceName.concat(titleCase(targetName)).concat("s");
        } else {
            // The name is redundant when there is only one way to get to the target type.
            return "";
            // return camelCase(targetName).concat("s");
        }
    }
    
    private static final Map<String, Identifier> makeNameToClassTypeId(final Iterable<ClassType> classTypes) {
        final Map<String, Identifier> map = new HashMap<String, Identifier>();
        for (final ClassType classType : classTypes) {
            map.put(classType.getName(), classType.getId());
        }
        return map;
    }
    
    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
}
