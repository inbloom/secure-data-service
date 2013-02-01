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
import openadk.library.common.ProgramTypeCode;
import openadk.library.hrfin.HRProgramType;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * HRProgramTypeConverter unit tests
 */
public class HRProgramTypeConverterTest extends AdkTest {

    private final HRProgramTypeConverter converter = new HRProgramTypeConverter();
    private Map<ProgramTypeCode, String> map = new HashMap<ProgramTypeCode, String>();

    @Test
    public void testNullObject() {
        String result = converter.convert(null);
        Assert.assertNull("Teaching Assignment list should be null", result);
    }

    @Test
    public void testMappings() {
        map.clear();
        map.put(ProgramTypeCode.ADULT_BASIC_EDUCATION, "Title I-Non-Academic");
        map.put(ProgramTypeCode.ADULT_CONTINUING_EDUCATION, "Title I-Non-Academic");
        map.put(ProgramTypeCode.ADULT_ENGLISH_AS_A_SECOND, "Bilingual/English as a Second Language");
        map.put(ProgramTypeCode.ADVANCED_ADULT_BASIC_EDUCATION, "Title I-Non-Academic");
        map.put(ProgramTypeCode.AGRICULTURE, "Title I-Non-Academic");
        map.put(ProgramTypeCode.ALTERNATIVE_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.ATHLETICS, "Title I-Non-Academic");
        map.put(ProgramTypeCode.AUTISM, "Special Education");
        map.put(ProgramTypeCode.BILINGUAL_EDUCATION_PROGRAM, "Bilingual/English as a Second Language");
        map.put(ProgramTypeCode.BRAIN_INJURY, "Special Education");
        map.put(ProgramTypeCode.BUSINESS, "Title I-Academic");
        map.put(ProgramTypeCode.CIVIC_SERVICES, "Title I-Non-Academic");
        map.put(ProgramTypeCode.COCURRICULAR_PROGRAMS, "Regular Education");
        map.put(ProgramTypeCode.COLLEGE_PREPARATORY, "Regular Education");
        map.put(ProgramTypeCode.COMMUNITY_JUNIOR_COLLEGE, "Regular Education");
        map.put(ProgramTypeCode.COMMUNITY_RECREATION, "Title I-Non-Academic");
        map.put(ProgramTypeCode.COMMUNITY_SERVICES_PROGRAM, "Title I-Non-Academic");
        map.put(ProgramTypeCode.COMPENSATORY_DISADVANTAGED, "Special Education");
        map.put(ProgramTypeCode.CONSUMER_AND_HOME_MAKING, "Title I-Non-Academic");
        map.put(ProgramTypeCode.CONTENT_ESL, "Bilingual/English as a Second Language");
        map.put(ProgramTypeCode.COORDINATION_OF_CASEWORK, "Title I-Non-Academic");
        map.put(ProgramTypeCode.COUNSELING_SERVICES, "Title I-Non-Academic");
        map.put(ProgramTypeCode.CROSS_CATEGORICAL, "Title I-Non-Academic");
        map.put(ProgramTypeCode.CURRICULUM_CONTENT_IN_NATIVE, "Title I-Academic");
        map.put(ProgramTypeCode.CUSTODY_AND_CHILD_CARE, "Title I-Non-Academic");
        map.put(ProgramTypeCode.DEAF_BLIND, "Special Education");
        map.put(ProgramTypeCode.DEVELOPMENTAL_BILINGUAL, "Special Education");
        map.put(ProgramTypeCode.DEVELOPMENTAL_DELAY, "Special Education");
        map.put(ProgramTypeCode.EARLY_IDENTIFICATION, "Special Education");
        map.put(ProgramTypeCode.EARLY_INTERVENTION, "Special Education");
        map.put(ProgramTypeCode.EMOTIONAL_DISTURBANCE, "Special Education");
        map.put(ProgramTypeCode.ENGLISH_AS_A_SECOND_LANGUAGE, "Bilingual/English as a Second Language");
        map.put(ProgramTypeCode.EVEN_START, "Special Education");
        map.put(ProgramTypeCode.EXTENDED_DAY_CHILD_CARE, "Special Education");
        map.put(ProgramTypeCode.GENERAL_EDUCATIONAL_DEVELOPMEN, "Title I-Academic");
        map.put(ProgramTypeCode.GIFTED_AND_TALENTED, "Special Education");
        map.put(ProgramTypeCode.HEAD_START, "Special Education");
        map.put(ProgramTypeCode.HEALTH, "Title I-Non-Academic");
        map.put(ProgramTypeCode.HEALTH_SERVICES, "Title I-Non-Academic");
        map.put(ProgramTypeCode.HEARING_IMPAIRMENT, "Special Education");
        map.put(ProgramTypeCode.IMMIGRANT_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.INDIAN_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.INTERNATIONAL_BACCALAUREATE, "Special Education");
        map.put(ProgramTypeCode.LEARNING_DISABILITIES, "Special Education");
        map.put(ProgramTypeCode.LIBRARY_MEDIA_SERVICES, "Title I-Non-Academic");
        map.put(ProgramTypeCode.LIFE_ENRICHMENT, "Title I-Non-Academic");
        map.put(ProgramTypeCode.MAGNET_SPECIAL_PROGRAM, "Special Education");
        map.put(ProgramTypeCode.MARKETING, "Title I-Non-Academic");
        map.put(ProgramTypeCode.MENTAL_RETARDATION, "Special Education");
        map.put(ProgramTypeCode.MIGRANT_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.MULTIPLE_DISABILITIES, "Special Education");
        map.put(ProgramTypeCode.NATIVE_LANGUAGE_SUPPORT, "Title I-Academic");
        map.put(ProgramTypeCode.OCCUPATIONAL, "Special Education");
        map.put(ProgramTypeCode.OCCUPATIONAL_HOME_ECONOMICS, "Special Education");
        map.put(ProgramTypeCode.ORTH_IMPAIRMENT, "Special Education");
        map.put(ProgramTypeCode.OTHER, "Special Education");
        map.put(ProgramTypeCode.OTHER_ADULT_CONTINUING, "Special Education");
        map.put(ProgramTypeCode.OTHER_COMMUNITY_SERVICES, "Special Education");
        map.put(ProgramTypeCode.OTHER_SP_ED, "Special Education");
        map.put(ProgramTypeCode.OTHER_VOCATIONAL_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.PSYCHOLOGICAL_SERVICE, "Title I-Non-Academic");
        map.put(ProgramTypeCode.PUBLIC_LIBRARY_SERVICES, "Title I-Non-Academic");
        map.put(ProgramTypeCode.REGULAR_EDUCATION, "Regular Education");
        map.put(ProgramTypeCode.REMEDIAL_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.RETRAINING_FOR_NEW_OCCUPATION, "Title I-Non-Academic");
        map.put(ProgramTypeCode.SCHOOL_TO_WORK_OPPORTUNITIES, "Special Education");
        map.put(ProgramTypeCode.SERVICE_LEARNING, "Title I-Non-Academic");
        map.put(ProgramTypeCode.SPECIAL_EDUCATION_SERVICES, "Special Education");
        map.put(ProgramTypeCode.SPECIAL_INTEREST, "Special Education");
        map.put(ProgramTypeCode.SPEECH_IMPAIREMENT, "Special Education");
        map.put(ProgramTypeCode.STUDENT_RETENTION, "Special Education");
        map.put(ProgramTypeCode.SUBSTANCE_ABUSE_EDUCATION, "Special Education");
        map.put(ProgramTypeCode.TECHNICAL, "Title I-Non-Academic");
        map.put(ProgramTypeCode.TECHNICAL_PREPARATORY, "Title I-Non-Academic");
        map.put(ProgramTypeCode.TECHNOLOGY_INDUSTRIAL, "Title I-Non-Academic");
        map.put(ProgramTypeCode.TRADE_AND_INDUSTRIAL, "Title I-Non-Academic");
        map.put(ProgramTypeCode.TWO_WAY_BILINGUAL_EDUCATION, "Bilingual/English as a Second Language");
        map.put(ProgramTypeCode.UPGRADING_IN_CURRENT_OCCUPATIO, "Title I-Non-Academic");
        map.put(ProgramTypeCode.VISUAL_IMPAIRMENT, "Special Education");
        map.put(ProgramTypeCode.VOCATIONAL_EDUCATION, "Regular Education");
        map.put(ProgramTypeCode.WELFARE_ACTIVITIES, "Title I-Non-Academic");

        List<HRProgramType> list = getHRProgramTypes();
        for (HRProgramType hrt : list) {
            testMapping(hrt.getCode(), converter.convert(hrt));

        }
    }

    private List<HRProgramType> getHRProgramTypes() {
        List<HRProgramType> list = new ArrayList<HRProgramType>(map.size() + 1);
        for (ProgramTypeCode code : map.keySet()) {
            HRProgramType hr = new HRProgramType();
            hr.setCode(code);
            list.add(hr);
        }
        HRProgramType hr = new HRProgramType();
        hr.setCode(ProgramTypeCode.wrap("something else"));
        list.add(hr);
        return list;
    }

    private void testMapping(String original, String mapped) {
        String expectedType = "Not Available";

        ProgramTypeCode originalCode = ProgramTypeCode.wrap(original);
        if (map.containsKey(originalCode)) {
            expectedType = map.get(originalCode);
        }
        Assert.assertEquals(expectedType, mapped);
    }
}

