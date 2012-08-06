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

import openadk.library.student.SchoolLevelType;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF SchoolLevelType to SLI school category
 *
 */

@Component
public class SchoolLevelTypeConverter {

    public String convert(SchoolLevelType schoolLevelType) {
        if (schoolLevelType == null) {
            return null;
        }

        return toSliSchoolCategory(schoolLevelType);
    }

    public List<String> convertAsList(SchoolLevelType schoolLevelType){
        if (schoolLevelType == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();
        list.add(toSliSchoolCategory(schoolLevelType));
        return list;
    }

    private String toSliSchoolCategory(SchoolLevelType schoolLevelType) {
        if (SchoolLevelType._0031_1304_ELEMENTARY.equals(schoolLevelType)) {
            return "Elementary School";
        }
        if (SchoolLevelType._0031_0013_ADULT.equals(schoolLevelType)) {
            return "Adult School";
        }
        if (SchoolLevelType._0031_0789_PRE_KINDERGARTEN.equals(schoolLevelType)) {
            return "Preschool/early childhood";
        }
        if (SchoolLevelType._0031_1302_ALL_LEVELS.equals(schoolLevelType)) {
            return "Ungraded";
        }
        if (SchoolLevelType._0031_1981_PRESCHOOL.equals(schoolLevelType)) {
            return "Infant/toddler School";
        }
        if (SchoolLevelType._0031_2397_PRIMARY.equals(schoolLevelType)) {
            return "Primary School";
        }
        if (SchoolLevelType._0031_2399_INTERMEDIATE.equals(schoolLevelType)) {
            return "Intermediate School";
        }
        if (SchoolLevelType._0031_2400_MIDDLE.equals(schoolLevelType)) {
            return "Middle School";
        }
        if (SchoolLevelType._0031_2401_JUNIOR.equals(schoolLevelType)) {
            return "Junior High School";
        }
        if (SchoolLevelType._0031_2402_HIGH_SCHOOL.equals(schoolLevelType)) {
            return "High School";
        }
        if (SchoolLevelType._0031_2403_SECONDARY.equals(schoolLevelType)) {
            return "SecondarySchool";
        }
        return "Ungraded";
    }

}
