require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the student "([^"]*)"/ do |arg1|
  id = "dd69083f-a053-4819-a3cd-a162cdc627d7" if arg1 == "Marvin Miller"
  id = "410b2004-44cb-4671-8abc-69739275177d" if arg1 == "Delilah D. Sims"
  id
end

Transform /the student\-parent association "([^"]*)"/ do |arg1|
  id = "dd69083f-a053-4819-a3cd-a162cdc627d7" if arg1 == "Marvin Miller to Mr. Miller"
  id = "0add1afd-1863-4005-a73f-5c66bf7985a7" if arg1 == "Delilah D. Sims to Mrs. Sims"
  id
end

Transform /the parent "([^"]*)"/ do |arg1|
  id = "eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d" if arg1 == "Mr. Miller"
  id = "c2efa2b3-f0c6-3767-cdd3-2e7495554acc" if arg1 == "Mrs. Sims"
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
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

Then /^I should see a link to get the list of its parents in the response labeled "([^"]*)"$/ do |arg1|
  assert(@res.body.include?(arg1), "Did not find text in the body including the text: "+arg1)
end

Given /^I do not teach (the student "[^"]*")$/ do |arg1|
  # No code needed, this is done as configuration
end

Then /^I get a message that I am not authorized$/ do
  assert(@res.code == 403, "Response code not expected: expected 403 but received "+@res.code.to_s)
end

Then /^I should receive a JSON object of the parent$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

When /^I make an API call to get (the student "[^"]*")'s list of parents$/ do |arg1|
  restHttpGet("/v1/students/#{arg1}/studentParentAssociations/parents")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a list containing the student "([^"]*)"'s parents$/ do |arg1|
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

When /^I make an API call to get (the student\-parent association "[^"]*")$/ do |arg1|
  restHttpGet("/v1/studentParentAssociations/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the parent "[^"]*")$/ do |arg1|
  restHttpGet("/parents/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

