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
require 'mongo'
require_relative '../../../../utils/sli_utils.rb'

$studentHash = {
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
  }

def transform_student_to_id(arg1)
  base = "00000000-abcd-0000-0000-0000000000"
  base << arg1.match(/(\d+)/)[0]
  base << "_id"
  base
end

Transform /the student "([^"]*)"/ do |arg1|
  base = transform_student_to_id(arg1)
  base
end

Transform /at School "([^"]*)"/ do |school|
  schoolId = "00000000-0000-0000-0000-000000000004" if school == "Lonely"
  schoolId = "00000000-0000-0000-0000-000000000002" if school == "Secured"
  schoolId = "00000000-0000-0000-0000-000000000005" if school == "Empty"
  schoolId
end

Transform /the section "([^"]*)"/ do |arg1|
  base = "00000000-0001-0000-0000-00000000000"
  base << arg1.match(/(\d+)/)[0]
  base << "_id"
  base
end

Transform /the cohort "([^"]*)"/ do |arg1|
  base = "00000000-1000-0000-0000-00000000000"
  base << arg1.match(/(\d+)/)[0]
  base << "_id"
  base
end

Transform /the program "([^"]*)"/ do |arg1|
  base = "00000000-0000-0000-0010-00000000000"
  base << arg1.match(/(\d+)/)[0]
  base << "_id"
  base
end

Given /^I am user "([^"]*)" in IDP "([^"]*)"$/ do |arg1, arg2|
  user = arg1
  pass = arg1+"1234"
  realm = arg2
  
  idpRealmLogin(user, pass, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I make an API call to get all students in (the section ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/sections/#{arg1}/studentSectionAssociations/students")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get all students in (the cohort ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/cohorts/#{arg1}/studentCohortAssociations/students")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get all students in (the program ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/programs/#{arg1}/studentProgramAssociations/students")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get student associations to (the section ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/sections/#{arg1}/studentSectionAssociations")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get student associations to (the cohort ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/cohorts/#{arg1}/studentCohortAssociations")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get student associations to (the program ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/programs/#{arg1}/studentProgramAssociations")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get student associations (at School ".*?")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/schools/#{arg1}/studentSchoolAssociations")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to update (the student "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  student_uri ="/v1/students/"+arg1
  restHttpGet(student_uri)
 
  assert(@res != nil, "Response from rest-client GET is nil")
  
  if (@res.code == 403) 
    data = $studentHash.to_json 
  else
    dataH = JSON.parse(@res.body)
    
    dataH['address'] = [{"streetNumberName" => "arg1",
                           "city" => "Urbania",
                           "stateAbbreviation" => "NC",
                           "postalCode" => "12345"}]
    
    data = dataH.to_json
  end

  restHttpPut(student_uri, data, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")  
end

Then /^I see the response "([^"]*)" restricted data and "([^"]*)" general data$/ do |arg1, arg2|
  expectedGeneral = (arg2 === "includes" ? true : false)
  expectedRestricted = (arg1 === "includes" ? true : false)
  actualGeneral = false
  actualRestricted = false
  
  begin
    dataH = JSON.parse(@res.body)
    actualGeneral = true if dataH.include? 'name'
    actualRestricted = true if dataH.include? 'economicDisadvantaged'
  rescue
  end
  assert(expectedRestricted == actualRestricted, "Expectations for seeing restricted data is incorrect.")
  assert(expectedGeneral == actualGeneral, "Expectations for seeing general data is incorrect. Expected #{expectedGeneral}, Actual #{actualGeneral}")
end

Then /^I the response should only include the students "(.*?)"$/ do |arg1|
  data = JSON.parse(@res.body)
  if (@res.code == 403)
    assert(arg1 == "none", "Expected to see students but received a 403")
  else
    student_id_array = []
    student_array = arg1.split(";")
    student_array.each { |student| student_id_array.push(transform_student_to_id(student))}
    data.each do |student|
      assert(student_id_array.include?(student["id"]), "Student #{student["id"]} returned when not expected; expected #{student_id_array.inspect}")
    end
  end
end

Then /^I the response should only include associaitons for the students "(.*?)"$/ do |arg1|
  data = JSON.parse(@res.body)
  if (@res.code == 403)
    assert(arg1 == "none", "Expected to see associations but received a 403")
  else
    student_id_array = []
    student_array = arg1.split(";")
    student_array.each { |student| student_id_array.push(transform_student_to_id(student))}
    data.each do |student|
      assert(student_id_array.include?(student["studentId"]), "Student #{student["studentId"]} returned when not expected; expected #{student_id_array.inspect}")
    end
  end
end


When /^I make an API call to get my student list$/ do
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students?limit=0")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should see a count of (\d+)$/ do |arg1|
  data = JSON.parse(@res.body)
  if (@res.code == 403)
    assert(arg1.to_i == 0, "Received 403 HTML code but expected non-zero count")
  else
    assert(data.count == arg1.to_i, "Count should match (#{arg1} != #{data.count})")
  end
end
  
When /^I make an API call to get my student list (at School ".*?")$/ do |school|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/schools/"+school+"/studentSchoolAssociations/students")
end

