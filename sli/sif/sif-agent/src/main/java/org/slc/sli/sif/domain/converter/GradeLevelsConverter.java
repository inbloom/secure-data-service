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

import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF GradeLevels to SLI gradesOffered.
 *
 * SLI values:
 * Adult Education
 * Early Education
 * Eighth grade
 * Eleventh grade
 * Fifth grade
 * First grade
 * Fourth grade
 * Grade 13
 * Infant/toddler
 * Kindergarten
 * Ninth grade
 * Other
 * Postsecondary
 * Preschool/Prekindergarten
 * Second grade
 * Seventh grade
 * Sixth grade
 * Tenth grade
 * Third grade
 * Transitional Kindergarten
 * Twelfth grade
 * Ungraded
 * Not Available
 *
 * @author slee
 *
 */
@Component
public class GradeLevelsConverter {

    private static final Map<GradeLevelCode, String> GRADE_LEVEL_CODE_MAP = new HashMap<GradeLevelCode, String>();
    static {
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._01, "First grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._02, "Second grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._03, "Third grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._04, "Fourth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._05, "Fifth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._06, "Sixth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._07, "Seventh grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._08, "Eighth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._09, "Ninth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._10, "Tenth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._11, "Eleventh grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode._12, "Twelfth grade");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode.KG, "Kindergarten");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode.OTHER, "Other");
        // SIF defines PG as Postgraduate/Adult, could be SLI Postsecondary or Adult Education
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode.PG, "Postsecondary");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode.PK, "Preschool/Prekindergarten");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode.UN, "Ungraded");
        GRADE_LEVEL_CODE_MAP.put(GradeLevelCode.UNKNOWN, "Not Available");
    }

    public List<String> convert(GradeLevels source) {
        if (source == null) {
            return null;
        }

        return toSliGradeList(source.getGradeLevels());
    }

    public String convert(GradeLevel source) {
        if (source == null) {
            return null;
        }
        return toSliGrade(GradeLevelCode.wrap(source.getCode()));
    }

    private List<String> toSliGradeList(GradeLevel[] gradeLevels) {
        List<String> list = new ArrayList<String>(gradeLevels.length);
        for (GradeLevel gradeLevel : gradeLevels) {
            list.add(convert(gradeLevel));
        }
        return list;
    }

    private String toSliGrade(GradeLevelCode gradeCode) {
        String mapping = GRADE_LEVEL_CODE_MAP.get(gradeCode);
        return mapping == null ? "Not Available" : mapping;
    }
}
