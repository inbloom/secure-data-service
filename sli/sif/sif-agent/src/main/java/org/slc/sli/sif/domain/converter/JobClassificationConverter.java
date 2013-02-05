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

import openadk.library.hrfin.JobClassification;
import openadk.library.hrfin.JobClassificationCode;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF JobClassification to SLI staffClassificationType enumeration.
 *
 * SLI values:
 *   Art Therapist 
 *   Athletic Trainer (Mapped from ATHLETIC_TRAINER)
 *   Assistant Principal 
 *   Assistant Superintendent 
 *   Certified Interpreter (Mapped from INTERPRETER)
 *   Counselor (Mapped from COUNSELOR)
 *   High School Counselor 
 *   Instructional Coordinator 
 *   Instructional Aide (Mapped from TEACHING_CLASSROOM_AIDE)
 *   Librarians/Media Specialists (Mapped from LIBRARIAN_MEDIA_CONSULTANT)
 *   Librarian 
 *   Principal  (Mapped from PRINCIPAL_HEADMASTER_HEADMISTRESS)
 *   Physical Therapist (Mapped from PHYSICAL_THERAPIST, RESPIRATORY_THERAPIST)
 *   Teacher (Mapped from TEACHER)
 *   Other 
 *   Superintendent (Mapped from SUPERINTENDENT_COMMISSIONER)
 *   School Nurse (Mapped from NURSE_PRACTITIONER, LICENSED_PRACTICAL_NURSE, REGISTERED_NURSE)
 *   Specialist/Consultant 
 *   Student Support Services Staff 
 *   School Leader 
 *   Substitute Teacher
 *   LEA Administrator 
 *   LEA Specialist 
 *   LEA System Administrator 
 *   LEA Administrative Support Staff 
 *   School Administrator (Mapped from OFFICIAL_ADMINISTRATIVE) 
 *   School Specialist 
 *   School Administrative Support Staff (Mapped from OFFICE_CLERICAL_ADMINISTRATIVE)
 */
@Component
public class JobClassificationConverter {

    private static final Map<JobClassificationCode, String> JOB_CLASSIFICATION_MAP = new HashMap<JobClassificationCode, String>();
    static {
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SECURITY_GUARD, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SPEECH_LANGUAGE_TECHNICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COMPUTER_TECHNICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CARPENTER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TEACHING_ASSISTANT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OPHTHALMOLOGIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.REHABILITATION_COUNSELOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.HISTORIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CONSTRUCTION_LABORER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DIETITIAN_NUTRITIONIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ELECTRICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PRINTER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DEPUTY_ASSOCIATE_VICE__ASSISTA, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COUNSELOR, "Counselor");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PLUMBER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.EXTRA_CURRICULAR_ACTIVITY_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PLANNING_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PSYCHOMETRIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.NURSE_PRACTITIONER, "School Nurse");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.WRITER_EDITOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MEDIA_TECHNOLOGIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PHYSICAL_THERAPIST, "Physical Therapist");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MAIL_CLERK, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.REMEDIAL_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MEDIA_CENTER_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CAREER_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PAINTER_AND_PAPERHANGER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FILE_CLERK, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LIBRARY_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.EDUCATION_DIAGNOSTICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CASEWORKER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TRANSITION_COORDINATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LICENSED_PRACTICAL_NURSE, "School Nurse");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GRAPHIC_ARTIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SWITCHBOARD_PBX_OPERATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TAX_ASSESSOR_COLLECTOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ACCOUNTANT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FINANCIAL_AID_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MANAGER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CURATOR_AND_ARCHIVIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OMBUDSPERSON, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OCCUPATIONAL_THERAPIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TEACHER_TRAINER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BILINGUAL_SPECIAL_EDUCATION_AI, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.EVALUATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ADMINISTRATIVE_INTERN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GARBAGE_COLLECTOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.INSTRUCTIONAL_PROGRAM_DIRECTOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OPTOMETRIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COMMANDANT_OF_CADETS, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SECRETARY, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.REGISTRAR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PRINCIPAL_HEADMASTER_HEADMISTRESS, "Principal");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OTHER_VEHICLE_OPERATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RECREATIONAL_THERAPIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SUPERINTENDENT_COMMISSIONER, "Superintendent");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DENTAL_HYGIENIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SUPERVISOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.VEHICLE_MECHANIC, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.VOLUNTEER_COORDINATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GARAGE_PARKINGLOT_ATTENDANT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PROFESSIONAL_OTHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GARDENER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SPEECH_PATHOLOGIST_THERAPIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STATISTICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DENTIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ADMISSIONS_OFFICER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RECREATION_WORKER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RESIDENT_DORMITORY_SUPERVISOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ENGINEERING_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GENERAL_OFFICE_CLERK, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CHILD_CARE_GIVER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PSYCHOLOGIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CAMPUS_MINISTER_CHAPLAIN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TUTOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STUDENT_ACTIVITY_ADVISOR_NON_A, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RESPIRATORY_THERAPIST, "Physical Therapist");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PHOTOGRAPHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FINANCIAL_AID_OFFICER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PHYSICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.POLICE_OFFICER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TEACHING_CLASSROOM_AIDE, "Instructional Aide");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.AUDIOLOGIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DEPUTY_ASSOCIATE_ASSISTANT_SUP, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FOOD_SERVER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ELEVATOR_OPERATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BUS_MONITOR_CROSSING_GUARD, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STORES_SUPPLIES_HANDLER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COOK_FOOD_PREPARER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RESOURCE_TEACHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OTHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BILINGUAL_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TECHNICAL, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FACILITIES_MAINTENANCE_WORKER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LEGISLATIVE_LIAISON, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MAINTENANCE_REPAIRERS_GENERAL_, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ASSISTANT_COUNSELOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.REGISTERED_NURSE, "School Nurse");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BRICK_MASON, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CASHIER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BEHAVIORAL_MANAGEMENT_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LOCKSMITH, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.INTERPRETER, "Certified Interpreter");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TRANSLATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DUPLICATING_PHOTOCOPYING_ASSIS, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STUDENT_TEACHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.EXTENDED_DAY_CARE_PROVIDER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MONITOR_PREFECT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PSYCHOLOGIST_ASSISTANT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STENOGRAPHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RECEPTIONIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ATTENDANCE_OFFICER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COMPUTER_PROGRAMMER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LIBRARIAN_MEDIA_CONSULTANT, "Librarians/Media Specialists");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.NEGOTIATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.WORK_STUDY_COORDINATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PLASTERER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.HVAC_MECHANIC, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COMPUTER_OPERATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BOARD_OF_EDUCATION_SCHOOL_BOAR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ANALYST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PSYCHIATRIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.EXECUTIVE_ASSISTANT_PERFORMS_P, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COMPUTER_AIDE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OFFICE_MANAGER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ATHLETIC_COACH, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CEMENT_MASON, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MEDIATOR_INTERVENTION_SPECIALI, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PUBLIC_RELATIONS_INFORMATIONAL, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.INTERNAL_AUDITOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TEACHER, "Teacher");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.AUDIOMETRIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.INSPECTOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SERVICE_WORK, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ENGINEER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.AUDITOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STAFF_DEVELOPER_TEACHER_TRAINE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FREIGHT_STOCK_AND_MATERIALS_, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RECORDS_CLERK, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ARCHITECT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DIETARY_TECHNICIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GRANT_DEVELOPER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BUS_DRIVER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BOOKKEEPING_ACCOUNTING_AUDITIN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PARAPROFESSIONALS, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TEACHING_INTERN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DATA_ENTRY_CLERK, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.VEHICLE_WASHER_EQUIPMENT_CLEAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.NETWORK_ADMINISTRATOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PERSONNEL_OFFICER_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PURCHASING_AGENT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FAMILY_COMMUNITY_SUPPORT_COORD, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SCHOOL_PRESIDENT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.NON_INSTRUCTIONAL_PROGRAM_DIR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CURRICULUM_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SOCIAL_WORKER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CRAFTS_AND_TRADES, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.BENEFITS_SPECIALIST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ACCREDITATION_OFFICER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DISPATCHER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CHILD_CARE_WORKER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DRAFTER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.RESEARCH_AND_DEVELOPMENT_SPECI, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.COMPUTER_SYSTEMS_ANALYST, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.FUNCTIONAL_APPLICATION_SUPPORT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.MESSENGER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.SCHOOL_SITE_COUNCIL_MEMBER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.STUDENT_PERSONNEL_OFFICER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.TYPIST_AND_WORDPROCESSOR, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OPERATIVE, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ELECTRICAL_AND_ELECTRONIC_REPA, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PHYSICIAN_ASSISTANT, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.DEAN_DEAN_OF_INSTRUCTIONS_DEAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ATHLETIC_TRAINER, "Athletic Trainer");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.GROUNDSKEEPER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.CUSTODIAN, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LAWYER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.ADMINISTRATIVE_SUPERVISORY, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.PROFESSIONAL_EDUCATIONAL, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.LABORER, "Other");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OFFICIAL_ADMINISTRATIVE, "School Administrator");
        JOB_CLASSIFICATION_MAP.put(JobClassificationCode.OFFICE_CLERICAL_ADMINISTRATIVE, "School Administrative Support Staff");
    }

    /**
     * Converts a SIF JobClassification into SLI staffClassificationType enum. 
     * 
     * @param jobClassification - the jobClassification
     * @param edorgType - whether the associated staff is at a school or lea
     * @return
     */
    public String convert(JobClassification jobClassification) {
        if (jobClassification == null) {
            return null;
        }
        return toSliEntryType(JobClassificationCode.wrap(jobClassification.getCode()));
    }

    private String toSliEntryType(JobClassificationCode entryTypeCode) {
        String mapping = null; 
        mapping = JOB_CLASSIFICATION_MAP.get(entryTypeCode);
        return mapping == null ? "Other" : mapping;
    }
}
