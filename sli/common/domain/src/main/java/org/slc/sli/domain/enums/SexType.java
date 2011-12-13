package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The gender of the student
 * 
 */
@XmlType(name = "SexType")
@XmlEnum
public enum SexType {

//    @XmlEnumValue("Female")
    Female("Female"),
 //   @XmlEnumValue("Male")
    Male("Male");
    private final String value;

    SexType(String v) {
        value = v;
    }

    public String getValue() {
        return value;
    }

    public static SexType fromValue(String v) {
        for (SexType c : SexType.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
   
    @Override
    public String toString() {
        return getValue();
    }

}
