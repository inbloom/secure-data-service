@smoke @RALLY_US209
Feature: As an SLI application, I want to be able to perform CRUD operations on association resources.
  This means I want to be able to perform CRUD on all association entities
  and verify that the correct links are made available.

  Background: Logged in as IT Admin Rick Rogers
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"

    Scenario Outline: CRUD round trip for an association entity
      # Create
      Given a valid association json document for <ASSOC TYPE>
      When I navigate to POST "/<ASSOC URI>"
      Then I should receive a return code of 201
      And I should receive a new ID for the association I just created
      # Read
      When I navigate to GET "/<ASSOC URI>/<NEWLY CREATED ASSOC ID>"
      Then I should receive a return code of 200
      And the response should contain the appropriate fields and values
      And "entityType" should be "<ASSOC TYPE>"
      And "<UPDATE FIELD>" should be "<OLD VALUE>"
      # Update
      When I set the "<UPDATE FIELD>" to "<NEW VALUE>"
      And I navigate to PUT "/<ASSOC URI>/<NEWLY CREATED ASSOC ID>"
      Then I should receive a return code of 204
      And I navigate to GET "/<ASSOC URI>/<NEWLY CREATED ASSOC ID>"
      And "<UPDATE FIELD>" should be "<NEW VALUE>"
      # Delete
      When I navigate to DELETE "/<ASSOC URI>/<NEWLY CREATED ASSOC ID>"
      Then I should receive a return code of 204
      And I navigate to GET "/<ASSOC URI>/<NEWLY CREATED ASSOC ID>"
      And I should receive a return code of 404

    Examples:
      | ASSOC TYPE                             | ASSOC URI                                | UPDATE FIELD             | OLD VALUE              | NEW VALUE              |
      | courseOffering                         | courseOfferings                          | localCourseCode          | LCCGR1                 | LCCGR2                 |
      | staffCohortAssociation                 | staffCohortAssociations                  | beginDate                | 2010-01-15             | 2011-02-01             |
      | staffEducationOrganizationAssociation  | staffEducationOrgAssignmentAssociations  | beginDate                | 2011-01-13             | 2011-02-15             |
      | staffProgramAssociation                | staffProgramAssociations                 | beginDate                | 2012-01-01             | 2012-02-01             |
      | studentAssessmentAssociation           | studentAssessments                       | retestIndicator          | 1st Retest             | 2nd Retest             |
      | studentCohortAssociation               | studentCohortAssociations                | beginDate                | 2012-02-29             | 2011-12-01             |
      | studentDisciplineIncidentAssociation   | studentDisciplineIncidentAssociations    | studentParticipationCode | Reporter               | Witness                |
      | studentParentAssociation               | studentParentAssociations                | livesWith                | true                   | false                  |
      | studentProgramAssociation              | studentProgramAssociations               | reasonExited             | Refused services       | Expulsion              |
      | studentSchoolAssociation               | studentSchoolAssociations                | entryGradeLevel          | First grade            | Third grade            |
      | studentSectionAssociation              | studentSectionAssociations               | homeroomIndicator        | true                   | false                  |
      | studentTranscriptAssociation           | courseTranscripts                        | finalLetterGradeEarned   | A                      | B                      |
      | teacherSchoolAssociation               | teacherSchoolAssociations                | programAssignment        | Regular Education      | Special Education      |
      | teacherSectionAssociation              | teacherSectionAssociations               | classroomPosition        | Teacher of Record      | Assistant Teacher      |

    Scenario Outline: Read All
      Given parameter "limit" is "0"
      When I navigate to GET "/<ASSOC URI>"
      Then I should receive a return code of 200
      And I should receive a collection of "<COUNT>" entities
      And each entity's "entityType" should be "<ASSOC TYPE>"

    Examples:
      | ASSOC TYPE                             | ASSOC URI                                | COUNT   |
      | courseOffering                         | courseOfferings                          | 95      |
      | staffCohortAssociation                 | staffCohortAssociations                  | 11      |
      | staffEducationOrganizationAssociation  | staffEducationOrgAssignmentAssociations  | 17      |
      | staffProgramAssociation                | staffProgramAssociations                 | 7       |
      | studentAssessmentAssociation           | studentAssessments                       | 8       |
      | studentCohortAssociation               | studentCohortAssociations                | 13      |
      | studentDisciplineIncidentAssociation   | studentDisciplineIncidentAssociations    | 4       |
      | studentParentAssociation               | studentParentAssociations                | 3       |
      | studentProgramAssociation              | studentProgramAssociations               | 18      |
      | studentSchoolAssociation               | studentSchoolAssociations                | 179     |
      | studentSectionAssociation              | studentSectionAssociations               | 264     |
      | studentTranscriptAssociation           | courseTranscripts                        | 3       |
      | teacherSchoolAssociation               | teacherSchoolAssociations                | 6       |
      | teacherSectionAssociation              | teacherSectionAssociations               | 8       |

    Scenario Outline: Unhappy paths: invalid or inaccessible references
      # Log in as a user with less accessible data
      Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
      And format "application/vnd.slc+json"
      # Read with invalid reference
      When I navigate to GET "/<ASSOC URI>/<INVALID REFERENCE>"
      Then I should receive a return code of 404
      # Update with invalid reference
      Given a valid association json document for <ASSOC TYPE>
      When I set the "<UPDATE FIELD>" to "<NEW VALUE>"
      And I navigate to PUT "/<ASSOC URI>/<INVALID REFERENCE>"
      Then I should receive a return code of 404
      # Update by setting end points to invalid references
      When I navigate to GET "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      And I set the "<END POINT 1 FIELD>" to "<INVALID REFERENCE>"
      And I navigate to PUT "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      Then I should receive a return code of 403
      And the error message should indicate "<BAD REFERENCE>"
      When I navigate to GET "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      And I set the "<END POINT 2 FIELD>" to "<INVALID REFERENCE>"
      And I navigate to PUT "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      Then I should receive a return code of 403
      And the error message should indicate "<BAD REFERENCE>"
      # Delete with invalid reference
      When I navigate to DELETE "/<ASSOC URI>/<INVALID REFERENCE>"
      Then I should receive a return code of 404
      # Create with inaccessible endpoint 1
      Given a valid association json document for <ASSOC TYPE>
      When I set the "<END POINT 1 FIELD>" to "<INACCESSIBLE END POINT 1 ID>"
      When I navigate to POST "/<ASSOC URI>"
      Then I should receive a return code of 403
      # Create with inaccessible endpoint 2
      Given a valid association json document for <ASSOC TYPE>
      When I set the "<END POINT 2 FIELD>" to "<INACCESSIBLE END POINT 2 ID>"
      When I navigate to POST "/<ASSOC URI>"
      Then I should receive a return code of 403
      # Update wby setting end points to inaccessible references
      When I navigate to GET "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      And I set the "<END POINT 1 FIELD>" to "<INACCESSIBLE END POINT 1 ID>"
      And I navigate to PUT "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      Then I should receive a return code of 403
      When I navigate to GET "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      And I set the "<END POINT 2 FIELD>" to "<INACCESSIBLE END POINT 2 ID>"
      And I navigate to PUT "/<ASSOC URI>/<ASSOC ID FOR UPDATE>"
      Then I should receive a return code of 403

    Examples:
      | ASSOC TYPE                             | ASSOC URI                                | ASSOC ID FOR UPDATE                    | UPDATE FIELD             | NEW VALUE              | END POINT 1 FIELD     | END POINT 2 FIELD              | INACCESSIBLE END POINT 1 ID            | INACCESSIBLE END POINT 2 ID            |
      | courseOffering                         | courseOfferings                          | <b360e8e8-54d1-4b00-952a-b817f91035ed> | localCourseCode          | LCCGR2                 | sessionId             | courseId                       | <389b0caa-dcd2-4e84-93b7-daa4a6e9b18e> | <e31f7583-417e-4c42-bd55-0bbe7518edf8> |
      | staffCohortAssociation                 | staffCohortAssociations                  | <b4e31b1a-8e55-8803-722c-14d8087c0712> | beginDate                | 2011-02-01             | staffId               | cohortId                       | <04f708bc-928b-420d-a440-f1592a5d1073> | <b1bd3db6-d020-4651-b1b8-a8dba688d9e1> |
      | staffEducationOrganizationAssociation  | staffEducationOrgAssignmentAssociations  | <0966614a-6c5d-4345-b451-7ec991823ac5> | beginDate                | 2011-02-15             | staffReference        | educationOrganizationReference | <04f708bc-928b-420d-a440-f1592a5d1073> | <b2c6e292-37b0-4148-bf75-c98a2fcc905f> |
      | staffProgramAssociation                | staffProgramAssociations                 | <9bf7591b-8fd5-11e1-86ec-0021701f543f> | beginDate                | 2012-02-01             | staffId               | programId                      | <04f708bc-928b-420d-a440-f1592a5d1073> | <9e909dfc-ba61-406d-bbb4-c953e8946f8b> |
      | studentAssessmentAssociation           | studentAssessments                       | <e85b5aa7-465a-6dd3-8ffb-d02461ed79f8> | retestIndicator          | 2nd Retest             | studentId             | assessmentId                   | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <abc16592-7d7e-5d27-a87d-dfc7fcb12346> |
      | studentCohortAssociation               | studentCohortAssociations                | <b40c5b02-8fd5-11e1-86ec-0021701f543f> | beginDate                | 2011-12-01             | studentId             | cohortId                       | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <b1bd3db6-d020-4651-b1b8-a8dba688d9e1> |
      | studentDisciplineIncidentAssociation   | studentDisciplineIncidentAssociations    | <0e26de6c-225b-9f67-9625-5113ad50a03b> | studentParticipationCode | Witness                | studentId             | disciplineIncidentId           | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <0e26de79-22aa-5d67-9201-5113ad50a03b> |
      | studentParentAssociation               | studentParentAssociations                | <c5aa1969-492a-5150-8479-71bfc4d57f1e> | livesWith                | false                  | studentId             | parentId                       | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <cb7a932f-2d44-800c-cd5a-cdb25a29fc75> |
      | studentProgramAssociation              | studentProgramAssociations               | <b3f68907-8fd5-11e1-86ec-0021701f543f> | reasonExited             | Expulsion              | studentId             | programId                      | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <9e909dfc-ba61-406d-bbb4-c953e8946f8b> |
      | studentSchoolAssociation               | studentSchoolAssociations                | <e7e0e926-874e-4d05-9177-9776d44c5fb5> | entryGradeLevel          | Third grade            | studentId             | schoolId                       | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <0f464187-30ff-4e61-a0dd-74f45e5c7a9d> |
      | studentSectionAssociation              | studentSectionAssociations               | <4ae72560-3518-4576-a35e-a9607668c9ad> | homeroomIndicator        | false                  | studentId             | sectionId                      | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <a47eb9aa-1c97-4c8e-9d0a-45689a66d4f8> |
      | studentTranscriptAssociation           | courseTranscripts                        | <f11a2a30-d4fd-4400-ae18-353c00d581a2> | finalLetterGradeEarned   | B                      | studentId             | courseId                       | <737dd4c1-86bd-4892-b9e0-0f24f76210be> | <e31f7583-417e-4c42-bd55-0bbe7518edf8> |
      | teacherSchoolAssociation               | teacherSchoolAssociations                | <26a4a0fc-fad4-45f4-a00d-285acd1f83eb> | programAssignment        | Special Education      | teacherId             | schoolId                       | <04f708bc-928b-420d-a440-f1592a5d1073> | <0f464187-30ff-4e61-a0dd-74f45e5c7a9d> |
      | teacherSectionAssociation              | teacherSectionAssociations               | <32b86a2a-e55c-4689-aedf-4b676f3da3fc> | classroomPosition        | Assistant Teacher      | teacherId             | sectionId                      | <04f708bc-928b-420d-a440-f1592a5d1073> | <a47eb9aa-1c97-4c8e-9d0a-45689a66d4f8> |