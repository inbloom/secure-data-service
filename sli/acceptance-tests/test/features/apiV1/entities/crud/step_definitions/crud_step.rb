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
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  #general
  id = @entityUri                               if human_readable_id == "ENTITY URI"
  id = @newId                                   if human_readable_id == "NEWLY CREATED ENTITY ID"
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"

  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################


Given /^entity URI "([^"]*)"$/ do |arg1|
  @entityUri = arg1
end


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
    "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "schoolYearAttendance" => [{
      "schoolYear" => "2010-2011",
      "attendanceEvent" => [{
        "date" => "2010-09-16",
        "event" => "Tardy"
      }]
    }]
  },

  "studentAcademicRecord" => {
    "studentId" => "61161008-2560-480d-aadf-4b0264dc2ae3_id",
    "sessionId" => "d23ebfc4-5192-4e6c-a52b-81cee2319072",
    "schoolYear" => "2010-2011"
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
    "schoolId" => "67ce204b-9999-4a11-aaab-000000000008",
    "localCourseCode" => "LCCMA1",
    "sessionId" => "67ce204b-9999-4a11-aacb-000000000002",
    "localCourseTitle" => "Math 1 - Intro to Mathematics",
    "courseId" => "67ce204b-9999-4a11-aacc-000000000004"
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
    "assessmentReferences" => ["c757f9f2dc788924ce0715334c7e86735c5e1327_id"]
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

  "super_assessment" => {
     "assessmentIdentificationCode"=> [{
        "identificationSystem"=> "State",
        "ID"=> "2001-Seventh grade Assessment 2"
     }],
     "objectiveAssessment"=> [{
        "nomenclature"=> "Nomenclature",
        "percentOfAssessment"=> 50,
        "identificationCode"=> "2001-Seventh grade Assessment 2.OA-2",
        "learningObjectives"=> ["df9165f2-653e-df27-a86c-bfc5f4b7577d"],
        "maxRawScore"=> 50,
        "objectiveAssessments"=> [{
          "nomenclature"=> "Nomenclature",
          "percentOfAssessment"=> 50,
          "identificationCode"=> "2001-Seventh grade Assessment 2.OA-2 Sub",
          "learningObjectives"=> ["df9165f2-653e-df27-a86c-bfc5f4b7577d"],
          "maxRawScore"=> 50,
          "objectiveAssessments"=> []
        }],
        "assessmentItem"=> [{
           "identificationCode"=> "2001-Seventh grade Assessment 2#3",
           "correctResponse"=> "true",
           "learningStandards"=> [],
           "maxRawScore"=> 10,
           "itemCategory"=> "True-False"
        }]
      }],
      "assessmentFamilyHierarchyName"=> "2001 Standard.2001 Seventh grade Standard",
      "assessmentItem"=> [{
        "identificationCode"=> "2001-Seventh grade Assessment 2#3",
        "correctResponse"=> "true",
        "learningStandards"=> [],
        "maxRawScore"=> 10,
        "itemCategory"=> "True-False"
        }, {
        "identificationCode"=> "2001-Seventh grade Assessment 2#1",
        "correctResponse"=> "true",
        "learningStandards"=> [],
        "maxRawScore"=> 10,
        "itemCategory"=> "True-False"
        }],
      "assessmentPerformanceLevel"=> [],
      "gradeLevelAssessed"=> "Seventh grade",
      "assessmentTitle"=> "2001-Seventh grade Assessment 2",
      "version" => 2 
  },

  "studentAssessment" => {
      "administrationDate" => "2001-08-28",
      "administrationLanguage" => "English",
      "studentId" => "274f4c71-1984-4607-8c6f-0a91db2d240a_id",
      "assessmentId" => "cc0a56b97a0c58c01fbd9e960c05e542c3755336_id",
      "scoreResults" => [
         {
           "result" => "68",
           "assessmentReportingMethod" => "Scale score"
        }],
      "administrationEnvironment" => "Classroom",
      "retestIndicator" => "Primary Administration",
      "linguisticAccommodations" => [ ],
      "studentAssessmentItems" => [
      {
          "assessmentItemResult" => "Correct",
          "rawScoreResult" => 10,
          "assessmentItem" => {
            "identificationCode"=> "2001-Seventh grade Assessment 2#3",
            "correctResponse"=> "true",
            "learningStandards"=> [],
            "maxRawScore"=> 10,
            "itemCategory"=> "True-False"
           }
       }],
      "studentObjectiveAssessments" => [
        {
          "scoreResults" => [
          {
            "result" => "28",
            "assessmentReportingMethod" => "Scale score"
          }],
          "objectiveAssessment" => {
            "nomenclature"=> "Nomenclature",
            "percentOfAssessment"=> 50,
            "identificationCode"=> "2001-Seventh grade Assessment 2.OA-2",
            "learningObjectives"=> ["df9165f2-653e-df27-a86c-bfc5f4b7577d"],
            "maxRawScore"=> 50
          }
        },
        { 
          "scoreResults" => [
          {
            "result" => "24",
            "assessmentReportingMethod" => "Scale score"
          }],
          "objectiveAssessment" => {
            "nomenclature"=> "Nomenclature",
            "percentOfAssessment"=> 50,
            "identificationCode"=> "2001-Seventh grade Assessment 2.OA-2 Sub",
            "learningObjectives"=> ["df9165f2-653e-df27-a86c-bfc5f4b7577d"],
            "maxRawScore"=> 50,
            "objectiveAssessments"=> []
          }
        }]
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
    "studentSectionAssociationId" => "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id49b277c3-4639-42c2-88ef-0f59dd5acba2_id",
    "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
    "sectionId" => "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id",
    "letterGradeEarned" => "B+",
    "gradeType" => "Final",
    "schoolYear" => "2010-2011"
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
      "gradingPeriodId" => "ef72b883-90fa-40fa-afc2-4cb1ae17623b",
      "schoolYear" => "2011-2012"
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
    }
}
  @fields = @entityData[arg1]
end

When /^I create an association of type "([^"]*)"$/ do |type|
  @assocData = {
    "studentCohortAssocation" => {
       "cohortId" => @newId,
       "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
       "endDate" => "2020-01-15",
       "beginDate" => "2011-04-01"
    },
    "courseOffering" => {
      "localCourseCode" => "LCC7252GR2",
      "localCourseTitle" => "German 2 - Outro to German",
      "sessionId" => "0410354d-dbcb-0214-250a-404401060c93",
      "courseId" => @newId,
      "schoolId" => "92d6d5a0-852c-45f4-907a-912752831772"
    },
    "section" => {
       "educationalEnvironment" => "Classroom",
       "sessionId" => "0410354d-dbcb-0214-250a-404401060c93",
       "populationServed" => "Regular Students",
       "sequenceOfCourse" => 3,
       "uniqueSectionCode" => "Motorcycle Repair 101",
       "mediumOfInstruction" => "Independent study",
       "programReference" => [],
       "courseOfferingId" => @assocId,
       "schoolId" => "92d6d5a0-852c-45f4-907a-912752831772",
       "availableCredit" => nil
    },
    "studentDisciplineIncidentAssociation" => {
       "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
       "disciplineIncidentId" => @newId,
       "studentParticipationCode" => "Reporter"
    },
    "studentParentAssociation" => {
       "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
       "parentId" => @newId,
       "livesWith" => true,
       "primaryContactStatus" => true,
       "relation" => "Father",
       "contactPriority" => 0,
       "emergencyContactStatus" => true
    },
    "studentProgramAssociation" => {
       "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
       "programId" => @newId,
       "beginDate" => "2011-05-01",
       "educationOrganizationId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    },
    "studentSectionAssociation" => {
      "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
      "sectionId" => @newId,
      "beginDate" => "2012-05-01"
    },
    "staffEducationOrganizationAssociation" => {
      "educationOrganizationReference" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
      "staffReference" => @newId,
      "beginDate" => "2000-01-01",
      "positionTitle" => "Hall monitor",
      "staffClassification" => "School Administrative Support Staff"
    },
    "staffEducationOrganizationAssociation2" => {
      "educationOrganizationReference" => "92d6d5a0-852c-45f4-907a-912752831772",
      "staffReference" => @newId,
      "beginDate" => "2000-01-01",
      "positionTitle" => "Hall monitor",
      "staffClassification" => "School Administrative Support Staff"
    },
    "studentSectionAssociation2" => {
      "studentId" => @newId,
      "sectionId" => "15ab6363-5509-470c-8b59-4f289c224107_id",
      "beginDate" => "2012-05-01"
    },
    "teacherSchoolAssociation" => {
      "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
      "programAssignment" => "Regular Education",
      "teacherId" => @newId,
      "instructionalGradeLevels" => ["First grade"],
      "academicSubjects" => ["Composite"]
    },
    "teacherSchoolAssociation2" => {
      "schoolId" => "92d6d5a0-852c-45f4-907a-912752831772",
      "programAssignment" => "Regular Education",
      "teacherId" => @newId,
      "instructionalGradeLevels" => ["First grade"],
      "academicSubjects" => ["Composite"]
    },
    "studentParentAssociation2" => {
      "parentId" => @newId,
      "studentId" => "737dd4c1-86bd-4892-b9e0-0f24f76210be_id",
      "livesWith" => true,
      "primaryContactStatus" => true,
      "relation" => "Father",
      "contactPriority" => 0,
      "emergencyContactStatus" => true
    },
    "staffProgramAssociation" => {
      "programId" => @newId,
      "staffId" => "b4c2a73f-336d-4c47-9b47-2d24871eef96",
      "beginDate" => "2012-01-01"
    }
  }
  @fields = @assocData[type]
end

When /^I POST the association of type "([^"]*)"$/ do |type|
  @assocUrl = {
    "studentCohortAssocation" => "studentCohortAssociations",
    "courseOffering" => "courseOfferings",
    "section" => "sections",
    "studentDisciplineIncidentAssociation" => "studentDisciplineIncidentAssociations",
    "studentParentAssociation" => "studentParentAssociations",
    "studentProgramAssociation" => "studentProgramAssociations",
    "studentSectionAssociation" => "studentSectionAssociations",
    "staffEducationOrganizationAssociation" => "staffEducationOrgAssignmentAssociations",
    "staffEducationOrganizationAssociation2" => "staffEducationOrgAssignmentAssociations",
    "studentSectionAssociation2" => "studentSectionAssociations",
    "teacherSchoolAssociation" => "teacherSchoolAssociations",
    "teacherSchoolAssociation2" => "teacherSchoolAssociations",
    "studentParentAssociation2" => "studentParentAssociations",
    "staffProgramAssociation" => "staffProgramAssociations"
  }
  if type != ""
    api_version = "v1.1"
    step "I navigate to POST \"/#{api_version}/#{@assocUrl[type]}\""
    headers = @res.raw_headers
    assert(headers != nil, "Headers are nil")
    assert(headers['location'] != nil, "There is no location link from the previous request")
    s = headers['location'][0]
    @assocId = s[s.rindex('/')+1..-1]
  end
end

Then /^I should receive a new entity URI$/ do
  step "I should receive an ID for the newly created entity"
  assert(@newId != nil, "After POST, URI is nil")
end

Then /^the tenant ID of the entity should be "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  tenant = result["metaData"]["tenantId"]
  assert(tenant == arg1, "Tenant ID expected #{arg1} but was #{tenant}")
end

Given /^my contextual access is defined by table:$/ do |table|
  @ctx={}
  table.hashes.each do |hash|
  @ctx[hash["Context"]]=hash["Ids"]
  end
end

Then /^uri was rewritten to "(.*?)"$/ do |expectedUri|
  version = "v1.1"
  root = expectedUri.match(/\/(.+?)\/|$/)[1]
  expected = version+expectedUri
  actual = @headers["x-executedpath"][0]

  #First, make sure the paths of the URIs are the same
  expectedPath = expected.gsub("@ids", "[^/]+")
  assert(actual.match(expectedPath), "Rewriten URI path didn't match, expected:#{expectedPath}, actual:#{actual}")

  #Then, validate the list of ids are the same
  ids = []
  if @ctx.has_key? root
    idsString = actual.match(/v1.1\/[^\/]*\/([^\/]*)\/?/)[1]
    actualIds = idsString.split(",")
    expectedIds = @ctx[root].split(",")
    
    assert(actualIds.length == expectedIds.length,"Infered Context IDs not equal: expected:#{expectedIds.inspect}, actual:#{actualIds.inspect}")
    expectedIds.each do |id|
      assert(actualIds.include?(id),"Infered Context IDs not equal: expected:#{expectedIds.inspect}, actual:#{actualIds.inspect}")
    end
  end
end

And /^field "(.*?)" is removed from the json document$/ do |arg1|
  puts @fields.inspect 
  @fields.delete "beginDate" 
  puts @fields.inspect 
end

Then /^I should see all entities$/ do
  jsonResult = JSON.parse(@res.body)
  apiSet = Set.new
  dbSet = Set.new
  currentEntity = @entity_type_to_uri.key(@entityUri)

  #Get entity ids from the api call
  jsonResult.each do |data|
    apiSet.add(data["id"])
  end

  #Get entity ids from the database
  @conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  @db = @conn.db(convertTenantIdToDbName("Midgar"))
  @coll = @db[currentEntity]

  @coll.find.each do |doc|
    dbSet.add(doc["_id"])
  end

  @conn.close
  diffSet = dbSet.difference(apiSet)

  #difference should be 0 between two non-empty sets of entity ids
  assert(apiSet.empty? == false, "Api returned 0 entities of type #{currentEntity}.")
  assert(dbSet.empty? == false, "No entities of type #{currentEntity} found in the database.")
  assert(diffSet.empty?, "Did not receive the expected entities:
    \n Number of expected (api) entities: #{apiSet.size}
    \n Number of actual (database) entities: #{dbSet.size}
    \n Outstanding entities: #{diffSet.inspect}")
end

Then /^I verify "(.*?)" and "(.*?)" should be subdoc'ed in mongo for this new "(.*?)"$/ do |subdoc1, subdoc2, type|
  @conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  @db = @conn.db(convertTenantIdToDbName("Midgar"))
  @coll = @db[type]
  entity = @coll.find_one("_id"=>@newId);
  assert(entity, "#{type} entity is not created")
  [subdoc1, subdoc2].each { |subdoc|
    if (type == "studentAssessment")
      subdoc_in_body = subdoc+"s"
    else 
      subdoc_in_body = subdoc
    end
    assert(!entity["body"][subdoc_in_body], "#{subdoc_in_body} still exists inside #{type}'s body") 
    assert(entity[subdoc], "#{subdoc} does not exists as a subdoc in #{type}") 
  }
end

Then /^I verify "(.*?)" and "(.*?)" is collapsed in response body$/ do |subdoc1, subdoc2| 
  [subdoc1, subdoc2].each { |subdoc|
    assert(@res[subdoc], "#{subdoc} does not exists in response body")
  }
end

Then /^"(.*?)" is hierachical with childrens at "(.*?)"$/ do |parent, child|
  result = JSON.parse(@res.body)
  assert(result[parent][0][child], "#{parent} does not contain any child at #{child}")
end

Then /^I verify there are "(.*?)" "(.*?)" in response body$/ do |count, type|
  result = JSON.parse(@res.body)
  assert(result[type].size == count.to_i, "expect #{count} #{type} in response body, got #{result[type].size} only")
end

Then /^I delete both studentAssessment and Assessment$/ do 
    step "I navigate to DELETE \"/v1/studentAssessments/df555ad7f41ee2d637371d8688dd517c3728d9d7_id\""
    step "I should receive a return code of 204"
    step "I navigate to DELETE \"/v1/assessments/cc0a56b97a0c58c01fbd9e960c05e542c3755336_id\""
    step "I should receive a return code of 204"
end
