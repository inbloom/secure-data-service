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

import openadk.library.student.SchoolLevelType;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF SchoolLevelType to SLI school category
 *
 * SLI Values
 * <xs:enumeration value="Elementary/Secondary School"/>
 * <xs:enumeration value="Elementary School"/>
 * <xs:enumeration value="High School"/>
 * <xs:enumeration value="Middle School"/>
 * <xs:enumeration value="Junior High School"/>
 * <xs:enumeration value="SecondarySchool"/>
 * <xs:enumeration value="Ungraded"/>
 * <xs:enumeration value="Adult School"/>
 * <xs:enumeration value="Infant/toddler School"/>
 * <xs:enumeration value="Preschool/early childhood"/>
 * <xs:enumeration value="Primary School"/>
 * <xs:enumeration value="Intermediate School"/>
 * <xs:enumeration value="All Levels"/>
 */

@Component
public class SchoolLevelTypeConverter {

    private static final Map<SchoolLevelType, String> SCHOOL_LEVEL_TYPE_MAP = new HashMap<SchoolLevelType, String>();
    static {
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_1304_ELEMENTARY, "Elementary School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType.ELEMENTARY, "Elementary School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_0013_ADULT, "Adult School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_0789_PRE_KINDERGARTEN, "Preschool/early childhood");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_1302_ALL_LEVELS, "All Levels");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_1981_PRESCHOOL, "Preschool/early childhood");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_2397_PRIMARY, "Primary School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_2399_INTERMEDIATE, "Intermediate School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_2400_MIDDLE, "Middle School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType.MIDDLE, "Middle School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_2401_JUNIOR, "Junior High School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType.JUNIOR, "Junior High School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_2402_HIGH_SCHOOL, "High School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType.HIGH, "High School");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_2403_SECONDARY, "SecondarySchool");
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType._0031_0787_INFANTS_TODDLERS, "Infant/toddler School");
        // not mapped in SLI
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType.INSTITUTION, null);
        SCHOOL_LEVEL_TYPE_MAP.put(SchoolLevelType.ZZ, null);
    }

    public String convert(SchoolLevelType schoolLevelType) {
        if (schoolLevelType == null) {
            return null;
        }

        return SCHOOL_LEVEL_TYPE_MAP.get(schoolLevelType);
    }

    public List<String> convertAsList(SchoolLevelType schoolLevelType) {
        if (schoolLevelType == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();
        String category = SCHOOL_LEVEL_TYPE_MAP.get(schoolLevelType);
        if (category != null) {
            list.add(category);
        }
        return list;
    }

}
