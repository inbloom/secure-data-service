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

import org.slc.sli.validation.schema.Restriction;

/**
 * 
 * SLI Schema Types
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
public enum NeutralSchemaType {
    
    BOOLEAN("boolean", true), INT("int", true), INTEGER("integer", true), LONG("long", true), DOUBLE("double", true), DATE(
            "date", true), TIME("time", true), DATETIME("datetime", true), DURATION("duration", true), STRING("string",
            true), ID("ID", true), IDREF("IDREF", true), TOKEN("token", false), LIST("list", false), COMPLEX("complex",
            false);
    
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
            propertyName = Restriction.PATTERN.getValue();
        } else if (facet instanceof XmlSchemaLengthFacet) {
            propertyName = Restriction.LENGTH.getValue();
        } else if (facet instanceof XmlSchemaMinLengthFacet) {
            propertyName = Restriction.MIN_LENGTH.getValue();
        } else if (facet instanceof XmlSchemaMaxLengthFacet) {
            propertyName = Restriction.MAX_LENGTH.getValue();
        } else if (facet instanceof XmlSchemaMinInclusiveFacet) {
            propertyName = Restriction.MIN_INCLUSIVE.getValue();
        } else if (facet instanceof XmlSchemaMaxInclusiveFacet) {
            propertyName = Restriction.MAX_INCLUSIVE.getValue();
        } else if (facet instanceof XmlSchemaMinExclusiveFacet) {
            propertyName = Restriction.MIN_EXCLUSIVE.getValue();
        } else if (facet instanceof XmlSchemaMaxExclusiveFacet) {
            propertyName = Restriction.MAX_EXCLUSIVE.getValue();
        } else if (facet instanceof XmlSchemaTotalDigitsFacet) {
            propertyName = Restriction.TOTAL_DIGITS.getValue();
        } else if (facet instanceof XmlSchemaFractionDigitsFacet) {
            propertyName = Restriction.FRACTION_DIGITS.getValue();
        } else {
            propertyName = "TODO_" + facet.getClass().getName();
        }
        
        return propertyName;
    }
    
}
