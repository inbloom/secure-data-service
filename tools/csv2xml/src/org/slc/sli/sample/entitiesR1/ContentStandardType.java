//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.05 at 01:12:38 PM EST 
//


package org.slc.sli.sample.entitiesR1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentStandardType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ContentStandardType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="National Standard"/>
 *     &lt;enumeration value="State Standard"/>
 *     &lt;enumeration value="College Entrance Standard"/>
 *     &lt;enumeration value="LEA Standard"/>
 *     &lt;enumeration value="Texas Essential Knowledge and Skills"/>
 *     &lt;enumeration value="SAT"/>
 *     &lt;enumeration value="PSAT"/>
 *     &lt;enumeration value="ACT"/>
 *     &lt;enumeration value="Advanced Placement"/>
 *     &lt;enumeration value="International Baccalaureate"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ContentStandardType")
@XmlEnum
public enum ContentStandardType {

    @XmlEnumValue("National Standard")
    NATIONAL_STANDARD("National Standard"),
    @XmlEnumValue("State Standard")
    STATE_STANDARD("State Standard"),
    @XmlEnumValue("College Entrance Standard")
    COLLEGE_ENTRANCE_STANDARD("College Entrance Standard"),
    @XmlEnumValue("LEA Standard")
    LEA_STANDARD("LEA Standard"),
    @XmlEnumValue("Texas Essential Knowledge and Skills")
    TEXAS_ESSENTIAL_KNOWLEDGE_AND_SKILLS("Texas Essential Knowledge and Skills"),
    SAT("SAT"),
    PSAT("PSAT"),
    ACT("ACT"),
    @XmlEnumValue("Advanced Placement")
    ADVANCED_PLACEMENT("Advanced Placement"),
    @XmlEnumValue("International Baccalaureate")
    INTERNATIONAL_BACCALAUREATE("International Baccalaureate");
    private final String value;

    ContentStandardType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ContentStandardType fromValue(String v) {
        for (ContentStandardType c: ContentStandardType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
