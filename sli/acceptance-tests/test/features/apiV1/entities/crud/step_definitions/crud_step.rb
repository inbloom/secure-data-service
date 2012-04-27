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
  id = $newEntityUri                            if human_readable_id == "NEWLY CREATED ENTITY URI"
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"

  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

$entityData = {
  "attendance" => {
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085",
    "schoolId" => "4cb03fa0-81ad-46e2-6505-09ab31af377e",
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
    "educationOrgId" => "9f5cb095-8e99-49a9-b130-bedfa20639d2",
    "programId" => ["cb292c7d-3503-414a-92a2-dc76a1585d79"]
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
    "careerPathway" => "Hospitality and Tourism"
  },
  "disciplineAction" => {
    "disciplineActionIdentifier" => "Discipline act XXX",
    "disciplines" => [{
        "codeValue" => "Discp Act 3",
        "shortDescription" => "Disciplinary Action 3",
        "educationOrganizationId" => ["1d303c61-88d4-404a-ba13-d7c5cc324bc5"]
    }],
    "disciplineDate" => "2012-01-28",
    "disciplineIncidentId" => ["0e26de79-22aa-5d67-9201-5113ad50a03b"],
    "studentId" => ["7a86a6a7-1f80-4581-b037-4a9328b9b650"],
    "responsibilitySchoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2",
    "assignmentSchoolId" => "2058ddfb-b5c6-70c4-3bee-b43e9e93307d"
  },
  "disciplineIncident" => {
    "incidentIdentifier" => "Incident ID XXX",
    "incidentDate" => "2012-02-14", 
    "incidentTime" => "01:00:00", 
    "incidentLocation" => "On School",
    "behaviors" => [{
        "shortDescription" => "Behavior 012 description",
        "codeValue" => "BEHAVIOR 012",
        "behaviorCategory" => "School Code of Conduct",
        "educationOrganizationId" => ["1d303c61-88d4-404a-ba13-d7c5cc324bc5"]
    }],
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
    "totalInstructionalDays" => 80
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
  }
}

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = $entityData[arg1]
end

Then /^I should receive a new entity URI$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = URI(headers['location'][0])
  $newEntityUri = s.path[10..-1]
  assert($newEntityUri != nil, "After POST, URI is nil")
end

Then /^the tenant ID of the entity should be "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  tenant = result["metaData"]["tenantId"]
  assert(tenant == arg1, "Tenant ID expected #{arg1} but was #{tenant}")
end
