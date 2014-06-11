Feature: Update data through API and retrieve through the bulk extract api end points a generated delta bulk extract file, and validate the file

  Scenario: Initialize security trust store for Bulk Extract application and LEAs
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased
  # Make IL-DAYBREAK a charter school to verify bulk extract will work
    Given I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/json"
    Then I PATCH the "organizationCategories" field of the entity specified by endpoint "educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id" to '[ "School", "Local Education Agency" ]'

@shortcut
Scenario: CREATE and verify deltas for private entities through API POST
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # CREATE parent entity via POST
  When I POST and validate the following entities:
    |  entityName                   |  entityType                |  returnCode  |
    |  newEducationOrganization     |  educationOrganization     |  201         |
    |  newDaybreakStudent           |  student                   |  201         |
    |  DbStudentSchoolAssociation   |  studentSchoolAssociation  |  201         |
    |  newParentFather              |  parent                    |  201         |
    |  newParentMother              |  parent                    |  201         |
    |  newStudentFatherAssociation  |  studentParentAssociation  |  201         |
    |  newStudentMotherAssociation  |  studentParentAssociation  |  201         |
    
  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify "2" public delta bulk extract files are generated in "Midgar"
  And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "student" delta was extracted in the same format as the api
   And The "studentSchoolAssociation" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  # UPDATE/UPSERT parent entity via PUT
  When I PUT and validate the following entities:
     |  field            |  entityName                   |  value                           |  returnCode  | endpoint                                             |
     |  loginId          |  newDaybreakStudent           |  super_student_you_rock@bazinga  |  204         | students/9bf3036428c40861238fdc820568fde53e658d88_id |
     |  loginId          |  newParentMother              |  super_mom_you_rock@bazinga.com  |  204         | parents/41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |
     |  loginId          |  newParentFather              |  super_dad_good_job@bazinga.com  |  204         | parents/41f42690a7c8eb5b99637fade00fc72f599dab07_id  |
     |  contactPriority  |  newStudentMotherAssociation  |  1                               |  204         | studentParentAssociations/9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id |
     |  postalCode       |  newEducationOrganization     |  11012                           |  204         | educationOrganizations/a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" public delta bulk extract files are generated in "Midgar"
   And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "student" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"

  # UPDATE parent and parentStudentAssociation fields via PATCH
  When I PATCH and validate the following entities:
    |  fieldName        |  entityType               | value                                 |  returnCode  | endpoint                                                           |
    |  postalCode       |  educationOrganization    | 11099                                 |  204         | educationOrganizations/a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id |
    |  studentLoginId   |  student                  | average_student_youre_ok@bazinga.com  |  204         | students/9bf3036428c40861238fdc820568fde53e658d88_id               |
    |  momLoginId       |  parent                   | average_mom_youre_ok@bazinga.com      |  204         | parents/41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id                |
    |  dadLoginId       |  parent                   | average_dad_youre_ok@bazinga.com      |  204         | parents/41f42690a7c8eb5b99637fade00fc72f599dab07_id                |
    |  contactPriority  |  studentParentAssociation | 1                                     |  204         | studentParentAssociations/9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id |
  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" public delta bulk extract files are generated in "Midgar"
   And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "student" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api

# DELETE entities
 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  When I DELETE and validate the following entities:
    |  entity        |  id                                           |  returnCode  |
    |  student       |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  parent        |  41f42690a7c8eb5b99637fade00fc72f599dab07_id  |  204         |
    |  parent        |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |  204         |
    |  studentParentAssociation        |  9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id  |  204         |



  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  deleted                               |
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I verify this "deleted" file should contain:
    |  id                                          | condition                             |
    |  9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
    |  41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                   |
    |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | entityType = parent                   |

Scenario: Update an existing edOrg with invalid API call, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I PUT the "missingEntity" for a "newEducationOrganization" entity to "WHOOPS" at "educationOrganizations/doesNotExistb015c219172d561c62350f0453f3_id "
 Then I should receive a return code of 404
  And deltas collection should have "0" records

Scenario: Create an invalid edOrg with the API, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/vnd.slc+json"
 When I POST a "invalidEducationOrganization" of type "educationOrganization"
 Then I should receive a return code of 403
  And deltas collection should have "0" records

Scenario: As SEA Admin, delete an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I DELETE an "educationOrganization" of id "17eaa66c1fc53cc1ec7d4aa25459d3924525832f_id"
 Then I should receive a return code of 204
 When I trigger a delta extract
  And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
  And I verify "2" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"



@shortcut
@RALLY_US5741
Scenario: Create Student, course offering and section as SEA Admin, users from different LEAs requesting Delta extracts
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
  When I PUT the the following entities:
    | entityName                     |  returnCode  | endpoint                                             |
    | newDaybreakStudent             |  204         | students/9bf3036428c40861238fdc820568fde53e658d88_id |
 When I POST and validate the following entities:
 # Note if you get a 409 after adding an entity, it may have duplicate natural keys of a pre-existing entity
    | entityName                     |  entityType                            |  returnCode  |
    | newClassPeriod3                |  classPeriod                          |  201         |
    | newParentFather                |  parent                                |  201         |
    | newParentMother                |  parent                                |  201         |
    | newStudentFatherAssociation    |  studentParentAssociation              |  201         |
    | newDaybreakCourse              |  course                                |  201         |
    | newCourseOffering              |  courseOffering                        |  201         |
    | newSection                     |  section                               |  201         |
    | newStudentSectionAssociation   |  studentSectionAssociation             |  201         |
    | newHighwindStudent             |  student                               |  201         |
    | HwStudentSchoolAssociation     |  studentSchoolAssociation              |  201         |
    | newStudentAssessment           |  studentAssessment                     |  201         |
    | newGradebookEntry              |  gradebookEntry                        |  201         |
    | newStaff                       |  staff                                 |  201         |
    | newStaffDaybreakAssociation    |  staffEducationOrganizationAssociation |  201         |
    | newStaffHighwindAssociation    |  staffEducationOrganizationAssociation |  201         |
    | newTeacher                     |  teacher                               |  201         |
    | newTeacherEdorgAssociation     |  staffEducationOrganizationAssociation |  201         |
    | newTeacherSchoolAssociation    |  teacherSchoolAssociation              |  201         |
    | newGrade                       |  grade                                 |  201         |
    | newReportCard                  |  reportCard                            |  201         |
    | newStudentAcademicRecord       |  studentAcademicRecord                 |  201         |
    | newAttendanceEvent             |  attendance                            |  201         |
    | newCohort                      |  cohort                                |  201         |
    | newStaffCohortAssociation      |  staffCohortAssociation                |  201         |
    | newStudentCohortAssociation    |  studentCohortAssociation              |  201         |
    | DbGradingPeriod                |  gradingPeriod                         |  201         |
    | DbSession                      |  session                               |  201         |
    | newProgram                     |  program                               |  201         |
    | newStudentProgramAssociation   |  studentProgramAssociation             |  201         |
   #| newStaffProgramAssociation     |  staffProgramAssociation               |  201         |
    | newStudentCompetency           |  studentCompetency                     |  201         |
   #| newDisciplineIncident          |  disciplineIncident                    |  201         |
   #| newDisciplineAction            |  disciplineAction                      |  201         |
   #| newStudentDiscIncidentAssoc    |  studentDisciplineIncidentAssociation  |  201         |
    | newGraduationPlan              |  graduationPlan                        |  201         |
    | newGradingPeriod               |  gradingPeriod                         |  201         |
    | newLearningObjective           |  learningObjective                     |  201         |
    | newLearningStandard            |  learningStandard                      |  201         |
    | newCompetencyLevelDescriptor   |  competencyLevelDescriptor             |  201         |
    | newStudentCompetencyObjective  |  studentCompetencyObjective            |  201         |
    | newSession                     |  session                               |  201         |
    | newSEACourse                   |  course                                |  201         |
    | newSEACourseOffering           |  courseOffering                        |  201         |
    | newAssessment                  |  assessment                            |  201         |
    | newBECalendarDate              |  calendarDate                          |  201         |
    | newStudentGradebookEntry       |  studentGradebookEntry                 |  201         |
    | newBellSchedule3               |  bellSchedule                          |  201         |
    
     When I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 Then I PATCH and validate the following entities:
 # Note if "value" is empty in this table, the patched field will be set to the string "value"
    |  fieldName            |  entityType                 | value            |  returnCode  | endpoint                                                                |
    |  date                 |  attendance                 | 2013-08-29       |  204         | attendances/95b973e29368712e2090fcad34d90fffb20aa9c4_id                 |

 When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "the public extract"
  And I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  program                               |
        |  gradingPeriod                         |
        |  learningObjective                     |
        |  learningStandard                      |
        |  competencyLevelDescriptor             |
        |  studentCompetencyObjective            |
        |  session                               |
        |  course                                |
        |  courseOffering                        |
        |  assessment                            |
        |  graduationPlan                        |
        |  calendarDate                          |
        |  section                               |
        |  cohort                                |
        |  classPeriod                           |
        |  bellSchedule                          |
  And I verify this "classPeriod" file should contain:
        | id                                          | condition                                                               |
        | d7873d123c22d3277c923132fa0bc90d742f205f_id | classPeriodName = Xth Period                                            |
        | d7873d123c22d3277c923132fa0bc90d742f205f_id | educationOrganizationId = 884daa27d806c2d725bc469b273d840493f84b4d_id   |
   And I verify this "bellSchedule" file should contain:
        | id                                          | condition                                                               |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | bellScheduleName = Maths 18                                             |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | educationOrganizationId = 884daa27d806c2d725bc469b273d840493f84b4d_id   |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | meetingTime.classPeriodId = d7873d123c22d3277c923132fa0bc90d742f205f_id |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | meetingTime.startTime = 13:00:00.000                                    |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | meetingTime.endTime = 13:55:00.000                                      |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | gradeLevels  = ["Tenth grade"]                                          |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | calendarDateReference = 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id     |
  And I verify this "program" file should contain:
        | id                                          | condition                                |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programType = Regular Education          |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programId = 12345                        |
  And I verify this "gradingPeriod" file should contain:
        | id                                          | condition                                |
        | 8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id | endDate = 2014-05-22                     |
        | 1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id | entityType = gradingPeriod               |
  And I verify this "learningObjective" file should contain:
        | id                                          | condition                                |
        | bc2dd61ff2234eb25835dbebe22d674c8a10e963_id | description = Description                |
  And I verify this "learningStandard" file should contain:
        | id                                          | condition                                |
        | 1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id | description = Description                |
  And I verify this "competencyLevelDescriptor" file should contain:
        | id                                          | condition                                |
        | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | description = Description                |
  And I verify this "studentCompetencyObjective" file should contain:
        | id                                          | condition                                |
        | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | description = Description                |
  And I verify this "session" file should contain:
        | id                                          | condition                                |
        | fe6e1a162e6f6825830d78d72cb55498afaedcd3_id | endDate = 2015-06-11                     |
        | 227097db8525f4631d873837754633daf8bfcb22_id | entityType = session                     |
  And I verify this "course" file should contain:
        | id                                          | condition                                |
        | 494d4c8281ec78c7d8634afb683d39f6afdc5b85_id | courseDescription = new SEA course |
        | 877e4934a96612529535581d2e0f909c5288131a_id | entityType = course                       |
  And I verify this "courseOffering" file should contain:
        | id                                          | condition                                |
        | 0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id | courseId = 877e4934a96612529535581d2e0f909c5288131a_id |
  And I verify this "assessment" file should contain:
        | id                                          | condition                                |
        | 8d58352d180e00da82998cf29048593927a25c8e_id | contentStandard = State Standard |
  And I verify this "graduationPlan" file should contain:
        | id                                          | condition                                |
        | a77cdbececc81173aa76a34c05f9aeb44126a64d_id | individualPlan = false |
  And I verify this "calendarDate" file should contain:
        | id                                          | condition                                |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | date = 2015-04-02                        |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | calendarEvent = Instructional day        |
  And I verify this "section" file should contain:
    | id                                          | condition                                                      |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | entityType = section                                           |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | sessionId = bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id        |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | courseOfferingId = 38edd8479722ccf576313b4640708212841a5406_id |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id         |
  And I verify this "cohort" file should contain:
    | id                                          | condition                                                |
    | cb99a7df36fadf8885b62003c442add9504b3cbd_id | entityType = cohort                                      |
    | cb99a7df36fadf8885b62003c442add9504b3cbd_id | cohortIdentifier = new-cohort-1                          |
 When I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds

  And format "application/json"
 Then I PATCH and validate the following entities:
    |  fieldName            |  entityType                 | value                                       |  returnCode  | endpoint                                                                |
    |  patchGradeLevels     |  bellSchedule               | Grade 13                                    |  204         | bellSchedules/25fcdbb0dec785bef50d85e9345cec8d1083348f_id                |
    |  patchProgramType     |  program                    | Adult/Continuing Education                  |  204         | programs/0ee2b448980b720b722706ec29a1492d95560798_id                    |
    |  patchEndDate         |  gradingPeriod              | 2015-07-01                                  |  204         | gradingPeriods/8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id              |
    |  patchDescription     |  learningObjective          | Patched description                         |  204         | learningObjectives/bc2dd61ff2234eb25835dbebe22d674c8a10e963_id          |
    |  patchDescription     |  learningStandard           | Patched description                         |  204         | learningStandards/1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id           |
    |  patchDescription     |  competencyLevelDescriptor  | Patched description                         |  204         | competencyLevelDescriptor/ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id   |
    |  patchDescription     |  studentCompetencyObjective | Patched description                         |  204         | studentCompetencyObjectives/ef680988e7c411cdb5438ded373512cd59cbfa7b_id |
    |  patchEndDate         |  session                    | 2015-06-12                                  |  204         | sessions/fe6e1a162e6f6825830d78d72cb55498afaedcd3_id                    |
    |  patchCourseDesc      |  course                     | Patched description                         |  204         | courses/494d4c8281ec78c7d8634afb683d39f6afdc5b85_id                     |
    |  patchCourseId        |  courseOffering             | 06ccb498c620fdab155a6d70bcc4123b021fa60d_id |  204         | courseOfferings/0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id             |
    |  patchContentStd      |  assessment                 | National Standard                           |  204         | assessments/8d58352d180e00da82998cf29048593927a25c8e_id                 |
    |  patchIndividualPlan  |  graduationPlan             | true                                        |  204         | graduationPlans/a77cdbececc81173aa76a34c05f9aeb44126a64d_id             |
    |  calendarEvent        |  calendarDate               | Holiday                                     |  204         | calendarDates/c7af73b8f98390a6d695a9e458529d6a149f0a21_id               |
  
 Given the unpack directory is empty
 When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I generate and retrieve the bulk extract delta via API for "the public extract"
   And I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  program                               |
        |  gradingPeriod                         |
        |  learningObjective                     |
        |  learningStandard                      |
        |  competencyLevelDescriptor             |
        |  studentCompetencyObjective            |
        |  session                               |
        |  course                                |
        |  courseOffering                        |
        |  assessment                            |
        |  graduationPlan                        |
        |  calendarDate                          |
        |  bellSchedule                          |
  And I verify this "bellSchedule" file should contain:
        | id                                          | condition                                |
        | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | gradeLevels = ["Grade 13"]               |
  And I verify this "program" file should contain:
        | id                                          | condition                                |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programType = Adult/Continuing Education |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programId = 12345                        |
  And I verify this "gradingPeriod" file should contain:
        | id                                          | condition                                |
        | 8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id | endDate = 2015-07-01                     |
  And I verify this "learningObjective" file should contain:
        | id                                          | condition                                |
        | bc2dd61ff2234eb25835dbebe22d674c8a10e963_id | description = Patched description        |
  And I verify this "learningStandard" file should contain:
        | id                                          | condition                                |
        | 1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id | description = Patched description        |
  And I verify this "competencyLevelDescriptor" file should contain:
        | id                                          | condition                                |
        | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | description = Patched description        |
  And I verify this "studentCompetencyObjective" file should contain:
        | id                                          | condition                                |
        | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | description = Patched description        |
  And I verify this "session" file should contain:
        | id                                          | condition                                |
        | fe6e1a162e6f6825830d78d72cb55498afaedcd3_id | endDate = 2015-06-12                     |
  And I verify this "course" file should contain:
        | id                                          | condition                                |
        | 494d4c8281ec78c7d8634afb683d39f6afdc5b85_id | courseDescription = Patched description  |
  And I verify this "courseOffering" file should contain:
        | id                                          | condition                                              |
        | 0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id | courseId = 06ccb498c620fdab155a6d70bcc4123b021fa60d_id |
  And I verify this "assessment" file should contain:
        | id                                          | condition                                              |
        | 8d58352d180e00da82998cf29048593927a25c8e_id | contentStandard = National Standard                |
  And I verify this "graduationPlan" file should contain:
        | id                                          | condition                                |
        | a77cdbececc81173aa76a34c05f9aeb44126a64d_id | individualPlan = true                    |
  And I verify this "calendarDate" file should contain:
        | id                                          | condition                                |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | date = 2015-04-02                        |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | calendarEvent = Holiday                  |

 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  attendance                            |
        |  grade                                 |
        |  gradebookEntry                        |
        |  parent                                |
        |  reportCard                            |
        |  staff                                 |
        |  staffCohortAssociation                |
        |  staffEducationOrganizationAssociation |
        |  student                               |
        |  studentAcademicRecord                 |
        |  studentAssessment                     |
        |  studentCohortAssociation              |
        |  studentParentAssociation              |
        |  studentSectionAssociation             |
        |  teacher                               |
        |  teacherSchoolAssociation              |
        |  studentProgramAssociation             |
        |  studentCompetency                     |
        |  studentGradebookEntry                 |

  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "student" file should contain:
    | id                                          | condition                             |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | loginId = new-student-min@bazinga.org |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | studentUniqueStateId = nsmin-1        |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | sex = Male                            |
  And I verify this "parent" file should contain:
    | id                                          | condition                             |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                   |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | loginId = new-dad@bazinga.org         |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | parentUniqueStateId = new-dad-1       |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | sex = Male                            |
    | 41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | parentUniqueStateId = new-mom-1       |
  And I verify this "studentParentAssociation" file should contain:
    | id                                                                                     | condition                                                |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation                    |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | parentId = 41f42690a7c8eb5b99637fade00fc72f599dab07_id   |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | relation = Father                                        |
  And I verify this "studentSectionAssociation" file should contain:
    | id                                                                                     | condition                                                |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | entityType = studentSectionAssociation                   |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | beginDate = 2013-08-27                                   |
  And I verify this "studentAssessment" file should contain:
    | id                                          | condition                                                   |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | entityType = studentAssessment                              |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | administrationDate = 2013-09-24                             |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | assessmentId = 8e6fceafe05daef1da589a1709ee278ba51d337a_id  |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | gradeLevelWhenAssessed = Eleventh grade                     |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id     |
  And I verify this "gradebookEntry" file should contain:
    | id                                                                                     | condition                                                      |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | entityType = gradebookEntry                                    |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | sectionId = 4030207003b03d055bba0b5019b31046164eff4e_id        |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | gradingPeriodId = 21b8ac38bf886e78a879cfdb973a9352f64d07b9_id  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | gradebookEntryType = Homework                                  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | dateAssigned = 2014-02-21                                      |
  And I verify this "staff" file should contain:
    | id                                          | condition                                                   |
    | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | entityType = staff                                          |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | entityType = staff                                          |
  And I verify this "staffEducationOrganizationAssociation" file should contain:
    | id                                          | condition                                                                    |
    | 9a5f2cc89f85609b5c188362e4ff767e02bc4483_id | entityType = staffEducationOrganizationAssociation                           |
    | 9a5f2cc89f85609b5c188362e4ff767e02bc4483_id | staffReference = 2472b775b1607b66941d9fb6177863f144c5ceae_id                 |
    | 9a5f2cc89f85609b5c188362e4ff767e02bc4483_id | educationOrganizationReference = a13489364c2eb015c219172d561c62350f0453f3_id |
    | afef1537920d10e093a8d301efbb463e364f8079_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | afef1537920d10e093a8d301efbb463e364f8079_id | educationOrganizationReference = 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
    | f44b0a272ba009b9668151070806e132f9e38364_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | f44b0a272ba009b9668151070806e132f9e38364_id | educationOrganizationReference = 99d527622dcb51c465c515c0636d17e085302d5e_id |
  And I verify this "teacher" file should contain:
    | id                                          | condition                                                   |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | entityType = teacher                                        |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | loginId = new-teacher-1@fakemail.com                        |
  And I verify this "teacherSchoolAssociation" file should contain:
    | id                                          | condition                                                   |
    | 7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id | entityType = teacherSchoolAssociation                       |
  And I verify this "grade" file should contain:
    | id                                          | condition                                                   |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | entityType = grade |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | sectionId = 4030207003b03d055bba0b5019b31046164eff4e_id |
  And I verify this "reportCard" file should contain:
    | id                                                                                     | condition               |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | entityType = reportCard |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id |
  And I verify this "studentAcademicRecord" file should contain:
    | id                                                                                     | condition                          |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | entityType = studentAcademicRecord |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id |
  And I verify this "attendance" file should contain:
    | id                                          | condition                                                |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | entityType = attendance                                  |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id   |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | schoolYearAttendance.attendanceEvent.sectionId = 4030207003b03d055bba0b5019b31046164eff4e_id   |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | schoolYearAttendance.attendanceEvent.date = 2013-08-29   |   
  And I verify this "staffCohortAssociation" file should contain:
    | id                                          | condition                                                |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | entityType = staffCohortAssociation                      |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | staffId = 2472b775b1607b66941d9fb6177863f144c5ceae_id    |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | cohortId = cb99a7df36fadf8885b62003c442add9504b3cbd_id   |
  And I verify this "studentCohortAssociation" file should contain:
    | id                                          | condition                                                |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | entityType = studentCohortAssociation                   |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | cohortId = cb99a7df36fadf8885b62003c442add9504b3cbd_id  |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id |
  And I verify this "studentProgramAssociation" file should contain:
    | id                                          | condition                                                |
    | 9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id | entityType = studentProgramAssociation                  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id | educationOrganizationId = 1b223f577827204a1c7e9c851dba06bea6b031fe_id |

 Given the extract download directory is empty
  When I request the latest bulk extract delta via API for "<IL-HIGHWIND>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  staff                                 |
        |  staffEducationOrganizationAssociation |
        |  student                               |
        |  studentSchoolAssociation              |

  And I log into "SDK Sample" with a token of "lstevenson", a "IT Administrator" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "student" file should contain:
    | id                                          | condition                             |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | entityType = student                  |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | loginId = new-hw-student1@bazinga.org |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | studentUniqueStateId = hwmin-1        |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | sex = Female                          |
  And I verify this "studentSchoolAssociation" file should contain:
    | id                                          | condition                                                |
    | d913396aef918602b8049027dbdce8826c054402_id | entityType = studentSchoolAssociation                    |
    | d913396aef918602b8049027dbdce8826c054402_id | studentId = b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |
    | d913396aef918602b8049027dbdce8826c054402_id | schoolId = 1b5de2516221069fd8f690349ef0cc1cffbb6dca_id   |
#    | d913396aef918602b8049027dbdce8826c054402_id | exitWithdrawDate = 2014-05-22                            |
    | d913396aef918602b8049027dbdce8826c054402_id | entryDate = 2013-08-27                                   |
  And I verify this "staff" file should contain:
    | id                                          | condition                                                |
    | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | entityType = staff                                       |
  And I verify this "staffEducationOrganizationAssociation" file should contain:
    | id                                          | condition                                                                    |
    | afef1537920d10e093a8d301efbb463e364f8079_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | afef1537920d10e093a8d301efbb463e364f8079_id | educationOrganizationReference = 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
    | f44b0a272ba009b9668151070806e132f9e38364_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | f44b0a272ba009b9668151070806e132f9e38364_id | educationOrganizationReference = 99d527622dcb51c465c515c0636d17e085302d5e_id |

  # Now delete the recently added entities and check the delete file
  Given I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I DELETE and validate the following entities:
    |  entity                     |  id                                           |  returnCode  |
    |  attendance                 |  95b973e29368712e2090fcad34d90fffb20aa9c4_id  |  204         |
    |  studentAssessment          |  d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id  |  204         |
    |  staffCohortAssociation     |  5e7d5f12cefbcb749069f2e5db63c1003df3c917_id  |  204         |
    |  gradebookEntry             |  4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id  |  204         |
    |  grade                      |  1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id  |  204         |
    |  reportCard                 |  1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id  |  204         |
    |  studentAcademicRecord      |  1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id  |  204         |
    |  studentCohortAssociation   |  9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id  |  204         |
    |  studentProgramAssociation  |  9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id  |  204         |
    |  studentSectionAssociation  |  4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id  |  204         |
    |  studentParentAssociation   |  9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id  |  204         |
    |  cohort                     |  cb99a7df36fadf8885b62003c442add9504b3cbd_id  |  204         |
    |  section                    |  4030207003b03d055bba0b5019b31046164eff4e_id  |  204         |
    |  courseOffering             |  38edd8479722ccf576313b4640708212841a5406_id  |  204         |
    |  course                     |  877e4934a96612529535581d2e0f909c5288131a_id  |  204         |
    |  staff                      |  e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id  |  204         |
    |  staffEducationOrganizationAssociation |  afef1537920d10e093a8d301efbb463e364f8079_id  |  204         |
    |  teacherSchoolAssociation   |  7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id  |  204         |
    |  teacher                    |  2472b775b1607b66941d9fb6177863f144c5ceae_id  |  204         |
    |  parent                     |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |  204         |
    |  parent                     |  41f42690a7c8eb5b99637fade00fc72f599dab07_id  |  204         |
    |  student                    |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  studentCompetency          |  6a74f51699a7554f89865e004695913aee277117_id  |  204         |
    |  studentSchoolAssociation   |  cbfe3a47491fdff0432d5d4abca339735da9461d_id  |  204         |
    |  session                    |  227097db8525f4631d873837754633daf8bfcb22_id  |  204         |
    |  gradingPeriod              |  1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id  |  204         |
    |  program                    |  0ee2b448980b720b722706ec29a1492d95560798_id  |  204         |
    |  gradingPeriod              |  8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id  |  204         |
    |  learningObjective          |  bc2dd61ff2234eb25835dbebe22d674c8a10e963_id  |  204         |
    |  learningStandard           |  1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id  |  204         |
    |  competencyLevelDescriptor  |  ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id  |  204         |
    |  studentCompetencyObjective |  ef680988e7c411cdb5438ded373512cd59cbfa7b_id  |  204         |
    |  session                    |  fe6e1a162e6f6825830d78d72cb55498afaedcd3_id  |  204         |
    |  course                     |  494d4c8281ec78c7d8634afb683d39f6afdc5b85_id  |  204         |
    |  courseOffering             |  0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id  |  204         |
    |  assessment                 |  8d58352d180e00da82998cf29048593927a25c8e_id  |  204         |
    |  graduationPlan             |  a77cdbececc81173aa76a34c05f9aeb44126a64d_id  |  204         |
    |  calendarDate               |  c7af73b8f98390a6d695a9e458529d6a149f0a21_id  |  204         |
    |  bellSchedule               |  25fcdbb0dec785bef50d85e9345cec8d1083348f_id  |  204         |
    |  classPeriod                |  d7873d123c22d3277c923132fa0bc90d742f205f_id  |  204         |

 Given the extraction zone is empty
  When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
  And I verify this "deleted" file should contain:
    | id                                          | condition                                |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | entityType = attendance                  |
    | cb99a7df36fadf8885b62003c442add9504b3cbd_id | entityType = cohort                      |
    | 877e4934a96612529535581d2e0f909c5288131a_id | entityType = course                      |
    | 38edd8479722ccf576313b4640708212841a5406_id | entityType = courseOffering              |
    | 1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id | entityType = gradingPeriod               |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                      |
    | 41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | entityType = parent                      |
    | 227097db8525f4631d873837754633daf8bfcb22_id | entityType = session                     |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | entityType = staffCohortAssociation      |
    | afef1537920d10e093a8d301efbb463e364f8079_id | entityType = staffEducationOrganizationAssociation |
    | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | entityType = staff                       |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                     |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | entityType = studentAssessment           |
    | 6a74f51699a7554f89865e004695913aee277117_id | entityType = studentCompetency           |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation    |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | entityType = teacher                     |
    | 7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id | entityType = teacherSchoolAssociation    |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | entityType = gradebookEntry            |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | entityType = grade                     |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | entityType = reportCard                |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | entityType = studentAcademicRecord     |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | entityType = studentCohortAssociation  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id | entityType = studentProgramAssociation |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | entityType = studentSectionAssociation |
    | c7af73b8f98390a6d695a9e458529d6a149f0a21_id                                            | entityType = calendarDate              |

  When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I retrieve the bulk extract delta via API for "the public extract"
  Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  deleted                               |
  And I verify this "deleted" file should contain:
       | id                                          | condition                                |
       | 0ee2b448980b720b722706ec29a1492d95560798_id | entityType = program                     |
       | 8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id | entityType = gradingPeriod               |
       | bc2dd61ff2234eb25835dbebe22d674c8a10e963_id | entityType = learningObjective           |
       | 1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id | entityType = learningStandard            |
       | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | entityType = competencyLevelDescriptor   |
       | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | entityType = studentCompetencyObjective  |
       | fe6e1a162e6f6825830d78d72cb55498afaedcd3_id | entityType = session                     |
       | 494d4c8281ec78c7d8634afb683d39f6afdc5b85_id | entityType = course                      |
       | 0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id | entityType = courseOffering              |
       | 8d58352d180e00da82998cf29048593927a25c8e_id | entityType = assessment                  |
       | a77cdbececc81173aa76a34c05f9aeb44126a64d_id | entityType = graduationPlan              |
       | 4030207003b03d055bba0b5019b31046164eff4e_id | entityType = section                     |
       | d7873d123c22d3277c923132fa0bc90d742f205f_id | entityType = classPeriod                 |
       | 25fcdbb0dec785bef50d85e9345cec8d1083348f_id | entityType = bellSchedule                |

Scenario: Test access to the api
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
  Then I should receive a return code of 200
  Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And the sli securityEvent collection is empty 
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
  Then I should receive a return code of 403
  And I should see a count of "2" in the security event collection 
  And I check to find if record is in sli db collection:
 | collectionName | expectedRecordCount | searchParameter      | searchValue                                                 | searchType |
 | securityEvent  | 2                   | body.tenantId        | Midgar                                                      | string     |
 | securityEvent  | 2                   | body.appId           | vavedRa9uB                                                  | string     |
 | securityEvent  | 2                   | body.className       | org.slc.sli.api.resources.BulkExtract                       | string     |
 | securityEvent  | 2                   | body.userEdOrg       | IL-DAYBREAK                                                 | string     |
 | securityEvent  | 1                   | body.targetEdOrgList | IL-HIGHWIND                                                 | string     |
 | securityEvent  | 1                   | body.logMessage      | Access Denied:User is not authorized to access this extract | string     |
  And "2" security event with field "body.actionUri" matching "http.*/api/rest/v1..*/bulk/extract/99d527622dcb51c465c515c0636d17e085302d5e_id/delta/.*" should be in the sli db
  
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request an unsecured latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>"
  Then I should receive a return code of 400
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "pavedz00ua"
  Then I should receive a return code of 403
  Given I log into "Paved Z00" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id paved>" clientId "<client id paved>"
  Then I should receive a return code of 200
  Given I log into "Paved Z00" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id paved>" clientId "<client id paved>"
  Then I should receive a return code of 403

Scenario: Trigger a public delta extract and check security events
   Given I ingest "bulk_extract_sea_delta_security_event.zip"
   And the following collections are empty in sli datastore:
     | collectionName              |
     | securityEvent               |
   And I trigger a delta extract
   Then I should see following map of entry counts in the corresponding sli db collections:
     | collectionName             | count |
     | securityEvent              | 5     |
  And I check to find if record is in sli db collection:
     | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
     | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                                             | string          |
     | securityEvent   | 2                   | body.className          | org.slc.sli.bulk.extract.extractor.DeltaExtractor                            | string          |
     | securityEvent   | 1                   | body.actionUri          | Bulk extract execution                                                       | string          |
     | securityEvent   | 1                   | body.actionUri          | Delta Extract Initiation                                                     | string          |
     | securityEvent   | 1                   | body.actionUri          | Delta Extract Finished                                                       | string          |
     | securityEvent   | 2                   | body.actionUri          | Writing extract file to the file system                                      | string          |
     | securityEvent   | 1                   | body.logMessage         | Generating archive for app 19cca28d-7357-4044-8df9-caad4b1c8ee4              | string          |
     | securityEvent   | 1                   | body.logMessage         | Generating archive for app 22c2a28d-7327-4444-8ff9-caad4b1c7aa3              | string          |
     | securityEvent   | 0                   | body.targetEdOrgList    | 884daa27d806c2d725bc469b273d840493f84b4d_id                                  | string          |

Scenario: Verify that the TeacherContextResolver works properly
  Given I clean the bulk extract file system and database
  Given the extraction zone is empty
  When I ingest "StaffAppend.zip"
  And I trigger a delta extract
  And I request the latest bulk extract delta using the api
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "352e8570bd1116d11a72755b987902440045d346_id" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  staffEducationOrganizationAssociation              |
  And I verify this "staffEducationOrganizationAssociation" file should contain:
    | id                                          | condition                             |
    | 78ded3395d49f8099bf2aa75ade2f7ca181fbae1_id | staffReference = 8107c5ce31cec58d4ac0b647e91b786b03091f02_id                  |
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "1b223f577827204a1c7e9c851dba06bea6b031fe_id" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  staffEducationOrganizationAssociation              |
  And I verify this "staffEducationOrganizationAssociation" file should contain:
    | id                                          | condition                             |
    | 10dd6d6902f6c6699be9a9a77bd75471409c0d34_id | staffReference = 8107c5ce31cec58d4ac0b647e91b786b03091f02_id                  |


  Scenario: Be a good neighbor and clean up before you leave
    Given I clean the bulk extract file system and database
