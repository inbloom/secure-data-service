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

import junit.framework.Assert;
import openadk.library.common.ExitType;
import openadk.library.common.ExitTypeCode;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * ExitTypeConverter unit tests
 */
public class ExitTypeConverterTest extends AdkTest {

    private final ExitTypeConverter converter = new ExitTypeConverter();

    @Test
    public void testNullObject() {
        String result = converter.convert(null);
        Assert.assertNull("Entry Type should be null", result);
    }

    @Test
    public void testMappings() {
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1907_TRANSFERRED_IN_LEA)),
                "Student is in a different public school in the same local education agency");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1908_TRANSFERRED_IN_STATE)),
                "Transferred to a public school in a different local education agency in the same state");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1909_TRANSFERRED_DIFFERENT_STATE)),
                "Transferred to a public school in a different state");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1910_TRANSFERRED_PRIVATE_IN_LEA)),
                "Transferred to a private, non-religiously-affiliated school in the same local education agency");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1911_TRANSFERRED_PRIVATE_IN_STATE)),
                "Transferred to a private, non-religiously-affiliated school in a different local education agency in the same state");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1912_TRANSFERRED_PRIVATE_IN_COUNTRY)),
                "Transferred to a private, non-religiously-affiliated school in a different state");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1913_TRANSFERRED_RELIGEOUS_IN_LEA)),
                "Transferred to a private, religiously-affiliated school in the same local education agency");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1914_TRANSFERRED_RELIGEOUS_IN_STATE)),
                "Transferred to a private, religiously-affiliated school in a different local education agency in the same state");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1915_TRANSFERRED_RELIGEOUS_IN_COUNTRY)),
                "Transferred to a private, religiously-affiliated school in a different state");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1916_TRANSFERRED_OUT_OF_COUNTRY)),
                "Transferred to a school outside of the country");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1917_TRANSFERRED_TO_AN_INSTITUTION)),
                "Transferred to an institution");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1918_TRANSFERRED_TO_HOME_SCHOOLING)),
                "Transferred to home schooling");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1919_TRANSFERRED_TO_A_CHARTER)),
                "Transferred to a charter school");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1921_GRADUATED_WITH_DEGREE)),
                "Graduated with regular, advanced, International Baccalaureate, or other type of diploma");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1922_COMPLETED_SCHOOL_WITH_OTHER)),
                "Completed school with other credentials");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1923_DIED_OR_INCAPACITATED)),
                "Died or is permanently incapacitated");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1924_WITHDRAWN_ILLNESS)),
                "Withdrawn due to illness");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1925_EXPELLED_OR_INVOLUNTARY)),
                "Expelled or involuntarily withdrawn");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1926_REACHED_MAXIMUM_AGE)),
                "Reached maximum age for services");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1927_DISCONTINUED_SCHOOLING)),
                "Discontinued schooling");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1928_COMPLETED_GRADE_12_NOT_GRADUATED)),
                "Completed grade 12, but did not meet all graduation requirements");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1930_ENROLLED_IN_A_POSTSECONDARY)),
                "Enrolled in a postsecondary early admission program, eligible to return");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._1931_NOT_ENROLLED_UNKNOWN)),
                "Not enrolled, unknown status");
        Assert.assertEquals(
                converter.convert(getExitType(ExitTypeCode._3499_STUDENT_IS_IN_THE_SAME_LOCAL)),
                "Student is in the same local education agency and receiving education servi, but is not assigned to a particular school");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3500_ENROLLED_IN_AN_ADULT)),
                "Enrolled in an adult education or training program");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3501_COMPLETED_A_STATE_RECOGNIZED)),
                "Completed a state-recognized vocational education program");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO)),
                "Not enrolled, eligible to return");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3503_ENROLLED_IN_A_FOREIGN)),
                "Enrolled in a foreign exchange program, eligible to return");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3504_WITHDRAWN_FROM_SCHOOL_UNDER)),
                "Withdrawn from school, under the age for compulsory attendance; eligible to return");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3505_EXITED)), "Exited");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3508_STUDENT_IS_IN_A_CHARTER)),
                "Student is in a charter school managed by the same local education agency");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._3509_COMPLETED_WITH_A_STATE)),
                "Completed with a state-recognized equivalency certificate");
        Assert.assertEquals(converter.convert(getExitType(ExitTypeCode._9999_OTHER)), "Other");
    }

    private ExitType getExitType(ExitTypeCode code) {
        ExitType et = new ExitType();
        et.setCode(code);
        return et;
    }

}
