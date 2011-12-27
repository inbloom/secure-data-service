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

Given /^I am authenticated on "([^"]*)"$/ do |arg1|
  idpLogin(@user, @passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

When /^I make a REST API call$/ do
  restHttpGet("/schools/eb3b8c35-f582-df23-e406-6947249a19f2", "application/json")
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

Given /^IT Administrator is allowed to change Student address$/ do
  # No code needed, this is done during configuration
end

When /^I make an API call to change the Student address$/ do
  student_uri = "/students/289c933b-ca69-448c-9afd-2c5879b7d221" 
  restHttpGet(student_uri,"application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  dataH = JSON.parse(@res.body)
  assert(dataH != nil, "Result of JSON parsing is nil")
  dataH['address'] = Hash['streetNumberName' => "1234 Somewhere"]
  data = dataH.to_json
  
  restHttpPut(student_uri, data, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
end

Then /^the Student address is changed$/ do
  #Validate the Put return code first
  assert(@res.code == 204, "Return code was not expected: "+@res.code.to_s+" but expected 204")
  
  #Then get the data to see it has changed
  restHttpGet("/students/289c933b-ca69-448c-9afd-2c5879b7d221","application/json")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['address']['streetNumberName'] == "1234 Somewhere", "Expected student address not found in response")
end

Given /^Educator is not allowed to change Student address$/ do
  # No code needed, this is done during configuration
end

Then /^a message is displayed that the Educator role does not allow this action$/ do
  #Validate the Put return code first
  assert(@res.code == 405, "Return code was not expected: "+@res.code.to_s+" but expected 405")
  
  #Then get the data to see it hasn't changed
  restHttpGet("/students/289c933b-ca69-448c-9afd-2c5879b7d221","application/json")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['address']['streetNumberName'] != "1234 Somewhere", "Expected student address not found in response")

end