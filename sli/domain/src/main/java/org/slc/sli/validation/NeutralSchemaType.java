package org.slc.sli.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;

import org.slc.sli.validation.schema.Restriction;

/**
 * 
 * SLI Schema Types
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
public enum NeutralSchemaType {
    
    BOOLEAN("boolean"), INT("int"), INTEGER("integer"), LONG("long"), DOUBLE("double", "float"), DATE("date"), TIME(
            "time"), DATETIME("datetime"), DURATION("duration"), STRING("string"), ID("id"), IDREF("IDREF"), TOKEN(
            "token"), LIST("list"), COMPLEX("complex"), CHOICE("choice"), REFERENCE("reference");
    
    // Attributes
    private final String name;
    private final List<String> mappedXsdTypes;
    
    // Constructors
    NeutralSchemaType(String name) {
        this(name, new String[] {});
    }
    
    NeutralSchemaType(String name, String... extraTypes) {
        this.name = name;
        List<String> xsdTypes = new ArrayList<String>(Arrays.asList(extraTypes));
        xsdTypes.add(name);
        mappedXsdTypes = Collections.unmodifiableList(xsdTypes);
    }
    
    // Methods
    public String getName() {
        return name;
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
        return findByName(qName) != null;
    }
    
    public static NeutralSchemaType findByName(String name) {
        for (NeutralSchemaType neutralSchemaType : NeutralSchemaType.values()) {
            if (neutralSchemaType.getMappedXsdTypes().contains(name.toLowerCase())) {
                return neutralSchemaType;
            }
        }
        return null;
    }
    
    public static NeutralSchemaType findByName(QName qName) {
        if (qName != null) {
            String name = qName.getLocalPart();
            return findByName(name);
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
        } else {
            throw new RuntimeException("Unhandled XmlSchemaFacet: " + facet.getClass().getCanonicalName());
        }
        
        return propertyName;
    }
    
    private List<String> getMappedXsdTypes() {
        return mappedXsdTypes;
    }
    
}
