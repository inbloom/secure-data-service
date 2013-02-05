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
require_relative '../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<(.+)>$/ do |template|
  id = template
  id = @newId.to_s                            if template == "'Previous School' ID"
  id = "737dd4c1-86bd-4892-b9e0-0f24f76210be_id" if template == "'Jones' ID"
  id = "bf88acdb-71f9-4c19-8de8-2cdc698936fe_id" if template == "'Christoff' ID"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if template == "'South Daybreak Elementary' ID"
  id = "92d6d5a0-852c-45f4-907a-912752831772" if template == "'Daybreak Central High' ID"
  id = "706ee3be-0dae-4e98-9525-f564e05aa388_id" if template == "'Valid Section' ID"
  id = "thisisaninvalididsoitshouldreturn404" if template == "'Invalid Section' ID"
  id = @newId                                 if template == "NEWLY CREATED ENTITY ID"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /v1/entity/<Place Holder Id>
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)$/ do |version, uri, template|
  version + uri + Transform(template)
end

# transform /v1/entity/<Place Holder Id>/association
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/[\w-]+)$/ do |version, uri, template, assoc|
  version + uri + Transform(template) + assoc
end

# transform /v1/entity/<Place Holder Id>/association/entity
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/[\w-]+)(\/[\w-]+)$/ do |version, uri, template, assoc, entity|
  version + uri + Transform(template) + assoc + entity
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end

def createXlengthString(x)
  value = ""
  x.times {value << 'a'}
  value
end

Given /^I create a valid base level student object$/ do
  @result = CreateEntityHash.createBaseStudent()
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create a valid base level school object$/ do
  if defined? @result
    oldParentId = @result["parentEducationAgencyReference"] 
  else
    oldParentId = "bd086bae-ee82-4cf2-baf9-221a9407ea07"
  end
  @result = CreateEntityHash.createBaseSchoolRandomId()

  if defined? oldParentId
    @result["parentEducationAgencyReference"] = oldParentId
  end
end

Given /^I create a blank json object$/ do
  @result = Hash[]
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = @result.to_json
  elsif @format == "application/xml"
    data = @result
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Given /^I create a student object with "([^"]*)" set to Guy$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result['sex'] = 'Guy'
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create a student object with sex equal to "([^"]*)" instead of "([^"]*)"$/ do |arg1, arg2|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result['sex'] = arg1
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create a create a school object with "([^"]*)" set to a single map$/ do |arg1|
  @result = CreateEntityHash.createBaseSchoolRandomId()
  @result[arg1] = Hash["streetNumberName" => "123 Elm Street",
                       'city'=>"New York",
                       "stateAbbreviation" => "NY",
                       "postalCode" => "12345"
                       ]
end

Given /^I create the same school object with "([^"]*)" as an array with the same map$/ do |arg1|
  @result = CreateEntityHash.createBaseSchoolRandomId()
  @result[arg1] = [
                    Hash["streetNumberName" => "123 Elm Street",
                         'city'=>"New York",
                         "stateAbbreviation" => "NY",
                         "postalCode" => "12345"
                         ]
                  ]
end


Given /^I create a student object with "([^"]*)" set to an array of names$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result[arg1] = [@result[arg1]]
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create the same student object with "([^"]*)" as a map with the same data$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentDefinedId(@lastStudentId)
end

Given /^an SSA object is valid except for "([^"]*)"$/ do |arg1|
  @result = Hash[
                "studentId" => "714c1304-8a04-4e23-b043-4ad80eb60992_id",
                "entryGradeLevel" => "EIGHTH_GRADE"
                ]
  @result[arg1] = "11111111-1111-1111-1111-111111111111"
end

Given /^I create a student object with "learningStyles.([^"]*)" equal to a string$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result["learningStyles"][arg1] = "no"
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create a student object with "([^"]*)" equal to a integer$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result[arg1] = 12345678
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create a school object with "([^"]*)" equal to a (\d+) character string$/ do |arg1, arg2|
  @result = CreateEntityHash.createBaseSchoolRandomId()
  @result[arg1] = createXlengthString(Integer(arg2))
end

Given /^I create a student object with "([^"]*)" set to a true string$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result[arg1] = "true"
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^I create a student object with "([^"]*)" set to MM\-DD\-YYYY$/ do |arg1|
  @result = CreateEntityHash.createBaseStudentRandomId()
  @result['birthData'][arg1] = "01-01-2012"
  @lastStudentId = @result['studentUniqueStateId']
end

Given /^a valid json document for ([^\"]*)$/ do |entity|
  @result = deep_copy($validationTestData[entity])
end

When /^I navigate to PUT "([^\"]*)"$/ do |url|
  @result = {} if !defined? @result
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I create a blank request body object$/ do
  @result = {}
end

When /^I create a base school object$/ do
  @result = CreateEntityHash.createBaseSchool()
end

When /^"([^\"]*)" has a value of "([^\"]*)"$/ do |key, value|
  @result[key] = value
end

When /^I query ([^\"]*) by ([^\"]*) = ([^\"]*)$/ do |entity_uri, field, value|
  steps %Q{
  Given parameter "#{field}" is "#{value}"
  When I navigate to GET "/v1/#{entity_uri}"
  }
end

When /^I set the ([^"]*) to ([^"]*)$/ do |key, value|
  @result = {} if !defined? @result
  @result[key] = convert(value)
end

Then /^"([^\"]*)" should be "([^\"]*)"$/ do |key, value|
  @result[key].should_not == nil
  @result[key].should == value
  @result.delete(key)
end

Then /^"([^"]*)" should contain "([^"]*)" and "([^"]*)"$/ do |key, val1, val2|
  @result[key].should_not == nil
  assert(@result[key].find(val1) != nil, "School's gradesOffered does not contain #{val1}")
  assert(@result[key].find(val2) != nil, "School's gradesOffered does not contain #{val2}")
  @result.delete(key)
end

Then /^there should be no other contents in the response body other than links$/ do
  @result.delete('links')
  @result.delete('id')
  @result.delete('address')
  @result.delete('organizationCategories')
  @result.delete('schoolCategories')
  @result.delete('metaData')
  @result.delete('parentEducationAgencyReference')
  assert(@result == {}, "The response body still contains data that was previously there but *not* in the PUT data")
end

Then /^a collection of size (\d+)$/ do |arg1|
  assert(@result != nil, "Result collection was nill")
  assert(@result.is_a?(Array), "Result collection was not an Array")
  assert(@result.size == Integer(arg1), "Result collection was not of size #{arg1}")
end

Then /^I should receive a new entity URI$/ do
  step "I should receive an ID for the newly created entity"
  assert(@newId != nil, "After POST, URI is nil")
end

Then /^I should receive only (\d+) record$/ do |count|
  steps "Then a collection of size #{convert(count)}"
end

Then /^the error message should contain "([^\"]*)"$/ do |string|
  assert(@res.body.to_s.include?(string), "Response does not contain the specified string")
end

# Entity data for POST and PUT validation
$validationTestData = {
    "gradingPeriod" => {
        "gradingPeriodIdentity" => {
            "educationalOrgIdentity" => [{
                                             "stateOrganizationId" => "Daybreak Elementary School",
                                         }],
            "stateOrganizationId" => "Daybreak Elementary School",
            "gradingPeriod" => "First Six Weeks",
            "schoolYear" => "2011-2012"
        },
        "beginDate" => "1890-07-01",
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
        "studentId" => "12345678-1234-1234-1234-1234567890ab",
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
        "studentId" => "1563ec1d-924d-4c02-8099-3a0e314ef1d4_id",
        "sessionId" => "c549e272-9a7b-4c02-aff7-b105ed76c904",
        "cumulativeGradePointsEarned" => 99.0
    },
    "student" => {
        "birthData" => {
            "birthDate" => "1994-04-04"
        },
        "sex" => "Male",
        "studentUniqueStateId" => "87654321",
        "economicDisadvantaged" => false,
        "name" => {
            "firstName" => "Mister",
            "middleName" => "John",
            "lastSurname" => "Doe"
        }
    },
    "cohort" => {
        "cohortIdentifier" => "ACC-TEST-COH-4",
        "cohortDescription" => "Validation Test Cohort Desc",
        "cohortType" => "Extracurricular Activity",
        "cohortScope" => "Statewide",
        "academicSubject" => "Physical, Health, and Safety Education",
        "educationOrgId" => "b1bd3db6-d020-4651-b1b8-a8dba688d9e1",
        "programId" => ["9b8c3aab-8fd5-11e1-86ec-0021701f543f_id"]
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
        "courseDescription" => "Validation Test Course Desc",
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
        "disciplineActionIdentifier" => "Validation Test Discip. Act. ID",
        "disciplines" => [[
                              {"codeValue" => "Discp Act 3"},
                              {"shortDescription" => "Disciplinary Action 3"},
                              {"description" => "Long disciplinary Action 3"}
                          ]],
        "disciplineDate" => "2012-01-28",
        "disciplineIncidentId" => ["0e26de79-22ea-5d67-9201-5113ad50a03b"],
        "studentId" => ["1563ec1d-924d-4c02-8099-3a0e314ef1d4_id"],
        "responsibilitySchoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
        "assignmentSchoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
    },
    "disciplineIncident" => {
        "incidentIdentifier" => "Validation Test Discip. Inc. ID",
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
        "nameOfInstitution" => "Validation Test School District",
        "address" => [
            "streetNumberName" => "111 Ave C",
            "city" => "Chicago",
            "stateAbbreviation" => "IL",
            "postalCode" => "10098",
            "nameOfCounty" => "Wake"
        ]
    },
    "gradebookEntry" => {
        "gradebookEntryType" => "Validation Test GBE Type",
        "dateAssigned" => "2012-02-14",
        "sectionId" => "7295e51e-cd51-4901-ae67-fa33966478c7_id"
    },
    "learningObjective" => {
        "academicSubject" => "Mathematics",
        "objective" => "Validation Test Objective",
        "objectiveGradeLevel" => "Fifth grade"
    },
    "learningStandard" => {
        "learningStandardId" => {
            "identificationCode" => "G.SRT.1"},
        "description" => "Validation Test Learning Standard Desc.",
        "gradeLevel" => "Ninth grade",
        "contentStandard"=>"State Standard",
        "subjectArea" => "English"
    },
    "program" => {
        "programId" => "ACC-TEST-PROG-3",
        "programType" => "Title I Part A",
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
        "populationServed" => "Migrant Students",
        "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
        "sessionId" => "fb0ac9e8-9e4e-48a0-95d2-ae07ee15ee92",
        "courseId" => "82ad1eb0-c6d4-4b00-909a-edd1c8d04e41"
    },
    "session" => {
        "sessionName" => "Validation Test Spring 2012",
        "schoolYear" => "2011-2012",
        "term" => "Spring Semester",
        "beginDate" => "2012-01-01",
        "endDate" => "2012-06-30",
        "totalInstructionalDays" => 80,
        "gradingPeriodReference" => ["b40a7eb5-dd74-4666-a5b9-5c3f4425f130", "ef72b883-90fa-40fa-afc2-4cb1ae17623b"],
    },
    "staff" => {
        "staffUniqueStateId" => "WLVDSUSID00001",
        "sex" => "Male",
        "hispanicLatinoEthnicity" => false,
        "highestLevelOfEducationCompleted" => "No Degree",
        "name" => {
            "firstName" => "Teaches",
            "middleName" => "D.",
            "lastSurname" => "Students"
        }
    },
    "studentGradebookEntry" => {
        "gradebookEntryId" => "706ee3be-0dae-4e98-9525-f564e05aa388_id008fd89d-88a2-43aa-8af1-74ac16a29380_id",
        "letterGradeEarned" => "A",
        "sectionId" => "706ee3be-0dae-4e98-9525-f564e05aa388_id",
        "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085_id",
        "numericGradeEarned" => 11,
        "dateFulfilled" => "2012-01-31",
        "diagnosticStatement" => "Validation Test Diag. Stmt."
    },
    "assessment" => {
        "assessmentTitle" => "Validation Test Assessment Title",
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
        "parentUniqueStateId" => "ValidationTestParentUniqId",
        "name" =>
            { "firstName" => "John",
              "lastSurname" => "Doe",
            }
    },
    "school" => {
        "shortNameOfInstitution" => "SCTS",
        "nameOfInstitution" => "Validation Test School",
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
        "teacherUniqueStateId" => "testing123",
        "highlyQualifiedTeacher" => true,
        "highestLevelOfEducationCompleted" => "No Degree",
        "name" => {
            "firstName" => "Rafe",
            "middleName" => "Hairfire",
            "lastSurname" => "Esquith"
        }
    },
    "grade" => {
        "studentSectionAssociationId" => "706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id",
        "letterGradeEarned" => "F--",
        "gradeType" => "Final"
    },
    "studentCompetency" => {
        "competencyLevel" => {
                                  "description" => "really hard competency"
                              },
        "diagnosticStatement" => "Validation Test Diag. Stmt."
    },
    "reportCard" => {
        "grades" => ["708c4e08-9942-11e1-a8a9-68a86d21d918", "708b3c95-9942-11e1-a8a9-68a86d21d918"],
        "studentCompetencyId" => ["b57643e4-9acf-11e1-89a7-68a86d21d918"],
        "gpaGivenGradingPeriod" => 3.14,
        "gpaCumulative" => 2.9,
        "numberOfDaysAbsent" => 999,
        "numberOfDaysInAttendance" => 150,
        "numberOfDaysTardy" => 10,
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id"
        #"gradingPeriodId" => "TODO"
    }
}
