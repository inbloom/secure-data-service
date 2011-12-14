package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Whether or not the student has limited English skills
 * 
 */
@XmlType(name = "LimitedEnglishProficiencyType")
@XmlEnum
public enum LimitedEnglishProficiencyType {

 //   @XmlEnumValue("Yes")
    YES("Yes"),
 //   @XmlEnumValue("No")
    NO("No"),
 //   @XmlEnumValue("Ever")
    EVER("Ever"),
 //   @XmlEnumValue("Unknown")
    NULL("Unknown");
    private final String value;

    LimitedEnglishProficiencyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LimitedEnglishProficiencyType fromValue(String v) {
        for (LimitedEnglishProficiencyType c : LimitedEnglishProficiencyType.values()) {
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

