package org.slc.sli.validation.utils;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;

/**
 * Base types are SimpleTypes like xs:int, xs:string, xs:float
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class BaseTypes {
    
    /**
     * Interface for something that creates new schemas.
     * 
     * @author Ryan Farris <rfarris@wgen.net>
     * 
     */
    public static interface SchemaFactory {
        public Schema createSchema();
    }
    
    public static final String PATTERN = "pattern";
    public static final String MAX_LENGTH = "max-length";
    public static final String MIN_LENGTH = "min-length";
    public static final String LENGTH = "length";
    public static final String TOTAL_DIGITS = "total-digits";
    public static final String FRACTION_DIGITS = "fraction-digits";
    public static final String MAX_INCLUSIVE = "max";
    public static final String MIN_INCLUSIVE = "min";
    public static final String MAX_EXCLUSIVE = "max-exclusive";
    public static final String MIN_EXCLUSIVE = "min-exclusive";
    public static final String WHITE_SPACE = "white-space";
    public static final String FIXED_VALUE = "fixed-value";
    public static final String XSD_TYPE = "xsd-type";
    
    Map<QName, SchemaFactory> mapping = new HashMap<QName, SchemaFactory>();
    
    /**
     * Creates a new Schema for the requested name. Returns null if the name is unknown.
     * New schemas are created each time. Schemas are never reused.
     */
    public Schema createBaseType(QName qName) {
        return mapping.containsKey(qName) ? mapping.get(qName).createSchema() : null;
    }
    
    /**
     * Checks if this name is in our inventory.
     */
    public boolean isBaseType(QName qName) {
        return mapping.containsKey(qName);
    }
    
    public BaseTypes() {
        // PRIMITIVE TYPES
        final String xmlSchemaNameSpace = "http://www.w3.org/2001/XMLSchema";
        mapping.put(new QName(xmlSchemaNameSpace, "string"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                return Schema.create(Type.STRING);
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "boolean"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                return Schema.create(Type.BOOLEAN);
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "decimal"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                // TODO: this should not be a double.
                Schema s = Schema.create(Type.DOUBLE);
                s.addProp(XSD_TYPE, "decimal");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "float"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                return Schema.create(Type.FLOAT);
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "duration"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(BaseTypes.PATTERN, "(-)?P(\\d+Y)?(\\d+M)?(\\d+D)?(T(\\d+H)?(\\d+M)?(\\d+(.\\d+)?S)?)?");
                s.addProp(XSD_TYPE, "duration");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "dateTime"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(BaseTypes.PATTERN,
                        "\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d(\\.\\d+)?(Z|[+-]\\d\\d:\\d\\d)?");
                s.addProp(XSD_TYPE, "dateTime");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "time"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(BaseTypes.PATTERN, "\\d\\d:\\d\\d:\\d\\d(\\.\\d+)?(Z|[+-]\\d\\d:\\d\\d)?");
                s.addProp(XSD_TYPE, "time");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "date"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(BaseTypes.PATTERN, "\\d\\d\\d\\d-\\d\\d-\\d\\d");
                s.addProp(XSD_TYPE, "date");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "gYear"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.INT);
                s.addProp(XSD_TYPE, "gYear");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "gYearMonth"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(BaseTypes.PATTERN, "\\d\\d\\d\\d-\\d\\d");
                s.addProp(XSD_TYPE, "gYearMonth");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "gMonthDay"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(BaseTypes.PATTERN, "\\d\\d-\\d\\d");
                s.addProp(XSD_TYPE, "gMonthDay");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "gDay"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.INT);
                s.addProp(BaseTypes.MIN_INCLUSIVE, "1");
                s.addProp(BaseTypes.MAX_INCLUSIVE, "31");
                s.addProp(XSD_TYPE, "gDay");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "gMonth"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.INT);
                s.addProp(BaseTypes.MIN_INCLUSIVE, "1");
                s.addProp(BaseTypes.MAX_INCLUSIVE, "12");
                s.addProp(XSD_TYPE, "gMonth");
                return s;
            }
        });
        
        // DERIVED TYPES
        mapping.put(new QName(xmlSchemaNameSpace, "normalizedString"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(XSD_TYPE, "normalizedString");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "token"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(XSD_TYPE, "token");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "int"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                return Schema.create(Type.INT);
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "integer"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                return Schema.create(Type.LONG);
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "long"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                return Schema.create(Type.LONG);
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "ID"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(XSD_TYPE, "ID");
                return s;
            }
        });
        mapping.put(new QName(xmlSchemaNameSpace, "IDREF"), new SchemaFactory() {
            @Override
            public Schema createSchema() {
                Schema s = Schema.create(Type.STRING);
                s.addProp(XSD_TYPE, "IDREF");
                return s;
            }
        });
    }
    
    public static void main(String... args) {
        boolean matches = "P23DT12H".matches("(-)?P(\\d+Y)?(\\d+M)?(\\d+D)?(T(\\d+H)?(\\d+M)?(\\d+(.\\d+)?S)?)?");
        System.out.println(matches);
    }
}
