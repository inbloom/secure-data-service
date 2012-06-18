require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the disciplineIncident "([^"]*)"/ do |arg1|
  id = "0e26de79-222a-5e67-9201-5113ad50a03b" if arg1 == "DISC-INC-2"
  id
end

Given /^I am user "([^"]*)" in IDP "([^"]*)"$/ do |arg1, arg2|
  user = arg1
  pass = arg1+"1234"
  realm = arg2
  
  idpRealmLogin(user, pass, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I make an API call to get (the disciplineIncident "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/disciplineIncidents/"+arg1)
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

