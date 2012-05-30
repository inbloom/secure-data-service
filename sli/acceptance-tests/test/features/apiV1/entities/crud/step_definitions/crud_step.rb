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

$entityData = {
  "gradingPeriod" => {
    "gradingPeriod" => "Summer Semester", 
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
    "studentId" => "eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d", 
    "sessionId" => "67ce204b-9999-4a11-aacb-000000000003"
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
    "gradesOffered" => "Eighth grade",
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
  "disciplineAction" => {
    "disciplineActionIdentifier" => "Discipline act XXX",
    "disciplines" => [[
        {"codeValue" => "Discp Act 3"},
        {"shortDescription" => "Disciplinary Action 3"},
        {"description" => "Long disciplinary Action 3"}
    ]],
    "disciplineDate" => "2012-01-28",
    "disciplineIncidentId" => ["0e26de79-22aa-5d67-9201-5113ad50a03b"],
    "studentId" => ["7a86a6a7-1f80-4581-b037-4a9328b9b650"],
    "responsibilitySchoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2",
    "assignmentSchoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2"
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
    "schoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2"
  },
  "educationOrganization" => {
    "organizationCategories" => ["State Education Agency"],
    "stateOrganizationId" => "15",
    "nameOfInstitution" => "Gotham City School District", 
    "address" => []
  },
  "gradebookEntry" => {
    "gradebookEntryType" => "Quiz", 
    "dateAssigned" => "2012-02-14", 
    "sectionId" => "58c9ef19-c172-4798-8e6e-c73e68ffb5a3"
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
    "schoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2",
    "sessionId" => "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e",
    "courseId" => "53777181-3519-4111-9210-529350429899"
  },
  "session" => {
    "sessionName" => "Spring 2012",
    "schoolYear" => "2011-2012",
    "term" => "Spring Semester",
    "beginDate" => "2012-01-01",
    "endDate" => "2012-06-31",
    "totalInstructionalDays" => 80,
    "gradingPeriodReference" => ["b40a7eb5-dd74-4666-a5b9-5c3f4425f130"] 
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
    "gradebookEntryId" => "008fd89d-88a2-43aa-8af1-74ac16a29380", 
    "letterGradeEarned" => "A", 
    "sectionId" => "706ee3be-0dae-4e98-9525-f564e05aa388", 
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
      "studentId" => "714c1304-8a04-4e23-b043-4ad80eb60992"
      #"gradingPeriodId" => "TODO"
  }
}

Given /^entity URI "([^"]*)"$/ do |arg1|
  @entityUri = arg1
end


Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = $entityData[arg1]
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
