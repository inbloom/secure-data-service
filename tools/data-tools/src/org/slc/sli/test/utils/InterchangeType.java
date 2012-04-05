package org.slc.sli.test.utils;

/**
 * File Type enumerator.
 *
 */
public enum InterchangeType {
    
    STUDENT("Student"),
    EDUCATION_ORGANIZATION("EducationOrganization"),
    EDUCATION_ORG_CALENDAR("EducationOrgCalendar"),
    MASTER_SCHEDULE("MasterSchedule"),
    STAFF_ASSOCIATION("StaffAssociation"),
    STUDENT_ENROLLMENT("StudentEnrollment"),
    ASSESSMENT_METADATA("AssessmentMetadata"),
    STUDENT_ASSESSMENT("StudentAssessment"),
    STUDENT_ATTENDANCE("Attendance"),
    STUDENT_GRADES("StudentGrades"),
    STUDENT_PARENT_ASSOCIATION("Parent");

    private String name;

    InterchangeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
