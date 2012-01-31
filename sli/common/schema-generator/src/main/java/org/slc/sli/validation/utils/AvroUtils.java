package org.slc.sli.validation.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.impl.DefaultPrettyPrinter;

/**
 * Utility classes for working with Avro Schema objects.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@SuppressWarnings("deprecation")
public class AvroUtils {
    
    public static final String IGNORE_PREFIX = "IGNORE_";
    public static final String FIELD_DOC = IGNORE_PREFIX + "FIELDDOC";
    
    /**
     * Clone a schema using the new name, namespace, and doc string.
     * 
     * For records and unions, copies the contained schemas.
     * 
     * @param parentSchema
     * @param name
     * @param nameSpace
     * @param doc
     * @return
     */
    public static Schema shallowCloneSchema(Schema parentSchema, String name, String nameSpace, String doc,
            boolean cloneProps) {
        Schema schema;
        switch (parentSchema.getType()) {
        case RECORD:
            schema = Schema.createRecord(name, doc, nameSpace, false);
            schema.setFields(new LinkedList<Schema.Field>(parentSchema.getFields()));
            break;
        case ENUM:
            schema = Schema.createEnum(name, doc, nameSpace, parentSchema.getEnumSymbols());
            break;
        case ARRAY:
            schema = Schema.createArray(parentSchema.getElementType());
            break;
        case FIXED:
            schema = Schema.createFixed(name, doc, nameSpace, parentSchema.getFixedSize());
            break;
        case MAP:
            schema = Schema.createMap(parentSchema.getValueType());
            break;
        case UNION:
            schema = Schema.createUnion(parentSchema.getTypes());
            break;
        default:
            schema = Schema.create(parentSchema.getType());
            break;
        }
        if (cloneProps) {
            for (Map.Entry<String, String> prop : parentSchema.getProps().entrySet()) {
                schema.addProp(prop.getKey(), prop.getValue());
            }
        }
        return schema;
    }
    
    public static boolean isPrimitiveSchema(Schema schema) {
        switch (schema.getType()) {
        case RECORD:
        case ENUM:
        case ARRAY:
        case MAP:
        case FIXED:
        case UNION:
            return false;
        default:
            return true;
        }
    }
    
    public static String schemaToJson(Schema schema) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator jsonGen = new JsonFactory().createJsonGenerator(writer);
            jsonGen.setPrettyPrinter(new DefaultPrettyPrinter());
            AvroUtils.schemaToJson(null, schema, true, jsonGen);
            jsonGen.close();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void schemaToJson(String fieldName, Schema schema, boolean first, JsonGenerator j)
            throws IOException {
        
        switch (schema.getType()) {
        case RECORD:
            if (!first) {
                if (fieldName != null) {
                    j.writeStringField(fieldName, schema.getFullName());
                } else {
                    j.writeString(schema.getFullName());
                }
            } else {
                if (fieldName != null) {
                    j.writeObjectFieldStart(fieldName);
                } else {
                    j.writeStartObject();
                }
                j.writeStringField("type", schema.getType().getName());
                j.writeStringField("name", schema.getName());
                if (schema.getNamespace() != null) {
                    j.writeStringField("namespace", schema.getNamespace());
                }
                if (schema.getDoc() != null) {
                    j.writeStringField("doc", schema.getDoc());
                }
                for (Map.Entry<String, String> prop : schema.getProps().entrySet()) {
                    j.writeStringField(prop.getKey(), prop.getValue());
                }
                j.writeArrayFieldStart("fields");
                for (Schema.Field field : schema.getFields()) {
                    j.writeStartObject();
                    j.writeStringField("name", field.name());
                    if (field.doc() != null) {
                        j.writeStringField("doc", field.doc());
                    }
                    for (Map.Entry<String, String> prop : field.props().entrySet()) {
                        if (!prop.getKey().startsWith("IGNORE")) {
                            j.writeStringField(prop.getKey(), prop.getValue());
                        }
                    }
                    
                    schemaToJson("type", field.schema(), false, j);
                    if (field.defaultValue() != null) {
                        j.writeStringField("default", field.defaultValue().getTextValue());
                    }
                    j.writeEndObject();
                }
                j.writeEndArray();
                j.writeEndObject();
            }
            break;
        case ARRAY:
            if (fieldName != null) {
                j.writeObjectFieldStart(fieldName);
            } else {
                j.writeStartObject();
            }
            j.writeStringField("type", schema.getType().getName());
            schemaToJson("items", schema.getElementType(), false, j);
            j.writeEndObject();
            break;
        case UNION:
            if (fieldName != null) {
                j.writeArrayFieldStart(fieldName);
            } else {
                j.writeStartArray();
            }
            for (Schema s : schema.getTypes()) {
                schemaToJson(null, s, false, j);
            }
            j.writeEndArray();
            break;
        case ENUM:
            if (!first) {
                if (fieldName != null) {
                    j.writeStringField(fieldName, schema.getFullName());
                } else {
                    j.writeString(schema.getFullName());
                }
            } else {
                if (fieldName != null) {
                    j.writeObjectFieldStart(fieldName);
                } else {
                    j.writeStartObject();
                }
                j.writeStringField("type", schema.getType().getName());
                j.writeStringField("name", schema.getName());
                j.writeStringField("namespace", schema.getNamespace());
                if (schema.getDoc() != null) {
                    j.writeStringField("doc", schema.getDoc());
                }
                for (Map.Entry<String, String> prop : schema.getProps().entrySet()) {
                    if (!prop.getKey().startsWith(IGNORE_PREFIX)) {
                        j.writeStringField(prop.getKey(), prop.getValue());
                    }
                }
                j.writeArrayFieldStart("symbols");
                for (String e : schema.getEnumSymbols()) {
                    j.writeString(e);
                }
                j.writeEndArray();
                j.writeEndObject();
            }
            break;
        case NULL:
            if (fieldName != null) {
                j.writeStringField(fieldName, "null");
            } else {
                j.writeString("null");
            }
            break;
        default:
            if (hasProps(schema)) {
                if (fieldName != null) {
                    j.writeObjectFieldStart(fieldName);
                } else {
                    j.writeStartObject();
                }
                j.writeStringField("type", schema.getType().getName());
                for (Map.Entry<String, String> prop : schema.getProps().entrySet()) {
                    if (!prop.getKey().startsWith(IGNORE_PREFIX)) {
                        j.writeStringField(prop.getKey(), prop.getValue());
                    }
                }
                j.writeEndObject();
            } else {
                if (fieldName != null) {
                    j.writeStringField(fieldName, schema.getType().getName());
                } else {
                    j.writeString(schema.getType().getName());
                }
            }
            break;
        }
        
    }
    
    private static boolean hasProps(Schema schema) {
        for (Map.Entry<String, String> prop : schema.getProps().entrySet()) {
            if (!prop.getKey().startsWith(IGNORE_PREFIX)) {
                return true;
            }
        }
        return false;
    }
    
    public static void main(String... args) throws Exception {
        
        Schema schema = Schema.createRecord("bozo", "doc", "org.slc", false);
        List<Schema.Field> fields = new LinkedList<Schema.Field>();
        fields.add(new Schema.Field("field1", ReflectData.makeNullable(Schema.create(Schema.Type.STRING)), "doc1", null));
        fields.add(new Schema.Field("field2", Schema.create(Schema.Type.INT), "doc2", null));
        fields.add(new Schema.Field("field3", Schema.create(Schema.Type.DOUBLE), "doc3", null));
        fields.add(new Schema.Field("field4", Schema.createArray(ReflectData.makeNullable(Schema
                .create(Schema.Type.DOUBLE))), "doc4", null));
        
        Schema schema2 = Schema.createRecord("InnerType", "docInner", "org.slc", false);
        schema2.setFields(new LinkedList<Schema.Field>());
        fields.add(new Schema.Field("field5", schema2, "doc5", null));
        
        schema.setFields(fields);
        schema.addProp("prop1", "test");
        
        String json = schemaToJson(schema);
        System.out.println(json);
    }
}
