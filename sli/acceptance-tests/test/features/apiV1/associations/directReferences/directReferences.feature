@RALLY_US209
@RALLY_US1803
@RALLY_US1804
@RALLY_US1961
@RALLY_DE85
@RALLY_DE87
Feature: As an SLI application, I want entity references displayed as usable links with exlicit link names
This means that when links are requested in a GET request, a link should be present that represents each reference field
and that the links are valid

Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

Scenario Outline: Control the presence of links by specifying an accept type format
   Given format <format>
     And I request <return links>
     And I navigate to GET "/<URI FOR ENTITY THAT CAN RETURN LINKS>/<ID OF ENTITY THAT CAN RETURN LINKS>"
    Then I should receive a return code of 200
     And the response should contain links if I requested them
    Examples:
        | format                     | return links |
        | "application/json"         | "links"      |
        | "application/vnd.slc+json" | "links"      |

Scenario Outline: Confirm all known reference fields generate two valid links that are implemented and update-able
   Given format "application/vnd.slc+json"
     And referring collection <source entity type> exposed as <source expose name>
     And referring field <reference field> with value <reference value>
     And referred collection <target entity type> exposed as <target expose name>
     And the link from references to referred entity is <target link name>
     And the link from referred entity to referring entities is <source link name>
     And the ID to use for testing is <testing ID>
     And the ID to use for testing a valid update is <new valid value>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a link named <target link name>
    When I navigate to GET the link named <target link name>
    Then I should receive a return code of 200
     And "entityType" should be <target entity type>
     And I should receive a link named <source link name>
    When I navigate to GET the link named <source link name>
    Then I should receive a return code of 200
     And each entity's "entityType" should be <source entity type>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
     And I set the <reference field> to "<INVALID REFERENCE>"
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 403
    When I set the <reference field> to <new valid value>
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then <reference field> should be <new valid value>
     And I should receive a link named <target link name>
    When I navigate to GET the link named <target link name>
    Then "id" should be "<NEW VALID VALUE>"
     And "entityType" should be <target entity type>
    Examples:
        | source entity type                      | source expose name                       | reference field                  | target entity type      | target expose name       | target link name           | source link name                            | testing ID                             | reference value                        | new valid value                        |
        | "educationOrganization"                 | "educationOrganizations"                 | "parentEducationAgencyReference" | "educationOrganization" | "educationOrganizations" | "getParentEducationOrganization"  | "getFeederEducationOrganizations" | "b2c6e292-37b0-4148-bf75-c98a2fcc905f" | "b1bd3db6-d020-4651-b1b8-a8dba688d9e1" | "bd086bae-ee82-4cf2-baf9-221a9407ea07" |
        | "section"                               | "sections"                               | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getSections"                               | "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" | "1cb50f82-7200-441a-a1b6-02d6532402a0" | "62101257-592f-4cbe-bcd5-b8cd24a06f73" |
        | "section"                               | "sections"                               | "courseOfferingId"               | "courseOffering"        | "courseOfferings"        | "getCourseOffering"        | "getSections"                               | "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" | "01ba881f-ae39-4b76-920e-42bc7e8769d7" | "c5b80f7d-93c5-11e1-adcc-101f74582c4c" |
        | "studentGradebookEntry"                 | "studentGradebookEntries"                | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentGradebookEntries"                | "0f5e6f78-5434-f906-e51b-d63ef970ef8f" | "5738d251-dd0b-4734-9ea6-417ac9320a15_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |
        | "studentGradebookEntry"                 | "studentGradebookEntries"                | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getStudentGradebookEntries"                | "0f5e6f78-5434-f906-e51b-d63ef970ef8f" | "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" | "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id" |

Scenario Outline: Confirm all known reference fields generate two valid links that are implemented and the natural keys are not update-able
   Given format "application/vnd.slc+json"
     And referring collection <source entity type> exposed as <source expose name>
     And referring field <reference field> with value <reference value>
     And referred collection <target entity type> exposed as <target expose name>
     And the link from references to referred entity is <target link name>
     And the link from referred entity to referring entities is <source link name>
     And the ID to use for testing is <testing ID>
     And the ID to use for testing a valid update is <new valid value>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a link named <target link name> with URI "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    When I navigate to GET "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    Then I should receive a return code of 200
     And "entityType" should be <target entity type>
     And I should receive a link named <source link name> with URI "/<URI OF ENTITIES THAT REFER TO TARGET>"
    When I navigate to GET "/<URI OF ENTITIES THAT REFER TO TARGET>"
    Then I should receive a return code of 200
     And each entity's "entityType" should be <source entity type>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
     And I set the <reference field> to "<INVALID REFERENCE>"
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 403
    When I set the <reference field> to <new valid value>
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 409
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then <reference field> should be <reference value>
     And I should receive a link named <target link name> with URI "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    When I navigate to GET "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    Then "id" should be "<REFERRED ENTITY ID>"
     And "entityType" should be <target entity type>
 Examples:
        | source entity type                      | source expose name                       | reference field                  | target entity type          | target expose name           | target link name                 | source link name             | testing ID                             | reference value                        | new valid value                        |
        | "reportCard"                            | "reportCards"                            | "studentId"                      | "student"                   | "students"                   | "getStudent"                     | "getReportCards"             | "cf0ca1c6-a9db-4180-bf23-8276c4e2624c" | "74cf790e-84c4-4322-84b8-fca7206f1085_id" | "41df2791-b33c-4b10-8de6-a24963bbd3dd_id" |
        | "reportCard"                            | "reportCards"                            | "gradingPeriodId"                | "gradingPeriod"             | "gradingPeriods"             | "getGradingPeriod"               | "getReportCards"             | "cf0ca1c6-a9db-4180-bf23-8276c4e2624c" | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "ef72b883-90fa-40fa-afc2-4cb1ae17623b" |
        | "grade"                                 | "grades"                                 | "studentSectionAssociationId"    | "studentSectionAssociation" | "studentSectionAssociations" | "getStudentSectionAssociation"   | "getGrades"                  | "708c4e08-9942-11e1-a8a9-68a86d21d918" | "e626922f-0393-4dc3-bfac-eeebcf47577e_id7ba1a2e9-989c-4b00-b5e0-9bf3b72c909d_id" | "11289b8a-71e0-4825-b705-5a8d29747e1b_id205786da-f3a1-470d-86a1-2dabff9b2146_id" |
        | "grade"                                 | "grades"                                 | "gradingPeriodId"                | "gradingPeriod"             | "gradingPeriods"             | "getGradingPeriod"               | "getGrades"                  | "708c4e08-9942-11e1-a8a9-68a86d21d918" | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "ef72b883-90fa-40fa-afc2-4cb1ae17623b" |
#        | "studentCompetency"                     | "studentCompetencies"                    | "objectiveId.learningObjectiveId"            | "learningObjective"         | "learningObjectives"         | "getLearningObjective"           | "getStudentCompetencies"     | "b57643e4-9acf-11e1-89a7-68a86d21d918" | "dd9165f2-65be-6d27-a8ac-bdc5f46757b6" | "dd9165f2-65fe-6d27-a8ec-bdc5f47757b7" |
        | "studentCompetency"                     | "studentCompetencies"                    | "studentSectionAssociationId"    | "studentSectionAssociation" | "studentSectionAssociations" | "getStudentSectionAssociation"   | "getStudentCompetencies"     | "b57643e4-9acf-11e1-89a7-68a86d21d918" | "e626922f-0393-4dc3-bfac-eeebcf47577e_id7ba1a2e9-989c-4b00-b5e0-9bf3b72c909d_id" | "11289b8a-71e0-4825-b705-5a8d29747e1b_id205786da-f3a1-470d-86a1-2dabff9b2146_id" |
        | "studentAcademicRecord"                 | "studentAcademicRecords"                 | "studentId"                      | "student"                   | "students"                   | "getStudent"                     | "getStudentAcademicRecords"  | "56afc8d4-6c91-48f9-8a51-de527c1131b7" | "74cf790e-84c4-4322-84b8-fca7206f1085_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |
        | "studentAcademicRecord"                 | "studentAcademicRecords"                 | "sessionId"                      | "session"                   | "sessions"                   | "getSession"                     | "getStudentAcademicRecords"  | "56afc8d4-6c91-48f9-8a51-de527c1131b7" | "fb0ac9e8-9e4e-48a0-95d2-ae07ee15ee92" | "62101257-592f-4cbe-bcd5-b8cd24a06f73" |
        | "attendance"                            | "attendances"                            | "studentId"                      | "student"                   | "students"                   | "getStudent"                     | "getAttendances"             | "530f0704-c240-4ed9-0a64-55c0308f91ee" | "74cf790e-84c4-4322-84b8-fca7206f1085_id" | "5738d251-dd0b-4734-9ea6-417ac9320a15_id" |
        | "gradebookEntry"                        | "gradebookEntries"                       | "sectionId"                      | "section"                   | "sections"                   | "getSection"                     | "getGradebookEntries"        | "706ee3be-0dae-4e98-9525-f564e05aa388_ide49dc00c-182d-4f22-98c5-3d35b5f6d993_id" | "706ee3be-0dae-4e98-9525-f564e05aa388_id" | "15ab6363-5509-470c-8b59-4f289c224107_id" |
        | "section"                               | "sections"                               | "schoolId"                       | "school"                    | "schools"                    | "getSchool"                      | "getSections"                | "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" | "ec2e4218-6483-4e9c-8954-0aecccfd4731" | "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" |
        | "studentGradebookEntry"                 | "studentGradebookEntries"                | "gradebookEntryId"               | "gradebookEntry"            | "gradebookEntries"           | "getGradebookEntry"              | "getStudentGradebookEntries" | "0f5e6f78-5434-f906-e51b-d63ef970ef8f" | "706ee3be-0dae-4e98-9525-f564e05aa388_id0b980a49-56b6-4d17-847b-2997b7227686_id" | "706ee3be-0dae-4e98-9525-f564e05aa388_ide49dc00c-182d-4f22-98c5-3d35b5f6d993_id" |
        | "cohort"                                | "cohorts"                                | "educationOrgId"                 | "educationOrganization"     | "educationOrganizations"     | "getEducationOrganization"       | "getCohorts"                 | "b408635d-8fd5-11e1-86ec-0021701f543f_id" | "b1bd3db6-d020-4651-b1b8-a8dba688d9e1" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" |

Scenario Outline: Confirm all association generate one valid links that is implemented and update-able
   Given format "application/vnd.slc+json"
     And referring collection <source entity type> exposed as <source expose name>
     And referring field <reference field> with value <reference value>
     And referred collection <target entity type> exposed as <target expose name>
     And the link from references to referred entity is <target link name>
     And the link from referred entity to referring entities is <source link name>
     And the ID to use for testing is <testing ID>
     And the ID to use for testing a valid update is <new valid value>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a link named <target link name>
    When I navigate to GET the link named <target link name>
    Then I should receive a return code of 200
     And "entityType" should be <target entity type>
    When I navigate to GET "/<URI OF ENTITIES THAT REFER TO TARGET>"
    Then I should receive a return code of 200
     And each entity's "entityType" should be <source entity type>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
     And I set the <reference field> to "<INVALID REFERENCE>"
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 403
    When I set the <reference field> to <new valid value>
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then <reference field> should be <new valid value>
     And I should receive a link named <target link name>
    When I navigate to GET the link named <target link name>
    Then "id" should be "<NEW VALID VALUE>"
     And "entityType" should be <target entity type>
    Examples:
        | source entity type                      | source expose name                       | reference field                  | target entity type      | target expose name       | target link name           | source link name                            | testing ID                             | reference value                        | new valid value                        |
        | "courseOffering"                        | "courseOfferings"                        | "courseId"                       | "course"                | "courses"                | "getCourse"                | "getCourseOfferings"                        | "b360e8e8-54d1-4b00-952a-b817f91035ed" | "52038025-1f18-456a-884e-d2e63f9a02f4" | "75de6e74-a2e0-47da-ad1e-df1c833af92c" |
        | "courseTranscript"                      | "courseTranscripts"                      | "studentId"                      | "student"               | "students"               | "getStudent"               | "getCourseTranscripts"                      | "36aeeabf-ee9b-46e6-8039-13320bf15226" | "5738d251-dd0b-4734-9ea6-417ac9320a15_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |

Scenario Outline: Confirm all association generate one valid links that is implemented and natural keys are not update-able
   Given format "application/vnd.slc+json"
     And referring collection <source entity type> exposed as <source expose name>
     And referring field <reference field> with value <reference value>
     And referred collection <target entity type> exposed as <target expose name>
     And the link from references to referred entity is <target link name>
     And the link from referred entity to referring entities is <source link name>
     And the ID to use for testing is <testing ID>
     And the ID to use for testing a valid update is <new valid value>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a link named <target link name> with URI "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    When I navigate to GET "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    Then I should receive a return code of 200
     And "entityType" should be <target entity type>
    When I navigate to GET "/<URI OF ENTITIES THAT REFER TO TARGET>"
    Then I should receive a return code of 200
     And each entity's "entityType" should be <source entity type>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
     And I set the <reference field> to "<INVALID REFERENCE>"
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 403
    When I set the <reference field> to <new valid value>
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 409
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then <reference field> should be <reference value>
     And I should receive a link named <target link name> with URI "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    When I navigate to GET "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    Then "id" should be "<REFERRED ENTITY ID>"
     And "entityType" should be <target entity type>
    Examples:
        | source entity type                      | source expose name                       | reference field                  | target entity type      | target expose name       | target link name           | source link name                            | testing ID                             | reference value                        | new valid value                        |
        | "studentDisciplineIncidentAssociation"  | "studentDisciplineIncidentAssociations"  | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentDisciplineIncidentAssociations"  | "74cf790e-84c4-4322-84b8-fca7206f1085_id0e26de6c-225b-9f67-9224-5113ad50a03b_id" | "74cf790e-84c4-4322-84b8-fca7206f1085_id" | "41df2791-b33c-4b10-8de6-a24963bbd3dd_id" |
        | "studentDisciplineIncidentAssociation"  | "studentDisciplineIncidentAssociations"  | "disciplineIncidentId"           | "disciplineIncident"    | "disciplineIncidents"    | "getDisciplineIncident"    | "getStudentDisciplineIncidentAssociations"  | "74cf790e-84c4-4322-84b8-fca7206f1085_id0e26de6c-225b-9f67-9224-5113ad50a03b_id" | "0e26de79-222a-5e67-9201-5113ad50a03b" | "0e26de79-22ea-5d67-9201-5113ad50a03b" |
        | "courseOffering"                        | "courseOfferings"                        | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getCourseOfferings"                        | "b360e8e8-54d1-4b00-952a-b817f91035ed" | "d23ebfc4-5192-4e6c-a52b-81cee2319072" | "c549e272-9a7b-4c02-aff7-b105ed76c904" |
        | "staffEducationOrganizationAssociation" | "staffEducationOrgAssignmentAssociations"| "educationOrganizationReference" | "educationOrganization" | "educationOrganizations" | "getEducationOrganization" | "getStaffEducationOrgAssignmentAssociations"| "0966614a-6c5d-4345-b451-7ec991823ac5" | "bd086bae-ee82-4cf2-baf9-221a9407ea07" | "b2c6e292-37b0-4148-bf75-c98a2fcc905f" |
        | "staffEducationOrganizationAssociation" | "staffEducationOrgAssignmentAssociations"| "staffReference"                 | "staff"                 | "staff"                  | "getStaff"                 | "getStaffEducationOrgAssignmentAssociations"| "c4d5d31b-001d-4573-b282-7e688a4676f9" | "85585b27-5368-4f10-a331-3abcaf3a3f4c" | "527406fd-0f6c-43b7-9dab-fab504c87c7f" |
        | "studentAssessment"                     | "studentAssessments"                     | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentAssessments"                     | "e5e13e61-01aa-066b-efe0-710f7a0e8755_id" | "5738c251-dd0b-4734-9ea6-417ac9320a15_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |
        | "studentAssessment"                     | "studentAssessments"                     | "assessmentId"                   | "assessment"            | "assessments"            | "getAssessment"            | "getStudentAssessments"                     | "e5e13e61-01aa-066b-efe0-710f7a0e8755_id" | "67ce204b-9999-4a11-bfea-000000004682" | "dd916592-7d7e-5d27-a87d-dfc7fcb757f6" |
        | "studentSchoolAssociation"              | "studentSchoolAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSchoolAssociations"              | "925d3f23-001f-4173-883b-04cf04ed9ad4" | "5738d251-dd0b-4734-9ea6-417ac9320a15_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |
        | "studentSchoolAssociation"              | "studentSchoolAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getStudentSchoolAssociations"              | "925d3f23-001f-4173-883b-04cf04ed9ad4" | "ec2e4218-6483-4e9c-8954-0aecccfd4731" | "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" |
        | "studentSectionAssociation"             | "studentSectionAssociations"             | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSectionAssociations"             | "706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id" | "74cf790e-84c4-4322-84b8-fca7206f1085_id" | "5738d251-dd0b-4734-9ea6-417ac9320a15_id" |
#        | "studentSectionAssociation"             | "studentSectionAssociations"             | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getStudentSectionAssociations"             | "706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id" | "706ee3be-0dae-4e98-9525-f564e05aa388_id" | "7295e51e-cd51-4901-ae67-fa33966478c7_id" |
        | "courseTranscript"                      | "courseTranscripts"                      | "courseId"                       | "course"                | "courses"                | "getCourse"                | "getCourseTranscripts"                      | "36aeeabf-ee9b-46e6-8039-13320bf15226" | "ddf01d82-9293-49ba-b16e-0fe5b4f4804d" | "75de6e74-a2e0-47da-ad1e-df1c833af92c" |
        | "studentParentAssociation"              | "studentParentAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentParentAssociations"              | "5738d251-dd0b-4734-9ea6-417ac9320a15_idc5aa1969-492a-5150-8479-71bfc4d57f1e_id" | "5738d251-dd0b-4734-9ea6-417ac9320a15_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |
        | "studentParentAssociation"              | "studentParentAssociations"              | "parentId"                       | "parent"                | "parents"                | "getParent"                | "getStudentParentAssociations"              | "5738d251-dd0b-4734-9ea6-417ac9320a15_idc5aa1969-492a-5150-8479-71bfc4d57f1e_id" | "38ba6ea7-7e73-47db-99e7-d0956f83d7e9" | "eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d" |
        | "teacherSchoolAssociation"              | "teacherSchoolAssociations"              | "teacherId"                      | "teacher"               | "teachers"               | "getTeacher"               | "getTeacherSchoolAssociations"              | "353dc847-51b5-44b8-a70f-a45c58fc8ee3" | "edce823c-ee28-4840-ae3d-74d9e9976dc5" | "67ed9078-431a-465e-adf7-c720d08ef512" |
        | "teacherSchoolAssociation"              | "teacherSchoolAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getTeacherSchoolAssociations"              | "353dc847-51b5-44b8-a70f-a45c58fc8ee3" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" | "ec2e4218-6483-4e9c-8954-0aecccfd4731" |
        | "teacherSectionAssociation"             | "teacherSectionAssociations"             | "teacherId"                      | "teacher"               | "teachers"               | "getTeacher"               | "getTeacherSectionAssociations"             | "ceffbb26-1327-4313-9cfc-1c3afd38122e_idff03a37c-3cbd-4f49-a7e7-ad306bfc9d6f_id" | "67ed9078-431a-465e-adf7-c720d08ef512" | "bcfcc33f-f4a6-488f-baee-b92fbd062e8d" |
        | "teacherSectionAssociation"             | "teacherSectionAssociations"             | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getTeacherSectionAssociations"             | "ceffbb26-1327-4313-9cfc-1c3afd38122e_idff03a37c-3cbd-4f49-a7e7-ad306bfc9d6f_id" | "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" | "47b5adbf-6fd0-4f07-ba5e-39612da2e234_id" |
        | "studentCohortAssociation"              | "studentCohortAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentCohortAssociations"              | "b408635d-8fd5-11e1-86ec-0021701f543f_idb40e2fc7-8fd5-11e1-86ec-0021701f543f_id" | "9f4019ca-dd53-4027-b11c-fc151268fafd_id" | "ace1dc53-8c1d-4c01-b922-c3ebb7ff5be8_id" |
        | "studentCohortAssociation"              | "studentCohortAssociations"              | "cohortId"                       | "cohort"                | "cohorts"                | "getCohort"                | "getStudentCohortAssociations"              | "b408635d-8fd5-11e1-86ec-0021701f543f_idb40c5b02-8fd5-11e1-86ec-0021701f543f_id" | "b408635d-8fd5-11e1-86ec-0021701f543f_id" | "b40926af-8fd5-11e1-86ec-0021701f543f_id" |
        | "studentProgramAssociation"             | "studentProgramAssociations"             | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentProgramAssociations"             | "9b8cafdc-8fd5-11e1-86ec-0021701f543f_idb3f68907-8fd5-11e1-86ec-0021701f543f_id" | "0f0d9bac-0081-4900-af7c-d17915e02378_id" | "dd4068df-0bea-4280-bbac-fbc736eea54d_id" |
        | "studentProgramAssociation"             | "studentProgramAssociations"             | "programId"                      | "program"               | "programs"               | "getProgram"               | "getStudentProgramAssociations"             | "9b8c3aab-8fd5-11e1-86ec-0021701f543f_idb3f6fe38-8fd5-11e1-86ec-0021701f543f_id" | "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id" | "9b8cafdc-8fd5-11e1-86ec-0021701f543f_id" |

Scenario Outline: Confirm direct references work
   Given format "application/vnd.slc+json"
    When I navigate to GET "/v1/<source resource>/<id>"
    Then I get a link to "<target link name>"
    When I navigate to that link
    Then I should receive a return code of 200
     And I should receive a collection with <count> elements
     And each entity's "entityType" should be "<target entity type>"
    Examples:
        | source resource | target entity type | target link name                                | id                                   | count |
        | assessments     | learningObjective  | objectiveAssessment.SAT-Math.learningObjectives | dd916592-7d7e-5d27-a87d-dfc7fcb12346 | 2     |
