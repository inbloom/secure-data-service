package org.slc.sli.modeling.xsd2xmi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.LazyLookup;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelVisitor;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Reference;
import org.slc.sli.modeling.uml.ReferenceType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Xsd2UmlConvert {
    
    private static final List<TaggedValue> EMPTY_TAGGED_VALUES = Collections.emptyList();
    
    public static final Model modelFromXmlSchema(final XmlSchema schema, final Xsd2UmlContext context,
            final LazyLookup lookup) {
        
        final XmlSchemaObjectCollection schemaItems = schema.getItems();
        
        final Map<Identifier, ClassType> classTypes = new HashMap<Identifier, ClassType>();
        final Map<Identifier, DataType> dataTypes = new HashMap<Identifier, DataType>();
        final Map<Identifier, EnumType> enumTypes = new HashMap<Identifier, EnumType>();
        final Map<Identifier, Association> associations = new HashMap<Identifier, Association>();
        final Map<Identifier, TagDefinition> tagDefinitions = new HashMap<Identifier, TagDefinition>();
        
        declareBuiltInTagDefinitions(tagDefinitions, context);
        declareBuiltInDataTypes(dataTypes, context);
        
        // Iterate XML Schema items
        for (int i = 0; i < schemaItems.getCount(); i++) {
            final XmlSchemaObject schemaObject = schemaItems.getItem(i);
            
            if (schemaObject instanceof XmlSchemaType) {
                final XmlSchemaType schemaType = (XmlSchemaType) schemaObject;
                final Identifier id = context.typeId.from(schemaType.getQName());
                
                convertType(schemaType, schema, context, new ModelVisitor() {
                    
                    @Override
                    public void classType(final ClassType classType) {
                        classTypes.put(id, classType);
                    }
                    
                    @Override
                    public void dataType(final DataType dataType) {
                        dataTypes.put(id, dataType);
                    }
                    
                    @Override
                    public void enumType(final EnumType enumType) {
                        enumTypes.put(id, enumType);
                    }
                }, lookup);
            } else if (schemaObject instanceof XmlSchemaElement) {
                throw new UnsupportedOperationException(schemaObject.getClass().getCanonicalName());
            } else if (schemaObject instanceof XmlSchemaInclude) {
                throw new UnsupportedOperationException(schemaObject.getClass().getCanonicalName());
            } else {
                throw new AssertionError(schemaObject);
            }
        }
        return new Model(classTypes, dataTypes, enumTypes, associations, tagDefinitions);
    }
    
    private static final void declareBuiltInDataTypes(final Map<Identifier, DataType> dataTypes,
            final Xsd2UmlContext context) {
        declareBuiltInDataType("duration", dataTypes, context);
        declareBuiltInDataType("dateTime", dataTypes, context);
        declareBuiltInDataType("time", dataTypes, context);
        declareBuiltInDataType("date", dataTypes, context);
        declareBuiltInDataType("gYearMonth", dataTypes, context);
        declareBuiltInDataType("gYear", dataTypes, context);
        declareBuiltInDataType("gMonthDay", dataTypes, context);
        declareBuiltInDataType("gDay", dataTypes, context);
        declareBuiltInDataType("gMonth", dataTypes, context);
        declareBuiltInDataType("boolean", dataTypes, context);
        declareBuiltInDataType("base64Binary", dataTypes, context);
        declareBuiltInDataType("hexBinary", dataTypes, context);
        declareBuiltInDataType("float", dataTypes, context);
        declareBuiltInDataType("decimal", dataTypes, context);
        declareBuiltInDataType("integer", dataTypes, context);
        declareBuiltInDataType("long", dataTypes, context);
        declareBuiltInDataType("int", dataTypes, context);
        declareBuiltInDataType("short", dataTypes, context);
        declareBuiltInDataType("byte", dataTypes, context);
        declareBuiltInDataType("double", dataTypes, context);
        declareBuiltInDataType("anyURI", dataTypes, context);
        declareBuiltInDataType("QName", dataTypes, context);
        declareBuiltInDataType("NOTATION", dataTypes, context);
        declareBuiltInDataType("string", dataTypes, context);
        declareBuiltInDataType("normalizedString", dataTypes, context);
        declareBuiltInDataType("token", dataTypes, context);
        declareBuiltInDataType("language", dataTypes, context);
        declareBuiltInDataType("ID", dataTypes, context);
        declareBuiltInDataType("IDREF", dataTypes, context);
    }
    
    private static final void declareBuiltInDataType(final String localName, final Map<Identifier, DataType> dataTypes,
            final Xsd2UmlContext context) {
        final Identifier id = context.typeId.from(new QName("http://www.w3.org/2001/XMLSchema", localName));
        final DataType dataType = new DataType(id, localName, false, EMPTY_TAGGED_VALUES);
        dataTypes.put(id, dataType);
    }
    
    private static final void declareBuiltInTagDefinitions(final Map<Identifier, TagDefinition> tagDefinitions,
            final Xsd2UmlContext context) {
        declareBuiltInTagDefinition("author", tagDefinitions, context);
        declareBuiltInTagDefinition("deprecated", tagDefinitions, context);
        declareBuiltInTagDefinition("documentation", tagDefinitions, context);
        declareBuiltInTagDefinition("version", tagDefinitions, context);
    }
    
    private static final void declareBuiltInTagDefinition(final String name,
            final Map<Identifier, TagDefinition> tagDefinitions, final Xsd2UmlContext context) {
        final Range range = new Range(Identifier.random(), Occurs.ZERO, Occurs.ZERO, EMPTY_TAGGED_VALUES);
        final Multiplicity multiplicity = new Multiplicity(Identifier.random(), EMPTY_TAGGED_VALUES, range);
        final Identifier id = context.tagDefinition.from(name);
        final TagDefinition tagDefinition = new TagDefinition(id, EMPTY_TAGGED_VALUES, name, multiplicity);
        tagDefinitions.put(id, tagDefinition);
    }
    
    private static final void convertType(final XmlSchemaType type, final XmlSchema schema,
            final Xsd2UmlContext context, final ModelVisitor handler, final LazyLookup lookup) {
        
        if (type instanceof XmlSchemaComplexType) {
            final XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            convertComplexType(complexType, schema, context, handler, lookup);
        } else if (type instanceof XmlSchemaSimpleType) {
            final XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) type;
            convertSimpleType(simpleType, schema, context, handler, lookup);
        } else {
            throw new AssertionError(type);
        }
    }
    
    private static final void convertComplexType(final XmlSchemaComplexType complexType, final XmlSchema schema,
            final Xsd2UmlContext context, final ModelVisitor handler, final LazyLookup lookup) {
        
        final List<Attribute> attributes = new LinkedList<Attribute>();
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        taggedValues.addAll(parseAnnotation(complexType, context, lookup));
        
        if (complexType.getContentModel() != null && complexType.getContentModel().getContent() != null) {
            final XmlSchemaContent content = complexType.getContentModel().getContent();
            if (content instanceof XmlSchemaComplexContentExtension) {
                final XmlSchemaComplexContentExtension schemaComplexContent = (XmlSchemaComplexContentExtension) content;
                final XmlSchemaComplexType complexBaseType = getComplexBaseType(schemaComplexContent, schema);
                if (complexBaseType != null) {
                    // complexSchema = parseComplexType(complexBaseType, complexSchema, schema);
                }
                attributes.addAll(parseFields(schemaComplexContent, schema, context, lookup));
            } else if (content instanceof XmlSchemaComplexContentRestriction) {
                throw new AssertionError(content);
            } else if (content instanceof XmlSchemaSimpleContentExtension) {
                throw new AssertionError(content);
            } else if (content instanceof XmlSchemaSimpleContentRestriction) {
                throw new AssertionError(content);
            } else {
                throw new AssertionError(content);
            }
        }
        
        attributes.addAll(parseFields(complexType, schema, context, lookup));
        
        final QName qualifiedName = complexType.getQName();
        final String localName = qualifiedName.getLocalPart();
        handler.classType(new ClassType(Identifier.random(), localName, false, attributes, taggedValues));
    }
    
    private static final XmlSchemaComplexType getComplexBaseType(
            final XmlSchemaComplexContentExtension schemaComplexContent, final XmlSchema schema) {
        final QName baseTypeName = schemaComplexContent.getBaseTypeName();
        final XmlSchemaType baseType = schema.getTypeByName(baseTypeName);
        if (baseType != null) {
            if (baseType instanceof XmlSchemaComplexType) {
                return (XmlSchemaComplexType) baseType;
            } else {
                throw new RuntimeException("Unsupported complex base type: " + baseType.getClass().getCanonicalName());
            }
        } else {
            throw new RuntimeException("Schema complex base type not found: " + baseTypeName);
        }
    }
    
    private static final void convertSimpleType(final XmlSchemaSimpleType simpleType, final XmlSchema schema,
            final Xsd2UmlContext context, final ModelVisitor handler, final LazyLookup lookup) {
        
        final XmlSchemaSimpleTypeContent content = simpleType.getContent();
        if (content instanceof XmlSchemaSimpleTypeList) {
            throw new AssertionError(content);
        } else if (content instanceof XmlSchemaSimpleTypeRestriction) {
            final XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) content;
            final XmlSchemaObjectCollection facets = restriction.getFacets();
            final int length = facets.getCount();
            final List<EnumLiteral> enumerationLiterals = new LinkedList<EnumLiteral>();
            for (int i = 0; i < length; i++) {
                final XmlSchemaObject item = facets.getItem(i);
                if (item instanceof XmlSchemaFacet) {
                    final XmlSchemaFacet facet = (XmlSchemaFacet) item;
                    if (facet instanceof XmlSchemaEnumerationFacet) {
                        final XmlSchemaEnumerationFacet enumFacet = (XmlSchemaEnumerationFacet) facet;
                        enumerationLiterals.add(new EnumLiteral(Identifier.random(), enumFacet.getValue().toString(),
                                parseAnnotation(enumFacet, context, lookup)));
                    } else if (facet instanceof XmlSchemaMinLengthFacet) {
                        @SuppressWarnings("unused")
                        final XmlSchemaMinLengthFacet minLengthFacet = (XmlSchemaMinLengthFacet) facet;
                    } else if (facet instanceof XmlSchemaMaxLengthFacet) {
                        @SuppressWarnings("unused")
                        final XmlSchemaMaxLengthFacet maxLengthFacet = (XmlSchemaMaxLengthFacet) facet;
                    } else {
                        throw new AssertionError(facet);
                    }
                } else {
                    throw new AssertionError(item);
                }
            }
            
            final QName qualifiedName = simpleType.getQName();
            final String localName = qualifiedName.getLocalPart();
            if (enumerationLiterals.isEmpty()) {
                handler.dataType(new DataType(Identifier.random(), localName, false, parseAnnotation(simpleType,
                        context, lookup)));
            } else {
                handler.enumType(new EnumType(Identifier.random(), localName, enumerationLiterals, parseAnnotation(
                        simpleType, context, lookup)));
            }
        } else if (content instanceof XmlSchemaSimpleTypeUnion) {
            throw new AssertionError(content);
        } else {
            throw new AssertionError(content);
        }
        
    }
    
    private static final List<Attribute> parseFields(final XmlSchemaComplexType schemaComplexType,
            final XmlSchema schema, final Xsd2UmlContext context, final LazyLookup lookup) {
        // parseAttributes(schemaComplexType.getAttributes(), schema);
        final List<Attribute> attributes = parseParticle(schemaComplexType.getParticle(), schema, context, lookup);
        return attributes;
    }
    
    private static final List<Attribute> parseFields(
            final XmlSchemaComplexContentExtension schemaComplexContentExtension, final XmlSchema schema,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        // parseAttributes(schemaComplexContentExtension.getAttributes(), schema);
        return parseParticle(schemaComplexContentExtension.getParticle(), schema, context, lookup);
    }
    
    private static final List<Attribute> parseParticle(final XmlSchemaParticle particle, final XmlSchema schema,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        
        final List<Attribute> attributes = new LinkedList<Attribute>();
        
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                final XmlSchemaElement element = (XmlSchemaElement) particle;
                attributes.add(parseElement(element, schema, context, lookup));
            } else if (particle instanceof XmlSchemaSequence) {
                final XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        attributes.addAll(parseParticle((XmlSchemaParticle) item, schema, context, lookup));
                    } else {
                        throw new RuntimeException("Unsupported XmlSchemaSequence item: "
                                + item.getClass().getCanonicalName());
                    }
                }
            } else if (particle instanceof XmlSchemaChoice) {
                
                final XmlSchemaChoice xmlSchemaChoice = (XmlSchemaChoice) particle;
                
                final XmlSchemaObjectCollection choices = xmlSchemaChoice.getItems();
                for (int i = 0; i < choices.getCount(); ++i) {
                    XmlSchemaObject item = xmlSchemaChoice.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        attributes.addAll(parseParticle((XmlSchemaParticle) item, schema, context, lookup));
                    }
                }
                
            } else {
                throw new RuntimeException("Unsupported XmlSchemaParticle item: "
                        + particle.getClass().getCanonicalName());
            }
        }
        return Collections.unmodifiableList(attributes);
    }
    
    private static final Attribute parseElement(final XmlSchemaElement element, final XmlSchema schema,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        taggedValues.addAll(parseAnnotation(element, context, lookup));
        
        final XmlSchemaType elementSchemaType = element.getSchemaType();
        if (elementSchemaType instanceof XmlSchemaComplexType) {
            final QName typeName = elementSchemaType.getQName();
            if (null == typeName) {
                // The type is anonymous.
                final QName baseType = elementSchemaType.getBaseSchemaTypeName();
                throw new RuntimeException(element.getQName() + " : " + baseType);
            } else {
                final Reference type = new Reference(context.typeId.from(typeName), ReferenceType.UNKNOWN_TYPE);
                return new Attribute(Identifier.random(), camelCase(element.getName()), type, multiplicity(range(
                        occurs(element.getMinOccurs()), occurs(element.getMaxOccurs()))), taggedValues, lookup);
            }
        } else if (elementSchemaType instanceof XmlSchemaSimpleType) {
            final XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) elementSchemaType;
            final Reference type = new Reference(context.typeId.from(getSimpleTypeName(simpleType)),
                    ReferenceType.UNKNOWN_TYPE);
            return new Attribute(Identifier.random(), camelCase(element.getName()), type, multiplicity(range(
                    occurs(element.getMinOccurs()), occurs(element.getMaxOccurs()))), taggedValues, lookup);
        } else {
            throw new AssertionError(elementSchemaType);
        }
    }
    
    private static final QName getSimpleTypeName(final XmlSchemaSimpleType simpleType) {
        final QName typeName = simpleType.getQName();
        if (null == typeName) {
            // The type is anonymous.
            final XmlSchemaSimpleTypeContent content = simpleType.getContent();
            if (content instanceof XmlSchemaSimpleTypeRestriction) {
                final XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) content;
                return restriction.getBaseTypeName();
            } else {
                throw new AssertionError(content);
            }
        } else {
            return typeName;
        }
    }
    
    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
    
    private static final Multiplicity multiplicity(final Range range) {
        return new Multiplicity(Identifier.random(), EMPTY_TAGGED_VALUES, range);
    }
    
    private static final Range range(final Occurs lowerBound, final Occurs upperBound) {
        return new Range(Identifier.random(), lowerBound, upperBound, EMPTY_TAGGED_VALUES);
    }
    
    private static final Occurs occurs(final long occurs) {
        if (occurs == 0L) {
            return Occurs.ZERO;
        } else if (occurs == 1L) {
            return Occurs.ONE;
        } else if (occurs == Long.MAX_VALUE) {
            return Occurs.UNBOUNDED;
        } else {
            throw new AssertionError(Long.valueOf(occurs));
        }
    }
    
    private static final List<TaggedValue> parseAnnotation(final XmlSchemaAnnotated schemaType,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        final XmlSchemaAnnotation annotation = schemaType.getAnnotation();
        if (schemaType == null || annotation == null) {
            return EMPTY_TAGGED_VALUES;
        } else {
            final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
            final TaggedValue taggedValue = parseDocumentation(annotation, context, lookup);
            if (taggedValue != null) {
                taggedValues.add(taggedValue);
            }
            parseAppInfo(schemaType);
            return taggedValues;
        }
    }
    
    private static final TaggedValue parseDocumentation(final XmlSchemaAnnotation annotation,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        final XmlSchemaObjectCollection children = annotation.getItems();
        for (int childIdx = 0; childIdx < children.getCount(); ++childIdx) {
            
            final XmlSchemaObject child = children.getItem(childIdx);
            if (child instanceof XmlSchemaDocumentation) {
                final XmlSchemaDocumentation documentation = (XmlSchemaDocumentation) child;
                final NodeList markup = documentation.getMarkup();
                final String text = stringValue(markup);
                return new TaggedValue(Identifier.random(), EMPTY_TAGGED_VALUES, text, new Reference(
                        context.tagDefinition.from("documentation"), ReferenceType.TAG_DEFINITION), lookup);
            }
        }
        return null;
    }
    
    private static final void parseAppInfo(XmlSchemaAnnotated annotated) {
        
        final XmlSchemaObjectCollection children = annotated.getAnnotation().getItems();
        for (int childId = 0; childId < children.getCount(); ++childId) {
            
            final XmlSchemaObject child = children.getItem(childId);
            if (child instanceof XmlSchemaAppInfo) {
                final XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) child;
                @SuppressWarnings("unused")
                final NodeList markup = appInfo.getMarkup();
            }
        }
    }
    
    private static final String stringValue(final NodeList markup) {
        final StringBuilder sb = new StringBuilder();
        final int length = markup.getLength();
        for (int i = 0; i < length; i++) {
            final Node node = markup.item(i);
            sb.append(node.getTextContent());
        }
        return sb.toString();
    }
}
