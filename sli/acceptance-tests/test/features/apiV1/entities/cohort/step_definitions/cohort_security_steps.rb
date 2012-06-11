require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the cohort "([^"]*)"/ do |arg1|
  id = "b40926af-8fd5-11e1-86ec-0021701f543f" if arg1 == "ACC-TEST-COH-2"
  id
end

Transform /the student "([^"]*)"/ do |arg1|
  id = "6f9692c7-5d41-4c07-82be-ba377ca0fbd2" if arg1 == "Todd Angulo"
  id = "22bf5f8f-5e6b-4749-9e1a-2efda072d506" if arg1 == "Agnes Trinh"
  id = "86af5b86-e05e-4360-858f-68ce05d32cf1" if arg1 == "Stella Rego"
  id = "ace7d09a-56b4-486a-85bd-64474ab64083" if arg1 == "Glenda Koch"
  id = "1c30fdce-11ad-4894-a95d-d8315c88ac7d" if arg1 == "Johnny Tallent"
  id = "33b80864-ec9a-4836-b114-47e45b291ac6" if arg1 == "Thelma Frasier"
  id
end

Given /^I am user "([^"]*)" in IDP "([^"]*)"$/ do |arg1, arg2|
  user = arg1
  pass = arg1+"1234"
  realm = arg2
  
  idpRealmLogin(user, pass, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I make an API call to get (the cohort "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/cohorts/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

Then /^I get a message that I am not authorized$/ do
  assert(@res.code == 403, "Response code not expected: expected 403 but received "+@res.code.to_s)
end

