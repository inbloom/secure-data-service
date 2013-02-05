=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'uri'
include REXML
require_relative '../../utils/sli_utils.rb'
require_relative '../../apiV1/utils/api_utils.rb'

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################


Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
@entityData = {
  "gradingPeriod" => {
    "gradingPeriodIdentity" => {
      "educationalOrgIdentity" => [{
        "stateOrganizationId" => "Daybreak Elementary School",
      }],
      "stateOrganizationId" => "Daybreak Elementary School",
      "gradingPeriod" => "First Six Weeks",
      "schoolYear" => "2011-2012"
    },
    "beginDate" => "2012-07-01",
    "endDate" => "2012-07-31",
    "totalInstructionalDays" => 20
  },
  "userAccount" => {
    "userName" => "bob3@bob.com",
    "firstName" => "Bob",
    "lastName" => "Roberts",
    "validated" => false,
    "environment" => "Sandbox"
  },

  "attendance" => {
    "entityType" => "attendance",
    "studentId" => "0c2756fd-6a30-4010-af79-488d6ef2735a_id",
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "schoolYearAttendance" => [{
      "schoolYear" => "2011-2012",
      "attendanceEvent" => [{
        "date" => "2011-09-16",
        "event" => "Tardy"
      }]
    }]
  },
  "studentAcademicRecord" => {
    "studentId" => "61161008-2560-480d-aadf-4b0264dc2ae3_id",
    "sessionId" => "d23ebfc4-5192-4e6c-a52b-81cee2319072"
  },
  "student" => {
    "birthData" => {
      "birthDate" => "1994-04-04"
    },
    "sex" => "Male",
    "studentUniqueStateId" => "123456",
    "economicDisadvantaged" => false,
    "name" => {
      "firstName" => "Mister",
      "middleName" => "John",
      "lastSurname" => "Doe"
    }
  },
  "cohort" => {
    "cohortIdentifier" => "ACC-TEST-COH-4",
    "cohortDescription" => "ultimate frisbee team",
    "cohortType" => "Extracurricular Activity",
    "cohortScope" => "Statewide",
    "academicSubject" => "Physical, Health, and Safety Education",
    "educationOrgId" => "92d6d5a0-852c-45f4-907a-912752831772"
  },
  "course" => {
    "courseTitle" => "Chinese 1",
    "numberOfParts" => 1,
    "courseCode" => [{
      "ID" => "C1",
      "identificationSystem" => "School course code",
      "assigningOrganizationCode" => "Bob's Code Generator"
    }],
    "courseLevel" => "Basic or remedial",
    "courseLevelCharacteristics" => ["Advanced Placement"],
    "gradesOffered" => ["Eighth grade"],
    "subjectArea" => "Foreign Language and Literature",
    "courseDescription" => "Intro to Chinese",
    "dateCourseAdopted" => "2001-01-01",
    "highSchoolCourseRequirement" => false,
    "courseDefinedBy" => "LEA",
    "minimumAvailableCredit" => {
      "credit" => 1.0
    },
    "maximumAvailableCredit" => {
      "credit" => 1.0
    },
    "careerPathway" => "Hospitality and Tourism",
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "uniqueCourseId" => "Chinese-1-10"
  },
  "courseOffering" => {
    "schoolId" => "92d6d5a0-852c-45f4-907a-912752831772",
    "localCourseCode" => "LCCMA1",
    "sessionId" => "4d796afd-b1ac-4f16-9e0d-6db47d445b55",
    "localCourseTitle" => "Math 1 - Intro to Mathematics",
    "courseId" => "9bd92e8e-df8e-4af9-95e7-2efed847d03d"
  },
  "disciplineAction" => {
    "disciplineActionIdentifier" => "Discipline act XXX",
    "disciplines" => [[
        {"codeValue" => "Discp Act 3"},
        {"shortDescription" => "Disciplinary Action 3"},
        {"description" => "Long disciplinary Action 3"}
    ]],
    "disciplineDate" => "2012-01-28",
    "disciplineIncidentId" => ["0e26de79-7efa-5e67-9201-5113ad50a03b"],
    "studentId" => ["61161008-2560-480d-aadf-4b0264dc2ae3_id"],
    "responsibilitySchoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "assignmentSchoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
  },
  "disciplineIncident" => {
    "incidentIdentifier" => "Incident ID XXX",
    "incidentDate" => "2012-02-14",
    "incidentTime" => "01:00:00",
    "incidentLocation" => "On School",
    "behaviors" => [[
        {"shortDescription" => "Behavior 012 description"},
        {"codeValue" => "BEHAVIOR 012"}
    ]],
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
  },
  "educationOrganization" => {
    "organizationCategories" => ["State Education Agency"],
    "stateOrganizationId" => "SomeUniqueSchoolDistrict-2422883",
    "nameOfInstitution" => "Gotham City School District",
    "address" => [
              "streetNumberName" => "111 Ave C",
              "city" => "Chicago",
              "stateAbbreviation" => "IL",
              "postalCode" => "10098",
              "nameOfCounty" => "Wake"
              ]
  },
  "gradebookEntry" => {
    "gradebookEntryType" => "Quiz",
    "dateAssigned" => "2012-02-14",
    "sectionId" => "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id"
  },
  "learningObjective" => {
    "academicSubject" => "Mathematics",
    "objective" => "Learn Mathematics",
    "objectiveGradeLevel" => "Fifth grade"
  },
  "learningStandard" => {
    "learningStandardId" => {
     "identificationCode" => "apiTestLearningStandard"},
    "description" => "a description",
    "gradeLevel" => "Ninth grade",
    "contentStandard"=>"State Standard",
    "subjectArea" => "English"
  },
  "program" => {
    "programId" => "ACC-TEST-PROG-3",
    "programType" => "Remedial Education",
    "programSponsor" => "Local Education Agency",
    "services" => [[
        {"codeValue" => "codeValue3"},
        {"shortDescription" => "Short description for acceptance test program 3"},
        {"description" => "This is a longer description of the services provided by acceptance test program 3. More detail could be provided here."}]]
  },
  "section" => {
    "uniqueSectionCode" => "SpanishB09",
    "sequenceOfCourse" => 1,
    "educationalEnvironment" => "Off-school center",
    "mediumOfInstruction" => "Independent study",
    "populationServed" => "Regular Students",
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "sessionId" => "d23ebfc4-5192-4e6c-a52b-81cee2319072",
    "courseOfferingId" => "00291269-33e0-415e-a0a4-833f0ef38189",
    "assessmentReferences" => ["29f044bd-1449-4fb7-8e9a-5e2cf9ad252a"]
  },
  "session" => {
    "sessionName" => "Spring 2012",
    "schoolYear" => "2011-2012",
    "term" => "Spring Semester",
    "beginDate" => "2012-01-01",
    "endDate" => "2012-06-30",
    "totalInstructionalDays" => 80,
    "gradingPeriodReference" => ["b40a7eb5-dd74-4666-a5b9-5c3f4425f130"],
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
  },
  "staff" => {
    "staffUniqueStateId" => "EMPLOYEE123456789",
    "sex" => "Male",
    "hispanicLatinoEthnicity" => false,
    "highestLevelOfEducationCompleted" => "Bachelor's",
    "name" => {
      "firstName" => "Teaches",
      "middleName" => "D.",
      "lastSurname" => "Students"
    }
  },
  "studentGradebookEntry" => {
    "gradebookEntryId" => "0dbb262b-8a3e-4a7b-82f9-72de95903d91_id20120613-56b6-4d17-847b-2997b7227686_id",
    "letterGradeEarned" => "A",
    "sectionId" => "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id",
    "studentId" => "2fab099f-47d5-4099-addf-69120db3b53b_id",
    "studentSectionAssociationId" => "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id49b277c3-4639-42c2-88ef-0f59dd5acba2_id",
    "numericGradeEarned" => 98,
    "dateFulfilled" => "2012-01-31",
    "diagnosticStatement" => "Finished the quiz in 5 minutes"
  },
  "assessment" => {
    "assessmentTitle" => "Writing Advanced Placement Test",
    "assessmentIdentificationCode" => [{
      "identificationSystem" => "School",
      "ID" => "01234B"
    }],
    "academicSubject" => "Mathematics",
    "assessmentCategory" => "Achievement test",
    "gradeLevelAssessed" => "Adult Education",
    "contentStandard" => "LEA Standard",
    "version" => 2
  },
  "parent" => {
    "parentUniqueStateId" => "ParentID101",
    "name" =>
    { "firstName" => "John",
      "lastSurname" => "Doe",
    }
  },
  "school" => {
    "shortNameOfInstitution" => "SCTS",
    "nameOfInstitution" => "School Crud Test School",
    "webSite" => "www.scts.edu",
    "stateOrganizationId" => "SomeUniqueSchool-24242342",
    "organizationCategories" => ["School"],
    "address" => [
      "addressType" => "Physical",
      "streetNumberName" => "123 Main Street",
      "city" => "Lebanon",
      "stateAbbreviation" => "KS",
      "postalCode" => "66952",
      "nameOfCounty" => "Smith County"
    ],
    "gradesOffered" => [
      "Kindergarten",
      "First grade",
      "Second grade",
      "Third grade",
      "Fourth grade",
      "Fifth grade"
    ]
  },
  "teacher" => {
    "birthDate" => "1954-08-31",
    "sex" => "Male",
    "yearsOfPriorTeachingExperience" => 32,
    "staffUniqueStateId" => "12345678",
    "highlyQualifiedTeacher" => true,
    "highestLevelOfEducationCompleted" => "Master's",
    "name" => {
      "firstName" => "Rafe",
      "middleName" => "Hairfire",
      "lastSurname" => "Esquith"
    }
  },
  "grade" => {
    "studentSectionAssociationId" => "9b02fbd2-0892-4399-a4ea-e048b3315f25_id00cbf81b-41df-4bda-99ad-a5717d3e81a1_id",
    "letterGradeEarned" => "B+",
    "gradeType" => "Final"
  },
  "studentCompetency" => {
     "competencyLevel" => {
       "description" => "really hard competency"
     },
     "objectiveId" => {
       "learningObjectiveId" => "dd9165f2-65be-6d27-a8ac-bdc5f46757b6"
     },
     "diagnosticStatement" => "passed with flying colors",
     "studentSectionAssociationId" => "9b02fbd2-0892-4399-a4ea-e048b3315f25_id00cbf81b-41df-4bda-99ad-a5717d3e81a1_id"
  },
  "reportCard" => {
      "grades" => ["ef42e2a2-9942-11e1-a8a9-68a86d21d918"],
      "studentCompetencyId" => ["3a2ea9f8-9acf-11e1-add5-68a86d83461b"],
      "gpaGivenGradingPeriod" => 3.14,
      "gpaCumulative" => 2.9,
      "numberOfDaysAbsent" => 15,
      "numberOfDaysInAttendance" => 150,
      "numberOfDaysTardy" => 10,
      "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
      "gradingPeriodId" => "ef72b883-90fa-40fa-afc2-4cb1ae17623b"
  },
  "graduationPlan" => {
       "creditsBySubject" => [{
            "subjectArea" => "English",
            "credits" => {
                "creditConversion" => 0,
                "creditType" => "Semester hour credit",
                "credit" => 6
             }
       }],
       "individualPlan" => false,
       "graduationPlanType" => "Minimum",
       "educationOrganizationId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
       "totalCreditsRequired" => {
            "creditConversion" => 0,
            "creditType" => "Semester hour credit",
            "credit" => 32
       }
    },
    "competencyLevelDescriptor" => {
      "description" => "Herman tends to throw tantrums",
      "codeValue" => "Temper Tantrum",
      "performanceBaseConversion" => "Basic"
    },
    "studentCompetencyObjective" => {
        "objectiveGradeLevel" => "Kindergarten",
        "objective" => "Phonemic Awareness",
        "studentCompetencyObjectiveId" => "SCO-K-1",
        "educationOrganizationId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731"
    },
    "studentProgramAssociation" => {
       "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
       "programId" => "9b8cafdc-8fd5-11e1-86ec-0021701f543f_id",
       "beginDate" => "2011-05-01",
       "educationOrganizationId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    },
    "studentSectionAssociation" => {
    "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
    "sectionId" => "15ab6363-5509-470c-8b59-4f289c224107_id",
    "beginDate" => "2012-05-01"
    },
    "studentSchoolAssociation" => {
    "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
    "schoolId"  => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "entryDate" => "2012-01-01",
    "entryGradeLevel" => "Kindergarten"
    },
    "courseTranscript" => {
    "courseAttemptResult" => "Pass",
    "creditsEarned" => {"credit" => 3.0},
    "courseId" => "9bd92e8e-df8e-4af9-95e7-2efed847d03d",
    "gradeType" => "Final",
    "studentAcademicRecordId" => "56afc8d4-6c91-48f9-8a11-de527c1131b7",
    "educationOrganizationReference" => ["92d6d5a0-852c-45f4-907a-912752831772"]
    }
}
  @fields = @entityData[arg1]
  @type = arg1
end


#line 342, graduationplan
#invalid-       "educationOrganizationId" => "b1bd3db6-d020-4651-b1b8-a8dba688d9e1",
#valid+       "educationOrganizationId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",


Then /^I should receive a new entity URI$/ do
  step "I should receive an ID for the newly created entity"
  assert(@newId != nil, "After POST, URI is nil")
end

When /^the entities referenced or associated edorg is out of my context$/ do
  case @type
    when "attendance" then @fields["schoolId"] = "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"
    when "cohort" then @fields["educationOrgId"] = "eb3b8c35-f582-df23-e406-6947249a19f2"
    when "course" then @fields["schoolId"] = "eb3b8c35-f582-df23-e406-6947249a19f2"
    when "courseOffering" then @fields["schoolId"] = "67ce204b-9999-4a11-aaab-000000000008"
    when "disciplineAction" then @fields["responsibilitySchoolId"] = "67ce204b-9999-4a11-aaab-000000000008"
    when "disciplineIncident" then @fields["schoolId"] = "67ce204b-9999-4a11-aaab-000000000008"
    when "gradebookEntry" then @fields["sectionId"] = "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
    when "graduationPlan" then @fields["educationOrganizationId"] = "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"
    when "section" then @fields["schoolId"] = "eb3b8c35-f582-df23-e406-6947249a19f2"
    when "session" then @fields["schoolId"] = "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
    when "studentGradebookEntry" then @fields["sectionId"] = "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
    when "studentProgramAssociation" then @fields["educationOrganizationId"] = "eb3b8c35-f582-df23-e406-6947249a19f2"
    when "studentSectionAssociation" then @fields["sectionId"] = "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
    when "studentSchoolAssociation" then @fields["schoolId"] = "67ce204b-9999-4a11-aaab-000000000008"
    when "courseTranscript" then @fields["courseId"] = "5c7aa39b-3193-4865-a10f-5e1e3c7dc7ea"
  end
end

When /^the entities referenced or associated edorg is in my context$/ do
  # no op. entities already contains valid reference
end

When /^I try to update the previously created entity with an invalid reference$/ do
  step "the entities referenced or associated edorg is out of my context"
  step "I navigate to PUT \"/v1/#@entityUri/#@newId\""
end

When /^I post the entity$/ do
  step "I navigate to POST \"/v1/#@entityUri\""
end

When /^I try to delete an entity that is out of my write context$/ do
  nop = false
  case @type
    when "attendance" then id = "4beb72d4-0f76-4071-92b4-61982dba7a7b"
    when "cohort" then id = "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826_id"
    when "course" then id = "5c7aa39b-3193-4865-a10f-5e1e3c7dc7ea"
    when "courseOffering" then id = "119ecc01-d1ea-473d-bafd-51382158800e"
    when "courseTranscript" then id = "09eced61-edd9-4826-a7bc-137ffecda877"
    when "disciplineAction" then id = "db7f1d4b-9689-b2f4-9281-d88d65999423"
    when "disciplineIncident" then id = "0e26de79-22ea-5d67-9201-5113ad50a03b"
    when "gradebookEntry" then nop = true
    when "graduationPlan" then id = "04e3a07a-a95f-4ac8-88f3-gradplan1115"
    when "section" then id = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id"
    when "session" then id = "c2b11399-1519-44b7-848d-085f268170d2"
    when "studentGradebookEntry" then id = "7f05ef51-c974-4071-b91b-f644f9b087cf"
    when "studentProgramAssociation" then nop = true
    when "studentSectionAssociation" then id = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_ide4bbd30f-6e98-4798-a200-5a71fd658fe6_id"
    when "studentSchoolAssociation" then id = "7c6f61e8-3716-4648-8860-170b62a8f460"
  end

  step "I navigate to DELETE \"/v1/#@entityUri/#{id}\"" unless nop
end
