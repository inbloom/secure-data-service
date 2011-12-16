package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Whether or not the school is a charter school
 */
@XmlType(name = "CharterStatusType")
@XmlEnum
public enum CharterStatusType {
    
    // @XmlEnumValue("School Charter")
    SCHOOL_CHARTER("School Charter"),
    // @XmlEnumValue("College/University Charter")
    COLLEGE_UNIVERSITY_CHARTER("College/University Charter"),
    // @XmlEnumValue("Open Enrollment")
    OPEN_ENROLLMENT("Open Enrollment"),
    // @XmlEnumValue("Not a Charter School")
    NOT_A_CHARTER_SCHOOL("Not a Charter School"),
    // @XmlEnumValue("Unknown")
    NULL("Unknown");
    
    private final String value;
    
    CharterStatusType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static CharterStatusType fromValue(String v) {
        for (CharterStatusType c : CharterStatusType.values()) {
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
