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


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'uri'
include REXML
require_relative '../../utils/sli_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^\/(<[^"]*>)$/ do |human_readable_id|

  #general
  id = "/v1/" + @entityUri                               if human_readable_id == "<ENTITY URI>"
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

$entityData = {
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
    "studentId" => "1563ec1d-924d-4c02-8099-3a0e314ef1d4",
    "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
    "schoolYearAttendance" => [{
      "schoolYear" => "2011-2012",
      "attendanceEvent" => [{
        "date" => "2011-09-16",
        "event" => "Tardy",
        "reason" => "Missed school bus"
      }]
    }]
  },
  "studentAcademicRecord" => {
    "studentId" => "1563ec1d-924d-4c02-8099-3a0e314ef1d4",
    "sessionId" => "c549e272-9a7b-4c02-aff7-b105ed76c904"
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
    "educationOrgId" => "b1bd3db6-d020-4651-b1b8-a8dba688d9e1",
    "programId" => ["9b8c3aab-8fd5-11e1-86ec-0021701f543f"]
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
    "schoolId" => "bd086bae-ee82-4cf2-baf9-221a9407ea07"
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
    "disciplineIncidentId" => ["0e26de79-22ea-5d67-9201-5113ad50a03b"],
    "studentId" => ["1563ec1d-924d-4c02-8099-3a0e314ef1d4"],
    "responsibilitySchoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
    "assignmentSchoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
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
    "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
  },
  "educationOrganization" => {
    "organizationCategories" => ["State Education Agency"],
    "stateOrganizationId" => "15",
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
    "sectionId" => "7295e51e-cd51-4901-ae67-fa33966478c7"
  },
  "learningObjective" => {
    "academicSubject" => "Mathematics",
    "objective" => "Math Test",
    "objectiveGradeLevel" => "Fifth grade"
  },
  "learningStandard" => {
    "learningStandardId" => {
     "identificationCode" => "G.SRT.1"},
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
    "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
    "sessionId" => "fb0ac9e8-9e4e-48a0-95d2-ae07ee15ee92",
    "courseOfferingId" => "71e04e2f-c9c0-4f94-b0b6-4a107630ab6a"
  },
  "session" => {
    "sessionName" => "Spring 2012",
    "schoolYear" => "2011-2012",
    "term" => "Spring Semester",
    "beginDate" => "2012-01-01",
    "endDate" => "2012-06-31",
    "totalInstructionalDays" => 80,
    "gradingPeriodReference" => ["b40a7eb5-dd74-4666-a5b9-5c3f4425f130", "ef72b883-90fa-40fa-afc2-4cb1ae17623b"],
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
  "studentSectionGradebookEntry" => {
    "gradebookEntryId" => "20120613-56b6-4d17-847b-2997b7227686",
    "letterGradeEarned" => "A",
    "sectionId" => "7b052b82-66f8-424b-8882-5313d11a8a5d",
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085",
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
    "stateOrganizationId" => "152901001",
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
    "studentSectionAssociationId" => "bac890d6-b580-4d9d-a0d4-8bce4e8d351a",
    "letterGradeEarned" => "B+",
    "gradeType" => "Final"
  },
  "studentCompetency" => {
     "competencyLevel" => [{
       "description" => "really hard competency"
     }],
     "diagnosticStatement" => "passed with flying colors"
  },
  "reportCard" => {
      "grades" => ["708c4e08-9942-11e1-a8a9-68a86d21d918", "708b3c95-9942-11e1-a8a9-68a86d21d918"],
      "studentCompetencyId" => ["b57643e4-9acf-11e1-89a7-68a86d21d918"],
      "gpaGivenGradingPeriod" => 3.14,
      "gpaCumulative" => 2.9,
      "numberOfDaysAbsent" => 15,
      "numberOfDaysInAttendance" => 150,
      "numberOfDaysTardy" => 10,
      "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378"
      #"gradingPeriodId" => "TODO"
  }
}

Given /^entity URI "([^"]*)"$/ do |arg1|
  @entityUri = arg1
end

Then /^I should not receive any metadata/ do
  assert(!@result[0].has_key?("metadata"), "Response contains metadata")
end

