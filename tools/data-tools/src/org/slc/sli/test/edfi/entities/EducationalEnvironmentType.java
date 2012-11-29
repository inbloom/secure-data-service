//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.16 at 01:39:34 PM EST 
//


package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EducationalEnvironmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EducationalEnvironmentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Classroom"/>
 *     &lt;enumeration value="Homebound"/>
 *     &lt;enumeration value="Hospital class"/>
 *     &lt;enumeration value="In-school suspension"/>
 *     &lt;enumeration value="Laboratory"/>
 *     &lt;enumeration value="Mainstream (Special Education)"/>
 *     &lt;enumeration value="Off-school center"/>
 *     &lt;enumeration value="Pull-out class"/>
 *     &lt;enumeration value="Resource room"/>
 *     &lt;enumeration value="Self-contained (Special Education)"/>
 *     &lt;enumeration value="Self-study"/>
 *     &lt;enumeration value="Shop"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EducationalEnvironmentType")
@XmlEnum
public enum EducationalEnvironmentType {

    @XmlEnumValue("Classroom")
    CLASSROOM("Classroom"),
    @XmlEnumValue("Homebound")
    HOMEBOUND("Homebound"),
    @XmlEnumValue("Hospital class")
    HOSPITAL_CLASS("Hospital class"),
    @XmlEnumValue("In-school suspension")
    IN_SCHOOL_SUSPENSION("In-school suspension"),
    @XmlEnumValue("Laboratory")
    LABORATORY("Laboratory"),
    @XmlEnumValue("Mainstream (Special Education)")
    MAINSTREAM_SPECIAL_EDUCATION("Mainstream (Special Education)"),
    @XmlEnumValue("Off-school center")
    OFF_SCHOOL_CENTER("Off-school center"),
    @XmlEnumValue("Pull-out class")
    PULL_OUT_CLASS("Pull-out class"),
    @XmlEnumValue("Resource room")
    RESOURCE_ROOM("Resource room"),
    @XmlEnumValue("Self-contained (Special Education)")
    SELF_CONTAINED_SPECIAL_EDUCATION("Self-contained (Special Education)"),
    @XmlEnumValue("Self-study")
    SELF_STUDY("Self-study"),
    @XmlEnumValue("Shop")
    SHOP("Shop");
    private final String value;

    EducationalEnvironmentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EducationalEnvironmentType fromValue(String v) {
        for (EducationalEnvironmentType c: EducationalEnvironmentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
