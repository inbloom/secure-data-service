package org.slc.sli.validation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaTotalDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.XmlSchemaWhiteSpaceFacet;

/**
 * Attempts to convert XSD to Avro Schema. Emphasis on "attempts".
 * 
 * If I had it to do over, this could would look much different.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class XsdToAvroSchema {
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String xsd = "/xsd/Ed-Fi-Core.xsd";
        String outputDir = "generated-schemas/";
        XmlSchema schema = loadSchema(xsd);
        XsdToAvroSchema gen = new XsdToAvroSchema();
        gen.schemasFromXSD(schema, outputDir);
    }
    
    Map<String, Schema> schemaMap = new HashMap<String, Schema>();
    XmlSchema root;
    String nameSpace = "org.slc.sli.domain";
    String enumNameSpace = nameSpace + ".enum";
    BaseTypes baseTypes = new BaseTypes();
    
    private void schemasFromXSD(XmlSchema schema, String outputDir) throws IOException {
        root = schema;
        System.out.println("SCHEMA " + schema.getItems().getCount());
        XmlSchemaObjectCollection schemaItems = schema.getItems();
        
        File outDir = null;
        File enumDir = null;
        if (outputDir != null) {
            outDir = new File(outputDir);
            outDir.mkdirs();
            enumDir = new File(outputDir, "enum");
            enumDir.mkdirs();
        }
        for (int i = 0; i < schemaItems.getCount(); i++) {
            XmlSchemaObject obj = schemaItems.getItem(i);
            Schema s = schemaFromObject(obj);
            
            if (s != null && !AvroUtils.isPrimitiveSchema(s)) {
                String json = AvroUtils.schemaToJson(s);
                
                if (outputDir != null) {
                    File file;
                    if (s.getType() == Schema.Type.ENUM) {
                        file = new File(enumDir, s.getName() + ".avpr");
                    } else {
                        file = new File(outDir, s.getName() + ".avpr");
                    }
                    if (!file.createNewFile()) {
                        throw new RuntimeException("File already exists.  Cowardly refusing to continue: "
                                + file.getAbsolutePath());
                    }
                    FileWriter out = new FileWriter(file);
                    out.write(json);
                    out.close();
                }
                System.out.println("TYPE: " + s.getFullName());
                System.out.println(json);
            }
        }
        
        // for (Map.Entry<String, Integer> entry : refCount.entrySet()) {
        // System.out.println(entry.getKey() + "\t" + entry.getValue());
        // }
    }
    
    private Schema schemaFromObject(XmlSchemaObject obj) {
        if (obj instanceof XmlSchemaAnnotated) {
            return schemaFromAnnotated((XmlSchemaAnnotated) obj);
        }
        // every other type is either documentation or annotation
        return null;
    }
    
    private Schema schemaFromAnnotated(XmlSchemaAnnotated anno) {
        if (anno instanceof XmlSchemaType) {
            return schemaFromType((XmlSchemaType) anno);
        }
        return null;
    }
    
    private Schema schemaFromType(XmlSchemaType type) {
        if (type instanceof XmlSchemaComplexType) {
            return schemaFromComplexType((XmlSchemaComplexType) type);
        } else if (type instanceof XmlSchemaSimpleType) {
            return schemaFromSimpleType((XmlSchemaSimpleType) type);
        } else {
            throw new RuntimeException("Unhandled schema type: " + type.getClass().getCanonicalName());
        }
    }
    
    /**
     * Create a named schema from complex type.
     */
    private Schema schemaFromComplexType(XmlSchemaComplexType elem) {
        String elementName = elem.getName();
        
        Schema schema = Schema.createRecord(elementName, parseAnnotation(elem), this.nameSpace, false);
        ArrayList<Schema.Field> fields = new ArrayList<Schema.Field>();
        fieldsFromComplexType(elem, fields);
        schema.setFields(fields);
        
        return schema;
    }
    
    /**
     * Create fields for the complex type and add them to the list of fields.
     */
    private void fieldsFromComplexType(XmlSchemaComplexType elem, List<Schema.Field> fields) {
        if (elem.getContentModel() != null && elem.getContentModel().getContent() != null) {
            if (elem.getContentModel().getContent() instanceof XmlSchemaComplexContentExtension) {
                fieldsFromComplexContentExtension((XmlSchemaComplexContentExtension) elem.getContentModel()
                        .getContent(), fields);
            } else {
                throw new RuntimeException("Unhandled content model: "
                        + elem.getContentModel().getContent().getClass().getCanonicalName());
            }
        }
        
        fieldsFromAttributes(elem.getAttributes(), fields);
        if (elem.getParticle() != null) {
            fieldsFromParticle(elem.getParticle(), fields);
        }
    }
    
    private void fieldsFromComplexContentExtension(XmlSchemaComplexContentExtension ext, List<Schema.Field> fields) {
        QName base = ext.getBaseTypeName();
        XmlSchemaType baseType = this.root.getTypeByName(base);
        if (baseType == null) {
            throw new RuntimeException("Cannot find base type for extension: " + base + " " + baseType);
        }
        
        if (baseType instanceof XmlSchemaComplexType) {
            fieldsFromComplexType((XmlSchemaComplexType) baseType, fields);
        } else {
            throw new RuntimeException("Unhandled extension base type: " + baseType.getClass().getCanonicalName());
        }
        
        fieldsFromAttributes(ext.getAttributes(), fields);
        
        if (ext.getParticle() != null) {
            fieldsFromParticle(ext.getParticle(), fields);
        }
    }
    
    /**
     * Creates the schema for a simple type.
     * 
     * There are 3 types of simple types.
     * 1) Simple type that extends a Xml Schema primitive type with additional restrictions.
     * 2) Simple type that extends another simple type with additional restrictions.
     * 3) Anonymous simple type that may be either of the above two, but has no name because it is
     * declared in-line as part of an element.
     * 
     * Determining type:
     * if the simple type has a base type, use that
     * otherwise look at the base type of the simple types's restriction content.
     * 
     * Pseudo-code:
     * if this is type 1 above, create schema.
     * else if this is type 2:
     * --- recursively create that simple type,
     * --- shallow clone the simple type
     * if there are any restrictions
     * --- add those restrictions as properties
     * if this is not an anonymous simple type
     * --- cache the schema using the name as the key
     * 
     */
    private Schema schemaFromSimpleType(XmlSchemaSimpleType s) {
        
        String name = s.getName();
        String space = this.nameSpace;
        
        String doc = parseAnnotation(s);
        
        // get the schema
        // TODO: Refactor these if statements. They have a grown a little too... organic.
        Schema schema = null;
        if (this.baseTypes.isBaseType(s.getQName())) {
            schema = this.baseTypes.createBaseType(s.getQName());
            
        } else if (s.getBaseSchemaTypeName() != null && this.baseTypes.isBaseType(s.getBaseSchemaTypeName())) {
            schema = this.baseTypes.createBaseType(s.getBaseSchemaTypeName());
            
        } else if (s.getBaseSchemaTypeName() != null && this.root.getTypeByName(s.getBaseSchemaTypeName()) != null) {
            XmlSchemaType parentType = this.root.getTypeByName(s.getBaseSchemaTypeName());
            if (!(parentType instanceof XmlSchemaSimpleType)) {
                throw new RuntimeException(
                        "Simple type inherits from non-simple type.  Don't know what to do about that. "
                                + parentType.getClass().getCanonicalName());
            }
            Schema parentSchema = schemaFromSimpleType((XmlSchemaSimpleType) parentType);
            if (name == null) {
                name = parentSchema.getName();
                space = parentSchema.getNamespace();
            }
            schema = AvroUtils.shallowCloneSchema(parentSchema, name, space, doc, true);
            
        } else if (s.getContent() != null && s.getContent() instanceof XmlSchemaSimpleTypeRestriction
                && ((XmlSchemaSimpleTypeRestriction) s.getContent()).getBaseTypeName() != null
                && this.root.getTypeByName(((XmlSchemaSimpleTypeRestriction) s.getContent()).getBaseTypeName()) != null) {
            XmlSchemaType parentType = this.root.getTypeByName(((XmlSchemaSimpleTypeRestriction) s.getContent())
                    .getBaseTypeName());
            if (!(parentType instanceof XmlSchemaSimpleType)) {
                throw new RuntimeException(
                        "Simple type inherits from non-simple type.  Don't know what to do about that. "
                                + parentType.getClass().getCanonicalName());
            }
            Schema parentSchema = schemaFromSimpleType((XmlSchemaSimpleType) parentType);
            if (name == null) {
                name = parentSchema.getName();
                if (!AvroUtils.isPrimitiveSchema(parentSchema)) {
                    space = parentSchema.getNamespace();
                }
            }
            schema = AvroUtils.shallowCloneSchema(parentSchema, name, space, doc, true);
            
        } else if (s.getContent() != null && s.getContent() instanceof XmlSchemaSimpleTypeRestriction
                && ((XmlSchemaSimpleTypeRestriction) s.getContent()).getBaseTypeName() != null
                && this.baseTypes.isBaseType(((XmlSchemaSimpleTypeRestriction) s.getContent()).getBaseTypeName())) {
            schema = this.baseTypes.createBaseType(((XmlSchemaSimpleTypeRestriction) s.getContent()).getBaseTypeName());
        } else {
            throw new RuntimeException("Simple type does meet expectations: " + ReflectionToStringBuilder.toString(s));
        }
        
        // add any restrictions
        if (s.getContent() != null && s.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
            XmlSchemaSimpleTypeRestriction res = (XmlSchemaSimpleTypeRestriction) s.getContent();
            XmlSchemaObjectCollection facets = res.getFacets();
            List<String> enumValues = new LinkedList<String>();
            // Add properties from schema first so facet properly overwrite if there are conflicts
            Map<String, String> props = new HashMap<String, String>();
            for (Map.Entry<String, String> prop : schema.getProps().entrySet()) {
                props.put(prop.getKey(), prop.getValue());
            }
            
            for (int i = 0; i < facets.getCount(); i++) {
                XmlSchemaObject facet = facets.getItem(i);
                if (facet instanceof XmlSchemaEnumerationFacet) {
                    enumValues.add(prepareEnum(((XmlSchemaEnumerationFacet) facet).getValue().toString()));
                } else if (facet instanceof XmlSchemaMaxExclusiveFacet) {
                    props.put(BaseTypes.MAX_EXCLUSIVE, ((XmlSchemaMaxExclusiveFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaMaxInclusiveFacet) {
                    props.put(BaseTypes.MAX_INCLUSIVE, ((XmlSchemaMaxInclusiveFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaMaxLengthFacet) {
                    props.put(BaseTypes.MAX_LENGTH, ((XmlSchemaMaxLengthFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaMinExclusiveFacet) {
                    props.put(BaseTypes.MIN_EXCLUSIVE, ((XmlSchemaMinExclusiveFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaMinInclusiveFacet) {
                    props.put(BaseTypes.MIN_INCLUSIVE, ((XmlSchemaMinInclusiveFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaMinLengthFacet) {
                    props.put(BaseTypes.MIN_LENGTH, ((XmlSchemaMinLengthFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaPatternFacet) {
                    props.put(BaseTypes.PATTERN, ((XmlSchemaPatternFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaWhiteSpaceFacet) {
                    props.put(BaseTypes.WHITE_SPACE, ((XmlSchemaWhiteSpaceFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaTotalDigitsFacet) {
                    props.put(BaseTypes.TOTAL_DIGITS, ((XmlSchemaTotalDigitsFacet) facet).getValue().toString());
                } else if (facet instanceof XmlSchemaFractionDigitsFacet) {
                    props.put(BaseTypes.FRACTION_DIGITS, ((XmlSchemaFractionDigitsFacet) facet).getValue().toString());
                } else {
                    props.put("TODO_" + facet.getClass().getName(), "Look up from XSD");
                }
            }
            
            // convert to enum if needed
            Schema newSchema;
            if (enumValues.size() > 0) {
                newSchema = Schema.createEnum(name, doc, this.enumNameSpace, enumValues);
            } else {
                newSchema = AvroUtils.shallowCloneSchema(schema, name, space, doc, false);
            }
            
            if (doc != null && AvroUtils.isPrimitiveSchema(newSchema)) {
                props.put(AvroUtils.FIELD_DOC, doc);
            }
            for (Map.Entry<String, String> prop : props.entrySet()) {
                newSchema.addProp(prop.getKey(), prop.getValue());
            }
            
            schema = newSchema;
        }
        
        return schema;
    }
    
    private String prepareEnum(String enumVal) {
        enumVal = enumVal.trim().replaceAll("['\"]", "").replaceAll("([\\s,/\\\\.;\\(\\)'\":-])+", "_")
                .replaceAll("_+$", "").toUpperCase();
        if (enumVal.matches("^\\d.*")) {
            enumVal = "TODO_" + enumVal;
        }
        return enumVal;
    }
    
    private void fieldsFromAttributes(XmlSchemaObjectCollection attrs, List<Schema.Field> fields) {
        if (attrs != null) {
            for (int i = 0; i < attrs.getCount(); i++) {
                XmlSchemaAttribute attr = (XmlSchemaAttribute) attrs.getItem(i);
                
                /*
                 * Handle form: <attribute name="ABC" type="MyType" />
                 */
                QName typeName = attr.getSchemaTypeName();
                XmlSchemaType type = this.root.getTypeByName(typeName);
                Schema schema;
                if (type != null) {
                    schema = schemaFromType(type);
                } else {
                    schema = this.baseTypes.createBaseType(typeName);
                }
                
                if (schema == null) {
                    /*
                     * Handle form:
                     * <attribute name="ABC">
                     * --- <simpleType>
                     * --- --- <restriction base="MyType">
                     */
                    
                    XmlSchemaSimpleType simpleType = attr.getSchemaType();
                    if (simpleType != null) {
                        schema = schemaFromSimpleType(simpleType);
                    }
                    
                    if (schema == null) {
                        throw new RuntimeException("Could not create schema for attribute type: " + attr.getName()
                                + ", " + typeName);
                    }
                }
                
                if (!(attr.getUse() != null && "required".equalsIgnoreCase(attr.getUse().getValue()))) {
                    schema = ReflectData.makeNullable(schema);
                }
                if (attr.getFixedValue() != null) {
                    schema.addProp(BaseTypes.FIXED_VALUE, attr.getFixedValue());
                }
                
                String name = camelize(attr.getName());
                
                String doc = schema.getProp(AvroUtils.FIELD_DOC);
                Schema.Field field = new Schema.Field(name, schema, doc, null);
                System.err.println("Added attribute: " + name);
                fields.add(field);
            }
        }
    }
    
    private void fieldsFromParticle(XmlSchemaParticle particle, List<Schema.Field> fields) {
        if (particle instanceof XmlSchemaElement) {
            XmlSchemaElement ele = ((XmlSchemaElement) particle);
            XmlSchemaType schemaType = ele.getSchemaType();
            
            /*
             * Check for and handle the following:
             * <complexType name="ABCD">
             * --- <complexContent>
             * --- --- <extension base="InheritedType">
             * --- --- --- <sequence>
             */
            if (schemaType instanceof XmlSchemaComplexType) {
                XmlSchemaComplexType elem = (XmlSchemaComplexType) schemaType;
                if (elem.getName() == null && elem.getContentModel() != null
                        && elem.getContentModel().getContent() != null
                        && elem.getContentModel().getContent() instanceof XmlSchemaComplexContentExtension) {
                    XmlSchemaComplexContentExtension ext = (XmlSchemaComplexContentExtension) elem.getContentModel()
                            .getContent();
                    fieldsFromComplexContentExtension(ext, fields);
                    return;
                }
            }
            
            Schema fieldSchema = schemaFromType(schemaType);
            if (fieldSchema != null) {
                String doc = fieldSchema.getProp(AvroUtils.FIELD_DOC);
                if (ele.getMaxOccurs() > 1) {
                    fieldSchema = Schema.createArray(fieldSchema);
                    if (ele.getMaxOccurs() != Long.MAX_VALUE) {
                        fieldSchema.addProp(BaseTypes.MAX_LENGTH, new Long(ele.getMaxOccurs()).toString());
                    }
                }
                if (ele.isNillable() || ele.getMinOccurs() == 0) {
                    fieldSchema = ReflectData.makeNullable(fieldSchema);
                }
                
                Schema.Field field = new Schema.Field(camelize(ele.getName()), fieldSchema, doc, null);
                fields.add(field);
            } else {
                throw new RuntimeException("Null field schema: " + schemaType.getClass().getCanonicalName());
            }
        } else if (particle instanceof XmlSchemaAny) {
            throw new RuntimeException("Unhandled particle type: " + particle.getClass().getCanonicalName());
        } else if (particle instanceof XmlSchemaSequence) {
            XmlSchemaSequence seq = (XmlSchemaSequence) particle;
            for (int i = 0; i < seq.getItems().getCount(); i++) {
                XmlSchemaObject item = seq.getItems().getItem(i);
                if (item instanceof XmlSchemaParticle) {
                    fieldsFromParticle((XmlSchemaParticle) item, fields);
                    
                } else {
                    throw new RuntimeException("Unhandled item type for XmlSchemaSequence: "
                            + item.getClass().getCanonicalName());
                }
            }
        } else {
            fields.add(new Schema.Field("TODO_" + particle.getClass().getSimpleName(), Schema.create(Schema.Type.NULL),
                    "Cannot handle element.", null));
            // throw new RuntimeException("Unhandled particle type: " +
            // particle.getClass().getCanonicalName());
        }
    }
    
    /**
     * Returns the contents of the first markup node of the first documentation element.
     * All additional other markup or documentation elements are ignored.
     * 
     * @param elem
     *            Element that has annotation
     * @return
     */
    private String parseAnnotation(XmlSchemaAnnotated elem) {
        if (elem.getAnnotation() == null || elem.getAnnotation().getItems() == null
                || elem.getAnnotation().getItems().getCount() == 0) {
            return null;
        }
        XmlSchemaObjectCollection docItems = elem.getAnnotation().getItems();
        for (int i = 0; i < docItems.getCount(); i++) {
            if (docItems.getItem(i) instanceof XmlSchemaDocumentation) {
                XmlSchemaDocumentation doc = (XmlSchemaDocumentation) docItems.getItem(i);
                if (doc.getMarkup() != null && doc.getMarkup().getLength() > 0) {
                    return doc.getMarkup().item(0).getTextContent().trim();
                }
            } else {
                throw new RuntimeException("Unhandled annotation type: "
                        + XmlSchemaDocumentation.class.getCanonicalName());
            }
        }
        return null;
    }
    
    public static XmlSchema loadSchema(String resourcePath) {
        Reader xsd = null;
        try {
            xsd = new InputStreamReader(XsdToAvroSchema.class.getResourceAsStream(resourcePath));
            XmlSchemaCollection col = new XmlSchemaCollection();
            return col.read(xsd, null);
        } finally {
            if (xsd != null) {
                try {
                    xsd.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    private static String camelize(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }
}
