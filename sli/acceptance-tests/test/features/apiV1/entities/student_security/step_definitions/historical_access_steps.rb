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

require 'json'
require_relative '../../../../utils/sli_utils.rb'


Then /^I should receive a code of (\d+) when accessing data about that student$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I move teacher12 to a new section$/ do
  updateHash = {
    "sectionId" => "00000000-0001-0000-0000-000000000004",
    "classroomPosition" => "Teacher of Record",
    "teacherId" => "00000000-0000-0000-0001-000000000012",
    "highlyQualifiedTeacher" => true,
    "beginDate" => "2011-09-01",
    "endDate" => "2005-12-16"
  }
  createHash = {
    "sectionId" => "00000000-0001-0000-0000-000000000005",
    "classroomPosition" => "Teacher of Record",
    "teacherId" => "00000000-0000-0000-0001-000000000012",
    "highlyQualifiedTeacher" => true,
    "beginDate" => "2012-07-16"
  }
  
  #Make the old association outdated
  restHttpPut("/v1/teacherSectionAssociations/00000000-0001-0000-0000-000000000018", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move teacher12 to new section, code was #{@res.code}")
  
  #Create the new association
  restHttpPost("/v1/teacherSectionAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move teacher12 to new section, code was #{@res.code}")
end

When /^I move student58 to a new section$/ do
  updateHash = {
    "repeatIdentifier" => "Not repeated",
    "studentId" => "00000000-abcd-0000-0000-000000000058",
    "sectionId" => "00000000-0001-0000-0000-000000000004",
    "beginDate" => "2011-01-01",
    "endDate" => "2005-12-16"
  }
  createHash = {
    "repeatIdentifier" => "Not repeated",
    "studentId" => "00000000-abcd-0000-0000-000000000058",
    "sectionId" => "00000000-0001-0000-0000-000000000005",
    "beginDate" => "2012-07-16"
  }
  
  #Make the old association outdated
  restHttpPut("/v1/studentSectionAssociations/48151623-4200-0000-0000-000000000011", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move student58 to new section, code was #{@res.code}")
  
  #Create the new association
  restHttpPost("/v1/studentSectionAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move student58 to new section, code was #{@res.code}")
end

When /^I move staff22 to a new school$/ do
  updateHash = {
    "staffClassification" => "Other",
    "educationOrganizationReference" => "00000000-0000-0000-0000-000000000004",
    "positionTitle" => "Researcher",
    "staffReference" => "00000000-0000-0000-0011-000000000022",
    "beginDate" => "1967-08-13",
    "endDate" => "2005-12-16"
  }
  createHash = {
    "staffClassification" => "Other",
    "educationOrganizationReference" => "00000000-0000-0000-0000-000000000005",
    "positionTitle" => "Researcher",
    "staffReference" => "00000000-0000-0000-0011-000000000022",
    "beginDate" => "2012-07-16"
  }
  
  #Make the old association outdated
  restHttpPut("/v1/staffEducationOrgAssignmentAssociations/10000000-1000-0000-0000-000000000022", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move staff22 to new school, code was #{@res.code}")
  
  #Create the new association
  restHttpPost("/v1/staffEducationOrgAssignmentAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move staff22 to new school, code was #{@res.code}")
end

When /^I move student61 to a new school$/ do
  updateHash = {
    "entryDate" => "2001-09-01",
    "entryGradeLevel" => "First grade",
    "schoolId" => "00000000-0000-0000-0000-000000000004",
    "studentId" => "00000000-abcd-0000-0000-000000000061",
    "exitWithdrawDate" => "2005-12-16"
  }
  createHash = {
    "entryDate" => "2012-07-16",
    "entryGradeLevel" => "First grade",
    "schoolId" => "00000000-0000-0000-0000-000000000005",
    "studentId" => "00000000-abcd-0000-0000-000000000061",
    "exitWithdrawDate" => "2005-12-16"
  }
  
  #Make the old association outdated
  restHttpPut("/v1/studentSchoolAssociations/00000000-7890-0000-0000-000000000057", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move student61 to new school, code was #{@res.code}")
  
  #Create the new association
  restHttpPost("/v1/studentSchoolAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move student61 to new school, code was #{@res.code}")
end

Then /^the stamper runs and completes$/ do
  pending # express the regexp above with the code you wish you had
end
