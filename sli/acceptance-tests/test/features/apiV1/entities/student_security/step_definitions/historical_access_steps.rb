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

require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the section "([^"]*)"/ do |arg1|
  base = "00000000-0001-0000-0000-00000000000"
  base << arg1.match(/(\d+)/)[0]
  base << "_id"
  base
end

Then /^I should be able to access data about (the student "[^"]*")$/ do |arg1|
  check_associated_data(arg1, 200)
end

Then /^I should not be able to access data about (the student "[^"]*")$/ do |arg1|
  check_associated_data(arg1, 403)
end

When /^I make an API call to get my section list$/ do
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/sections?limit=0")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the section ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/sections/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should be able to access data about (the section ".*?")$/ do |arg1|
  check_section_data(arg1, 200)
end

Then /^I should not be able to access data about (the section ".*?")$/ do |arg1|
  check_section_data(arg1, 403)
end

When /^I move teacher12 to a new section$/ do
  updateHash = {
    "sectionId" => "00000000-0001-0000-0000-000000000004_id",
    "classroomPosition" => "Teacher of Record",
    "teacherId" => "00000000-0000-0000-0001-000000000012",
    "highlyQualifiedTeacher" => true,
    "beginDate" => "2011-09-01",
    "endDate" => "2005-12-16"
  }
  createHash = {
    "sectionId" => "00000000-0001-0000-0000-000000000005_id",
    "classroomPosition" => "Teacher of Record",
    "teacherId" => "00000000-0000-0000-0001-000000000012",
    "highlyQualifiedTeacher" => true,
    "beginDate" => "2012-07-16"
  }
  
  #Create the new association
  restHttpPost("/v1/teacherSectionAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move teacher12 to new section, code was #{@res.code}")

  #Make the old association outdated
  restHttpPut("/v1/teacherSectionAssociations/00000000-0001-0000-0000-000000000004_id00000000-0001-0000-0000-000000000018_id", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move teacher12 to new section, code was #{@res.code}")

end

When /^I move student58 to a new section$/ do
  updateHash = {
    "repeatIdentifier" => "Not repeated",
    "studentId" => "00000000-abcd-0000-0000-000000000058_id",
    "sectionId" => "00000000-0001-0000-0000-000000000004_id",
    "beginDate" => "2011-01-01",
    #"endDate" => "2005-12-16"
  }
  createHash = {
    "repeatIdentifier" => "Not repeated",
    "studentId" => "00000000-abcd-0000-0000-000000000058_id",
    "sectionId" => "00000000-0001-0000-0000-000000000005_id",
    "beginDate" => "2012-07-16"
  }
  
  #Create the new association
  restHttpPost("/v1/studentSectionAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move student58 to new section, code was #{@res.code}")

  #Make the old association outdated
  restHttpPut("/v1/studentSectionAssociations/00000000-0001-0000-0000-000000000004_id48151623-4200-0000-0000-000000000011_id", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move student58 to new section, code was #{@res.code}")

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
  
  #Create the new association
  restHttpPost("/v1/staffEducationOrgAssignmentAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move staff22 to new school, code was #{@res.code}")

  #Make the old association outdated
  restHttpPut("/v1/staffEducationOrgAssignmentAssociations/10000000-1000-0000-0000-000000000022", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move staff22 to new school, code was #{@res.code}")
  
end

When /^I move student61 to a new school$/ do
  updateHash = {
    "entryDate" => "2001-09-01",
    "entryGradeLevel" => "First grade",
    "schoolId" => "00000000-0000-0000-0000-000000000004",
    "studentId" => "00000000-abcd-0000-0000-000000000061_id",
    "exitWithdrawDate" => "2005-12-16"
  }
  createHash = {
    "entryDate" => "2012-07-16",
    "entryGradeLevel" => "First grade",
    "schoolId" => "00000000-0000-0000-0000-000000000005",
    "studentId" => "00000000-abcd-0000-0000-000000000061_id",
    "exitWithdrawDate" => "2015-12-16"
  }

  #Create the new association
  restHttpPost("/v1/studentSchoolAssociations", createHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
  assert(@res.code == 201, "Failed to move student61 to new school, code was #{@res.code}")

  #Make the old association outdated
  restHttpPut("/v1/studentSchoolAssociations/00000000-7890-0000-0000-000000000057", updateHash.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Failed to move student61 to new school, code was #{@res.code}")
end

Then /^the stamper runs and completes$/ do

  puts `ruby ../opstools/edorg/edorg_stamper.rb 127.0.0.1:27017`
  #Clear the session caches
  db = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])['sli']
  db[:userSession].update({"body.cache" => {"$exists" => true}}, {"$unset" => {"body.cache" =>1}}, {:upsert => false, :multi => true})
  assert(db[:userSession].find({"body.cache" => {"$exists" => true}}).count == 0)
  #puts `ruby ../opstools/teacher_security_stamper/teacher_stamper.rb 127.0.0.1:27017`
end

private
def check_associated_data(arg1, response)
  @format = "application/vnd.slc+json"
  ["courseTranscripts", "studentAcademicRecords", "attendances", "studentAssessments", "reportCards", "studentGradebookEntries", "studentDisciplineIncidentAssociations", "studentParentAssociations"].each do |endpoint|
    restHttpGet("/v1/#{endpoint}") if endpoint.include? arg1
    restHttpGet("/v1/students/#{arg1}/#{endpoint}") unless endpoint.include? arg1
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == response, "Get on endpoint #{endpoint}, expected code: #{response} but actual code was #{@res.code}")
    data = JSON.parse(@res.body) unless response == 403
    assert(data.count == 1, "Expected to only see one #{endpoint} but saw #{data.count}") unless response == 403
  end
  check_grades(arg1, response)
end
def check_grades(arg1, response)
  @format = "application/vnd.slc+json"
  #First get student section Associations, then get the grades.
  restHttpGet("/v1/students/#{arg1}/studentSectionAssociations")
  ssa = JSON.parse(@res.body)
  grades = []
  studentCompetencies = []
  if (response != 403)
    ssa.each do |association|
      #Get the grade for this.
      restHttpGet("/v1/studentSectionAssociations/#{association["id"]}/grades")
      grade = JSON.parse(@res.body)
      grades += grade
      #Get the student Competency for this.
      restHttpGet("/v1/studentSectionAssociations/#{association["id"]}/studentCompetencies")
      studentCompetency = JSON.parse(@res.body)
      studentCompetencies += studentCompetency
    end
    grades = grades.flatten.uniq
    assert(grades.count >= 1, "Expected to only see one grade, but saw #{grades.count}")
    studentCompetencies = studentCompetencies.flatten.uniq
    assert(studentCompetencies.count >= 1, "Expected to only see one studentCompetency, but saw #{studentCompetencies.count}")
  end
end
def check_section_data(arg1, response)
  @format = "application/vnd.slc+json"
  ["gradebookEntries"].each do |endpoint|
    restHttpGet("/v1/#{endpoint}") if endpoint.include? arg1
    restHttpGet("/v1/sections/#{arg1}/#{endpoint}") unless endpoint.include? arg1
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == response, "Get on endpoint #{endpoint}, expected code: #{response} but actual code was #{@res.code}")
    data = JSON.parse(@res.body) unless response == 403
    assert(data.count == 1, "Expected to only see one #{endpoint} but saw #{data.count}") unless response == 403
  end
  #check sessions
  #check course/courseoffering
end
