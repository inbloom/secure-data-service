package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "EntryType")
@XmlEnum
public enum EntryType {
    TRANSFER_PUBLIC_SAME_LOCAL_AGENCY("Transfer from a public school in the same local education agency"), TRANSFER_PUBLIC_DIFFERENT_LOCAL_AGENCY_SAME_STATE(
            "Transfer from a public school in a different local education agency in the same state"), TRANSFER_PUBLIC_DIFFERENT_STATE(
            "Transfer from a public school in a different state"), TRANSFER_PRIVATE_NON_RELIGIOUS_SAME_LOCAL_AGENCY(
            "Transfer from a private, non-religiously-affiliated school in the same local education agency"), TRANSFER_PRIVATE_NON_RELIGIOUS_DIFFERENT_LOCAL_AGENCY_SAME_STATE(
            "Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state"), TRANSFER_PRIVATE_NON_RELIGIOUS_DIFFERENT_STATE(
            "Transfer from a private, non-religiously-affiliated school in a different state"), TRANSFER_PRIVATE_RELIGIOUS_SAME_LOCAL_AGENCY(
            "Transfer from a private, religiously-affiliated school in the same local education agency"), TRANSFER_PRIVATE_RELIGIOUS_DIFFERENT_LOCAL_AGENCY_SAME_STATE(
            "Transfer from a private, religiously-affiliated school in a different local education agency in the same state"), TRANSFER_PRIVATE_RELIGIOUS_DIFFERENT_STATE(
            "Transfer from a private, religiously-affiliated school in a different state"), TRANSFER_OUTSIDE_COUNTRY(
            "Transfer from a school outside of the country"), TRANSFER_INSTITUTION("Transfer from an institution"), TRANSFER_CHARTER(
            "Transfer from a charter school"), TRANSFER_HOME_SCHOOL("Transfer from home schooling"), REENTRY_NO_INTERRUPTION(
            "Re-entry from the same school with no interruption of schooling"), REENTRY_VOLUNTARY_WITHDRAWAL(
            "Re-entry after a voluntary withdrawal"), REENTRY_INVOLUNTARY_WITHDRAWAL(
            "Re-entry after an involuntary withdrawal"), ORIGINAL("Original entry into a United States school"), ORIGINAL_FOREIGN_NO_INTERRUPTION(
            "Original entry into a United States school from a foreign country with no interruption in schooling"), ORIGINAL_FOREIGN_INTERRUPTION(
            "Original entry into a United States school from a foreign country with an interruption in schooling"), NEXT_YEAR(
            "Next year school"), OTHER("Other");
    
    private final String value;
    
    private EntryType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static EntryType fromValue(String str) {
        for (EntryType t : EntryType.values()) {
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
