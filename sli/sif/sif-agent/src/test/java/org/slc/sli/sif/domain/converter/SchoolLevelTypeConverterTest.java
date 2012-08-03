/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.List;

import junit.framework.Assert;
import openadk.library.student.SchoolLevelType;

import org.junit.Test;

import org.slc.sli.sif.ADKTest;

public class SchoolLevelTypeConverterTest extends ADKTest {

    private final SchoolLevelTypeConverter converter = new SchoolLevelTypeConverter();

    @Test
    public void testNull() {
        String result = converter.convert(null);
        Assert.assertNull("School category should be null", result);
    }

    @Test
    public void testListNull() {
        List<String> result = converter.convertAsList(null);
        Assert.assertNull("School category should be null", result);
    }

    @Test
    public void testEmpty() {
        SchoolLevelType type = SchoolLevelType.wrap("");
        String result = converter.convert(type);
        Assert.assertEquals("Ungraded", result);
    }

    @Test
    public void testListEmpty() {
        SchoolLevelType type = SchoolLevelType.wrap("");
        List<String> result = converter.convertAsList(type);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Ungraded", result.get(0));
    }

    @Test
    public void testMapped(){
        testAll(SchoolLevelType._0031_0013_ADULT, "Adult School");
        testAll(SchoolLevelType._0031_0789_PRE_KINDERGARTEN, "Preschool/early childhood");
        testAll(SchoolLevelType._0031_1302_ALL_LEVELS, "Ungraded");
        testAll(SchoolLevelType._0031_1304_ELEMENTARY, "Elementary School");
        testAll(SchoolLevelType._0031_1981_PRESCHOOL, "Infant/toddler School");
        testAll(SchoolLevelType._0031_2397_PRIMARY, "Primary School");
        testAll(SchoolLevelType._0031_2399_INTERMEDIATE, "Intermediate School");
        testAll(SchoolLevelType._0031_2400_MIDDLE, "Middle School");
        testAll(SchoolLevelType._0031_2401_JUNIOR, "Junior High School");
        testAll(SchoolLevelType._0031_2402_HIGH_SCHOOL, "High School");
        testAll(SchoolLevelType._0031_2403_SECONDARY, "SecondarySchool");
    }

    @Test
    public void testDefault(){
        testAll(SchoolLevelType._0031_0787_INFANTS_TODDLERS, "Ungraded");
        testAll(SchoolLevelType.ELEMENTARY, "Ungraded");
        testAll(SchoolLevelType.HIGH, "Ungraded");
        testAll(SchoolLevelType.INSTITUTION, "Ungraded");
        testAll(SchoolLevelType.JUNIOR, "Ungraded");
        testAll(SchoolLevelType.MIDDLE, "Ungraded");
        testAll(SchoolLevelType.ZZ, "Ungraded");
        testAll(SchoolLevelType.wrap("something else"), "Ungraded");
    }

    private void testAll(SchoolLevelType type, String expected) {
        testCategory(type, expected);
        testCategoryList(type, expected);
    }

    private void testCategory(SchoolLevelType type, String expected){
        String result = converter.convert(type);
        Assert.assertEquals(expected, result);
    }

    private void testCategoryList(SchoolLevelType type, String expected){
        List<String> result = converter.convertAsList(type);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(expected, result.get(0));
    }

}
