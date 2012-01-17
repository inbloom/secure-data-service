require 'json'
require_relative '../../utils/sli_utils.rb'

Transform /^the school "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-8da22127fd2d" if arg1 == "Fry High School"
  id = "eb4d7e1b-7bed-890a-d974-8da22127fd2d" if arg1 == "Watson Elementary School"
  id = "eb4d7e1b-7bed-890a-d5b4-8da22127fd2d" if arg1 == "Parker-Dust Middle School"
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
  id = "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d" if arg1 == "FHS-Math101"
  id = "eb4d7e1b-7bed-890a-d974-cdb25a29fc2d" if arg1 == "FHS-Science101"
  id = "eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d" if arg1 == "FHS-English101"
  id = "eb4d7e1b-7bed-890a-d5b4-cdb25a29fc2d" if arg1 == "WES-English"
  id = "eb4d7e1b-7bed-890a-d9b4-cdb25a29fc2d" if arg1 == "WES-Math"
  id = "eb4d7e1b-7bed-890a-d5f4-cdb25a29fc2d" if arg1 == "PDMS-Trig"
  id = "eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d" if arg1 == "PDMS-Geometry"
  id
end


Transform /^list of teachers from school "([^"]*)"&/ do |arg1|
  array = ["John Doe", "Ted Bear"] if arg1 == "Fry High School"
  array = ["John Doe", "Elizabeth Jane"] if arg1 == "Watson Elementary School"
  array = ["John Doe", "Emily Jane"] if arg1 == "Parker-Dust Middle School"
  array
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

Then /^I receive a JSON response that includes the school "[^"]*" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result[nameOfInstitution] == arg1, "School name returned was "+result[nameOfInstitution]+" and expected "+arg1)
end

Then /^I should get a message that I am not authorized$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

When /^I make an API call to get (the teacher "[^"]*")$/ do |arg1|
  restHttpGet("/teachers/"+arg1)
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
  assert(result["name"]["lastSurname"] == lastName, "Teacher last name returned was "+result["name"]["lastName"]+" but expected "+lastName)
end

When /^I make an API call to get list of teachers from the school "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I receive a JSON response that includes a (list of teachers from school "[^"]*")$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end
