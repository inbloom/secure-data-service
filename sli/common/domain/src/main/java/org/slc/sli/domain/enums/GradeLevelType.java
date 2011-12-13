package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * What grade the student is in
 * 
 */
@XmlType(name = "GradeLevelType")
@XmlEnum
public enum GradeLevelType {
    INFANT_TODDLER("Infant/toddler"), EARLY_EDUCATION("Early Education"), PRESCHOOL_PREKINDERGARTEN(
            "Preschool/Prekindergarten"), KINDERGARTEN("Kindergarten"), TRANSITIONAL_KINDERGARTEN(
            "Transitional Kindergarten"), FIRST_GRADE("First grade"), SECOND_GRADE("Second grade"), THIRD_GRADE(
            "Third grade"), FOURTH_GRADE("Fourth grade"), FIFTH_GRADE("Fifth grade"), SIXTH_GRADE("Sixth grade"), SEVENTH_GRADE(
            "Seventh grade"), EIGHTH_GRADE("Eighth grade"), NINTH_GRADE("Ninth grade"), TENTH_GRADE("Tenth grade"), ELEVENTH_GRADE(
            "Eleventh grade"), TWELFTH_GRADE("Twelfth grade"), GRADE_13("Grade 13"), POSTSECONDARY("Postsecondary"), ADULT_EDUCATION(
            "Adult Education"), UNGRADED("Ungraded"), OTHER("Other");
    
    private final String value;
    
    private GradeLevelType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static GradeLevelType fromValue(String str) {
        for (GradeLevelType t : GradeLevelType.values()) {
            if (t.getValue().equalsIgnoreCase(str)) {
                return t;
            }
        }
        throw new IllegalArgumentException(str);
    }
    
    @Override
    public String toString() {
        return getValue();
    }
    
}
