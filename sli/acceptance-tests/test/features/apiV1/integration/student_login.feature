@RALLY_US4305
@RALLY_US4306

Feature: As a student or staff I want to use apps that access the inBloom API

  Background: None

  Scenario: As a student, I want access to my data to use educational apps
  # Log in via simple-idp and authenticate student credentials
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    When I navigate to GET "/v1/home"
    Then I should validate all the HATEOS links

  Scenario: I check the response body fields of specific API endpoints
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And I am accessing data about myself, "matt.sollars"
    When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id":
      | field                                                 | value                                       |
      | id                                                    | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
      | entityType                                            | student                                     |
      | studentUniqueStateId                                  | 800000025                                   |
      | sex                                                   | Female                                      |
      | oldEthnicity                                          | White, Not Of Hispanic Origin               |
      | profileThumbnail                                      | 800000025 thumb                             |
      | limitedEnglishProficiency                             | NotLimited                                  |
      | displacementStatus                                    | Status BBB                                  |
      | hispanicLatinoEthnicity                               | false                                       |
      #| studentCharacteristics.beginDate                      | 20013-04-20                                 |
      | name.firstName                                        | Matt                                        |
      | name.middleName                                       | Aida                                        |
      | name.lastSurname                                      | Sollars                                     |
      | studentIdentificationCode.0.identificationCode        | abcde                                       |
      | studentIdentificationCode.0.identificationSystem      | District                                    |
      | studentIdentificationCode.0.assigningOrganizationCode | School                                      |
      | address.0.streetNumberName                            | 707 Elm Street                              |
      | electronicMail.0.emailAddress                         | 4859@fakemail.com                           |
      | telephone.0.telephoneNumber                           | (115)555-5072                               |
      | homeLanguages.0.language                              | Cambodian (Khmer)                           |
      | studentIndicators.0.indicator                         | Indicator 1                                 |
      | otherName.0.lastSurname                               | Charles                                     |
      | languages.0.language                                  | Norwegian                                   |
      | programParticipations.0.program                       | Adult/Continuing Education                  |
      | section504Disabilities.0                              | Sensory Impairment                          |
      | cohortYears.0.schoolYear                              | 2011-2012                                   |
      | race.0                                                | Black - African American                    |
      | disabilities.0.disability                             | Deaf-Blindness                              |
      | studentCharacteristics.0.endDate                      | 2014-08-01                                  |
      | studentCharacteristics.0.designatedBy                 | Teacher                                     |
      | studentCharacteristics.0.characteristic               | Unaccompanied Youth                         |

    When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/parents?sortBy=parentUniqueStateId":
      | field                           | value             |
      | 0.address.0.streetNumberName    | 707 Elm Street    |
      | 0.electronicMail.0.emailAddress | 9008@fakemail.com |
      | 0.loginId                       | 9008@fakemail.com |
      | 0.name.firstName                | Brenton           |
      | 0.otherName.0.firstName         | Brice             |
      | 0.parentUniqueStateId           | 800000025-dad     |
      | 0.sex                           | Male              |
      | 0.telephone.0.telephoneNumber   | (512)555-2418     |
      | 1.parentUniqueStateId           | 800000025-mom     |

    Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentAssessments":
      | field                                                                          |
      | 0.administrationDate                                                           |
      | 0.administrationEndDate                                                        |
      | 0.administrationEnvironment                                                    |
      | 0.administrationLanguage.language                                              |
      | 0.administrationLanguage.identificationSystem                                  |
      | 0.gradeLevelWhenAssessed                                                       |
      | 0.linguisticAccommodations.0                                                   |
      | 0.performanceLevelDescriptors.0.0.codeValue                                    |
      | 0.reasonNotTested                                                              |
      | 0.retestIndicator                                                              |
      | 0.scoreResults.0.result                                                        |
      | 0.scoreResults.0.assessmentReportingMethod                                     |
      | 0.serialNumber                                                                 |
      | 0.specialAccommodations                                                        |
      | 0.studentAssessmentItems.0.assessmentItem.correctResponse                      |
      | 0.studentAssessmentItems.0.assessmentItem.identificationCode                   |
      | 0.studentAssessmentItems.0.assessmentItem.itemCategory                         |
      | 0.studentAssessmentItems.0.assessmentItem.maxRawScore                          |
      | 0.studentAssessmentItems.0.assessmentItem.nomenclature                         |
      | 0.studentAssessmentItems.0.assessmentItem.learningStandards                    |
      | 0.studentAssessmentItems.0.assessmentItemResult                                |
      | 0.studentAssessmentItems.0.assessmentResponse                                  |
      | 0.studentAssessmentItems.0.rawScoreResult                                      |
      | 0.studentAssessmentItems.0.responseIndicator                                   |
      | 0.studentAssessmentItems.0.responseIndicator                                   |
      | 0.studentObjectiveAssessments.0.objectiveAssessment.assessmentPerformanceLevel |
      | 0.studentObjectiveAssessments.0.objectiveAssessment.identificationCode         |
      | 0.studentObjectiveAssessments.0.objectiveAssessment.maxRawScore                |
      | 0.studentObjectiveAssessments.0.objectiveAssessment.nomenclature               |
      | 0.studentObjectiveAssessments.0.objectiveAssessment.percentOfAssessment        |
      | 0.studentObjectiveAssessments.0.objectiveAssessment.learningObjectives         |

    Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentAssessments/assessments":
      | field                                                                              |
      | 0.academicSubject                                                                  |
      | 0.assessmentCategory                                                               |
      | 0.assessmentFamilyHierarchyName                                                    |
      | 0.assessmentForm                                                                   |
      | 0.assessmentIdentificationCode                                                     |
      | 0.assessmentPeriodDescriptor.beginDate                                             |
      | 0.assessmentPeriodDescriptor.codeValue                                             |
      | 0.assessmentPeriodDescriptor.description                                           |
      | 0.assessmentPeriodDescriptor.endDate                                               |
      | 0.assessmentPeriodDescriptor.shortDescription                                      |
      | 0.assessmentTitle                                                                  |
      | 0.contentStandard                                                                  |
      | 0.gradeLevelAssessed                                                               |
      | 0.lowestGradeLevelAssessed                                                         |
      | 0.maxRawScore                                                                      |
      | 0.nomenclature                                                                     |
      | 0.objectiveAssessment.0.assessmentPerformanceLevel                                 |
      | 0.objectiveAssessment.0.identificationCode                                         |
      | 0.objectiveAssessment.0.maxRawScore                                                |
      | 0.objectiveAssessment.0.nomenclature                                               |
      | 0.objectiveAssessment.0.percentOfAssessment                                        |
      | 0.objectiveAssessment.0.learningObjectives                                         |
      | 0.objectiveAssessment.0.assessmentPerformanceLevel.0.assessmentReportingMethod     |
      | 0.objectiveAssessment.0.assessmentPerformanceLevel.0.maximumScore                  |
      | 0.objectiveAssessment.0.assessmentPerformanceLevel.0.minimumScore                  |
      | 0.objectiveAssessment.0.assessmentPerformanceLevel.0.performanceLevelDescriptor    |
      | 0.objectiveAssessment.0.objectiveAssessments.0.identificationCode                  |
      | 0.objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.correctResponse    |
      | 0.objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.identificationCode |
      | 0.objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.itemCategory       |
      | 0.objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.maxRawScore        |
      | 0.objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.nomenclature       |
      | 0.objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.learningStandards  |
      | 0.revisionDate                                                                     |
  #| 0.version                                                                          |
    Then I verify the following response body fields exist in "/learningObjectives?limit=1":
      | field                                     |
      | 0.academicSubject                         |
      | 0.description                             |
      | 0.learningObjectiveId.contentStandardName |
      | 0.learningObjectiveId.identificationCode  |
      | 0.objective                               |
      | 0.objectiveGradeLevel                     |
    Then I verify the following response body fields exist in "/learningStandards/c772fbb0f9b9210d1f2a1bfcd53018b205c46da6_id":
      | field                                  |
      | contentStandard                        |
      #| courseTitle                                |
      | description                            |
      | gradeLevel                             |
      | learningStandardId.contentStandardName |
      | learningStandardId.identificationCode  |
      | subjectArea                            |

    Then I verify the following response body fields exist in "/schools/a13489364c2eb015c219172d561c62350f0453f3_id":
      | field                              |
      | administrativeFundingControl       |
      | charterStatus                      |
      | gradesOffered                      |
      | magnetSpecialProgramEmphasisSchool |
      | schoolCategories                   |
      | schoolType                         |
      | titleIPartASchoolDesignation       |

    Then I verify the following response body fields exist in "/educationOrganizations/884daa27d806c2d725bc469b273d840493f84b4d_id/courses":
      | field                               |
      | 0.careerPathway                     |
      | 0.courseCode.0.identificationSystem |
      | 0.courseCode.0.ID                   |
      | 0.courseDefinedBy                   |
      | 0.courseDescription                 |
      | 0.courseGPAApplicability            |
      | 0.courseTitle                       |
      | 0.dateCourseAdopted                 |
      | 0.gradesOffered                     |
      | 0.highSchoolCourseRequirement       |
      | 0.maximumAvailableCredit.credit     |
      | 0.minimumAvailableCredit.credit     |
      | 0.numberOfParts                     |
      | 0.subjectArea                       |
      | 0.uniqueCourseId                    |
    Then I verify the following response body fields exist in "/schools/a13489364c2eb015c219172d561c62350f0453f3_id/courseOfferings":
      | field              |
      | 0.localCourseCode  |
      | 0.localCourseTitle |
    Then I verify the following response body fields exist in "/educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id/sessions":
      | field                    |
      | 0.beginDate              |
      | 0.endDate                |
      | 0.schoolYear             |
      | 0.sessionName            |
      | 0.term                   |
      | 0.totalInstructionalDays |
    Then I verify the following response body fields exist in "/gradingPeriods/e71e876487c72d1c1c0e9f7fa413815706e7f422_id":
      | field                               |
      | beginDate                           |
      | endDate                             |
      | gradingPeriodIdentity.schoolYear    |
      | gradingPeriodIdentity.gradingPeriod |
      | gradingPeriodIdentity.schoolId      |
    Then I verify the following response body fields exist in "/educationOrganizations/884daa27d806c2d725bc469b273d840493f84b4d_id/graduationPlans":
      | field                                                      |
      | 0.creditsByCourse.0.courseCode.0.identificationSystem      |
      | 0.creditsByCourse.0.courseCode.0.ID                        |
      | 0.creditsByCourse.0.courseCode.0.assigningOrganizationCode |
      | 0.creditsByCourse.0.credits.credit                         |
      | 0.creditsByCourse.0.credits.creditConversion               |
      | 0.creditsByCourse.0.credits.creditType                     |
      | 0.creditsByCourse.0.gradeLevel                             |
      | 0.creditsBySubject.0.credits.credit                        |
      | 0.creditsBySubject.0.credits.creditConversion              |
      | 0.creditsBySubject.0.credits.creditType                    |
      | 0.creditsBySubject.0.subjectArea                           |
      | 0.graduationPlanType                                       |
      | 0.individualPlan                                           |
      | 0.totalCreditsRequired                                     |
    Then I verify the following response body fields exist in "/competencyLevelDescriptor/c91ae4718903d20289607c3c4335759e652ad569_id":
      | field                     |
      | codeValue                 |
      | description               |
      | performanceBaseConversion |
    Then I verify the following response body fields exist in "/educationOrganizations/884daa27d806c2d725bc469b273d840493f84b4d_id/studentCompetencyObjectives":
      | field                          |
      | 0.objective                    |
      | 0.objectiveGradeLevel          |
      | 0.studentCompetencyObjectiveId |

    Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentProgramAssociations/programs":
      | field                    |
      | 0.programId              |
      | 0.programSponsor         |
      | 0.programType            |
      | 0.services.0.0.codeValue |

    Then I verify the following response body fields exist in "/sections/24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id":
      | field                  |
      | availableCredit        |
      | educationalEnvironment |
      | mediumOfInstruction    |
      | populationServed       |
      | sequenceOfCourse       |
      | uniqueSectionCode      |

  Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/attendances":
    | field                                               |
    | 0.schoolYearAttendance.0.schoolYear                 |
    | 0.schoolYearAttendance.0.attendanceEvent.0.date     |
    | 0.schoolYearAttendance.0.attendanceEvent.0.event    |
    | 0.schoolYearAttendance.0.attendanceEvent.0.reason   |
  Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/yearlyAttendances":
    | field                        |
    | 0.attendanceEvent.0.date     |
    | 0.attendanceEvent.0.event    |
    | 0.attendanceEvent.0.reason   |
    | 0.schoolYear                 |

  Then I verify the following response body fields exist in "/studentSectionAssociations/24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id33ee33e252908a2e95eb8d0b4f85f96ffd4b0bae_id/grades":
    | field                         |
    | 0.diagnosticStatement         |
    | 0.gradeType                   |
    | 0.letterGradeEarned           |
    | 0.numericGradeEarned          |
    | 0.performanceBaseConversion   |
    | 0.schoolYear                  |
  Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentGradebookEntries":
    | field                         |
    | 0.dateFulfilled               |
    | 0.diagnosticStatement         |
    | 0.letterGradeEarned           |
    | 0.numericGradeEarned          |
  Then I verify the following response body fields exist in "/studentAcademicRecords/5aaf89278b7226f668f46509403d86a2b5968978_idf686ca38e2c6acd3eeb149ba351c6da21930e096_id/courseTranscripts":
    | field                                             |
    | 0.additionalCreditsEarned.0.additionalCreditType  |
    | 0.additionalCreditsEarned.0.credit                |
    | 0.courseAttemptResult                             |
    | 0.creditsAttempted                                |
    | 0.creditsEarned                                   |
    | 0.finalLetterGradeEarned                          |
    | 0.finalNumericGradeEarned                         |
    | 0.gradeLevelWhenTaken                             |
    | 0.gradeType                                       |
    | 0.methodCreditEarned                              |
  Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentAcademicRecords":
    | field                                      |
    | 0.academicHonors.0.academicHonorsType      |
    | 0.academicHonors.0.honorAwardDate          |
    | 0.academicHonors.0.honorsDescription       |
    | 0.classRanking.classRank                   |
    | 0.classRanking.classRankingDate            |
    | 0.classRanking.percentageRanking           |
    | 0.classRanking.totalNumberInClass          |
    | 0.cumulativeCreditsAttempted               |
    | 0.cumulativeCreditsEarned                  |
    | 0.gradeValueQualifier                      |
    | 0.projectedGraduationDate                  |
    | 0.recognitions.0.recognitionAwardDate      |
    | 0.recognitions.0.recognitionDescription    |
    | 0.recognitions.0.recognitionType           |
    | 0.schoolYear                               |
  Then I verify the following response body fields exist in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/reportCards":
    | field                       |
    | 0.gpaCumulative             |
    | 0.gpaGivenGradingPeriod     |
    | 0.numberOfDaysAbsent        |
    | 0.numberOfDaysInAttendance  |
    | 0.numberOfDaysTardy         |
    | 0.schoolYear                |

  @student_blacklist
  Scenario: Student should NOT have access to certain fields in API entity response bodies
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And I am accessing data about myself, "matt.sollars"
    When I navigate to GET "/v1/students/067198fd6da91e1aa8d67e28e850f224d6851713_id"
    Then I verify the following response body fields do not exist in the response:
      | field                         |
      | economicDisadvantaged         |
      | schoolFoodServicesEligibility |

  @student_staff
  Scenario: Student should see limited set of fields on staff related entities
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    # staff
    Then I verify the following response body fields exist in "/staff/143760f37839b2608d2c929ef26d30c900f6a434_id":
      | field                       |
      | name                        |
    Then I verify the following response body fields do not exist in the response:
      | field                              |
      | staffUniqueStateId                 |
      | staffIdentificationCode            |
      | otherName                          |
      | sexType                            |
      | birthDate                          |
      | address                            |
      | hispanicLatinoEthnicity            |
      | oldEthnicityType                   |
      | race                               |
      | highestLevelOfEducationCompleted   |
      | yearsOfPriorProfessionalExperience |
      | yearsOfPriorTeachingExperience     |
      | credentials                        |
      | loginId                            |
    # teacher
    Then I verify the following response body fields exist in "/teachers/4b07dba2b6868c0827315b99ea94fc74c0f7c902_id":
      | field                       |
      | name                        |
    Then I verify the following response body fields do not exist in the response:
      | field                              |
      | staffUniqueStateId                 |
      | staffIdentificationCode            |
      | otherName                          |
      | sexType                            |
      | birthDate                          |
      | address                            |
      | hispanicLatinoEthnicity            |
      | oldEthnicityType                   |
      | race                               |
      | highestLevelOfEducationCompleted   |
      | yearsOfPriorProfessionalExperience |
      | yearsOfPriorTeachingExperience     |
      | credentials                        |
      | loginId                            |
    # staffEdorgAssociation
    Then I verify the following response body fields exist in "/staffEducationOrgAssignmentAssociations/472d10b94e4a24aa78f5f03e33a92d0ca4af336b_id":
      | field                          |
      | staffReference                 |
      | educationOrganizationReference |
    Then I verify the following response body fields do not exist in the response:
      | field                         |
      | staffClassification           |
      | positionTitle                 |
      | beginDate                     |
      | endDate                       |
    # staffCohortAssociations
    Then I verify the following response body fields exist in "/staffCohortAssociations/ee3d3035994b88d465553f38a265fff4597946d2_id":
      | field                       |
      | staffId                     |
      | cohortId                    |
    Then I verify the following response body fields do not exist in the response:
      | field                         |
      | beginDate                     |
      | endDate                       |
      | studentRecordAccess           |
    # staffProgramAssociations
    Then I verify the following response body fields exist in "/staffProgramAssociations/2cc6a6a3e5990518e6c196630873b6adc0736b86_id":
      | field                       |
      | staffId                     |
      | programId                   |
    Then I verify the following response body fields do not exist in the response:
      | field                         |
      | beginDate                     |
      | endDate                       |
      | studentRecordAccess           |
    # teacherSectionAssociation 
    Then I verify the following response body fields exist in "/teacherSectionAssociations/e9b81633cba273dc9cc567d7f0f76a1c070c150d_id2d275caf63e615e3d699f39cae4714084366024d_id":
      | field                       |
      | teacherId                   |
      | sectionId                   |
    Then I verify the following response body fields do not exist in the response:
      | field                         |
      | beginDate                     |
      | endDate                       |
      | highlyQualifiedTeacher        |
      | classroomPosition             |
    # teacherSchoolAssociation
    Then I verify the following response body fields exist in "/teacherSchoolAssociations/8495e720e4f1261f3845aeb1f499ec40359669a5_id":
      | field                       |
      | teacherId                   |
      | schoolId                    |
    Then I verify the following response body fields do not exist in the response:
      | field                         |
      | programAssignment             |
      | academicSubjects              |
    

  @student_parent
  Scenario: Student can see all parent fields
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And I am accessing data about myself, "matt.sollars"
    Then I verify the following response body fields exist in "/parents/7f5b783a051b72820eab5f8188c45ade72869f0f_id":
      | field                             |
      | sex                               |
      | parentUniqueStateId               |
      | name.lastSurname                  |
      | name.middleName                   |
      | name.firstName                    |
      | electronicMail.0.emailAddress     |
      | electronicMail.0.emailAddressType |
      | telephone.0.telephoneNumber       |
      | telephone.0.telephoneNumberType   |
    Then I verify the following response body fields exist in "/parents/5f8989384287747b1960d16edd95ff2bb318e3bd_id":
      | field                             |
      | sex                               |
      | parentUniqueStateId               |
      | name.lastSurname                  |
      | name.middleName                   |
      | name.firstName                    |
      | electronicMail.0.emailAddress     |
      | electronicMail.0.emailAddressType |
      | telephone.0.telephoneNumber       |
      | telephone.0.telephoneNumberType   |

  @student_parent
  Scenario: Student can see all studentParentAssociations fields
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And I am accessing data about myself, "matt.sollars"
    Then I verify the following response body fields exist in "/studentParentAssociations/067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id":
      | field                  |
      | parentId               |
      | livesWith              |
      | studentId              |
      | relation               |
      | contactPriority        |
      | emergencyContactStatus |
    Then I verify the following response body fields exist in "/studentParentAssociations/067198fd6da91e1aa8d67e28e850f224d6851713_ide2f8c24b3e1ab8ead6e134d661a464d0f90e4c8e_id":
      | field                  |
      | parentId               |
      | livesWith              |
      | studentId              |
      | relation               |
      | contactPriority        |
      | emergencyContactStatus |
