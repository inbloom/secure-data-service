package org.slc.sli.ingestion;

/**
 * File Type enumerator.
 *
 * @author okrook
 *
 */
public enum FileType {

    XML_ASSESSMENT_METADATA("AssessmentMetadata", FileFormat.EDFI_XML),
    XML_STUDENT_ATTENDANCE("Attendance", FileFormat.EDFI_XML),
    XML_EDUCATION_ORGANIZATION("EducationOrganization", FileFormat.EDFI_XML),
    XML_EDUCATION_ORG_CALENDAR("EducationOrgCalendar", FileFormat.EDFI_XML),
    XML_MASTER_SCHEDULE("MasterSchedule", FileFormat.EDFI_XML),
    XML_STAFF_ASSOCIATION("StaffAssociation", FileFormat.EDFI_XML),
    XML_STUDENT_ASSESSMENT("StudentAssessment", FileFormat.EDFI_XML),
    XML_STUDENT_COHORT("StudentCohort", FileFormat.EDFI_XML),
    XML_STUDENT_DISCIPLINE("StudentDiscipline", FileFormat.EDFI_XML),
    XML_STUDENT_ENROLLMENT("StudentEnrollment", FileFormat.EDFI_XML),
    XML_STUDENT_GRADES("StudentGrades", FileFormat.EDFI_XML),
    XML_STUDENT_PARENT_ASSOCIATION("StudentParent", FileFormat.EDFI_XML),
    XML_STUDENT_PROGRAM("StudentProgram", FileFormat.EDFI_XML);

    private final String name;
    private final FileFormat fileFormat;

    FileType(String name, FileFormat fileFormat) {
        this.name = name;
        this.fileFormat = fileFormat;
    }

    public String getName() {
        return name;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public static FileType findByNameAndFormat(String name, FileFormat fileFormat) {
        if (name != null) {
            for (FileType ft : FileType.values()) {
                if (ft.getName().toLowerCase().equals(name.toLowerCase()) && ft.getFileFormat().equals(fileFormat)) {
                    return ft;
                }
            }
        }
        return null;
    }


}
