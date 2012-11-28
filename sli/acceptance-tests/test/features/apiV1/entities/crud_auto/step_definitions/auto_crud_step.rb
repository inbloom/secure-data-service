=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require_relative '../../../../utils/sli_utils.rb'
require_relative '../../common.rb'
require_relative '../../../utils/api_utils.rb'

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
    "studentId" => "0c2756fd-6a30-4010-af79-488d6ef2735a",
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
    "studentId" => "61161008-2560-480d-aadf-4b0264dc2ae3",
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
    "educationOrgId" => "92d6d5a0-852c-45f4-907a-912752831772",
    "programId" => ["9b8cafdc-8fd5-11e1-86ec-0021701f543f"]
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
    "studentId" => ["61161008-2560-480d-aadf-4b0264dc2ae3"],
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
    "endDate" => "2012-06-31",
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
    "gradebookEntryId" => "20120613-56b6-4d17-847b-2997b7227686",
    "letterGradeEarned" => "A",
    "sectionId" => "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id",
    "studentId" => "2fab099f-47d5-4099-addf-69120db3b53b",
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
    "studentSectionAssociationId" => "00cbf81b-41df-4bda-99ad-a5717d3e81a1",
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
     "studentSectionAssociationId" => "00cbf81b-41df-4bda-99ad-a5717d3e81a1"
  },
  "reportCard" => {
      "grades" => ["ef42e2a2-9942-11e1-a8a9-68a86d21d918"],
      "studentCompetencyId" => ["3a2ea9f8-9acf-11e1-add5-68a86d83461b"],
      "gpaGivenGradingPeriod" => 3.14,
      "gpaCumulative" => 2.9,
      "numberOfDaysAbsent" => 15,
      "numberOfDaysInAttendance" => 150,
      "numberOfDaysTardy" => 10,
      "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
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
       "educationOrganizationId" => "b1bd3db6-d020-4651-b1b8-a8dba688d9e1",
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
When /^I navigate to POST for each resource available$/ do
  successList =[]
  failurList = []
  resources.each do |resource|
    begin
    #post is not allowed for associations
    if (resource.include? "ssociation") == false
      steps %Q{
          Given a valid entity json document for a \"#{resource[1..-2]}\"
          When I navigate to POST \"/v1#{resource}\"
          Then I should receive a return code of 201
          And I should receive an ID for the newly created entity
      }
      assert(@newId != nil, "After POST, URI is nil")
      steps %Q{
          When I navigate to GET \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 200
          And the response should contain the appropriate fields and values
      }
      #test DELETE operation
      steps %Q{
          When I navigate to DELETE \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 204
          And I navigate to GET \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 404
      }
      puts  "|#{resource[1..-2]}|"
    end
    rescue =>e
      $stderr.puts"#{resource} ==> #{e}"
    end
  end
end

def resources
  config_path = File.expand_path("../../../../../../../../api/src/main/resources/wadl/v1_resources.json", __FILE__)
  v1_resources = JSON.parse(File.read(config_path))['resources']
  get_resource_paths v1_resources
end

def get_resource_paths resources, base = ""
  paths = []
  resources.each do |resource|
    paths << base + resource['path']
    if resource.has_key? 'subResources'
      #do nothing for now
    end
  end
  paths
end
