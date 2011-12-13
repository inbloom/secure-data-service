package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The type of school
 * 
 */
@XmlType(name = "SchoolType")
@XmlEnum
public enum SchoolType {

//    @XmlEnumValue("Alternative")
    ALTERNATIVE("Alternative"),
 //   @XmlEnumValue("Regular")
    REGULAR("Regular"),
//    @XmlEnumValue("Special Education")
    SPECIAL_EDUCATION("Special Education"),
 //   @XmlEnumValue("Vocational")
    VOCATIONAL("Vocational"),
 //   @XmlEnumValue("Unknown")
    NULL("Unknown");
    
    private final String value;

    SchoolType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SchoolType fromValue(String v) {
        for (SchoolType c : SchoolType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    @Override
    public String toString() {
        return value();
    }

}
