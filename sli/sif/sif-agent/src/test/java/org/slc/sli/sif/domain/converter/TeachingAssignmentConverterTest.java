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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.common.TeachingArea;
import openadk.library.student.TeachingAssignment;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * GradeLevelsConverter unit tests
 */
public class TeachingAssignmentConverterTest extends AdkTest {

    private final TeachingAssignmentConverter converter = new TeachingAssignmentConverter();
    private Map<TeachingArea, String> map = new HashMap<TeachingArea, String>();

    @Test
    public void testNullObject() {
        List<String> result = converter.convert(null);
        Assert.assertNull("Teaching Assignment list should be null", result);
    }

    @Test
    public void testEmptyList() {
        TeachingAssignment list = new TeachingAssignment();
        List<String> result = converter.convert(list);
        Assert.assertEquals(1, result.size());
        String academicSubjectType = result.get(0);
        Assert.assertEquals("Not Available", academicSubjectType);
    }

    @Test
    public void testMappings() {
        map.clear();
        map.put(TeachingArea.ACCOUNTING, "Business and Marketing");
        map.put(TeachingArea.AGRICULTURE_OR_NATURAL, "Agriculture, Food, and Natural Resources");
        map.put(TeachingArea.AMERICAN_INDIAN_NATIVE, "Other");
        map.put(TeachingArea.ANTHROPOLOGY, "Other");
        map.put(TeachingArea.AUTISM, "Other");
        map.put(TeachingArea.BASIC_SKILLS_OR_REMEDIAL, "Other");
        map.put(TeachingArea.BILINGUAL_EDUCATION, "Other");
        map.put(TeachingArea.BIOLOGY_OR_LIFE_SCIENCE, "Science");
        map.put(TeachingArea.BUSINESS_AND_MANAGEMENT, "Business and Marketing");
        map.put(TeachingArea.BUSINESS_OFFICE, "Other");
        map.put(TeachingArea.CAREER_EDUCATION, "Other");
        map.put(TeachingArea.CHEMISTRY, "Science");
        map.put(TeachingArea.CHINESE, "Foreign Language and Literature");
        map.put(TeachingArea.CIVICS, "Other");
        map.put(TeachingArea.COMMUNICATIONS_TECH, "Communication and Audio/Visual Technology");
        map.put(TeachingArea.COMPUTER_SCIENCE, "Computer and Information Sciences");
        map.put(TeachingArea.COSMETOLOGY, "Other");
        map.put(TeachingArea.DANCE, "Other");
        map.put(TeachingArea.DEAF_AND_HARD_OF_HEARING, "Other");
        map.put(TeachingArea.DESIGN, "Other");
        map.put(TeachingArea.DEVELOPMENTALLY_DELAYED, "Other");
        map.put(TeachingArea.DRAMA_TEACHER, "Other");
        map.put(TeachingArea.EARLY_CHILDHOOD, "Other");
        map.put(TeachingArea.EARLY_CHILDHOOD_SPECIAL, "Other");
        map.put(TeachingArea.EARTH_SPACE_SCIENCE_GEOLOGY, "Science");
        map.put(TeachingArea.ECONOMICS, "Business and Marketing");
        map.put(TeachingArea.ELEMENTARY, "Other");
        map.put(TeachingArea.EMOTIONALLY_DISTURBED, "Other");
        map.put(TeachingArea.ENGLISH_OR_LANG_ARTS, "English Language and Literature");
        map.put(TeachingArea.ESL, "English");
        map.put(TeachingArea.FAMILY_AND_CONSUMER_SCIENCE, "Other");
        map.put(TeachingArea.FOOD_SERVICES, "Other");
        map.put(TeachingArea.FRENCH, "Foreign Language and Literature");
        map.put(TeachingArea.GENERAL_SCIENCE, "Science");
        map.put(TeachingArea.GEOGRAPHY, "Science");
        map.put(TeachingArea.GERMAN, "Foreign Language and Literature");
        map.put(TeachingArea.GIFTED_AND_TALENTED, "Other");
        map.put(TeachingArea.HEALTH_EDUCATION, "Physical, Health, and Safety Education");
        map.put(TeachingArea.HEALTH_PROFESSIONS, "Health Care Sciences");
        map.put(TeachingArea.HISTORY, "Social Sciences and History");
        map.put(TeachingArea.HUMANITIES, "Human Services");
        map.put(TeachingArea.ITALIAN, "Foreign Language and Literature");
        map.put(TeachingArea.JAPANESE, "Foreign Language and Literature");
        map.put(TeachingArea.JOURNALISM_COMMUNICATIONS, "Communication and Audio/Visual Technology");
        map.put(TeachingArea.KINDERGARTEN, "Other");
        map.put(TeachingArea.LATIN, "Foreign Language and Literature");
        map.put(TeachingArea.LAW, "Public, Protective, and Government Service");
        map.put(TeachingArea.LEARNING_DISABILITIES, "Other");
        map.put(TeachingArea.MATHEMATICS, "Mathematics");
        map.put(TeachingArea.MENTALLY_DISABLED, "Other");
        map.put(TeachingArea.MILDLY_MODERATELY_DISABLED, "Other");
        map.put(TeachingArea.MILITARY_SCIENCE, "Military Science");
        map.put(TeachingArea.MUSIC, "Other");
        map.put(TeachingArea.ORTHOPEDICALLY_IMPAIRED, "Other");
        map.put(TeachingArea.OTHER, "Other");
        map.put(TeachingArea.OTHER_AREA_OR_ETHNIC, "Other");
        map.put(TeachingArea.OTHER_BUSINESS, "Other");
        map.put(TeachingArea.OTHER_LANGUAGES, "Foreign Language and Literature");
        map.put(TeachingArea.OTHER_NATURAL_SCIENCES, "Life and Physical Sciences");
        map.put(TeachingArea.OTHER_SOCIAL_STUDIES, "Other");
        map.put(TeachingArea.OTHER_SPECIAL_ED, "Other");
        map.put(TeachingArea.OTHER_VOCATIONAL_TECH, "Other");
        map.put(TeachingArea.PHILOSOPHY, "Other");
        map.put(TeachingArea.PHYSICAL_EDUCATION, "Physical, Health, and Safety Education");
        map.put(TeachingArea.PHYSICAL_SCIENCE, "Science");
        map.put(TeachingArea.PHYSICS, "Science");
        map.put(TeachingArea.POLITICAL_SCIENCE_AND, "Other");
        map.put(TeachingArea.PSYCHOLOGY, "Other");
        map.put(TeachingArea.READING, "Reading");
        map.put(TeachingArea.RELIGION, "Religious Education and Theology");
        map.put(TeachingArea.RUSSIAN, "Foreign Language and Literature");
        map.put(TeachingArea.SOCIAL_STUDIES, "Social Studies");
        map.put(TeachingArea.SOCIOLOGY, "Social Studies");
        map.put(TeachingArea.SPANISH, "Foreign Language and Literature");
        map.put(TeachingArea.SPECIAL_EDUCATION, "Other");
        map.put(TeachingArea.SPEECH, "Other");
        map.put(TeachingArea.SPEECH_LANGUAGE_IMPAIRED, "Other");
        map.put(TeachingArea.TRADES_AND_INDUSTR, "Other");
        map.put(TeachingArea.TRAUMATICALLY_BRAIN_INJURED, "Other");
        map.put(TeachingArea.VISUAL_ARTS, "Fine and Performing Arts");
        map.put(TeachingArea.VISUALLY_IMPAIRED, "Other");

        List<TeachingAssignment> list = getTeachingAssignments();
        int newCounter = 0;
        for (TeachingAssignment ta : list) {
            List<String> results = converter.convert(ta);
            Assert.assertEquals(1, results.size());
            TeachingAssignment original = list.get(newCounter++);
            testMapping(original.getCode(), results.get(0));

        }
    }

    private List<TeachingAssignment> getTeachingAssignments() {
        List<TeachingAssignment> tas = new ArrayList<TeachingAssignment>(map.size() + 1);
        for (TeachingArea code : map.keySet()) {
            tas.add(new TeachingAssignment(code));
        }
        tas.add(new TeachingAssignment((TeachingArea.wrap("something else"))));
        return tas;
    }

    private void testMapping(String original, String mapped) {
        String expectedType = "Not Available";

        TeachingArea originalCode = TeachingArea.wrap(original);
        if (map.containsKey(originalCode)) {
            expectedType = map.get(originalCode);
        }
        Assert.assertEquals(expectedType, mapped);
    }
}

