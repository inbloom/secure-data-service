package org.slc.sli.validation;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaTotalDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaWhiteSpaceFacet;

/**
 * 
 * SLI Schema Types
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
public enum NeutralSchemaType {
    
    BOOLEAN("boolean", true), INT("int", true), INTEGER("integer", true), LONG("long", true), FLOAT("float", true), DOUBLE(
            "double", true), DECIMAL("decimal", true), DATE("date", true), TIME("time", true), DATETIME("datetime",
            true), DURATION("duration", true), STRING("string", true), ID("ID", true), IDREF("IDREF", true), RESTRICTED(
            "restricted", false), TOKEN("token", false), LIST("list", false), COMPLEX("complex", false);
    
    // Attributes
    private final String name;
    private final boolean isPrimitive = true;
    
    // Constructors
    NeutralSchemaType(String name, boolean isPrimitive) {
        this.name = name;
    }
    
    // Methods
    public String getName() {
        return name;
    }
    
    public boolean isPrimitive() {
        return isPrimitive;
    }
    
    public static boolean exists(QName qName) {
        boolean exists = false;
        
        NeutralSchemaType schemaType = findByName(qName);
        if (schemaType != null) {
            exists = true;
        }
        
        return exists;
    }
    
    public static boolean isPrimitive(QName qName) {
        boolean isPrimitive = false;
        
        NeutralSchemaType schemaType = findByName(qName);
        if (schemaType != null) {
            isPrimitive = schemaType.isPrimitive();
        }
        
        return isPrimitive;
    }
    
    public static boolean isRestricted(QName qName) {
        boolean isRestricted = false;
        
        NeutralSchemaType schemaType = findByName(qName);
        if (schemaType != null) {
            isRestricted = (schemaType.equals(RESTRICTED));
        }
        
        return isRestricted;
    }
    
    public static NeutralSchemaType findByName(String name) {
        for (NeutralSchemaType neutralSchemaType : NeutralSchemaType.values()) {
            if (neutralSchemaType.getName().toLowerCase().equals(name.toLowerCase())) {
                return neutralSchemaType;
            }
        }
        return null;
    }
    
    public static NeutralSchemaType findByName(QName qName) {
        if (qName != null) {
            String name = qName.getLocalPart();
            for (NeutralSchemaType neutralSchemaType : NeutralSchemaType.values()) {
                if (neutralSchemaType.getName().toLowerCase().equals(name.toLowerCase())) {
                    return neutralSchemaType;
                }
            }
        }
        return null;
    }
    
    public static String lookupPropertyName(XmlSchemaFacet facet) {
        String propertyName = "";
        
        if (facet instanceof XmlSchemaPatternFacet) {
            propertyName = RestrictedSchema.PATTERN;
        } else if (facet instanceof XmlSchemaLengthFacet) {
            propertyName = RestrictedSchema.LENGTH;
        } else if (facet instanceof XmlSchemaMinLengthFacet) {
            propertyName = RestrictedSchema.MIN_LENGTH;
        } else if (facet instanceof XmlSchemaMaxLengthFacet) {
            propertyName = RestrictedSchema.MAX_LENGTH;
        } else if (facet instanceof XmlSchemaMinInclusiveFacet) {
            propertyName = RestrictedSchema.MIN_INCLUSIVE;
        } else if (facet instanceof XmlSchemaMaxInclusiveFacet) {
            propertyName = RestrictedSchema.MAX_INCLUSIVE;
        } else if (facet instanceof XmlSchemaMinExclusiveFacet) {
            propertyName = RestrictedSchema.MIN_EXCLUSIVE;
        } else if (facet instanceof XmlSchemaMaxExclusiveFacet) {
            propertyName = RestrictedSchema.MAX_EXCLUSIVE;
        } else if (facet instanceof XmlSchemaTotalDigitsFacet) {
            propertyName = RestrictedSchema.TOTAL_DIGITS;
        } else if (facet instanceof XmlSchemaFractionDigitsFacet) {
            propertyName = RestrictedSchema.FRACTION_DIGITS;
        } else if (facet instanceof XmlSchemaWhiteSpaceFacet) {
            propertyName = RestrictedSchema.WHITE_SPACE;
        } else {
            propertyName = "TODO_" + facet.getClass().getName();
        }
        
        return propertyName;
    }
    
}
