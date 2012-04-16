package org.slc.sli.modeling.xsd2xmi.extract;

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
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
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
import org.slc.sli.modeling.uml.DefaultModelLookup;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
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
import org.slc.sli.modeling.xml.XmlTools;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Xsd2UmlConvert {
    
    private static final List<TaggedValue> EMPTY_TAGGED_VALUES = Collections.emptyList();
    
    /**
     * The xs:annotation is parsed into {@link TaggedValue} with one entry per xs:documentation or
     * xs:appinfo.
     */
    private static final List<TaggedValue> annotations(final XmlSchemaAnnotated schemaType,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        final XmlSchemaAnnotation annotation = schemaType.getAnnotation();
        if (schemaType == null || annotation == null) {
            return EMPTY_TAGGED_VALUES;
        } else {
            final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
            final XmlSchemaObjectCollection children = annotation.getItems();
            
            for (int childIdx = 0; childIdx < children.getCount(); ++childIdx) {
                
                final XmlSchemaObject child = children.getItem(childIdx);
                if (child instanceof XmlSchemaDocumentation) {
                    final XmlSchemaDocumentation documentation = (XmlSchemaDocumentation) child;
                    taggedValues.add(documentation(documentation, context, lookup));
                } else if (child instanceof XmlSchemaAppInfo) {
                    final XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) child;
                    taggedValues.add(parseAppInfo(appInfo, context, lookup));
                } else {
                    throw new AssertionError(child);
                }
            }
            
            return taggedValues;
        }
    }
    
    private static final QName camelCase(final QName name) {
        final String text = name.getLocalPart();
        return new QName(camelCase(text));
    }
    
    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
    
    private static final void convertComplexType(final XmlSchemaComplexType complexType, final XmlSchema schema,
            final Xsd2UmlContext context, final ModelVisitor handler, final LazyLookup lookup,
            final QName complexTypeName, final List<TaggedValue> taggedValues) {
        
        // final QName complexTypeName = complexType.getQName();
        final Identifier complexTypeId = context.ensureId(complexTypeName);
        
        final List<Attribute> attributes = new LinkedList<Attribute>();
        
        if (complexType.getContentModel() != null && complexType.getContentModel().getContent() != null) {
            final XmlSchemaContent content = complexType.getContentModel().getContent();
            if (content instanceof XmlSchemaComplexContentExtension) {
                final XmlSchemaComplexContentExtension complexContentExtension = (XmlSchemaComplexContentExtension) content;
                attributes.addAll(parseFields(complexContentExtension, schema, context, lookup));
                // The base of the restriction is interpreted as a UML generalization.
                final QName baseTypeName = complexContentExtension.getBaseTypeName();
                final Identifier baseId = context.ensureId(baseTypeName);
                final Reference baseReference = new Reference(baseId, ReferenceType.UNKNOWN_TYPE);
                final Reference typeReference = new Reference(complexTypeId, ReferenceType.CLASS_TYPE);
                handler.generalization(generalization(typeReference, baseReference, context, lookup));
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
        
        handler.classType(new ClassType(complexTypeId, complexTypeName, false, attributes, taggedValues, lookup));
    }
    
    private static final Generalization generalization(final Reference child, final Reference parent,
            final Xsd2UmlContext ctxt, final LazyLookup lookup) {
        return new Generalization(new QName(""), Identifier.random(), EMPTY_TAGGED_VALUES, child, parent, lookup);
    }
    
    /**
     * The strategy here is that we discover the facets and then decide whether we are going to
     * create the UML EnumType or DataType.
     */
    private static final void convertSimpleType(final XmlSchemaSimpleType simpleType, final XmlSchema schema,
            final Xsd2UmlContext context, final ModelVisitor handler, final LazyLookup lookup) {
        final QName simpleTypeName = simpleType.getQName();
        final Identifier simpleTypeId = context.ensureId(simpleTypeName);
        
        final XmlSchemaSimpleTypeContent content = simpleType.getContent();
        if (content instanceof XmlSchemaSimpleTypeList) {
            throw new AssertionError(content);
        } else if (content instanceof XmlSchemaSimpleTypeRestriction) {
            final XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) content;
            
            // The base of the restriction is interpreted as a UML generalization.
            final QName baseTypeName = restriction.getBaseTypeName();
            final Identifier baseId = context.ensureId(baseTypeName);
            final Reference baseReference = new Reference(baseId, ReferenceType.UNKNOWN_TYPE);
            
            // Facets are collected as either tagged values or enumeration literals.
            final XmlSchemaObjectCollection facets = restriction.getFacets();
            
            final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
            final List<EnumLiteral> enumLiterals = new LinkedList<EnumLiteral>();
            
            for (int i = 0, length = facets.getCount(); i < length; i++) {
                final XmlSchemaObject item = facets.getItem(i);
                if (item instanceof XmlSchemaFacet) {
                    final XmlSchemaFacet facet = (XmlSchemaFacet) item;
                    if (facet instanceof XmlSchemaEnumerationFacet) {
                        final XmlSchemaEnumerationFacet enumFacet = (XmlSchemaEnumerationFacet) facet;
                        final EnumLiteral enumLiteral = enumLiteralFromFacet(enumFacet, context, lookup);
                        enumLiterals.add(enumLiteral);
                    } else if (facet instanceof XmlSchemaMinLengthFacet) {
                        final XmlSchemaMinLengthFacet minLengthFacet = (XmlSchemaMinLengthFacet) facet;
                        final TaggedValue minLength = minLength(minLengthFacet, context, lookup);
                        taggedValues.add(minLength);
                    } else if (facet instanceof XmlSchemaMaxLengthFacet) {
                        final XmlSchemaMaxLengthFacet maxLengthFacet = (XmlSchemaMaxLengthFacet) facet;
                        final TaggedValue maxLength = maxLength(maxLengthFacet, context, lookup);
                        taggedValues.add(maxLength);
                    } else if (facet instanceof XmlSchemaPatternFacet) {
                        final XmlSchemaPatternFacet patternFacet = (XmlSchemaPatternFacet) facet;
                        final TaggedValue pattern = pattern(patternFacet, context, lookup);
                        taggedValues.add(pattern);
                    } else {
                        // Implement other facets as we need them.
                        throw new AssertionError(facet);
                    }
                } else {
                    throw new AssertionError(item);
                }
            }
            
            // The name comes from the simple type.
            final QName qualifiedName = simpleType.getQName();
            // We also add the annotations to the list of tagged values.
            taggedValues.addAll(annotations(simpleType, context, lookup));
            final Reference typeReference;
            if (enumLiterals.isEmpty()) {
                final DataType dataType = new DataType(simpleTypeId, qualifiedName, false, taggedValues, lookup);
                handler.dataType(dataType);
                typeReference = new Reference(dataType.getId(), dataType.getKind());
            } else {
                final EnumType enumType = new EnumType(simpleTypeId, qualifiedName, enumLiterals, taggedValues, lookup);
                handler.enumType(enumType);
                typeReference = new Reference(enumType.getId(), enumType.getKind());
            }
            handler.generalization(generalization(typeReference, baseReference, context, lookup));
        } else if (content instanceof XmlSchemaSimpleTypeUnion) {
            throw new AssertionError(content);
        } else {
            throw new AssertionError(content);
        }
    }
    
    private static final void convertType(final XmlSchemaType type, final XmlSchema schema,
            final Xsd2UmlContext context, final ModelVisitor handler, final LazyLookup lookup, final QName name,
            final List<TaggedValue> taggedValues) {
        
        if (type instanceof XmlSchemaComplexType) {
            final XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            convertComplexType(complexType, schema, context, handler, lookup, name, taggedValues);
        } else if (type instanceof XmlSchemaSimpleType) {
            final XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) type;
            convertSimpleType(simpleType, schema, context, handler, lookup);
        } else {
            throw new AssertionError(type);
        }
    }
    
    private static final void declareBuiltInDataType(final String localName, final Map<Identifier, DataType> dataTypes,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        final QName name = new QName("http://www.w3.org/2001/XMLSchema", localName);
        final Identifier id = context.ensureId(name);
        final DataType dataType = new DataType(id, name, false, EMPTY_TAGGED_VALUES, lookup);
        dataTypes.put(id, dataType);
    }
    
    private static final void declareBuiltInDataTypes(final Map<Identifier, DataType> dataTypes,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        declareBuiltInDataType("duration", dataTypes, context, lookup);
        declareBuiltInDataType("dateTime", dataTypes, context, lookup);
        declareBuiltInDataType("time", dataTypes, context, lookup);
        declareBuiltInDataType("date", dataTypes, context, lookup);
        declareBuiltInDataType("gYearMonth", dataTypes, context, lookup);
        declareBuiltInDataType("gYear", dataTypes, context, lookup);
        declareBuiltInDataType("gMonthDay", dataTypes, context, lookup);
        declareBuiltInDataType("gDay", dataTypes, context, lookup);
        declareBuiltInDataType("gMonth", dataTypes, context, lookup);
        declareBuiltInDataType("boolean", dataTypes, context, lookup);
        declareBuiltInDataType("base64Binary", dataTypes, context, lookup);
        declareBuiltInDataType("hexBinary", dataTypes, context, lookup);
        declareBuiltInDataType("float", dataTypes, context, lookup);
        declareBuiltInDataType("decimal", dataTypes, context, lookup);
        declareBuiltInDataType("integer", dataTypes, context, lookup);
        declareBuiltInDataType("long", dataTypes, context, lookup);
        declareBuiltInDataType("int", dataTypes, context, lookup);
        declareBuiltInDataType("short", dataTypes, context, lookup);
        declareBuiltInDataType("byte", dataTypes, context, lookup);
        declareBuiltInDataType("double", dataTypes, context, lookup);
        declareBuiltInDataType("anyURI", dataTypes, context, lookup);
        declareBuiltInDataType("QName", dataTypes, context, lookup);
        declareBuiltInDataType("NOTATION", dataTypes, context, lookup);
        declareBuiltInDataType("string", dataTypes, context, lookup);
        declareBuiltInDataType("normalizedString", dataTypes, context, lookup);
        declareBuiltInDataType("token", dataTypes, context, lookup);
        declareBuiltInDataType("language", dataTypes, context, lookup);
        declareBuiltInDataType("ID", dataTypes, context, lookup);
        declareBuiltInDataType("IDREF", dataTypes, context, lookup);
    }
    
    private static final List<TagDefinition> declareTagDefinitions(final Xsd2UmlContext ctxt, final LazyLookup lookup) {
        final List<TagDefinition> tagDefinitions = new LinkedList<TagDefinition>();
        tagDefinitions.add(makeTagDefinition("author", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("deprecated", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("documentation", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("version", Occurs.ONE, ctxt, lookup));
        // Tag definitions used to represent W3C XML Schema facets.
        tagDefinitions.add(makeTagDefinition("length", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("maxLength", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("minLength", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("pattern", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("whiteSpace", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("maxExclusive", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("minExclusive", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("maxInclusive", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("minInclusive", Occurs.ONE, ctxt, lookup));
        // Tag definition used to represent SLI appinfo.
        tagDefinitions.add(makeTagDefinition("referenceType", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("personallyIdentifiableInfo", Occurs.ONE, ctxt, lookup));
        tagDefinitions.add(makeTagDefinition("readEnforcement", Occurs.ONE, ctxt, lookup));
        return tagDefinitions;
    }
    
    private static final TaggedValue documentation(final XmlSchemaDocumentation documentation,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        final NodeList markup = documentation.getMarkup();
        final String text = XmlTools.collapseWhitespace(stringValue(markup));
        final Identifier tagDefinitionId = context.ensureTagDefinitionId("documentation");
        final Reference tagDefinition = new Reference(tagDefinitionId, ReferenceType.TAG_DEFINITION);
        return new TaggedValue(Identifier.random(), EMPTY_TAGGED_VALUES, text, tagDefinition, lookup);
    }
    
    private static final EnumLiteral enumLiteralFromFacet(final XmlSchemaEnumerationFacet enumFacet,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        final List<TaggedValue> annotation = annotations(enumFacet, context, lookup);
        final QName name = new QName(enumFacet.getValue().toString());
        return new EnumLiteral(Identifier.random(), name, annotation);
    }
    
    /**
     * Extracts the {@link Model} from the {@link XmlSchema}.
     * 
     * This function does not do anything to create associations because these may require some
     * heuristics to extract. This functionality will be deferred to a post-processor.
     */
    public static final Model extractModel(final String name, final XmlSchema schema) {
        // We do some tricks here to enable forward references.
        final Xsd2UmlContext context = new Xsd2UmlContext();
        final DefaultModelLookup lookup = new DefaultModelLookup();
        final Model model = internalModelFromXmlSchema(name, schema, context, lookup);
        lookup.setModel(model);
        return model;
    }
    
    private static final QName getSimpleTypeName(final XmlSchemaSimpleType simpleType) {
        final QName typeName = simpleType.getQName();
        if (null == typeName) {
            // The type is anonymous.
            final XmlSchemaSimpleTypeContent content = simpleType.getContent();
            if (content instanceof XmlSchemaSimpleTypeRestriction) {
                final XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) content;
                return restriction.getBaseTypeName();
            } else if (content instanceof XmlSchemaSimpleTypeList) {
                final XmlSchemaSimpleTypeList list = (XmlSchemaSimpleTypeList) content;
                return list.getItemTypeName();
            } else {
                throw new AssertionError(content);
            }
        } else {
            return typeName;
        }
    }
    
    private static final Model internalModelFromXmlSchema(final String name, final XmlSchema schema,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        
        final XmlSchemaObjectCollection schemaItems = schema.getItems();
        
        final Map<Identifier, ClassType> classTypes = new HashMap<Identifier, ClassType>();
        final Map<Identifier, DataType> dataTypes = new HashMap<Identifier, DataType>();
        final Map<Identifier, EnumType> enumTypes = new HashMap<Identifier, EnumType>();
        final Map<Identifier, Association> associations = new HashMap<Identifier, Association>();
        final Map<Identifier, Generalization> generalizations = new HashMap<Identifier, Generalization>();
        final Map<Identifier, TagDefinition> tagDefinitions = new HashMap<Identifier, TagDefinition>();
        
        for (final TagDefinition tagDefinition : declareTagDefinitions(context, lookup)) {
            tagDefinitions.put(tagDefinition.getId(), tagDefinition);
        }
        declareBuiltInDataTypes(dataTypes, context, lookup);
        
        // Iterate XML Schema items
        for (int i = 0, count = schemaItems.getCount(); i < count; i++) {
            final XmlSchemaObject schemaObject = schemaItems.getItem(i);
            
            if (schemaObject instanceof XmlSchemaType) {
                // Capture the anonymous inner type as a type with the name of the element.
                // The SLI.xsd should be fixed to only have named types.
                final XmlSchemaType schemaType = (XmlSchemaType) schemaObject;
                final Identifier id = context.ensureId(schemaType.getQName());
                
                convertType(schemaType, schema, context, new ModelVisitor() {
                    
                    @Override
                    public void classType(final ClassType classType) {
                        if (id.equals(classType.getId())) {
                            classTypes.put(id, classType);
                        } else {
                            throw new AssertionError();
                        }
                    }
                    
                    @Override
                    public void dataType(final DataType dataType) {
                        if (id.equals(dataType.getId())) {
                            dataTypes.put(id, dataType);
                        } else {
                            throw new AssertionError();
                        }
                    }
                    
                    @Override
                    public void enumType(final EnumType enumType) {
                        if (id.equals(enumType.getId())) {
                            enumTypes.put(id, enumType);
                        } else {
                            throw new AssertionError();
                        }
                    }
                    
                    @Override
                    public void generalization(final Generalization generalization) {
                        generalizations.put(generalization.getId(), generalization);
                    }
                }, lookup, schemaType.getQName(), annotations(schemaType, context, lookup));
            } else if (schemaObject instanceof XmlSchemaElement) {
                final XmlSchemaElement element = (XmlSchemaElement) schemaObject;
                if (element.getSchemaTypeName() == null) {
                    final XmlSchemaType elementType = element.getSchemaType();
                    final Identifier id = context.ensureId(element.getQName());
                    convertType(elementType, schema, context, new ModelVisitor() {
                        
                        @Override
                        public void classType(final ClassType classType) {
                            if (id.equals(classType.getId())) {
                                classTypes.put(id, classType);
                            } else {
                                throw new AssertionError();
                            }
                        }
                        
                        @Override
                        public void dataType(final DataType dataType) {
                            if (id.equals(dataType.getId())) {
                                dataTypes.put(id, dataType);
                            } else {
                                throw new AssertionError();
                            }
                        }
                        
                        @Override
                        public void enumType(final EnumType enumType) {
                            if (id.equals(enumType.getId())) {
                                enumTypes.put(id, enumType);
                            } else {
                                throw new AssertionError();
                            }
                        }
                        
                        @Override
                        public void generalization(final Generalization generalization) {
                            throw new AssertionError();
                        }
                    }, lookup, element.getQName(), annotations(element, context, lookup));
                }
            } else if (schemaObject instanceof XmlSchemaInclude) {
                final XmlSchemaInclude includedSchema = (XmlSchemaInclude) schemaObject;
                final XmlSchema embeddedSchema = includedSchema.getSchema();
                
                // While we load the embedded schema the context for looking up identifiers is
                // shared so that names resolve to the same objects.
                final Model embeddedModel = internalModelFromXmlSchema("", embeddedSchema, context, lookup);
                
                for (final ClassType classType : embeddedModel.getClassTypeMap().values()) {
                    classTypes.put(classType.getId(), classType);
                }
                for (final DataType dataType : embeddedModel.getDataTypeMap().values()) {
                    dataTypes.put(dataType.getId(), dataType);
                }
                for (final EnumType enumType : embeddedModel.getEnumTypeMap().values()) {
                    enumTypes.put(enumType.getId(), enumType);
                }
                for (final Generalization generalization : embeddedModel.getGeneralizationMap().values()) {
                    generalizations.put(generalization.getId(), generalization);
                }
            } else {
                throw new AssertionError(schemaObject);
            }
        }
        return new Model(name, classTypes, dataTypes, enumTypes, associations, generalizations, tagDefinitions);
    }
    
    private static final TagDefinition makeTagDefinition(final String name, final Occurs upper,
            final Xsd2UmlContext ctxt, final LazyLookup lookup) {
        final Range range = new Range(Identifier.random(), Occurs.ZERO, upper, EMPTY_TAGGED_VALUES);
        final Multiplicity multiplicity = new Multiplicity(Identifier.random(), EMPTY_TAGGED_VALUES, range);
        final Identifier id = ctxt.ensureTagDefinitionId(name);
        return new TagDefinition(id, EMPTY_TAGGED_VALUES, new QName("", name), multiplicity);
    }
    
    private static final TaggedValue maxLength(final XmlSchemaMaxLengthFacet maxLength, final Xsd2UmlContext ctxt,
            final LazyLookup lookup) {
        final String value = maxLength.getValue().toString();
        final List<TaggedValue> annotations = annotations(maxLength, ctxt, lookup);
        final Identifier tagDefinitionId = ctxt.ensureTagDefinitionId("maxLength");
        final Reference tagDefinition = new Reference(tagDefinitionId, ReferenceType.TAG_DEFINITION);
        return new TaggedValue(Identifier.random(), annotations, value, tagDefinition, lookup);
    }
    
    private static final TaggedValue minLength(final XmlSchemaMinLengthFacet minLength, final Xsd2UmlContext ctxt,
            final LazyLookup lookup) {
        final String value = minLength.getValue().toString();
        final List<TaggedValue> annotations = annotations(minLength, ctxt, lookup);
        final Identifier tagDefinitionId = ctxt.ensureTagDefinitionId("minLength");
        final Reference tagDefinition = new Reference(tagDefinitionId, ReferenceType.TAG_DEFINITION);
        return new TaggedValue(Identifier.random(), annotations, value, tagDefinition, lookup);
    }
    
    private static final TaggedValue pattern(final XmlSchemaPatternFacet patternFacet, final Xsd2UmlContext ctxt,
            final LazyLookup lookup) {
        final String value = patternFacet.getValue().toString();
        final List<TaggedValue> annotations = annotations(patternFacet, ctxt, lookup);
        final Identifier tagDefinitionId = ctxt.ensureTagDefinitionId("pattern");
        final Reference tagDefinition = new Reference(tagDefinitionId, ReferenceType.TAG_DEFINITION);
        return new TaggedValue(Identifier.random(), annotations, value, tagDefinition, lookup);
    }
    
    private static final Multiplicity multiplicity(final Range range, final LazyLookup lookup) {
        return new Multiplicity(Identifier.random(), EMPTY_TAGGED_VALUES, range);
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
    
    private static final TaggedValue parseAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlContext context,
            final LazyLookup lookup) {
        final NodeList markup = appInfo.getMarkup();
        for (int i = 0; i < markup.getLength(); i++) {
            final Node node = markup.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                final Element element = (Element) node;
                
                final String localName = element.getLocalName();
                final Identifier tagDefinitionId = context.ensureTagDefinitionId(camelCase(localName));
                final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                return new TaggedValue(Identifier.random(), EMPTY_TAGGED_VALUES, text, new Reference(tagDefinitionId,
                        ReferenceType.TAG_DEFINITION), lookup);
            }
        }
        throw new AssertionError();
    }
    
    private static final Attribute parseElement(final XmlSchemaElement element, final XmlSchema schema,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        taggedValues.addAll(annotations(element, context, lookup));
        
        final XmlSchemaType elementSchemaType = element.getSchemaType();
        final Occurs minOccurs = occurs(element.getMinOccurs());
        final Occurs maxOccurs = occurs(element.getMaxOccurs());
        final Multiplicity multiplicity = multiplicity(range(minOccurs, maxOccurs, lookup), lookup);
        if (elementSchemaType instanceof XmlSchemaComplexType) {
            final QName typeName = elementSchemaType.getQName();
            if (null == typeName) {
                // The type is anonymous.
                final QName baseType = elementSchemaType.getBaseSchemaTypeName();
                throw new RuntimeException(element.getQName() + " : " + baseType);
            } else {
                final Reference type = new Reference(context.ensureId(typeName), ReferenceType.UNKNOWN_TYPE);
                return new Attribute(Identifier.random(), camelCase(element.getQName()), type, multiplicity,
                        taggedValues, lookup);
            }
        } else if (elementSchemaType instanceof XmlSchemaSimpleType) {
            final XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) elementSchemaType;
            final Reference type = new Reference(context.ensureId(getSimpleTypeName(simpleType)),
                    ReferenceType.UNKNOWN_TYPE);
            return new Attribute(Identifier.random(), camelCase(element.getQName()), type, multiplicity, taggedValues,
                    lookup);
        } else {
            throw new AssertionError(elementSchemaType);
        }
    }
    
    private static final List<Attribute> parseFields(
            final XmlSchemaComplexContentExtension schemaComplexContentExtension, final XmlSchema schema,
            final Xsd2UmlContext context, final LazyLookup lookup) {
        // parseAttributes(schemaComplexContentExtension.getAttributes(), schema);
        return parseParticle(schemaComplexContentExtension.getParticle(), schema, context, lookup);
    }
    
    private static final List<Attribute> parseFields(final XmlSchemaComplexType schemaComplexType,
            final XmlSchema schema, final Xsd2UmlContext context, final LazyLookup lookup) {
        // parseAttributes(schemaComplexType.getAttributes(), schema);
        final List<Attribute> attributes = parseParticle(schemaComplexType.getParticle(), schema, context, lookup);
        return attributes;
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
    
    private static final Range range(final Occurs lowerBound, final Occurs upperBound, final LazyLookup lookup) {
        return new Range(Identifier.random(), lowerBound, upperBound, EMPTY_TAGGED_VALUES);
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
