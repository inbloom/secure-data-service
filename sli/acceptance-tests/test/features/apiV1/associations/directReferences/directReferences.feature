Feature: As an SLI application, I want entity references displayed as usable links with exlicit link names
This means that when links are requested in a GET request, a link should be present that represents each reference field
and that the links are valid
  
Background: Nothing yet
    Given I am logged in using "demo" "demo1234"
      And I have access to all data

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
#       | "application/xml"          |              |

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
    Then I should receive a return code of 400
    When I set the <reference field> to <new valid value>
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then <reference field> should be <new valid value>
     And I should receive a link named <target link name> with URI "/<URI OF REFERENCED ENTITY>/<NEW VALID VALUE>"
    When I navigate to GET "/<URI OF REFERENCED ENTITY>/<NEW VALID VALUE>"
    Then "id" should be "<NEW VALID VALUE>"
     And "entityType" should be <target entity type>
    Examples:
        | source entity type                      | source expose name                       | reference field                  | target entity type      | target expose name       | target link name           | source link name                            | testing ID                             | reference value                        | new valid value                        |
        | "attendance"                            | "attendances"                            | "studentId"                      | "student"               | "students"               | "getStudent"               | "getAttendances"                            | "4beb72d4-0f76-4071-92b4-61982dba7a7b" | "7a86a6a7-1f80-4581-b037-4a9328b9b650" | "dd69083f-a053-4819-a3cd-a162cdc627d7" |
        | "educationOrganization"                 | "educationOrganizations"                 | "parentEducationAgencyReference" | "educationOrganization" | "educationOrganizations" | "getEducationOrganization" | "getEducationOrganizations"                 | "67ce204b-9999-4a11-aabe-000000000001" | "67ce204b-9999-4a11-aabe-000000000000" | "67ce204b-9999-4a11-aabe-000000000002" |
        | "gradebookEntry"                        | "gradebookEntries"                       | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getGradebookEntries"                       | "008fd89d-88a2-43aa-8af1-74ac16a29380" | "706ee3be-0dae-4e98-9525-f564e05aa388" | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" |
        | "school"                                | "schools"                                | "parentEducationAgencyReference" | "educationOrganization" | "educationOrganizations" | "getEducationOrganization" | "getSchools"                                | "67ce204b-9999-4a11-aaab-000000000002" | "1d303c61-88d4-404a-ba13-d7c5cc324bc5" | "67ce204b-9999-4a11-aabe-000000000002" |
        | "schoolSessionAssociation"              | "schoolSessionAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getSchoolSessionAssociations"              | "67ce204b-9999-4a11-aacd-000000000001" | "67ce204b-9999-4a11-aaab-000000000008" | "67ce204b-9999-4a11-aaab-000000000002" |
        | "schoolSessionAssociation"              | "schoolSessionAssociations"              | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getSchoolSessionAssociations"              | "67ce204b-9999-4a11-aacd-000000000001" | "67ce204b-9999-4a11-aacb-000000000000" | "bbea7ac0-a016-4ece-bb1b-47b1fc251d56" |
        | "section"                               | "sections"                               | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getSections"                               | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" | "eb3b8c35-f582-df23-e406-6947249a19f2" | "fdacc41b-8133-f12d-5d47-358e6c0c791c" |
        | "section"                               | "sections"                               | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getSections"                               | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" | "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e" | "bbea7ac0-a016-4ece-bb1b-47b1fc251d56" |
        | "section"                               | "sections"                               | "courseId"                       | "course"                | "courses"                | "getCourse"                | "getSections"                               | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" | "53777181-3519-4111-9210-529350429899" | "53777181-3519-4111-9210-529350429899" |
        | "sectionAssessmentAssociation"          | "sectionAssessmentAssociations"          | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getSectionAssessmentAssociations"          | "df3e6d22-1c3f-460a-870a-49aba80bfb18" | "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d" | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" |
        | "sectionAssessmentAssociation"          | "sectionAssessmentAssociations"          | "assessmentId"                   | "assessment"            | "assessments"            | "getAssessment"            | "getSectionAssessmentAssociations"          | "df3e6d22-1c3f-460a-870a-49aba80bfb18" | "6a53f63e-deb8-443d-8138-fc5a7368239c" | "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" |
        | "sessionCourseAssociation"              | "sessionCourseAssociations"              | "courseId"                       | "course"                | "courses"                | "getCourse"                | "getSessionCourseAssociations"              | "9ff65bb1-ef8b-4588-83af-d58f39c1bf68" | "93d33f0b-0f2e-43a2-b944-7d182253a79a" | "53777181-3519-4111-9210-529350429899" |
        | "sessionCourseAssociation"              | "sessionCourseAssociations"              | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getSessionCourseAssociations"              | "9ff65bb1-ef8b-4588-83af-d58f39c1bf68" | "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e" | "bbea7ac0-a016-4ece-bb1b-47b1fc251d56" |
        | "staffEducationOrganizationAssociation" | "staffEducationOrganizationAssociations" | "staffReference"                 | "staff"                 | "staff"                  | "getStaff"                 | "getStaffEducationOrganizationAssociations" | "c3c19cba-2ae1-4c9f-8497-40d273db5bdf" | "269be4c9-a806-4051-a02d-15a7af3ffe3e" | "0e26de79-222a-4d67-9201-5113ad50a03b" |
        | "staffEducationOrganizationAssociation" | "staffEducationOrganizationAssociations" | "educationOrganizationReference" | "educationOrganization" | "educationOrganizations" | "getEducationOrganization" | "getStaffEducationOrganizationAssociations" | "c3c19cba-2ae1-4c9f-8497-40d273db5bdf" | "4f0c9368-8488-7b01-0000-000059f9ba56" | "67ce204b-9999-4a11-aabe-000000000002" |
        | "studentAssessmentAssociation"          | "studentAssessmentAssociations"          | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentAssessmentAssociations"          | "1e0ddefb-6b61-4f7d-b8c3-33bb5676115a" | "7afddec3-89ec-402c-8fe6-cced79ae3ef5" | "dd69083f-a053-4819-a3cd-a162cdc627d7" |
        | "studentAssessmentAssociation"          | "studentAssessmentAssociations"          | "assessmentId"                   | "assessment"            | "assessments"            | "getAssessment"            | "getStudentAssessmentAssociations"          | "1e0ddefb-6b61-4f7d-b8c3-33bb5676115a" | "6a53f63e-deb8-443d-8138-fc5a7368239c" | "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" |
        | "studentSchoolAssociation"              | "studentSchoolAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSchoolAssociations"              | "122a340e-e237-4766-98e3-4d2d67786572" | "714c1304-8a04-4e23-b043-4ad80eb60992" | "dd69083f-a053-4819-a3cd-a162cdc627d7" |
        | "studentSchoolAssociation"              | "studentSchoolAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getStudentSchoolAssociations"              | "122a340e-e237-4766-98e3-4d2d67786572" | "eb3b8c35-f582-df23-e406-6947249a19f2" | "fdacc41b-8133-f12d-5d47-358e6c0c791c" |
        | "studentSectionAssociation"             | "studentSectionAssociations"             | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSectionAssociations"             | "4efb4b14-bc49-f388-0000-0000c9355702" | "4efb3a11-bc49-f388-0000-0000c93556fb" | "dd69083f-a053-4819-a3cd-a162cdc627d7" |
        | "studentSectionAssociation"             | "studentSectionAssociations"             | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getStudentSectionAssociations"             | "4efb4b14-bc49-f388-0000-0000c9355702" | "4efb4292-bc49-f388-0000-0000c9355701" | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" |
        | "studentSectionGradebookEntry"          | "studentSectionGradebookEntries"         | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSectionGradebookEntries"         | "2713b97a-5632-44a5-8e04-031074bcb326" | "dd69083f-a053-4819-a3cd-a162cdc627d7" | "7a86a6a7-1f80-4581-b037-4a9328b9b650" |
        | "studentSectionGradebookEntry"          | "studentSectionGradebookEntries"         | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getStudentSectionGradebookEntries"         | "2713b97a-5632-44a5-8e04-031074bcb326" | "706ee3be-0dae-4e98-9525-f564e05aa388" | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" |
        | "studentSectionGradebookEntry"          | "studentSectionGradebookEntries"         | "gradebookEntryId"               | "gradebookEntry"        | "gradebookEntries"       | "getGradebookEntry"        | "getStudentSectionGradebookEntries"         | "2713b97a-5632-44a5-8e04-031074bcb326" | "008fd89d-88a2-43aa-8af1-74ac16a29380" | "e49dc00c-182d-4f22-98c5-3d35b5f6d993" |
        | "studentTranscriptAssociation"          | "studentTranscriptAssociations"          | "courseId"                       | "course"                | "courses"                | "getCourse"                | "getStudentTranscriptAssociations"          | "09eced61-edd9-4826-a7bc-137ffecda877" | "53777181-3519-4111-9210-529350429899" | "93d33f0b-0f2e-43a2-b944-7d182253a79a" |
        | "studentTranscriptAssociation"          | "studentTranscriptAssociations"          | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentTranscriptAssociations"          | "09eced61-edd9-4826-a7bc-137ffecda877" | "e1af7127-743a-4437-ab15-5b0dacd1bde0" | "714c1304-8a04-4e23-b043-4ad80eb60992" |
        | "teacherSchoolAssociation"              | "teacherSchoolAssociations"              | "teacherId"                      | "teacher"               | "teachers"               | "getTeacher"               | "getTeacherSchoolAssociations"              | "52c1f410-602e-46b6-9b40-77bf55d77568" | "244520d2-8c6b-4a1e-b35e-d67819ec0211" | "fa45033c-5517-b14b-1d39-c9442ba95782" |
        | "teacherSchoolAssociation"              | "teacherSchoolAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getTeacherSchoolAssociations"              | "52c1f410-602e-46b6-9b40-77bf55d77568" | "0f464187-30ff-4e61-a0dd-74f45e5c7a9d" | "eb3b8c35-f582-df23-e406-6947249a19f2" |
        | "teacherSectionAssociation"             | "teacherSectionAssociations"             | "teacherId"                      | "teacher"               | "teachers"               | "getTeacher"               | "getTeacherSectionAssociations"             | "660315c1-cf34-4904-b9f8-b5fb678c62d4" | "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94" | "244520d2-8c6b-4a1e-b35e-d67819ec0211" |
        | "teacherSectionAssociation"             | "teacherSectionAssociations"             | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getTeacherSectionAssociations"             | "660315c1-cf34-4904-b9f8-b5fb678c62d4" | "4efb4262-bc49-f388-0000-0000c9355700" | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" |
