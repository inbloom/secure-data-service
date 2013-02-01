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


package org.slc.sli.dashboard.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used for adding test data to a student.
 * 
 * @author dwalker
 *
 */
public class StudentSummaryBuilder {
    
    // Section Ids
    private static final String EIGHTH_GRADE_ENGLISH_SEC_ID = "fc4de89d-534e-4ae7-ae3c-b4a536e1a4ac";
    
    // Course IDs
    private static final String SEVENTH_GRADE_ENGLISH_COURSE_ID = "5fac98fc-62a4-49d1-9417-11b0823a2e7a";
    private static final String SEVENTH_GRADE_COMPOSITION_COURSE_ID = "9eb8fe49-a08a-4722-a6d4-fe1d2ec69ddd";
    
    private static Map<String, Object> createSeventhGradeEnglishTranscript() {
        Map<String, Object> studentTranscript = new LinkedHashMap<String, Object>();
        studentTranscript.put("id", "dc41d13b-9f8c-486d-b720-6b3c8a761f06");
        studentTranscript.put("finalLetterGradeEarned", "F");
        studentTranscript.put("courseId", SEVENTH_GRADE_ENGLISH_COURSE_ID);
        return studentTranscript;
    }
    
    private static Map<String, Object> createSeventhGradeCompositionTranscript() {
        Map<String, Object> studentTranscript = new LinkedHashMap<String, Object>();
        studentTranscript.put("id", "b66f4677-c250-4ad9-a318-cf77ccefc651");
        studentTranscript.put("finalLetterGradeEarned", "D-");
        studentTranscript.put("courseId", SEVENTH_GRADE_COMPOSITION_COURSE_ID);
        return studentTranscript;
    }
    
    private static Map<String, Object> createSeventhGradeEnglishSection() {
        // 7th Grade English Section 6
        Map<String, Object> sessions = new LinkedHashMap<String, Object>();
        sessions.put("id", "432f3ed2-f413-4ed0-82f3-d2ba00c5b61a");
        sessions.put("schoolYear", "2010-2011");
        sessions.put("sessionName", "Fall 2010 East Daybreak Junior High");
        sessions.put("term", "Fall Semester");
        sessions.put("endDate", "2010-12-16");
        sessions.put("beginDate", "2010-09-06");
        
        Map<String, Object> courses = new LinkedHashMap<String, Object>();
        courses.put("id", "5fac98fc-62a4-49d1-9417-11b0823a2e7a");
        courses.put("subjectArea", "English Language and Literature");
        courses.put("courseTitle", "7th Grade English");
        
        Map<String, Object> sections = new LinkedHashMap<String, Object>();
        sections.put("sessions", sessions);
        sections.put("courses", courses);
        sections.put("courseId", SEVENTH_GRADE_ENGLISH_COURSE_ID);
        sections.put("sessionId", "432f3ed2-f413-4ed0-82f3-d2ba00c5b61a");
        
        Map<String, Object> sectionAssoc = new LinkedHashMap<String, Object>();
        sectionAssoc.put("id", "a38a9d24-26c8-4f56-9af8-bacac6fc445d");
        sectionAssoc.put("sections", sections);
        sectionAssoc.put("sectionId", "3bca9b7d-03b5-4bdd-8e5e-b225a78b9e4e");
        return sectionAssoc;
    }
    
    private static Map<String, Object> createEighthGradeEnglishSection() {
        Map<String, Object> sessions = new LinkedHashMap<String, Object>();
        sessions.put("id", "c689b232-b075-4bae-a579-8c7dc0f471cb");
        sessions.put("schoolYear", "2011-2012");
        sessions.put("sessionName", "Fall 2011 East Daybreak Junior High");
        sessions.put("term", "Fall Semester");
        sessions.put("endDate", "2011-12-16");
        sessions.put("beginDate", "2011-09-06");
        
        Map<String, Object> courses = new LinkedHashMap<String, Object>();
        courses.put("id", "48e8e237-1039-455f-a5ee-d3cc188ceac8");
        courses.put("subjectArea", "English Language and Literature");
        courses.put("courseTitle", "8th Grade English");
        
        Map<String, Object> sections = new LinkedHashMap<String, Object>();
        sections.put("sessions", sessions);
        sections.put("courses", courses);
        sections.put("courseId", "48e8e237-1039-455f-a5ee-d3cc188ceac8");
        sections.put("sessionId", "c689b232-b075-4bae-a579-8c7dc0f471cb");
        
        Map<String, Object> sectionAssoc = new LinkedHashMap<String, Object>();
        sectionAssoc.put("id", "db7836e8-97b4-4079-9971-63a741403e43");
        sectionAssoc.put("sections", sections);
        sectionAssoc.put("sectionId", EIGHTH_GRADE_ENGLISH_SEC_ID);
        return sectionAssoc;
    }
    
    public static Map<String, Object> createSeventhGradeCompositionSection() {
        Map<String, Object> sessions = new LinkedHashMap<String, Object>();
        sessions.put("id", "bf67a1cb-c7df-40b2-bfa8-99a0691e8c09");
        sessions.put("schoolYear", "2010-2011");
        sessions.put("sessionName", "Spring 2011 East Daybreak Junior High");
        sessions.put("term", "Spring Semester");
        sessions.put("endDate", "2011-05-16");
        sessions.put("beginDate", "2011-01-06");
        
        Map<String, Object> courses = new LinkedHashMap<String, Object>();
        courses.put("id", "9eb8fe49-a08a-4722-a6d4-fe1d2ec69ddd");
        courses.put("subjectArea", "English Language and Literature");
        courses.put("courseTitle", "7th Grade Composition");
        
        Map<String, Object> sections = new LinkedHashMap<String, Object>();
        sections.put("sessions", sessions);
        sections.put("courses", courses);
        sections.put("courseId", SEVENTH_GRADE_COMPOSITION_COURSE_ID);
        sections.put("sessionId", "bf67a1cb-c7df-40b2-bfa8-99a0691e8c09");
        
        Map<String, Object> sectionAssoc = new LinkedHashMap<String, Object>();
        sectionAssoc.put("id", "d720837a-ee94-4add-bd4b-7d4176b38d05");
        sectionAssoc.put("sections", sections);
        sectionAssoc.put("sectionId", "bcdc582d-149d-449f-a06a-9a3181e6bb97");
        return sectionAssoc;
    }
    
    public static void addFullDetailTranscripts(Map<String, Object> student) {
        // StudentTranscripts
        List<Map<String, Object>> courseTranscripts = new ArrayList<Map<String, Object>>();
        courseTranscripts.add(createSeventhGradeEnglishTranscript());
        courseTranscripts.add(createSeventhGradeCompositionTranscript());
        
        // StudentSections
        List<Map<String, Object>> studentSectionAssociations = new ArrayList<Map<String, Object>>();
        studentSectionAssociations.add(createSeventhGradeEnglishSection());
        studentSectionAssociations.add(createEighthGradeEnglishSection());
        studentSectionAssociations.add(createSeventhGradeCompositionSection());
        
        Map<String, Object> transcripts = new LinkedHashMap<String, Object>();
        transcripts.put("courseTranscripts", courseTranscripts);
        transcripts.put("studentSectionAssociations", studentSectionAssociations);
        student.put("transcript", transcripts);
    }
    
    public static void addNullTranscripts(Map<String, Object> student, String sectionId) {
        student.put("transcript", null);
    }
    
    public static void addNullStudentSectionAssociations(Map<String, Object> student, String sectionId) {
        Map<String, Object> transcripts = new LinkedHashMap<String, Object>();
        transcripts.put("studentSectionAssociations", null);
        student.put("transcript", transcripts);
    }
    
    public static void addRealGradeBookEntries(Map<String, Object> student) {
        
        // Letter Grade Passed
        List<Map<String, Object>> gradeBookEntries = new ArrayList<Map<String, Object>>();
        Map<String, Object> entry = new LinkedHashMap<String, Object>();
        entry.put("id", "ad3ea581-9b27-4c38-97ee-480a44e1147e");
        entry.put("gradebookEntryId", "6e42d32c-2be3-45de-97fe-894d4c065aa2");
        entry.put("sectionId", "da5b4d1a-63a3-46d6-a4f1-396b3308af83");
        entry.put("letterGradeEarned", "C");
        entry.put("studentId", "0d563d12-3d0c-4601-adb6-2da746d78bd5");
        Map<String, Object> details = new LinkedHashMap<String, Object>();
        details.put("id", "6e42d32c-2be3-45de-97fe-894d4c065aa2");
        details.put("dateAssigned", "2011-09-29"); // dateAssigned=2011-09-29,
        entry.put("gradebookEntries", details);
        entry.put("dateFulfilled", "2011-09-29");
        gradeBookEntries.add(entry);
        
        // Numeric Grade Passed
        entry = new LinkedHashMap<String, Object>();
        entry.put("id", "63c71862-adf6-465f-846b-8effde9e764c");
        entry.put("gradebookEntryId", "00f627d7-1ccd-4c63-a1b3-64e104ec73de");
        entry.put("sectionId", "da5b4d1a-63a3-46d6-a4f1-396b3308af83");
        entry.put("letterGradeEarned", null);
        entry.put("numericGradeEarned", 73.0);
        entry.put("studentId", "0d563d12-3d0c-4601-adb6-2da746d78bd5");
        details = new LinkedHashMap<String, Object>();
        details.put("id", "00f627d7-1ccd-4c63-a1b3-64e104ec73de");
        details.put("dateAssigned", "2011-10-27"); // dateAssigned=2011-09-29,
        entry.put("gradebookEntries", details);
        entry.put("dateFulfilled", "2011-10-27");
        gradeBookEntries.add(entry);
        
        // No grade passed case
        entry = new LinkedHashMap<String, Object>();
        entry.put("id", "63c71862-adf6-465f-846b-8effde9e764d");
        entry.put("gradebookEntryId", "00f627d7-1ccd-4c63-a1b3-64e104ec73df");
        entry.put("sectionId", "da5b4d1a-63a3-46d6-a4f1-396b3308af83");
        entry.put("letterGradeEarned", null);
        entry.put("numericGradeEarned", null);
        entry.put("studentId", "0d563d12-3d0c-4601-adb6-2da746d78bd5");
        details = new LinkedHashMap<String, Object>();
        details.put("id", "00f627d7-1ccd-4c63-a1b3-64e104ec73df");
        details.put("dateAssigned", "2011-10-28"); // dateAssigned=2011-09-29,
        entry.put("gradebookEntries", details);
        entry.put("dateFulfilled", "2011-10-28");
        gradeBookEntries.add(entry);
        
        // Bad Date passed case
        entry = new LinkedHashMap<String, Object>();
        entry.put("id", "63c71862-adf6-465f-846b-8effde9e764g");
        entry.put("gradebookEntryId", "00f627d7-1ccd-4c63-a1b3-64e104ec73dg");
        entry.put("sectionId", "da5b4d1a-63a3-46d6-a4f1-396b3308af83");
        entry.put("letterGradeEarned", "B");
        entry.put("numericGradeEarned", null);
        entry.put("studentId", "0d563d12-3d0c-4601-adb6-2da746d78bd5");
        details = new LinkedHashMap<String, Object>();
        details.put("id", "00f627d7-1ccd-4c63-a1b3-64e104ec73dg");
        details.put("dateAssigned", "2011-10-28"); // dateAssigned=2011-09-29,
        entry.put("gradebookEntries", details);
        entry.put("dateFulfilled", "dfafsadadsf");
        gradeBookEntries.add(entry);
        
        student.put("studentGradebookEntries", gradeBookEntries);
    }
}
