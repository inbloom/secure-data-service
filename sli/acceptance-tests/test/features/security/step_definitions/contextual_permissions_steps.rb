require 'json'
require_relative '../../utils/sli_utils.rb'

Transform /^the school "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-8da22127fd2d" if arg1 == "Fry High School"
  id = "eb4d7e1b-7bed-890a-d974-8da22127fd2d" if arg1 == "Watson Elementary School"
  id = "eb4d7e1b-7bed-890a-d5b4-8da22127fd2d" if arg1 == "Parker-Dust Middle School"
  id
end




Given /^I am a valid "([^"]*)" end user "([^"]*)" with password "([^"]*)"$/ do |arg1, arg2, arg3|
  @realm = arg1
  @user = arg2
  @password = arg3
end

Given /^I am authenticated to SEA\/LEA IDP$/ do
  idpRealmLogin(@user, @password, @realm)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^I have a Role attribute that equals "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Given /^my School is "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the school "[^"]*")$/ do |arg1|
  restHttpGet("/schools/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the School entity and its attributes$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

Then /^I should get a message that I am not authorized$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I make an API call to get the teacher "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I receive a JSON response that includes the Teacher entity and its attributes$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I make an API call to get list of teachers from the school "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I receive a JSON response that includes the teacher "([^"]*)" and the teacher "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I receive a JSON response that includes the Teacher$/ do
  pending # express the regexp above with the code you wish you had
end
