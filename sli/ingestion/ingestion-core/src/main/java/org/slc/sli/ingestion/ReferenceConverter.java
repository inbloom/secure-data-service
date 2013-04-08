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

package org.slc.sli.ingestion;


/**
 *
 */
public enum ReferenceConverter {

    STUDENT( "StudentReference", "student" ),
    PARENT( "ParentReference", "parent"),
    COURSE( "CourseReference", "course"),
    CALENDAR_DATE( "CalendarDateReference", "calendarDate"),
    EDORG( "EducationalOrgReference", "educationOrganization"),
    ASSESSMENT( "AssessmentReference", "assessment"),
    TEACHER( "TeacherReference", "teacher"),
    ASSESSMENTFAMILY( "AssessmentFamilyReference", "assessmentFamily"),
    SCHOOL( "SchoolReference", "school"),
    COHORT("CohortReference","cohort"),
    STUDENTASSESSMENT("StudentAssessmentReference","studentAssessment"),
    SECTION("SectionReference","section");

    /*
    CLASS_PERIOD("ClassPeriodReference", "classPeriod"),
    SCHOOL( "SchoolReference", "school"),
    GRADING_PERIOD( "GradingPeriodReference", "gradingPeriod"),
    SESSION( "SessionReference", "session"),
    COURSE_OFFERING(""),
    STUDENT_GRADES(),

    /*
    ATTENDANCE("AttendanceRefernece", "attendance"), // !!!!
    PERFORMANCE_LEVEL( "PerformanceLevelReference", "???"),  // !!!!
    */



    // *******************************************************************************************************



    private final String referenceName;
    private final String entityName;

    private ReferenceConverter(String referenceName, String entityName) {
        this.referenceName = referenceName;
        this.entityName = entityName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getEntityName() {
        return entityName;
    }


    public static boolean isReferenceType( String typeName ) {
        return( (typeName != null && typeName.endsWith(REFERENCE )) ? true : false );
    }

    public static ReferenceConverter fromReferenceName(String refName) {

        ReferenceConverter found = null;
        for (ReferenceConverter converter : values()) {
            if (converter.getReferenceName().equals(refName)) {
                found = converter;
                break;
            }
        }
        return found;
    }

    private static final String REFERENCE="Reference";
}
