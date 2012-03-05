require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the student "([^"]*)"/ do |arg1|
  id = "dd69083f-a053-4819-a3cd-a162cdc627d7" if arg1 == "Marvin Miller"
  id = "410b2004-44cb-4671-8abc-69739275177d" if arg1 == "Delilah D. Sims"
  id
end

Transform /the specific attendance event "([^"]*)"/ do |arg1|
  id = "b80ba316-8a6d-4223-8070-917eb8aadb7c" if arg1 == "Marvin Miller Attendance event"
  id = "0e5034ac-b055-4485-9855-27bf6210f1e6" if arg1 == "Delilah D. Sims Attendance event"
  id
end

Given /^I am user "([^"]*)" in IDP "([^"]*)"$/ do |arg1, arg2|
  user = arg1
  pass = arg1+"1234"
  realm = arg2
  
  idpRealmLogin(user, pass, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^I am assigned the Educator role in my IDP$/ do
  # No code needed, this is done as configuration
end

Given /^I teach (the student "[^"]*")$/ do |arg1|
  # No code needed, this is done as configuration
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  restHttpGet("/students/"+arg1,"application/vnd.slc+json")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

Then /^I should see a link to get the list of its attendance events in the response labeled "([^"]*)"$/ do |arg1|
  assert(@res.body.include?(arg1), "Did not find text in the body including the text: "+arg1)
end

When /^I make an API call to get (the student "[^"]*")'s attendance events list$/ do |arg1|
  restHttpGet("/v1/students/#{arg1}/attendances")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a list containing (the student "[^"]*")'s attendance events$/ do |arg1|
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")end

Given /^I do not teach (the student "[^"]*")$/ do |arg1|
  # No code needed, this is done as configuration
end

Then /^I get a message that I am not authorized$/ do
  assert(@res.code == 403, "Response code not expected: expected 403 but received "+@res.code.to_s)
end

When /^I make an API call to get (the specific attendance event "[^"]*")$/ do |arg1|
  restHttpGet("/attendances/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a JSON object of the attendance event$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end
