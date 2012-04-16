package org.slc.sli.modeling.xsd2xmi.linker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.DefaultModelLookup;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.LazyLookup;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Reference;
import org.slc.sli.modeling.uml.ReferenceType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;

/**
 * This class takes an incoming UML {@link Model} and converts attributes to associations based upon
 * heuristics provided by plug-ins.
 */
public final class Xsd2UmlLinker {
    
    private static final List<TaggedValue> EMPTY_TAGGED_VALUES = Collections.emptyList();
    
    private static final Attribute cleanUpAttribute(final ClassType classType, final Attribute attribute,
            final LazyLookup lookup, final Map<String, Identifier> referenceMap,
            final Map<QName, AssociationEnd> classNavigations) {
        final Identifier id = attribute.getId();
        final QName name = attribute.getName();
        final Type type = attribute.getType();
        if (!isReference(attribute)) {
            final Reference typeRef = new Reference(type.getId(), type.getKind());
            final Multiplicity multiplicity = attribute.getMultiplicity();
            final List<TaggedValue> taggedValues = attribute.getTaggedValues();
            return new Attribute(id, name, typeRef, multiplicity, taggedValues, lookup);
        } else {
            final AssociationEnd associationEnd = convertAttributeToAssociationEnd(attribute, lookup, referenceMap);
            classNavigations.put(associationEnd.getName(), associationEnd);
            return null;
        }
    }
    
    private static final boolean isReference(final Attribute attribute) {
        final List<TaggedValue> taggedValues = attribute.getTaggedValues();
        for (final TaggedValue taggedValue : taggedValues) {
            if (taggedValue.getTagDefinition().getName().getLocalPart().equals("referenceType")) {
                return true;
            }
        }
        return false;
    }
    
    private static final String getReferenceType(final Attribute attribute) {
        final List<TaggedValue> taggedValues = attribute.getTaggedValues();
        for (final TaggedValue taggedValue : taggedValues) {
            if (taggedValue.getTagDefinition().getName().getLocalPart().equals("referenceType")) {
                return taggedValue.getValue();
            }
        }
        throw new AssertionError();
    }
    
    private static final AssociationEnd convertAttributeToAssociationEnd(final Attribute attribute,
            final LazyLookup lookup, final Map<String, Identifier> referenceMap) {
        final String referenceType = getReferenceType(attribute);
        if (referenceMap.containsKey(referenceType)) {
            final Reference reference = new Reference(referenceMap.get(referenceType), ReferenceType.CLASS_TYPE);
            return new AssociationEnd(attribute.getMultiplicity(), associationEnd(attribute.getName()), true,
                    attribute.getId(), attribute.getTaggedValues(), reference, lookup);
        } else {
            throw new IllegalStateException(referenceType);
        }
    }
    
    private static final QName associationEnd(final QName name) {
        final String localName = name.getLocalPart();
        if (localName.endsWith("Id")) {
            final String trimmed = localName.substring(0, localName.length() - 2);
            return new QName(trimmed);
        } else {
            return name;
        }
    }
    
    private static final List<Attribute> cleanUp(final ClassType classType, final List<Attribute> attributes,
            final LazyLookup lookup, final Map<String, Identifier> referenceMap,
            final Map<QName, AssociationEnd> classNavigations) {
        final List<Attribute> result = new LinkedList<Attribute>();
        for (final Attribute attribute : attributes) {
            final Attribute cleanUp = cleanUpAttribute(classType, attribute, lookup, referenceMap, classNavigations);
            if (cleanUp != null) {
                result.add(cleanUp);
            }
        }
        return result;
    }
    
    private static final Map<Identifier, ClassType> cleanUp(final Map<Identifier, ClassType> classTypeMap,
            final LazyLookup lookup, final Map<String, Identifier> referenceMap,
            final Map<Type, Map<QName, AssociationEnd>> navigations) {
        final Map<Identifier, ClassType> result = new HashMap<Identifier, ClassType>();
        for (final ClassType classType : classTypeMap.values()) {
            result.put(classType.getId(), cleanUpClassType(classType, lookup, referenceMap, navigations));
        }
        return result;
    }
    
    private static final ClassType cleanUpClassType(final ClassType classType, final LazyLookup lookup,
            final Map<String, Identifier> referenceMap, final Map<Type, Map<QName, AssociationEnd>> navigations) {
        final Identifier id = classType.getId();
        final QName name = classType.getName();
        boolean isAbstract = classType.isAbstract();
        final HashMap<QName, AssociationEnd> classNavigations = new HashMap<QName, AssociationEnd>();
        final List<Attribute> attributes = cleanUp(classType, classType.getAttributes(), lookup, referenceMap,
                classNavigations);
        if (classNavigations.size() > 0) {
            navigations.put(classType, classNavigations);
        }
        final List<TaggedValue> taggedValues = classType.getTaggedValues();
        return new ClassType(id, name, isAbstract, attributes, taggedValues, lookup);
    }
    
    public static Model link(final Model model) {
        // The new model needs to have a new lookup basis for forward references.
        // Unfortunately, this is a bit tedious.
        final DefaultModelLookup lookup = new DefaultModelLookup();
        
        // And we are going to have to do a lookup.
        final Map<String, Identifier> referenceMap = makeReferenceTypeMap(model.getClassTypeMap().values());
        final Map<Type, Map<QName, AssociationEnd>> navigations = new HashMap<Type, Map<QName, AssociationEnd>>();
        
        final String name = model.getName();
        final Map<Identifier, ClassType> classTypeMap = model.getClassTypeMap();
        final Map<Identifier, DataType> dataTypeMap = resetDataTypes(model.getDataTypeMap(), lookup);
        final Map<Identifier, EnumType> enumTypeMap = resetEnumTypes(model.getEnumTypeMap(), lookup);
        final Map<Identifier, Association> associationMap = model.getAssociationMap();
        final Map<Identifier, Generalization> generalizationMap = model.getGeneralizationMap();
        final Map<Identifier, TagDefinition> tagDefinitionMap = model.getTagDefinitionMap();
        final Map<Identifier, ClassType> cleanUp = cleanUp(classTypeMap, lookup, referenceMap, navigations);
        final Model modelOut = new Model(name, cleanUp, dataTypeMap, enumTypeMap, associationMap, generalizationMap,
                tagDefinitionMap);
        // Make it possible for AssociationEnd(s) to dereference in the next step (all types have
        // been discovered).
        lookup.setModel(modelOut);
        
        final List<Association> associations = makeAssociations(navigations, lookup);
        
        return addAssociations(modelOut, associations);
    }
    
    private static Model addAssociations(final Model model, final List<Association> associations) {
        final DefaultModelLookup lookup = new DefaultModelLookup();
        
        final String name = model.getName();
        final Map<Identifier, ClassType> c = resetClassTypes(model.getClassTypeMap(), lookup);
        final Map<Identifier, DataType> d = resetDataTypes(model.getDataTypeMap(), lookup);
        final Map<Identifier, EnumType> e = resetEnumTypes(model.getEnumTypeMap(), lookup);
        final Map<Identifier, Association> a = resetAssociations(associations);
        final Map<Identifier, Generalization> g = model.getGeneralizationMap();
        final Map<Identifier, TagDefinition> t = model.getTagDefinitionMap();
        final Model modelOut = new Model(name, c, d, e, a, g, t);
        lookup.setModel(modelOut);
        
        return modelOut;
    }
    
    private static final Map<Identifier, Association> resetAssociations(final List<Association> associations) {
        final Map<Identifier, Association> map = new HashMap<Identifier, Association>();
        for (final Association association : associations) {
            map.put(association.getId(), association);
            // System.out.println("lhs=" + association.getLHS().getName() + ", rhs=" +
            // association.getRHS().getName());
        }
        return map;
    }
    
    private static final List<Association> makeAssociations(final Map<Type, Map<QName, AssociationEnd>> navigations,
            final LazyLookup lookup) {
        final List<Association> associations = new LinkedList<Association>();
        // Make sure that every target has a reciprocal navigation path.
        for (final Type source : navigations.keySet()) {
            final Map<QName, AssociationEnd> sourceAttributes = navigations.get(source);
            final int degeneracy = sourceAttributes.size();
            for (final QName name : sourceAttributes.keySet()) {
                final AssociationEnd existingEnd = sourceAttributes.get(name);
                final Type target = existingEnd.getType();
                // Notice how the roles of source and target become switched.
                if (!hasNavigation(target, source, navigations)) {
                    final QName missingEndName = makeReciprocalName(existingEnd.getName(), degeneracy, source.getName());
                    associations.add(makeAssociation(source, existingEnd, missingEndName, lookup));
                } else {
                    // FIXME
                }
            }
        }
        return associations;
    }
    
    private static final QName titleCase(final QName name) {
        final String text = name.getLocalPart();
        return new QName(titleCase(text));
    }
    
    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
    
    private static final QName makeReciprocalName(final QName sourceName, final int degeneracy, final QName targetName) {
        if (degeneracy > 1) {
            return new QName(sourceName.getLocalPart().concat(titleCase(targetName).getLocalPart()).concat("s"));
        } else {
            return new QName(targetName.getLocalPart().concat("s"));
        }
    }
    
    private static final Association makeAssociation(final Type source, final AssociationEnd existingEnd,
            final QName name, final LazyLookup lookup) {
        final Range range = new Range(Identifier.random(), Occurs.ZERO, Occurs.UNBOUNDED, EMPTY_TAGGED_VALUES);
        final Multiplicity multiplicity = new Multiplicity(Identifier.random(), EMPTY_TAGGED_VALUES, range);
        final Reference reference = new Reference(source.getId(), source.getKind());
        final AssociationEnd missingEnd = new AssociationEnd(multiplicity, name, false, Identifier.random(),
                EMPTY_TAGGED_VALUES, reference, lookup);
        return new Association(Identifier.random(), new QName(""), missingEnd, existingEnd, EMPTY_TAGGED_VALUES);
    }
    
    private static final boolean hasNavigation(final Type source, final Type target,
            final Map<Type, Map<QName, AssociationEnd>> navigations) {
        if (navigations.containsKey(source)) {
            final Map<QName, AssociationEnd> attributes = navigations.get(source);
            for (final QName name : attributes.keySet()) {
                final AssociationEnd end = attributes.get(name);
                final Type endType = end.getType();
                return endType.equals(target);
            }
            return false;
        } else {
            return false;
        }
    }
    
    private static final Map<String, Identifier> makeReferenceTypeMap(final Collection<ClassType> classTypes) {
        final Map<String, Identifier> map = new HashMap<String, Identifier>();
        for (final ClassType classType : classTypes) {
            map.put(classType.getName().getLocalPart(), classType.getId());
        }
        return map;
    }
    
    private static final ClassType resetClass(final ClassType classType, final LazyLookup lookup) {
        final Identifier id = classType.getId();
        final QName name = classType.getName();
        boolean isAbstract = classType.isAbstract();
        final List<TaggedValue> taggedValues = classType.getTaggedValues();
        return new ClassType(id, name, isAbstract, classType.getAttributes(), taggedValues, lookup);
    }
    
    private static final Map<Identifier, ClassType> resetClassTypes(final Map<Identifier, ClassType> dataTypeMap,
            final LazyLookup lookup) {
        final Map<Identifier, ClassType> result = new HashMap<Identifier, ClassType>();
        for (final ClassType dataType : dataTypeMap.values()) {
            result.put(dataType.getId(), resetClass(dataType, lookup));
        }
        return result;
    }
    
    private static final DataType resetData(final DataType classType, final LazyLookup lookup) {
        final Identifier id = classType.getId();
        final QName name = classType.getName();
        boolean isAbstract = classType.isAbstract();
        final List<TaggedValue> taggedValues = classType.getTaggedValues();
        return new DataType(id, name, isAbstract, taggedValues, lookup);
    }
    
    private static final Map<Identifier, DataType> resetDataTypes(final Map<Identifier, DataType> dataTypeMap,
            final LazyLookup lookup) {
        final Map<Identifier, DataType> result = new HashMap<Identifier, DataType>();
        for (final DataType dataType : dataTypeMap.values()) {
            result.put(dataType.getId(), resetData(dataType, lookup));
        }
        return result;
    }
    
    private static final EnumType resetEnumType(final EnumType enumType, final LazyLookup lookup) {
        final Identifier id = enumType.getId();
        final QName name = enumType.getName();
        final List<TaggedValue> taggedValues = enumType.getTaggedValues();
        return new EnumType(id, name, enumType.getLiterals(), taggedValues, lookup);
    }
    
    private static final Map<Identifier, EnumType> resetEnumTypes(final Map<Identifier, EnumType> enumTypeMap,
            final LazyLookup lookup) {
        final Map<Identifier, EnumType> result = new HashMap<Identifier, EnumType>();
        for (final EnumType dataType : enumTypeMap.values()) {
            result.put(dataType.getId(), resetEnumType(dataType, lookup));
        }
        return result;
    }
}
