package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * NOTE: These strongly typed domain classes are deprecated and should no longer be used.
 * Please use see the Entity interface for their replacement.
 * 
 * Some bureaucratic thing, I don't know. But edfi has it so we included it
 * 
 */
@Deprecated
@XmlType(name = "TitleIPartASchoolDesignationType")
@XmlEnum
public enum TitleIPartASchoolDesignationType {
    
    // @XmlEnumValue("Not designated as a Title I Part A school")
    NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL("Not designated as a Title I Part A school"),
    // @XmlEnumValue("Title I Part A Schoolwide Assistance Program School")
    TITLE_I_PART_A_SCHOOLWIDE_ASSISTANCE_PROGRAM_SCHOOL("Title I Part A Schoolwide Assistance Program School"),
    // @XmlEnumValue("Title I Part A Targeted Assistance School")
    TITLE_I_PART_A_TARGETED_ASSISTANCE_SCHOOL("Title I Part A Targeted Assistance School"),
    // @XmlEnumValue("Title I targeted eligible school - no program")
    TITLE_I_TARGETED_ELIGIBLE_SCHOOL_NO_PROGRAM("Title I targeted eligible school - no program"),
    // @XmlEnumValue("Title I targeted school")
    TITLE_I_TARGETED_SCHOOL("Title I targeted school"),
    // @XmlEnumValue("Title I school wide eligible - Title I targeted program")
    TITLE_I_SCHOOL_WIDE_ELIGIBLE_TITLE_I_TARGETED_PROGRAM("Title I school wide eligible - Title I targeted program"),
    // @XmlEnumValue("Title I school wide eligible school - no program")
    TITLE_I_SCHOOL_WIDE_ELIGIBLE_SCHOOL_NO_PROGRAM("Title I school wide eligible school - no program"),
    // @XmlEnumValue("Unknown")
    NULL("Unknown");
    
    private final String value;
    
    TitleIPartASchoolDesignationType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static TitleIPartASchoolDesignationType fromValue(String v) {
        for (TitleIPartASchoolDesignationType c : TitleIPartASchoolDesignationType.values()) {
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
