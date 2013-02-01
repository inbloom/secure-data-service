/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.sif.domain.converter;

import java.util.HashMap;
import java.util.Map;

import openadk.library.common.ExitType;
import openadk.library.common.ExitTypeCode;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF ExitType to SLI ExitType enumeration.
 *
 * SLI values:
 *     Student is in a different public school in the same local education agency
 *     Transferred to a public school in a different local education agency in the same state
 *     Transferred to a public school in a different state
 *     Transferred to a private, non-religiously-affiliated school in the same local education agency
 *     Transferred to a private, non-religiously-affiliated school in a different local education agency in the same state
 *     Transferred to a private, non-religiously-affiliated school in a different state
 *     Transferred to a private, religiously-affiliated school in the same local education agency
 *     Transferred to a private, religiously-affiliated school in a different local education agency in the same state
 *     Transferred to a private, religiously-affiliated school in a different state
 *     Transferred to a school outside of the country
 *     Transferred to an institution
 *     Transferred to home schooling
 *     Transferred to a charter school
 *     Graduated with regular, advanced, International Baccalaureate, or other type of diploma
 *     Completed school with other credentials
 *     Died or is permanently incapacitated
 *     Withdrawn due to illness
 *     Expelled or involuntarily withdrawn
 *     Reached maximum age for services
 *     Discontinued schooling
 *     Completed grade 12, but did not meet all graduation requirements
 *     Enrolled in a postsecondary early admission program, eligible to return
 *     Not enrolled, unknown status
 *     Student is in the same local education agency and receiving education services, but is not assigned to a particular school
 *     Enrolled in an adult education or training program
 *     Completed a state-recognized vocational education program
 *     Not enrolled, eligible to return
 *     Enrolled in a foreign exchange program, eligible to return
 *     Withdrawn from school, under the age for compulsory attendance; eligible to return
 *     Exited
 *     Student is in a charter school managed by the same local education agency
 *     Completed with a state-recognized equivalency certificate
 *     Removed by Child Protective Services
 *     Transferred to a private school in the state
 *     Graduated outside of state prior to enrollment
 *     Completed equivalency certificate outside of state
 *     Enrolled in University High School Diploma Program
 *     Court ordered to a GED program, has not earned a GED
 *     Incarcerated in a state jail or federal penitentiary as an adult
 *     Graduated from another state under Interstate Compact on Educational Opportunity for Military Children
 *     Dropout
 *     End of school year
 *     Invalid enrollment
 *     No show
 *     Other
 */
@Component
public class ExitTypeConverter {

    private static final Map<ExitTypeCode, String> EXIT_TYPE_CODE_MAP = new HashMap<ExitTypeCode, String>();
    static {
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1907_TRANSFERRED_IN_LEA, "Student is in a different public school in the same local education agency");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1908_TRANSFERRED_IN_STATE, "Transferred to a public school in a different local education agency in the same state");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1909_TRANSFERRED_DIFFERENT_STATE, "Transferred to a public school in a different state");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1910_TRANSFERRED_PRIVATE_IN_LEA, "Transferred to a private, non-religiously-affiliated school in the same local education agency");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1911_TRANSFERRED_PRIVATE_IN_STATE, "Transferred to a private, non-religiously-affiliated school in a different local education agency in the same state");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1912_TRANSFERRED_PRIVATE_IN_COUNTRY, "Transferred to a private, non-religiously-affiliated school in a different state");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1913_TRANSFERRED_RELIGEOUS_IN_LEA, "Transferred to a private, religiously-affiliated school in the same local education agency");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1914_TRANSFERRED_RELIGEOUS_IN_STATE, "Transferred to a private, religiously-affiliated school in a different local education agency in the same state");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1915_TRANSFERRED_RELIGEOUS_IN_COUNTRY, "Transferred to a private, religiously-affiliated school in a different state");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1916_TRANSFERRED_OUT_OF_COUNTRY, "Transferred to a school outside of the country");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1917_TRANSFERRED_TO_AN_INSTITUTION, "Transferred to an institution");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1918_TRANSFERRED_TO_HOME_SCHOOLING, "Transferred to home schooling");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1919_TRANSFERRED_TO_A_CHARTER, "Transferred to a charter school");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1921_GRADUATED_WITH_DEGREE, "Graduated with regular, advanced, International Baccalaureate, or other type of diploma");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1922_COMPLETED_SCHOOL_WITH_OTHER, "Completed school with other credentials");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1923_DIED_OR_INCAPACITATED, "Died or is permanently incapacitated");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1924_WITHDRAWN_ILLNESS, "Withdrawn due to illness");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1925_EXPELLED_OR_INVOLUNTARY, "Expelled or involuntarily withdrawn");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1926_REACHED_MAXIMUM_AGE, "Reached maximum age for services");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1927_DISCONTINUED_SCHOOLING, "Discontinued schooling");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1928_COMPLETED_GRADE_12_NOT_GRADUATED, "Completed grade 12, but did not meet all graduation requirements");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1930_ENROLLED_IN_A_POSTSECONDARY, "Enrolled in a postsecondary early admission program, eligible to return");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._1931_NOT_ENROLLED_UNKNOWN, "Not enrolled, unknown status");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3499_STUDENT_IS_IN_THE_SAME_LOCAL, "Student is in the same local education agency and receiving education servi, but is not assigned to a particular school");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3500_ENROLLED_IN_AN_ADULT, "Enrolled in an adult education or training program");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3501_COMPLETED_A_STATE_RECOGNIZED, "Completed a state-recognized vocational education program");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO, "Not enrolled, eligible to return");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3503_ENROLLED_IN_A_FOREIGN, "Enrolled in a foreign exchange program, eligible to return");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3504_WITHDRAWN_FROM_SCHOOL_UNDER, "Withdrawn from school, under the age for compulsory attendance; eligible to return");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3505_EXITED, "Exited");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3508_STUDENT_IS_IN_A_CHARTER, "Student is in a charter school managed by the same local education agency");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._3509_COMPLETED_WITH_A_STATE, "Completed with a state-recognized equivalency certificate");
        EXIT_TYPE_CODE_MAP.put(ExitTypeCode._9999_OTHER, "Other");
    }

    public String convert(ExitType exitType) {
        if (exitType == null) {
            return null;
        }
        return toSliExitType(ExitTypeCode.wrap(exitType.getCode()));
    }

    private String toSliExitType(ExitTypeCode exitTypeCode) {
        String mapping = EXIT_TYPE_CODE_MAP.get(exitTypeCode);
        return mapping == null ? "Other" : mapping;
    }
}
