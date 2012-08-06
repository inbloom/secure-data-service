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

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF GradeLevels to SLI gradesOffered.
 *
 * @author slee
 *
 */

@Component
public class GradeLevelsConverter {

    public List<String> convert(GradeLevels source) {
        if (source == null) {
            return null;
        }

        return toSliGradeList(source.getGradeLevels());
    }

    private List<String> toSliGradeList(GradeLevel[] gradeLevels) {
        List<String> list = new ArrayList<String>(gradeLevels.length);
        for (GradeLevel gradeLevel : gradeLevels) {
            list.add(toSliGrade(gradeLevel.getCode()));
        }
        return list;
    }

    private String toSliGrade(String gradeCode) {
        if (GradeLevelCode._01.getValue().equals(gradeCode)) {
            return "First grade";
        }
        if (GradeLevelCode._02.getValue().equals(gradeCode)) {
            return "Second grade";
        }
        if (GradeLevelCode._03.getValue().equals(gradeCode)) {
            return "Third grade";
        }
        if (GradeLevelCode._04.getValue().equals(gradeCode)) {
            return "Fourth grade";
        }
        if (GradeLevelCode._05.getValue().equals(gradeCode)) {
            return "Fifth grade";
        }
        if (GradeLevelCode._06.getValue().equals(gradeCode)) {
            return "Sixth grade";
        }
        if (GradeLevelCode._07.getValue().equals(gradeCode)) {
            return "Seventh grade";
        }
        if (GradeLevelCode._08.getValue().equals(gradeCode)) {
            return "Eighth grade";
        }
        if (GradeLevelCode._09.getValue().equals(gradeCode)) {
            return "Ninth grade";
        }
        if (GradeLevelCode._10.getValue().equals(gradeCode)) {
            return "Tenth grade";
        }
        if (GradeLevelCode._11.getValue().equals(gradeCode)) {
            return "Eleventh grade";
        }
        if (GradeLevelCode._12.getValue().equals(gradeCode)) {
            return "Twelfth grade";
        }
        if (GradeLevelCode.KG.getValue().equals(gradeCode)) {
            return "Kindergarten";
        }
        if (GradeLevelCode.UN.getValue().equals(gradeCode)) {
            return "Ungraded";
        }
        if (GradeLevelCode.PG.getValue().equals(gradeCode)) {
            return "Preschool/Prekindergarten";
        }
        if (GradeLevelCode.PK.getValue().equals(gradeCode)) {
            return "Adult Education";
        }
        if (GradeLevelCode.OTHER.getValue().equals(gradeCode)) {
            return "Other";
        }
        if (GradeLevelCode.UNKNOWN.getValue().equals(gradeCode)) {
            return "Not Available";
        }
        return "Not Available";
    }
}

