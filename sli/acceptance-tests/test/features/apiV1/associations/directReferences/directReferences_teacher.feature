@RALLY_US209
@RALLY_US1803
@RALLY_US1804
@RALLY_US1961
@RALLY_DE85
@RALLY_DE87
Feature: As an SLI application, I want entity references displayed as usable links with exlicit link names
This means that when links are requested in a GET request, a link should be present that represents each reference field
and that the links are valid.  This test is using a teacher with IT Admin rights.  Teachers cannot change the direct edorg reference

Background: Nothing yet
    Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"

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
     And I print debug info
    Then I should receive a return code of 204
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then <reference field> should be <new valid value>
     And I should receive a link named <target link name>
    When I navigate to GET the link named <target link name>
    Then "id" should be "<NEW VALID VALUE>"
     And "entityType" should be <target entity type>
    Examples:
        | source entity type                      | source expose name                       | reference field                  | target entity type      | target expose name       | target link name           | source link name                            | testing ID                             | reference value                        | new valid value                        |
#        | "educationOrganization"                 | "educationOrganizations"                 | "parentEducationAgencyReference" | "educationOrganization" | "educationOrganizations" | "getParentEducationOrganization"  | "getFeederEducationOrganizations" | "92d6d5a0-852c-45f4-907a-912752831772" | "b2c6e292-37b0-4148-bf75-c98a2fcc905f" | "bd086bae-ee82-4cf2-baf9-221a9407ea07" |
        | "section"                               | "sections"                               | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getSections"                               | "15ab6363-5509-470c-8b59-4f289c224107_id" | "c549e272-9a7b-4c02-aff7-b105ed76c904" | "4d796afd-b1ac-4f16-9e0d-6db47d445b55" |
        | "section"                               | "sections"                               | "courseOfferingId"               | "courseOffering"        | "courseOfferings"        | "getCourseOffering"        | "getSections"                               | "15ab6363-5509-470c-8b59-4f289c224107_id" | "88ddb0c4-1787-4ed8-884e-96aa774e6d42" | "00291269-33e0-415e-a0a4-833f0ef38189" |
        | "studentGradebookEntry"                 | "studentGradebookEntries"                | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentGradebookEntries"                | "d9a593c2-c974-4071-b91b-3289012388a9" | "bf88acdb-71f9-4c19-8de8-2cdc698936fe_id" | "0f0d9bac-0081-4900-af7c-d17915e02378_id" |
        #| "studentGradebookEntry"                 | "studentGradebookEntries"                | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getStudentGradebookEntries"                | "20120613-5434-f906-e51b-d63ef970ef8f" | "0dbb262b-8a3e-4a7b-82f9-72de95903d91_id" | "15ab6363-5509-470c-8b59-4f289c224107_id" |

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
        | "reportCard"                            | "reportCards"                            | "studentId"                      | "student"                   | "students"                   | "getStudent"                     | "getReportCards"             | "8770da5b-dca5-4ced-7919-201211120001" | "0f0d9bac-0081-4900-af7c-d17915e02378_id" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" |
        | "reportCard"                            | "reportCards"                            | "gradingPeriodId"                | "gradingPeriod"             | "gradingPeriods"             | "getGradingPeriod"               | "getReportCards"             | "8770da5b-dca5-4ced-7919-201211120001" | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "ef72b883-90fa-40fa-afc2-4cb1ae17623b" |
        | "grade"                                 | "grades"                                 | "studentSectionAssociationId"    | "studentSectionAssociation" | "studentSectionAssociations" | "getStudentSectionAssociation"   | "getGrades"                  | "ef42e2a2-9942-11e1-7919-201211130001" | "15ab6363-5509-470c-8b59-4f289c224107_id066345e8-2633-474d-9088-7b3828bf873a_id" | "15ab6363-5509-470c-8b59-4f289c224107_id0955cdd8-a14d-4a94-ac68-9e637617f51b_id" |
        | "grade"                                 | "grades"                                 | "gradingPeriodId"                | "gradingPeriod"             | "gradingPeriods"             | "getGradingPeriod"               | "getGrades"                  | "ef42e2a2-9942-11e1-7919-201211130001" | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "ef72b883-90fa-40fa-afc2-4cb1ae17623b" |
        #        | "studentCompetency"                     | "studentCompetencies"                    | "objectiveId.learningObjectiveId"            | "learningObjective"         | "learningObjectives"         | "getLearningObjective"           | "getStudentCompetencies"     | "b57643e4-9acf-11e1-89a7-68a86d21d918" | "dd9165f2-65be-6d27-a8ac-bdc5f46757b6" | "dd9165f2-65fe-6d27-a8ec-bdc5f47757b7" |
        | "studentCompetency"                     | "studentCompetencies"                    | "studentSectionAssociationId"    | "studentSectionAssociation" | "studentSectionAssociations" | "getStudentSectionAssociation"   | "getStudentCompetencies"     | "b57643e4-9acf-11e1-7919-201211130002" | "15ab6363-5509-470c-8b59-4f289c224107_id066345e8-2633-474d-9088-7b3828bf873a_id" | "15ab6363-5509-470c-8b59-4f289c224107_id0955cdd8-a14d-4a94-ac68-9e637617f51b_id" |
        | "studentAcademicRecord"                 | "studentAcademicRecords"                 | "studentId"                      | "student"                   | "students"                   | "getStudent"                     | "getStudentAcademicRecords"  | "56afc8d4-6c91-48f9-8a11-de527c1131b7" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" | "f694c1f1-f06e-4059-9a9b-ff8700a4fbce_id" |
        | "studentAcademicRecord"                 | "studentAcademicRecords"                 | "sessionId"                      | "session"                   | "sessions"                   | "getSession"                     | "getStudentAcademicRecords"  | "56afc8d4-6c91-48f9-8a11-de527c1131b7" | "4d796afd-b1ac-4f16-9e0d-6db47d445b55" | "d23ebfc4-5192-4e6c-a52b-81cee2319072" |
        | "attendance"                            | "attendances"                            | "studentId"                      | "student"                   | "students"                   | "getStudent"                     | "getAttendances"             | "4beb72d4-0f76-4071-7919-201211130003" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" | "0f0d9bac-0081-4900-af7c-d17915e02378_id" |
        | "gradebookEntry"                        | "gradebookEntries"                       | "sectionId"                      | "section"                   | "sections"                   | "getSection"                     | "getGradebookEntries"        | "15ab6363-5509-470c-8b59-4f289c224107_ide49dc00c-182d-4f22-7919-201211130004_id" | "15ab6363-5509-470c-8b59-4f289c224107_id" | "47b5adbf-6fd0-4f07-ba5e-39612da2e234_id" |
        | "section"                               | "sections"                               | "schoolId"                       | "school"                    | "schools"                    | "getSchool"                      | "getSections"                | "47b5adbf-6fd0-4f07-ba5e-39612da2e234_id" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" | "92d6d5a0-852c-45f4-907a-912752831772" |
        | "studentGradebookEntry"                 | "studentGradebookEntries"                | "gradebookEntryId"               | "gradebookEntry"            | "gradebookEntries"           | "getGradebookEntry"              | "getStudentGradebookEntries" | "d9a593c2-c974-4071-b91b-3289012388a9" | "15ab6363-5509-470c-8b59-4f289c224107_ide49dc00c-182d-4f22-7919-201211130004_id" | "47b5adbf-6fd0-4f07-ba5e-39612da2e234_id008fd89d-88a2-43aa-8af1-74ac16a29380_id" |
        | "cohort"                                | "cohorts"                                | "educationOrgId"                 | "educationOrganization"     | "educationOrganizations"     | "getEducationOrganization"       | "getCohorts"                 | "f95269af-cb73-4694-7919-201211130010_id" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" | "92d6d5a0-852c-45f4-907a-912752831772" |

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
        | "courseTranscript"                      | "courseTranscripts"                      | "studentId"                      | "student"               | "students"               | "getStudent"               | "getCourseTranscripts"                      | "36aeeabf-ee9b-46e6-7919-201311130015" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" | "ae479bef-eb57-4c2e-8896-84983b1d4ed2_id" |

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
        | "studentDisciplineIncidentAssociation"  | "studentDisciplineIncidentAssociations"  | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentDisciplineIncidentAssociations"  | "0c2756fd-6a30-4010-af79-488d6ef2735a_id20120613-8d5a-c796-76e3-d77d5d497e6c_id" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" | "0f0d9bac-0081-4900-af7c-d17915e02378_id" |
        | "studentDisciplineIncidentAssociation"  | "studentDisciplineIncidentAssociations"  | "disciplineIncidentId"           | "disciplineIncident"    | "disciplineIncidents"    | "getDisciplineIncident"    | "getStudentDisciplineIncidentAssociations"  | "0c2756fd-6a30-4010-af79-488d6ef2735a_id20120613-8d5a-c796-76e3-d77d5d497e6c_id" | "0e26de79-7efa-5e67-9201-5113ad50a03b" | "0e26de79-7efa-5e67-7919-201211130050" |
        | "courseOffering"                        | "courseOfferings"                        | "sessionId"                      | "session"               | "sessions"               | "getSession"               | "getCourseOfferings"                        | "b360e8e8-54d1-4b00-952a-b817f91035ed" | "d23ebfc4-5192-4e6c-a52b-81cee2319072" | "e100045b-25b3-41d6-9f1c-cd86db18eb68" |
        | "staffEducationOrganizationAssociation" | "staffEducationOrgAssignmentAssociations"| "educationOrganizationReference" | "educationOrganization" | "educationOrganizations" | "getEducationOrganization" | "getStaffEducationOrgAssignmentAssociations"| "05e3de47-9e41-c048-a572-3eb4c7ee9095" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" | "92d6d5a0-852c-45f4-907a-912752831772" |
        | "studentAssessment"          | "studentAssessments"                     | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentAssessments"                     | "e5e13e61-01aa-066b-efe0-710f7a011115_id" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" | "f694c1f1-f06e-4059-9a9b-ff8700a4fbce_id" |
        | "studentAssessment"          | "studentAssessments"                     | "assessmentId"                   | "assessment"            | "assessments"            | "getAssessment"            | "getStudentAssessments"                     | "e5e13e61-01aa-066b-efe0-710f7a011115_id" | "b94b5194d45cd707465627c0cd6c4f68f3558600_id" | "717268b3610fbb303baaa9edced364d9d364df3e_id" |
        | "studentSchoolAssociation"              | "studentSchoolAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSchoolAssociations"              | "0950fa79-bad5-4085-a3c0-bc98ee122231" | "b8e346c8-025e-44ba-9ae1-f2fa4e832b08_id" | "f694c1f1-f06e-4059-9a9b-ff8700a4fbce_id" |
        | "studentSchoolAssociation"              | "studentSchoolAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getStudentSchoolAssociations"              | "0950fa79-bad5-4085-a3c0-bc98ee122231" | "92d6d5a0-852c-45f4-907a-912752831772" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" |
        | "studentSectionAssociation"             | "studentSectionAssociations"             | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentSectionAssociations"             | "15ab6363-5509-470c-8b59-4f289c224107_id066345e8-2633-474d-9088-7b3828bf873a_id" | "bf88acdb-71f9-4c19-8de8-2cdc698936fe_id" | "f694c1f1-f06e-4059-9a9b-ff8700a4fbce_id" |
#        | "studentSectionAssociation"             | "studentSectionAssociations"             | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getStudentSectionAssociations"             | "706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id" | "706ee3be-0dae-4e98-9525-f564e05aa388_id" | "7295e51e-cd51-4901-ae67-fa33966478c7_id" |
        | "courseTranscript"                      | "courseTranscripts"                      | "courseId"                       | "course"                | "courses"                | "getCourse"                | "getCourseTranscripts"                      | "36aeeabf-ee9b-46e6-7919-201311130015" | "9a00b72a-e46f-4fe5-b138-f041913727a5" | "fabc4143-629b-4176-8dcf-2e01e33c9c25" |
        | "studentParentAssociation"              | "studentParentAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentParentAssociations"              | "0c2756fd-6a30-4010-af79-488d6ef2735a_idc5aa1969-492a-5150-8479-71bfc4d87984_id" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" | "0f0d9bac-0081-4900-af7c-d17915e02378_id" |
        | "studentParentAssociation"              | "studentParentAssociations"              | "parentId"                       | "parent"                | "parents"                | "getParent"                | "getStudentParentAssociations"              | "0c2756fd-6a30-4010-af79-488d6ef2735a_idc5aa1969-492a-5150-8479-71bfc4d87984_id" | "9b8f7237-ce8e-4dff-98cf-66535880987b" | "9b8f7237-ce8e-4dff-7919-201211130040" |
        | "teacherSchoolAssociation"              | "teacherSchoolAssociations"              | "teacherId"                      | "teacher"               | "teachers"               | "getTeacher"               | "getTeacherSchoolAssociations"              | "9d4e4031-3a5d-4965-98b9-257ff887a774" | "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b" | "edce823c-ee28-4840-ae3d-74d9e9976dc5" |
        | "teacherSchoolAssociation"              | "teacherSchoolAssociations"              | "schoolId"                       | "school"                | "schools"                | "getSchool"                | "getTeacherSchoolAssociations"              | "9d4e4031-3a5d-4965-98b9-257ff887a774" | "92d6d5a0-852c-45f4-907a-912752831772" | "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" |
        | "teacherSectionAssociation"             | "teacherSectionAssociations"             | "teacherId"                      | "teacher"               | "teachers"               | "getTeacher"               | "getTeacherSectionAssociations"             | "15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id" | "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b" | "edce823c-ee28-4840-ae3d-74d9e9976dc5" |
        | "teacherSectionAssociation"             | "teacherSectionAssociations"             | "sectionId"                      | "section"               | "sections"               | "getSection"               | "getTeacherSectionAssociations"             | "15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id" | "15ab6363-5509-470c-8b59-4f289c224107_id" | "47b5adbf-6fd0-4f07-ba5e-39612da2e234_id" |
        | "studentCohortAssociation"              | "studentCohortAssociations"              | "studentId"                      | "student"               | "students"               | "getStudent"               | "getStudentCohortAssociations"              | "f95269af-cb73-4694-7919-201211130010_idcc87b3af-70f1-4141-7919-201311130011_id" | "e1dd7a73-5000-4293-9b6d-b5f02b7b3b34_id" | "0c2756fd-6a30-4010-af79-488d6ef2735a_id" |
        | "studentCohortAssociation"              | "studentCohortAssociations"              | "cohortId"                       | "cohort"                | "cohorts"                | "getCohort"                | "getStudentCohortAssociations"              | "f95269af-cb73-4694-7919-201211130010_idcc87b3af-70f1-4141-7919-201311130011_id" | "f95269af-cb73-4694-7919-201211130010_id" | "f95269af-cb73-4694-7919-201211130030_id" |


