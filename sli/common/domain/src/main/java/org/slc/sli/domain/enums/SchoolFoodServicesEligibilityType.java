package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * NOTE: These strongly typed domain classes are deprecated and should no longer be used.
 * Please use see the Entity interface for their replacement.
 * 
 * Whether or not students can get free or reduced lunch
 * 
 */
@Deprecated
@XmlType(name = "SchoolFoodServicesEligibilityType")
@XmlEnum
public enum SchoolFoodServicesEligibilityType {
    
    // @XmlEnumValue("Free")
    FREE("Free"),
    // @XmlEnumValue("Full price")
    FULL_PRICE("Full price"),
    // @XmlEnumValue("Reduced price")
    REDUCED_PRICE("Reduced price"),
    // @XmlEnumValue("Unknown")
    NULL("Unknown");
    private final String value;
    
    SchoolFoodServicesEligibilityType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static SchoolFoodServicesEligibilityType fromValue(String v) {
        for (SchoolFoodServicesEligibilityType c : SchoolFoodServicesEligibilityType.values()) {
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
