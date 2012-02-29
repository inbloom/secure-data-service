require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /^the student "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-5d8aa9fbfc2d" if arg1 == "Student I teach"
  id = "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d" if arg1 == "Student I dont"
  id
end

Transform /^the specific attendance event "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-5d8aa9fbfc2d" if arg1 == "Attendance event of Student I teach"
  id = "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d" if arg1 == "Attendance event of Student I dont"
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

Given /^I teach "([^"]*)"$/ do |arg1|
  # No code needed, this is done as configuration
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  restHttpGet("/students/"+arg1)
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

When /^I make an API call to get the student "([^"]*)"'s attendance events list$/ do |arg1|
  restHttpGet("/v1/students/#{arg1}/attendances")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a list containing the student "([^"]*)"'s attendance events$/ do |arg1|
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")end

Given /^I do not teach "([^"]*)"$/ do |arg1|
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
