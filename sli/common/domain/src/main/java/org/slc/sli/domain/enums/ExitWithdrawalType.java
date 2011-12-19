package org.slc.sli.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * NOTE: These strongly typed domain classes are deprecated and should no longer be used.
 * Please use see the Entity interface for their replacement.
 * 
 * How the student left the school
 * 
 */
@Deprecated
@XmlType(name = "ExitWithdrawalType")
@XmlEnum
public enum ExitWithdrawalType {
    TRANSFER_PUBLIC_SAME_LOCAL_AGENCY("Student is in a different public school in the same local education agency"), TRANSFER_PUBLIC_DIFFERENT_LOCAL_AGENCY_SAME_STATE(
            "Transferred to a public school in a different local education agency in the same state"), TRANSFER_PUBLIC_DIFFERENT_STATE(
            "Transferred to a public school in a different state"), TRANSFER_PRIVATE_NON_RELIGIOUS_SAME_LOCAL_AGENCY(
            "Transferred to a private, non-religiously-affiliated school in the same local education agency"), TRANSFER_PRIVATE_NON_RELIGIOUS_DIFFERENT_LOCAL_AGENCY_SAME_STATE(
            "Transferred to a private, non-religiously-affiliated school in a different local education agency in the same state"), TRANSFER_PRIVATE_NON_RELIGIOUS_DIFFERENT_STATE(
            "Transferred to a private, non-religiously-affiliated school in a different state"), TRANSFER_PRIVATE_RELIGIOUS_SAME_LOCAL_AGENCY(
            "Transferred to a private, religiously-affiliated school in the same local education agency"), TRANSFER_PRIVATE_RELIGIOUS_DIFFERENT_LOCAL_AGENCY_SAME_STATE(
            "Transferred to a private, religiously-affiliated school in a different local education agency in the same state"), TRANSFER_PRIVATE_RELIGIOUS_DIFFERENT_STATE(
            "Transferred to a private, religiously-affiliated school in a different state"), TRANSFER_OUTSIDE_COUNTRY(
            "Transferred to a school outside of the country"), TRANSFER_INSTITUTION("Transferred to an institution"), TRANSFER_CHARTER(
            "Transferred to a charter school"), TRANSFER_HOME_SCHOOL("Transferred to home schooling"), GRADUATED(
            "Graduated with regular, advanced, International Baccalaureate, or other type of diploma"), COMPLETED_OTHER(
            "Completed school with other credentials"), DIED_OR_INCAPACITATED("Died or is permanently incapacitated"), ILLNESS(
            "Withdrawn due to illness"), EXPELLED("Expelled or involuntarily withdrawn"), MAX_AGE(
            "Reached maximum age for services"), DISCONTINUED("Discontinued schooling"), NOT_MEET_REQUIREMENTS(
            "Completed grade 12, but did not meet all graduation requirements"), POSTSECONDARY_EARLY_ADMISSION(
            "Enrolled in a postsecondary early admission program, eligible to return"), UNKNOWN(
            "Not enrolled, unknown status"), RECEIVING_EDUCATION_NOT_ENROLLED(
            "Student is in the same local education agency and receiving education services, but is not assigned to a particular school"), ADULT_ED(
            "Enrolled in an adult education or training program"), VOCATIONAL(
            "Completed a state-recognized vocational education program"), NOT_ENROLLED(
            "Not enrolled, eligible to return"), FOREIGN_EXCHANGE(
            "Enrolled in a foreign exchange program, eligible to return"), WITHDRAWN_UNDER_AGE(
            "Withdrawn from school, under the age for compulsory attendance; eligible to return"), EXITED("Exited"), CHARTER_SAME_LOCAL_AGENCY(
            "Student is in a charter school managed by the same local education agency"), EQUIVALENCY_CERTIFICATE(
            "Completed with a state-recognized equivalency certificate"), OTHER("Other");
    private final String value;
    
    private ExitWithdrawalType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ExitWithdrawalType fromValue(String str) {
        for (ExitWithdrawalType t : ExitWithdrawalType.values()) {
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
