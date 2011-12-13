package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "OldEthnicityType")
@XmlEnum
public enum OldEthnicityType {

//    @XmlEnumValue("American Indian Or Alaskan Native")
    AMERICAN_INDIAN_OR_ALASKAN_NATIVE("American Indian Or Alaskan Native"),
//    @XmlEnumValue("Asian Or Pacific Islander")
    ASIAN_OR_PACIFIC_ISLANDER("Asian Or Pacific Islander"),
//    @XmlEnumValue("Black, Not Of Hispanic Origin")
    BLACK_NOT_OF_HISPANIC_ORIGIN("Black, Not Of Hispanic Origin"),
//    @XmlEnumValue("Hispanic")
    HISPANIC("Hispanic"),
//    @XmlEnumValue("White, Not Of Hispanic Origin")
    WHITE_NOT_OF_HISPANIC_ORIGIN("White, Not Of Hispanic Origin"),
 //   @XmlEnumValue("none")
    NULL("none");
    private final String value;

    OldEthnicityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OldEthnicityType fromValue(String v) {
        for (OldEthnicityType c : OldEthnicityType.values()) {
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
