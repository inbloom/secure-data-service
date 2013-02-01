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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * GradeLevelsConverter unit tests
 */
public class GradeLevelsConverterTest extends AdkTest {

    private final GradeLevelsConverter converter = new GradeLevelsConverter();
    private Map<GradeLevelCode, String> map = new HashMap<GradeLevelCode, String>();

    @Test
    public void testNullObject() {
        List<String> result = converter.convert((GradeLevels) null);
        Assert.assertNull("Grade levels list should be null", result);
    }

    @Test
    public void testEmptyList() {
        GradeLevels list = new GradeLevels();
        list.setGradeLevels(new GradeLevel[0]);
        List<String> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyGradeLevel() {
        GradeLevels list = new GradeLevels();
        GradeLevel original = new GradeLevel();
        list.add(original);
        List<String> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        String gradeLevel = result.get(0);
        Assert.assertEquals("Not Available", gradeLevel);
    }

    @Test
    public void testMappings() {
        map.clear();
        map.put(GradeLevelCode._01, "First grade");
        map.put(GradeLevelCode._02, "Second grade");
        map.put(GradeLevelCode._03, "Third grade");
        map.put(GradeLevelCode._04, "Fourth grade");
        map.put(GradeLevelCode._05, "Fifth grade");
        map.put(GradeLevelCode._06, "Sixth grade");
        map.put(GradeLevelCode._07, "Seventh grade");
        map.put(GradeLevelCode._08, "Eighth grade");
        map.put(GradeLevelCode._09, "Ninth grade");
        map.put(GradeLevelCode._10, "Tenth grade");
        map.put(GradeLevelCode._11, "Eleventh grade");
        map.put(GradeLevelCode._12, "Twelfth grade");
        map.put(GradeLevelCode.KG, "Kindergarten");
        map.put(GradeLevelCode.OTHER, "Other");
        map.put(GradeLevelCode.PG, "Postsecondary");
        map.put(GradeLevelCode.PK, "Preschool/Prekindergarten");
        map.put(GradeLevelCode.UN, "Ungraded");
        map.put(GradeLevelCode.UNKNOWN, "Not Available");

        GradeLevels list = getGradeLevels();
        List<String> results = converter.convert(list);

        Assert.assertEquals(list.size(), results.size());

        int newCounter = 0;
        for (String gradeLevel : results) {
            Assert.assertNotNull(gradeLevel);
            GradeLevel original = list.get(newCounter++);
            testMapping(original.getCode(), gradeLevel);
        }
    }

    private GradeLevels getGradeLevels() {
        GradeLevels gradeLevels = new GradeLevels();

        for (GradeLevelCode code : map.keySet()) {
            gradeLevels.add(getGradeLevel(code));
        }

        gradeLevels.add(getGradeLevel(GradeLevelCode.wrap("something else")));

        return gradeLevels;
    }

    private GradeLevel getGradeLevel(GradeLevelCode code) {
        GradeLevel level = new GradeLevel();
        level.setCode(code);
        return level;
    }

    private void testMapping(String original, String mapped) {
        String expectedType = "Not Available";

        GradeLevelCode originalCode = GradeLevelCode.wrap(original);
        if (map.containsKey(originalCode)) {
            expectedType = map.get(originalCode);
        }
        Assert.assertEquals(expectedType, mapped);
    }
}
