require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the program "([^"]*)"/ do |arg1|
  id = "9b8cafdc-8fd5-11e1-86ec-0021701f543f" if arg1 == "ACC-TEST-PROG-1"
  id = "9b8c3aab-8fd5-11e1-86ec-0021701f543f" if arg1 == "ACC-TEST-PROG-2"
  id
end

Transform /the student "([^"]*)"/ do |arg1|
  id = "103fe655-a376-4945-aa6e-fc389ff138d4" if arg1 == "Randy Voelker"
  id = "9c206f2d-8f9c-4bb7-a0d4-03f500c2136f" if arg1 == "Curtis Omeara"
  id = "431b6cca-dfd9-4512-93ce-36796e9310e1" if arg1 == "Theresa Deguzman"
  id = "9b00720f-1341-4e1a-b0d0-34ef1671ec87" if arg1 == "Paul Bunker"
  id = "e4f71ad7-13e5-472f-812f-99d0a8448f59" if arg1 == "Sabrina Knepper"
  id = "737dd4c1-86bd-4892-b9e0-0f24f76210be" if arg1 == "Roberta Jones"
  id = "eaa8286a-9a4f-452a-978d-aba7351c5b4f" if arg1 == "Christopher Bode"
  id
end

Given /^I am user "([^"]*)" in IDP "([^"]*)"$/ do |arg1, arg2|
  user = arg1
  pass = arg1+"1234"
  realm = arg2
  
  idpRealmLogin(user, pass, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I make an API call to get (the program "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/programs/"+arg1)
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


