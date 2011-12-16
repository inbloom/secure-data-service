package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The operating status of the school/institution
 * 
 */
@XmlType(name = "OperationalStatusType")
@XmlEnum
public enum OperationalStatusType {
    
    // @XmlEnumValue("Active")
    ACTIVE("Active"),
    // @XmlEnumValue("Added")
    ADDED("Added"),
    // @XmlEnumValue("Changed Agency")
    CHANGED_AGENCY("Changed Agency"),
    // @XmlEnumValue("Closed")
    CLOSED("Closed"),
    // @XmlEnumValue("Continuing")
    CONTINUING("Continuing"),
    // @XmlEnumValue("Future")
    FUTURE("Future"),
    // @XmlEnumValue("Inactive")
    INACTIVE("Inactive"),
    // @XmlEnumValue("New")
    NEW("New"),
    // @XmlEnumValue("Reopened")
    REOPENED("Reopened"),
    // @XmlEnumValue("Unknown")
    NULL("Unknown");
    private final String value;
    
    OperationalStatusType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static OperationalStatusType fromValue(String v) {
        for (OperationalStatusType c : OperationalStatusType.values()) {
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
