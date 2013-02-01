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
require_relative '../../utils/sli_utils.rb'

Transform /^the school "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-8da22127fd2d" if arg1 == "Fry High School"
  id = "eb4d7e1b-7bed-890a-d974-8da22127fd2d" if arg1 == "Watson Elementary School"
  id = "eb4d7e1b-7bed-890a-d5b4-8da22127fd2d" if arg1 == "Parker-Dust Middle School"
  id = "46c2e439-f800-4aaf-901c-8cf3299658cc" if arg1 == "Parker Elementary School"
  id
end

Transform /^the teacher "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-1d729a37fd2d" if arg1 == "John Doe 1"
  id = "eb4d7e1b-7bed-890a-d974-1d729a37fd2d" if arg1 == "Ted Bear"
  id = "eb4d7e1b-7bed-890a-d5b4-1d729a37fd2d" if arg1 == "John Doe 2"
  id = "eb4d7e1b-7bed-890a-d9b4-1d729a37fd2d" if arg1 == "Elizabeth Jane"
  id = "eb4d7e1b-7bed-890a-d5f4-1d729a37fd2d" if arg1 == "John Doe 3"
  id = "eb4d7e1b-7bed-890a-d9f4-1d729a37fd2d" if arg1 == "Emily Jane"
  id
end

Transform /^the section "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d_id" if arg1 == "FHS-Math101"
  id = "eb4d7e1b-7bed-890a-d974-cdb25a29fc2d_id" if arg1 == "FHS-Science101"
  id = "eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d_id" if arg1 == "FHS-English101"
  id = "eb4d7e1b-7bed-890a-d5b4-cdb25a29fc2d_id" if arg1 == "WES-English"
  id = "eb4d7e1b-7bed-890a-d9b4-cdb25a29fc2d_id" if arg1 == "WES-Math"
  id = "eb4d7e1b-7bed-890a-d5f4-cdb25a29fc2d_id" if arg1 == "PDMS-Trig"
  id = "eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d_id" if arg1 == "PDMS-Geometry"
  id
end

Transform /^the student "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-5d8aa9fbfc2d_id" if arg1 == "Doris Hanes"
  id = "eb4d7e1b-7bed-890a-d974-5d8aa9fbfc2d_id" if arg1 == "Danny Fields"
  id = "eb4d7e1b-7bed-890a-dd74-5d8aa9fbfc2d_id" if arg1 == "Gail Newman"
  id = "eb4d7e1b-7bed-890a-e174-5d8aa9fbfc2d_id" if arg1 == "Mark Moody"
  id = "eb4d7e1b-7bed-890a-e574-5d8aa9fbfc2d_id" if arg1 == "Irma Atkons"
  id = "eb4d7e1b-7bed-890a-e974-5d8aa9fbfc2d_id" if arg1 == "Austin Durran"
  id = "eb4d7e1b-7bed-890a-d5b4-5d8aa9fbfc2d_id" if arg1 == "Kristy Carillo"
  id = "eb4d7e1b-7bed-890a-d9b4-5d8aa9fbfc2d_id" if arg1 == "Forrest Hopper"
  id = "eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d_id" if arg1 == "Lavern Chaney"
  id = "eb4d7e1b-7bed-890a-e1b4-5d8aa9fbfc2d_id" if arg1 == "Emil Oneil"
  id = "eb4d7e1b-7bed-890a-e5b4-5d8aa9fbfc2d_id" if arg1 == "Kesley Krauss"
  id = "eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d_id" if arg1 == "Hal Kessler"
  id = "eb4d7e1b-7bed-890a-d9f4-5d8aa9fbfc2d_id" if arg1 == "Millie Lovel"
  id = "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d_id" if arg1 == "Brock Ott"
  id = "eb4d7e1b-7bed-890a-e1f4-5d8aa9fbfc2d_id" if arg1 == "Elnora Fin"
  id = "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d_id" if arg1 == "Freeman Marcum"
  id
end


Transform /^the student competency objective "([^"]*)"$/ do |arg1|
  id = "313db42ad65b911b0897d8240e26ca4b50bddb5e_id" if arg1 == "Learn to read"
  id
end



Transform /list of teachers from school "([^\"]*)"/ do |arg1|
  array = ["eb4d7e1b-7bed-890a-d574-1d729a37fd2d",
           "eb4d7e1b-7bed-890a-d974-1d729a37fd2d"] if arg1 == "Fry High School"
  array = ["eb4d7e1b-7bed-890a-d5b4-1d729a37fd2d",
           "eb4d7e1b-7bed-890a-d9b4-1d729a37fd2d"] if arg1 == "Watson Elementary School"
  array = ["eb4d7e1b-7bed-890a-d5f4-1d729a37fd2d",
           "eb4d7e1b-7bed-890a-d9f4-1d729a37fd2d"] if arg1 == "Parker-Dust Middle School"
  array
end

Transform /list of sections that "([^\"]*)" teaches/ do |arg1|
  array = ["eb4d7e1b-7bed-890a-d574-cdb25a29fc2d_id",
           "eb4d7e1b-7bed-890a-d974-cdb25a29fc2d_id"] if arg1 == "John Doe 1"
  array = ["eb4d7e1b-7bed-890a-d974-cdb25a29fc2d_id",
           "eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d_id"] if arg1 == "Ted Bear"
  array = ["eb4d7e1b-7bed-890a-d5b4-cdb25a29fc2d_id"] if arg1 == "John Doe 2"
  array = ["eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d_id"] if arg1 == "Elizabeth Jane"
  array = ["eb4d7e1b-7bed-890a-d5f4-cdb25a29fc2d_id",
           "eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d_id"] if arg1 == "John Doe 3"
  array = [] if arg1 == "Emily Jane"
  array
end

Transform /list of students in section "([^\"]*)"/ do |arg1|
  array = ["eb4d7e1b-7bed-890a-d574-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-d974-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-dd74-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e174-5d8aa9fbfc2d_id"] if arg1 == "FHS-Math101"
  array = ["eb4d7e1b-7bed-890a-d974-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-dd74-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e574-5d8aa9fbfc2d_id"] if arg1 == "FHS-Science101"
  array = ["eb4d7e1b-7bed-890a-e174-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e574-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e974-5d8aa9fbfc2d_id"] if arg1 == "FHS-English101"
  array = ["eb4d7e1b-7bed-890a-d5b4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-d9b4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d_id"] if arg1 == "WES-English"
  array = ["eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e1b4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e5b4-5d8aa9fbfc2d_id"] if arg1 == "WES-Math"
  array = ["eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-d9f4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d_id"] if arg1 == "PDMS-Trig"
  array = ["eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d_id",
           "eb4d7e1b-7bed-890a-e1f4-5d8aa9fbfc2d_id", 
           "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d_id"] if arg1 == "PDMS-Geometry"
  array
end

Transform /the staff "[^"]*"/ do |arg1| 
  id = nil 
  case arg1 
  when /Rick Rogers/ 
    id = "/v1/staff/85585b27-5368-4f10-a331-3abcaf3a3f4c" 
  end 
  id 
end 

Given /^I have a Role attribute that equals "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Given /^my School is "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the student competency objective "[^"]*")$/ do |arg1|
  restHttpGet("/v1/studentCompetencyObjectives/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end
                                  

When /^I make an API call to get (the school "[^"]*")$/ do |arg1|
  restHttpGet("/v1/schools/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the staff "[^"]*")$/ do |arg1|
  puts #{arg1}
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the school "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["nameOfInstitution"] == arg1, "School name returned was "+result["nameOfInstitution"]+" and expected "+arg1)
end

Then /^I should get a message that I am not authorized$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

When /^I make an API call to get (the teacher "[^"]*")$/ do |arg1|
  restHttpGet("/v1/teachers/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the teacher "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  nameArray = arg1.split
  firstName = nameArray[0]
  lastName = nameArray[1]
  assert(result["name"]["firstName"] == firstName, "Teacher first name returned was "+result["name"]["firstName"]+" but expected "+firstName)
  assert(result["name"]["lastSurname"] == lastName, "Teacher last name returned was "+result["name"]["lastSurname"]+" but expected "+lastName)
end

When /^I make an API call to get list of teachers from (the school "[^"]*")$/ do |arg1|
  restHttpGet("/v1/schools/" + arg1 + "/teacherSchoolAssociations/teachers")
#  assert(false, "#{arg1} Response from rest-client GET is nil")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes a (list of teachers from school "[^"]*")$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  numMatches = 0
  result.each {|jsonObj| 
    # Find each ID in the JSON
    assert(arg1.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
    numMatches += 1
  }
  assert(numMatches == arg1.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+arg1.length.to_s+" maches")
end

When /^I make an API call to get the list of sections taught by (the teacher "[^"]*")$/ do |arg1|
  restHttpGet("/v1/teachers/" + arg1 + "/teacherSectionAssociations/sections")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the (list of sections that "[^"]*" teaches)$/ do |arg1|
  if arg1.empty?
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  else
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    numMatches = 0
    result.each {|jsonObj| 
      # Find each ID in the JSON
      assert(arg1.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
      numMatches += 1
    }
    assert(numMatches == arg1.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+arg1.length.to_s+" maches")
  end
end

Given /^I teach in "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the section "[^"]*")$/ do |arg1|
  restHttpGet("/v1/sections/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the section "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["uniqueSectionCode"] == arg1, "Section name returned was "+result["uniqueSectionCode"]+" and expected "+arg1)
end

When /^I make an API call to get a list of students in (the section "[^"]*")$/ do |arg1|
  restHttpGet("/v1/sections/" + arg1 + "/studentSectionAssociations/students")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the (list of students in section "[^"]*")$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  numMatches = 0
  result.each {|jsonObj| 
    # Find each ID in the JSON
    assert(arg1.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
    numMatches += 1
  }
  assert(numMatches == arg1.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+arg1.length.to_s+" maches")
end

Given /^I teach the student "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a return code of "(.*?)"$/ do |arg1|
  assert(@res.code == arg1.to_i, "Response code should be #{arg1}, not #{@res.code}")
end

When /^I expire my staffEdorgAssignmentAssociation$/ do
  id = "2c6face89f0c2854667310b46808e21156ed73cc_id"
  body = { "beginDate" => "2007-07-07",
    "endDate" => "2001-07-07",
    "staffReference" => "67ed9078-431a-465e-adf7-c720d08ef512", 
    "educationOrganizationReference" => "ec2e4218-6483-4e9c-8954-0aecccfd4731", 
    "staffClassification" => "Teacher" }
  restHttpPut("/v1/staffEducationOrgAssignmentAssociations/#{id}", body.to_json, "application/json")
end

Given /^I have reset my staffEdorgAssignmentAssociation$/ do
  id = "2c6face89f0c2854667310b46808e21156ed73cc_id"
  db = Mongo::Connection.new('localhost').db(convertTenantIdToDbName('Midgar'))['staffEducationOrganizationAssociation']
  db.update({"_id" => id}, {'$unset' => { 'body.endDate' => true }})
  
end

Then /^I receive a JSON response that includes the student "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  nameArray = arg1.split
  firstName = nameArray[0]
  lastName = nameArray[1]
  assert(result["name"]["firstName"] == firstName, "Student first name returned was "+result["name"]["firstName"]+" but expected "+firstName)
  assert(result["name"]["lastSurname"] == lastName, "Student last name returned was "+result["name"]["lastSurname"]+" but expected "+lastName)
end
