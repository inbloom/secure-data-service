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

import openadk.library.common.TeachingArea;
import openadk.library.student.TeachingAssignment;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF TeachingArea to SLI academicSubjectType.
 *
 * @author slee
 *
 */
@Component
public class TeachingAssignmentConverter {

    private static final Map<TeachingArea, String> TEACHING_AREA_CODE_MAP = new HashMap<TeachingArea, String>();
    static {
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ACCOUNTING, "Business and Marketing");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.AGRICULTURE_OR_NATURAL, "Agriculture, Food, and Natural Resources");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.AMERICAN_INDIAN_NATIVE, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ANTHROPOLOGY, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.AUTISM, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.BASIC_SKILLS_OR_REMEDIAL, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.BILINGUAL_EDUCATION, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.BIOLOGY_OR_LIFE_SCIENCE, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.BUSINESS_AND_MANAGEMENT, "Business and Marketing");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.BUSINESS_OFFICE, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.CAREER_EDUCATION, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.CHEMISTRY, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.CHINESE, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.CIVICS, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.COMMUNICATIONS_TECH, "Communication and Audio/Visual Technology");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.COMPUTER_SCIENCE, "Computer and Information Sciences");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.COSMETOLOGY, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.DANCE, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.DEAF_AND_HARD_OF_HEARING, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.DESIGN, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.DEVELOPMENTALLY_DELAYED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.DRAMA_TEACHER, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.EARLY_CHILDHOOD, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.EARLY_CHILDHOOD_SPECIAL, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.EARTH_SPACE_SCIENCE_GEOLOGY, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ECONOMICS, "Business and Marketing");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ELEMENTARY, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.EMOTIONALLY_DISTURBED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ENGLISH_OR_LANG_ARTS, "English Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ESL, "English");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.FAMILY_AND_CONSUMER_SCIENCE, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.FOOD_SERVICES, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.FRENCH, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.GENERAL_SCIENCE, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.GEOGRAPHY, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.GERMAN, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.GIFTED_AND_TALENTED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.HEALTH_EDUCATION, "Physical, Health, and Safety Education");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.HEALTH_PROFESSIONS, "Health Care Sciences");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.HISTORY, "Social Sciences and History");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.HUMANITIES, "Human Services");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ITALIAN, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.JAPANESE, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.JOURNALISM_COMMUNICATIONS, "Communication and Audio/Visual Technology");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.KINDERGARTEN, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.LATIN, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.LAW, "Public, Protective, and Government Service");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.LEARNING_DISABILITIES, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.MATHEMATICS, "Mathematics");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.MENTALLY_DISABLED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.MILDLY_MODERATELY_DISABLED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.MILITARY_SCIENCE, "Military Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.MUSIC, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.ORTHOPEDICALLY_IMPAIRED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_AREA_OR_ETHNIC, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_BUSINESS, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_LANGUAGES, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_NATURAL_SCIENCES, "Life and Physical Sciences");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_SOCIAL_STUDIES, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_SPECIAL_ED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.OTHER_VOCATIONAL_TECH, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.PHILOSOPHY, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.PHYSICAL_EDUCATION, "Physical, Health, and Safety Education");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.PHYSICAL_SCIENCE, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.PHYSICS, "Science");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.POLITICAL_SCIENCE_AND, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.PSYCHOLOGY, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.READING, "Reading");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.RELIGION, "Religious Education and Theology");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.RUSSIAN, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.SOCIAL_STUDIES, "Social Studies");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.SOCIOLOGY, "Social Studies");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.SPANISH, "Foreign Language and Literature");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.SPECIAL_EDUCATION, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.SPEECH, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.SPEECH_LANGUAGE_IMPAIRED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.TRADES_AND_INDUSTR, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.TRAUMATICALLY_BRAIN_INJURED, "Other");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.VISUAL_ARTS, "Fine and Performing Arts");
        TEACHING_AREA_CODE_MAP.put(TeachingArea.VISUALLY_IMPAIRED, "Other");
    }

    public List<String> convert(TeachingAssignment source) {
        if (source == null) {
            return null;
        }

        List<String> list = new ArrayList<String>(0);
        list.add(toSliAcademicSubjectType(TeachingArea.wrap(source.getCode())));
        return list;
    }

    private String toSliAcademicSubjectType(TeachingArea code) {
        String mapping = TEACHING_AREA_CODE_MAP.get(code);
        return mapping == null ? "Not Available" : mapping;
    }
}

