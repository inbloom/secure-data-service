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


package org.slc.sli.test.generators;

import java.util.Random;

import javax.xml.bind.annotation.XmlEnumValue;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.BehaviorCategoryType;
import org.slc.sli.test.edfi.entities.CohortScopeType;
import org.slc.sli.test.edfi.entities.CohortType;
import org.slc.sli.test.edfi.entities.DisciplineActionLengthDifferenceReasonType;
import org.slc.sli.test.edfi.entities.IncidentLocationType;
import org.slc.sli.test.edfi.entities.ProgramType;
import org.slc.sli.test.edfi.entities.ReasonExitedType;
import org.slc.sli.test.edfi.entities.ReporterDescriptionType;
import org.slc.sli.test.edfi.entities.StudentParticipationCodeType;
import org.slc.sli.test.edfi.entities.WeaponItemType;

/**
 * Util tp facilitate to randomly generate common data type
 *
 * @author slee
 *
 */
public class GeneratorUtils
{
    static Random generator = new Random(31);

    /**
     * Randomly generate a ProgramType.
     *
     * @return <code>ProgramType</code>
     */
    public static ProgramType generateProgramType() {
        int roll = generator.nextInt(38);
        
        switch (roll) {
            case 0: return ProgramType.ADULT_CONTINUING_EDUCATION;
            case 1: return ProgramType.ALTERNATIVE_EDUCATION;
            case 2: return ProgramType.ATHLETICS;
            case 3: return ProgramType.BILINGUAL;
            case 4: return ProgramType.BILINGUAL_SUMMER;
            case 5: return ProgramType.CAREER_AND_TECHNICAL_EDUCATION;
            case 6: return ProgramType.COCURRICULAR_PROGRAMS;
            case 7: return ProgramType.COLLEGE_PREPARATORY;
            case 8: return ProgramType.COMMUNITY_JUNIOR_COLLEGE_EDUCATION_PROGRAM;
            case 9: return ProgramType.COMMUNITY_SERVICE_PROGRAM;
            case 10: return ProgramType.COMPENSATORY_SERVICES_FOR_DISADVANTAGED_STUDENTS;
            case 11: return ProgramType.COUNSELING_SERVICES;
            case 12: return ProgramType.ENGLISH_AS_A_SECOND_LANGUAGE_ESL;
            case 13: return ProgramType.EVEN_START;
            case 14: return ProgramType.EXTENDED_DAY_CHILD_CARE_SERVICES;
            case 15: return ProgramType.GIFTED_AND_TALENTED;
            case 16: return ProgramType.HEAD_START;
            case 17: return ProgramType.HEALTH_SERVICES_PROGRAM;
            case 18: return ProgramType.HIGH_SCHOOL_EQUIVALENCY_PROGRAM_HSEP;
            case 19: return ProgramType.IDEA;
            case 20: return ProgramType.IMMIGRANT_EDUCATION;
            case 21: return ProgramType.INDIAN_EDUCATION;
            case 22: return ProgramType.INTERNATIONAL_BACCALAUREATE;
            case 23: return ProgramType.LIBRARY_MEDIA_SERVICES_PROGRAM;
            case 24: return ProgramType.MAGNET_SPECIAL_PROGRAM_EMPHASIS;
            case 25: return ProgramType.MIGRANT_EDUCATION;
            case 26: return ProgramType.NEGLECTED_AND_DELINQUENT_PROGRAM;
            case 27: return ProgramType.OPTIONAL_FLEXIBLE_SCHOOL_DAY_PROGRAM_OFSDP;
            case 28: return ProgramType.OTHER;
            case 29: return ProgramType.REGULAR_EDUCATION;
            case 30: return ProgramType.REMEDIAL_EDUCATION;
            case 31: return ProgramType.SECTION_504_PLACEMENT;
            case 32: return ProgramType.SERVICE_LEARNING;
            case 33: return ProgramType.SPECIAL_EDUCATION;
            case 34: return ProgramType.STUDENT_RETENTION_DROPOUT_PREVENTION;
            case 35: return ProgramType.SUBSTANCE_ABUSE_EDUCATION_PREVENTION;
            case 36: return ProgramType.TEACHER_PROFESSIONAL_DEVELOPMENT_MENTORING;
            case 37: return ProgramType.TECHNICAL_PREPARATORY;
            case 38: return ProgramType.TITLE_I_PART_A;
            default: return ProgramType.VOCATIONAL_EDUCATION;
        }
    }

    /**
     * Randomly generate a CohortType.
     *
     * @return <code>CohortType</code>
     */
    public static CohortType generateCohortType () {
        int roll = generator.nextInt(10);
        switch (roll) {
            case 0: return CohortType.ACADEMIC_INTERVENTION;
            case 1: return CohortType.ATTENDANCE_INTERVENTION;
            case 2: return CohortType.CLASSROOM_PULLOUT;
            case 3: return CohortType.COUNSELOR_LIST;
            case 4: return CohortType.DISCIPLINE_INTERVENTION;
            case 5: return CohortType.EXTRACURRICULAR_ACTIVITY;
            case 6: return CohortType.FIELD_TRIP;
            case 7: return CohortType.IN_SCHOOL_SUSPENSION;
            case 8: return CohortType.OTHER;
            case 9: return CohortType.PRINCIPAL_WATCH_LIST;
            case 10: return CohortType.STUDY_HALL;
            default: return CohortType.STUDY_HALL;
        }
    }

    /**
     * Randomly generate a CohortScopeType.
     *
     * @return <code>CohortScopeType</code>
     */
    public static CohortScopeType generateCohortScopeType () {
        int roll = generator.nextInt(6);
        switch (roll) {
            case 0: return CohortScopeType.CLASSROOM;
            case 1: return CohortScopeType.COUNSELOR;
            case 2: return CohortScopeType.DISTRICT;
            case 3: return CohortScopeType.PRINCIPAL;
            case 4: return CohortScopeType.SCHOOL;
            case 5: return CohortScopeType.STATEWIDE;
            case 6: return CohortScopeType.TEACHER;
            default: return CohortScopeType.TEACHER;
        }
    }

    /**
     * Randomly generate a AcademicSubjectType.
     *
     * @return <code>AcademicSubjectType</code>
     */
    public static AcademicSubjectType generateAcademicSubjectType () {
        int roll = generator.nextInt(31);
        switch (roll) {
            case 0:  return AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES;
            case 1:  return AcademicSubjectType.ARCHITECTURE_AND_CONSTRUCTION;
            case 2:  return AcademicSubjectType.BUSINESS_AND_MARKETING;
            case 3:  return AcademicSubjectType.COMMUNICATION_AND_AUDIO_VISUAL_TECHNOLOGY;
            case 4:  return AcademicSubjectType.COMPOSITE;
            case 5:  return AcademicSubjectType.COMPUTER_AND_INFORMATION_SCIENCES;
            case 6:  return AcademicSubjectType.CRITICAL_READING;
            case 7:  return AcademicSubjectType.ELA;
            case 8:  return AcademicSubjectType.ENGINEERING_AND_TECHNOLOGY;
            case 9 : return AcademicSubjectType.MISCELLANEOUS;
            case 10: return AcademicSubjectType.ENGLISH;
            case 11: return AcademicSubjectType.ENGLISH_LANGUAGE_AND_LITERATURE;
            case 12: return AcademicSubjectType.FINE_AND_PERFORMING_ARTS;
            case 13: return AcademicSubjectType.FOREIGN_LANGUAGE_AND_LITERATURE;
            case 14: return AcademicSubjectType.HEALTH_CARE_SCIENCES;
            case 15: return AcademicSubjectType.HOSPITALITY_AND_TOURISM;
            case 16: return AcademicSubjectType.HUMAN_SERVICES;
            case 17: return AcademicSubjectType.LIFE_AND_PHYSICAL_SCIENCES;
            case 18: return AcademicSubjectType.MANUFACTURING;
            case 19: return AcademicSubjectType.MATHEMATICS;
            case 20: return AcademicSubjectType.MILITARY_SCIENCE;
            case 21: return AcademicSubjectType.MISCELLANEOUS;
            case 22: return AcademicSubjectType.OTHER;
            case 23: return AcademicSubjectType.PHYSICAL_HEALTH_AND_SAFETY_EDUCATION;
            case 24: return AcademicSubjectType.PUBLIC_PROTECTIVE_AND_GOVERNMENT_SERVICE;
            case 25: return AcademicSubjectType.READING;
            case 26: return AcademicSubjectType.RELIGIOUS_EDUCATION_AND_THEOLOGY;
            case 27: return AcademicSubjectType.SCIENCE;
            case 28: return AcademicSubjectType.SOCIAL_SCIENCES_AND_HISTORY;
            case 29: return AcademicSubjectType.SOCIAL_STUDIES;
            case 30: return AcademicSubjectType.TRANSPORTATION_DISTRIBUTION_AND_LOGISTICS;
            case 31: return AcademicSubjectType.WRITING;
            default: return AcademicSubjectType.WRITING;
        }
    }

    /**
     * Randomly generate a ReasonExitedType.
     *
     * @return <code>ReasonExitedType</code>
     */
    public static ReasonExitedType generateReasonExitedType() {
        int roll = generator.nextInt(28);
        
        switch (roll) {
            case 0: return ReasonExitedType.TRANSFER_TO_REGULAR_EDUCATION;
            case 1: return ReasonExitedType.RECEIVED_A_CERTIFICATE;
            case 2: return ReasonExitedType.REACHED_MAXIMUM_AGE;
            case 3: return ReasonExitedType.DIED;
            case 4: return ReasonExitedType.DIED_OR_IS_PERMANENTLY_INCAPACITATED;
            case 5: return ReasonExitedType.DISCONTINUED_SCHOOLING;
            case 6: return ReasonExitedType.GRADUATED_WITH_A_HIGH_SCHOOL_DIPLOMA;
            case 7: return ReasonExitedType.RECEIVED_CERTIFICATE_OF_COMPLETION_MODIFIED_DIPLOMA_OR_FINISHED_IEP_REQUIREMENTS;
            case 8: return ReasonExitedType.PROGRAM_COMPLETION;
            case 9: return ReasonExitedType.NO_LONGER_RECEIVING_SPECIAL_EDUCATION;
            case 10: return ReasonExitedType.REFUSED_SERVICES;
            case 11: return ReasonExitedType.TRANSFERRED_TO_ANOTHER_DISTRICT_OR_SCHOOL_KNOWN_TO_BE_CONTINUING_IN_PROGRAM_SERVICE;
            case 12: return ReasonExitedType.TRANSFERRED_TO_ANOTHER_DISTRICT_OR_SCHOOL_NOT_KNOWN_TO_BE_CONTINUING_IN_PROGRAM_SERVICE;
            case 13: return ReasonExitedType.TRANSFERRED_TO_ANOTHER_DISTRICT_OR_SCHOOL_KNOWN_NOT_TO_BE_CONTINUING_IN_PROGRAM_SERVICE;
            case 14: return ReasonExitedType.DISCONTINUED_SCHOOLING_SPECIAL_EDUCATION_ONLY;
            case 15: return ReasonExitedType.SUSPENDED_FROM_SCHOOL;
            case 16: return ReasonExitedType.DISCONTINUED_SCHOOLING_NOT_SPECIAL_EDUCATION;
            case 17: return ReasonExitedType.EXPULSION;
            case 18: return ReasonExitedType.PROGRAM_DISCONTINUED;
            case 19: return ReasonExitedType.COMPLETION_OF_IFSP_PRIOR_TO_REACHING_MAXIMUM_AGE_FOR_PART_C;
            case 20: return ReasonExitedType.ELIGIBLE_FOR_IDEA_PART_B;
            case 21: return ReasonExitedType.NOT_ELIGIBLE_FOR_PART_B_EXIT_WITH_REFERRALS_TO_OTHER_PROGRAMS;
            case 22: return ReasonExitedType.PART_B_ELIGIBILITY_NOT_DETERMINED;
            case 23: return ReasonExitedType.MOVED_OUT_OF_STATE;
            case 24: return ReasonExitedType.WITHDRAWAL_BY_A_PARENT_OR_GUARDIAN;
            case 25: return ReasonExitedType.UNKNOWN_REASON;
            case 26: return ReasonExitedType.NOT_ELIGIBLE_FOR_PART_B_EXIT_WITH_NO_REFERRALS;
            case 27: return ReasonExitedType.ATTEMPTS_TO_CONTACT_THE_PARENT_AND_OR_CHILD_WERE_UNSUCCESSFUL;
            case 28: return ReasonExitedType.OTHER;
            default: return ReasonExitedType.OTHER;
        }
    }

    /**
     * Randomly generate a IncidentLocationType.
     *
     * @return <code>IncidentLocationType</code>
     */
    public static IncidentLocationType generateIncidentLocationType() {
        int roll = generator.nextInt(22);
        
        switch (roll) {
            case 0: return IncidentLocationType.ON_SCHOOL;
            case 1: return IncidentLocationType.ADMINISTRATIVE_OFFICES_AREA;
            case 2: return IncidentLocationType.CAFETERIA_AREA;
            case 3: return IncidentLocationType.CLASSROOM;
            case 4: return IncidentLocationType.HALLWAY_OR_STAIRS;
            case 5: return IncidentLocationType.LOCKER_ROOM_OR_GYM_AREAS;
            case 6: return IncidentLocationType.RESTROOM;
            case 7: return IncidentLocationType.LIBRARY_MEDIA_CENTER;
            case 8: return IncidentLocationType.COMPUTER_LAB;
            case 9: return IncidentLocationType.AUDITORIUM;
            case 10: return IncidentLocationType.ON_SCHOOL_OTHER_INSIDE_AREA;
            case 11: return IncidentLocationType.ATHLETIC_FIELD_OR_PLAYGROUND;
            case 12: return IncidentLocationType.STADIUM;
            case 13: return IncidentLocationType.PARKING_LOT;
            case 14: return IncidentLocationType.ON_SCHOOL_OTHER_OUTSIDE_AREA;
            case 15: return IncidentLocationType.OFF_SCHOOL;
            case 16: return IncidentLocationType.BUS_STOP;
            case 17: return IncidentLocationType.SCHOOL_BUS;
            case 18: return IncidentLocationType.WALKING_TO_OR_FROM_SCHOOL;
            case 19: return IncidentLocationType.OFF_SCHOOL_AT_OTHER_SCHOOL;
            case 20: return IncidentLocationType.OFF_SCHOOL_AT_OTHER_SCHOOL_DISTRICT_FACILITY;
            case 21: return IncidentLocationType.ONLINE;
            case 22: return IncidentLocationType.UNKNOWN;
            default: return IncidentLocationType.UNKNOWN;
        }
    }

    /**
     * Randomly generate a ReporterDescriptionType.
     *
     * @return <code>ReporterDescriptionType</code>
     */
    public static ReporterDescriptionType generateReporterDescriptionType () {
        int roll = generator.nextInt(3);
        switch (roll) {
            case 0: return ReporterDescriptionType.PARENT;
            case 1: return ReporterDescriptionType.STAFF;
            case 2: return ReporterDescriptionType.STUDENT;
            case 3: return ReporterDescriptionType.OTHER;
            default: return ReporterDescriptionType.OTHER;
        }
    }

    /**
     * Randomly generate a WeaponItemType.
     *
     * @return <code>WeaponItemType</code>
     */
    public static WeaponItemType generateWeaponItemType() {
        int roll = generator.nextInt(10);
        
        switch (roll) {
            case 0: return WeaponItemType.FIREARM;
            case 1: return WeaponItemType.ILLEGAL_KNIFE;
            case 2: return WeaponItemType.NON_ILLEGAL_KNIFE;
            case 3: return WeaponItemType.CLUB;
            case 4: return WeaponItemType.OTHER_SHARP_OBJECTS;
            case 5: return WeaponItemType.OTHER_OBJECT;
            case 6: return WeaponItemType.SUBSTANCE_USED_AS_WEAPON;
            case 7: return WeaponItemType.KNIFE;
            case 8: return WeaponItemType.UNKNOWN;
            case 9: return WeaponItemType.NONE;
            case 10: return WeaponItemType.OTHER;
            default: return WeaponItemType.OTHER;
        }
    }

    /**
     * Randomly generate a BehaviorCategoryType.
     *
     * @return <code>BehaviorCategoryType</code>
     */
    public static BehaviorCategoryType generateBehaviorCategoryType () {
        int roll = generator.nextInt(3);
        switch (roll) {
            case 0: return BehaviorCategoryType.STATE_LAW_CRIME;
            case 1: return BehaviorCategoryType.STATE_OFFENSE;
            case 2: return BehaviorCategoryType.SCHOOL_VIOLATION;
            case 3: return BehaviorCategoryType.SCHOOL_CODE_OF_CONDUCT;
            default: return BehaviorCategoryType.SCHOOL_CODE_OF_CONDUCT;
        }
    }

    /**
     * Randomly generate a DisciplineActionLengthDifferenceReasonType.
     *
     * @return <code>DisciplineActionLengthDifferenceReasonType</code>
     */
    public static DisciplineActionLengthDifferenceReasonType generateDisciplineActionLengthDifferenceReasonType() {
        int roll = generator.nextInt(10);
        
        switch (roll) {
            case 0: return DisciplineActionLengthDifferenceReasonType.NO_DIFFERENCE;
            case 1: return DisciplineActionLengthDifferenceReasonType.TERM_MODIFIED_BY_DISTRICT;
            case 2: return DisciplineActionLengthDifferenceReasonType.TERM_MODIFIED_BY_COURT_ORDER;
            case 3: return DisciplineActionLengthDifferenceReasonType.TERM_MODIFIED_BY_MUTUAL_AGREEMENT;
            case 4: return DisciplineActionLengthDifferenceReasonType.STUDENT_COMPLETED_TERM_REQUIREMENTS_SOONER_THAN_EXPECTED;
            case 5: return DisciplineActionLengthDifferenceReasonType.STUDENT_INCARCERATED;
            case 6: return DisciplineActionLengthDifferenceReasonType.TERM_DECREASED_DUE_TO_EXTENUATING_HEALTH_RELATED_CIRCUMSTANCES;
            case 7: return DisciplineActionLengthDifferenceReasonType.STUDENT_WITHDREW_FROM_SCHOOL;
            case 8: return DisciplineActionLengthDifferenceReasonType.SCHOOL_YEAR_ENDED;
            case 9: return DisciplineActionLengthDifferenceReasonType.CONTINUATION_OF_PREVIOUS_YEAR_S_DISCIPLINARY_ACTION_ASSIGNMENT;
            case 10: return DisciplineActionLengthDifferenceReasonType.TERM_MODIFIED_BY_PLACEMENT_PROGRAM_DUE_TO_STUDENT_BEHAVIOR_WHILE_IN_THE_PLACEMENT;
            case 11: return DisciplineActionLengthDifferenceReasonType.OTHER;
            default: return DisciplineActionLengthDifferenceReasonType.OTHER;
        }
    }

    /**
     * Randomly generate a StudentParticipationCodeType.
     *
     * @return <code>StudentParticipationCodeType</code>
     */
    public static StudentParticipationCodeType generateStudentParticipationCodeType () {
        int roll = generator.nextInt(3);
        switch (roll) {
            case 0: return StudentParticipationCodeType.VICTIM;
            case 1: return StudentParticipationCodeType.PERPETRATOR;
            case 2: return StudentParticipationCodeType.WITNESS;
            case 3: return StudentParticipationCodeType.REPORTER;
            default: return StudentParticipationCodeType.REPORTER;
        }
    }

}
