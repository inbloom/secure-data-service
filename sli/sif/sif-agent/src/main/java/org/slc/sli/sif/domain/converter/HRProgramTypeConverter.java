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
import java.util.Map;

import openadk.library.common.ProgramTypeCode;
import openadk.library.hrfin.HRProgramType;

import org.springframework.stereotype.Component;

/**
 * A custom converter to convert SIF HRProgramType to SLI ProgramAssignment.
 *
 * Valid SLI values:
 * Regular Education
 * Title I-Academic
 * Title I-Non-Academic
 * Special Education
 * Bilingual/English as a Second Language
 *
 * @author slee
 *
 */
@Component
public class HRProgramTypeConverter {
    private static final Map<ProgramTypeCode, String> PROGRAM_TYPE_MAP = new HashMap<ProgramTypeCode, String>();
    static {
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ADULT_BASIC_EDUCATION, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ADULT_CONTINUING_EDUCATION, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ADULT_ENGLISH_AS_A_SECOND, "Bilingual/English as a Second Language");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ADVANCED_ADULT_BASIC_EDUCATION, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.AGRICULTURE, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ALTERNATIVE_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ATHLETICS, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.AUTISM, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.BILINGUAL_EDUCATION_PROGRAM, "Bilingual/English as a Second Language");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.BRAIN_INJURY, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.BUSINESS, "Title I-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.CIVIC_SERVICES, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COCURRICULAR_PROGRAMS, "Regular Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COLLEGE_PREPARATORY, "Regular Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COMMUNITY_JUNIOR_COLLEGE, "Regular Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COMMUNITY_RECREATION, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COMMUNITY_SERVICES_PROGRAM, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COMPENSATORY_DISADVANTAGED, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.CONSUMER_AND_HOME_MAKING, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.CONTENT_ESL, "Bilingual/English as a Second Language");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COORDINATION_OF_CASEWORK, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.COUNSELING_SERVICES, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.CROSS_CATEGORICAL, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.CURRICULUM_CONTENT_IN_NATIVE, "Title I-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.CUSTODY_AND_CHILD_CARE, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.DEAF_BLIND, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.DEVELOPMENTAL_BILINGUAL, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.DEVELOPMENTAL_DELAY, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.EARLY_IDENTIFICATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.EARLY_INTERVENTION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.EMOTIONAL_DISTURBANCE, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ENGLISH_AS_A_SECOND_LANGUAGE, "Bilingual/English as a Second Language");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.EVEN_START, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.EXTENDED_DAY_CHILD_CARE, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.GENERAL_EDUCATIONAL_DEVELOPMEN, "Title I-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.GIFTED_AND_TALENTED, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.HEAD_START, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.HEALTH, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.HEALTH_SERVICES, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.HEARING_IMPAIRMENT, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.IMMIGRANT_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.INDIAN_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.INTERNATIONAL_BACCALAUREATE, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.LEARNING_DISABILITIES, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.LIBRARY_MEDIA_SERVICES, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.LIFE_ENRICHMENT, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.MAGNET_SPECIAL_PROGRAM, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.MARKETING, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.MENTAL_RETARDATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.MIGRANT_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.MULTIPLE_DISABILITIES, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.NATIVE_LANGUAGE_SUPPORT, "Title I-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OCCUPATIONAL, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OCCUPATIONAL_HOME_ECONOMICS, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.ORTH_IMPAIRMENT, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OTHER, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OTHER_ADULT_CONTINUING, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OTHER_COMMUNITY_SERVICES, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OTHER_SP_ED, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.OTHER_VOCATIONAL_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.PSYCHOLOGICAL_SERVICE, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.PUBLIC_LIBRARY_SERVICES, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.REGULAR_EDUCATION, "Regular Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.REMEDIAL_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.RETRAINING_FOR_NEW_OCCUPATION, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.SCHOOL_TO_WORK_OPPORTUNITIES, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.SERVICE_LEARNING, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.SPECIAL_EDUCATION_SERVICES, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.SPECIAL_INTEREST, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.SPEECH_IMPAIREMENT, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.STUDENT_RETENTION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.SUBSTANCE_ABUSE_EDUCATION, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.TECHNICAL, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.TECHNICAL_PREPARATORY, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.TECHNOLOGY_INDUSTRIAL, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.TRADE_AND_INDUSTRIAL, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.TWO_WAY_BILINGUAL_EDUCATION, "Bilingual/English as a Second Language");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.UPGRADING_IN_CURRENT_OCCUPATIO, "Title I-Non-Academic");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.VISUAL_IMPAIRMENT, "Special Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.VOCATIONAL_EDUCATION, "Regular Education");
        PROGRAM_TYPE_MAP.put(ProgramTypeCode.WELFARE_ACTIVITIES, "Title I-Non-Academic");
    }

    public String convert(HRProgramType hrProgramType) {
        if (hrProgramType == null) {
            return null;
        }

        return toSliProgramAssignment(ProgramTypeCode.wrap(hrProgramType.getCode()));
    }

    private String toSliProgramAssignment(ProgramTypeCode programTypeCode) {
        String mapping = PROGRAM_TYPE_MAP.get(programTypeCode);
        return mapping == null ? "Not Available" : mapping;
    }

}

