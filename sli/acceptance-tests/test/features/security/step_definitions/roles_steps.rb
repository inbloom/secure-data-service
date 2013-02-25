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

Given /^I am valid SEA\/LEA end user "([^"]*)" with password "([^"]*)"$/ do |arg1, arg2| 
  @user = arg1
  @passwd = arg2
end

Given /^I have a Role attribute returned from the "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^the role attribute equals "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^I am authenticated on "([^"]*)"$/ do |realm|
  idpRealmLogin(@user, @passwd, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I make a REST API call$/ do
  student_uri = "/v1/students/0636ffd6-ad7d-4401-8de9-40538cf696c8_id" 
  restHttpGet(student_uri,"application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I get the JSON response displayed$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

Then /^I get response that I am not authorized to do that operation because I do not have a valid SLI Default Role$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

Given /^I do not have a Role attribute returned from the "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^"([^"]*)" is allowed to change Student address$/ do |arg1|
  # No code needed, this is done during configuration
end

When /^I make an API call to change the Student address to "([^"]*)"$/ do |arg1|
  student_uri = "/v1/students/0636ffd6-ad7d-4401-8de9-40538cf696c8_id" 
  restHttpGet(student_uri,"application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  dataH = JSON.parse(@res.body)
  @receivedAddress = dataH['address']
  assert(dataH != nil, "Result of JSON parsing is nil")
  dataH['address'] = [Hash["streetNumberName" => arg1,
                           "city" => "Urbania",
                           "stateAbbreviation" => "NC",
                           "postalCode" => "12345"]]
  data = dataH.to_json

  restHttpPut(student_uri, data, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
  @address = arg1
end

Then /^the Student address is changed$/ do
  #Validate the Put return code first
  assert(@res.code == 204, "Return code was not expected: "+@res.code.to_s+" but expected 204")
  
  #Then get the data to see it has changed
  restHttpGet("/v1/students/0636ffd6-ad7d-4401-8de9-40538cf696c8_id","application/json")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['address'][0]["streetNumberName"] == @address, "Expected student address not found in response")
end

Given /^"([^"]*)" is not allowed to change Student address$/ do |arg1|
  # No code needed, this is done during configuration
end

Then /^a message is displayed that the "([^"]*)" role does not allow this action$/ do |arg1|
  #Validate the Put return code first
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
  
  #Then get the data to see it hasn't changed
  restHttpGet("/v1/students/0636ffd6-ad7d-4401-8de9-40538cf696c8_id","application/json")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['address'] == @receivedAddress, "Expected student address not found in response")

end

Given /^"([^"]*)" is not allowed to view Student data$/ do |arg1|
  # No code needed, this is done during configuration
end

When /^I make an API call to view a Student's data$/ do
  student_uri = "/v1/students/0636ffd6-ad7d-4401-8de9-40538cf696c8_id" 
  restHttpGet(student_uri,"application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^a message is displayed that the "([^"]*)" role cannot view this data$/ do |arg1|
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")  
end

Given /^"([^"]*)" is allowed to view restricted Student fields$/ do |arg1|
  # No code needed, this is done during configuration
end

Then /^the Student restricted fields are visible in the response$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['economicDisadvantaged'] != nil, "Expected restricted student fields were nil in response")
  assert(result['economicDisadvantaged'] != "", "Expected restricted student fields were blank in response")
end

Given /^"([^"]*)" is not allowed to view restricted Student fields$/ do |arg1|
  # No code needed, this is done during configuration
end

Then /^the Student restricted fields are not visible in the response$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['economicDisadvantaged'] == nil, "Expected no restriced student fields, but saw them in response")
end

When /^I make an API call to view another staff's data$/ do
  student_uri = "/v1/teachers/e9ca4497-e1e5-4fc4-ac7b-24badbad998b" 
  restHttpGet(student_uri,"application/json")
  assert(@res != nil, "Response from rest-client GET is nil")

end

Then /^the Staff restricted fields are not visible in the response$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['race'] == nil, "Expected restricted staff field: race to be nil in response")
  assert(result['sex'] == nil, "Expected restricted staff field: sex to be nil in response")
  assert(result['birthDate'] == nil, "Expected restricted staff field: birthDate to be nil in response")
  assert(result['address'] == nil, "Expected restricted staff field: address to be nil in response")
  assert(result['hispanicLatinoEthnicity'] == nil, "Expected restricted staff field: hispanicLatinoEthnicity to be nil in response")
  assert(result['oldEthnicity'] == nil, "Expected restricted staff field: oldEthnicity to be nil in response")
  assert(result['highestLevelOfEducationCompleted'] == nil, "Expected restricted staff field: highestLevelOfEducationCompleted to be nil in response")
  assert(result['yearsOfPriorProfessionalExperience'] == nil, "Expected restricted staff field: yearsOfPriorProfessionalExperience to be nil in response")
  assert(result['yearsOfPriorTeachingExperience'] == nil, "Expected restricted staff field: yearsOfPriorTeachingExperience to be nil in response")
  assert(result['credentials'] == nil, "Expected restricted staff field: credentials to be nil in response")
  assert(result['loginId'] == nil, "Expected restricted staff field: loginId to be nil in response")
  assert(result['staffUniqueStateId'] == nil, "Expected restricted staff field: staffUniqueStateId to be nil in response")
  assert(result['staffIdentificationCode'] == nil, "Expected restricted staff field: staffIdentificationCode to be nil in response")

end

