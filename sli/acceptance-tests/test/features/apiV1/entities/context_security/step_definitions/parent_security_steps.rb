require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the student "([^"]*)"/ do |arg1|
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if arg1 == "Marvin Miller"
  id = "6a98d5d3-d508-4b9c-aec2-59fce7e16825" if arg1 == "Delilah D. Sims"
  id
end

Transform /the studentParentAssociation "([^"]*)"/ do |arg1|
  id = "dd69083f-a053-4819-a3cd-a162cdc627d7" if arg1 == "Marvin Miller to Mr. Miller"
  id = "83279cab-ec94-4a04-a1f3-c87752d1d725" if arg1 == "Delilah D. Sims to Mrs. Sims"
  id
end

Transform /the parent "([^"]*)"/ do |arg1|
  id = "eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d" if arg1 == "Mr. Miller"
  id = "c2efa2b3-f0c6-3767-cdd3-2e7495554acc" if arg1 == "Mrs. Sims"
  id
end

Then /^I should see a link "([^"]*)"/ do |arg1|
  assert(@res.body.include?(arg1), "Did not find text in the body including the text: "+arg1)
end

Then /^I should receive a JSON object of the parent$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

And /^in that object there should be a link to (the \w+ "[^"]*")$/ do |arg1|
  assert(@res.body.include?(arg1), "Did not find the link in the response");
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

When /^I make an API call to get (the studentParentAssociation "[^"]*")$/ do |arg1|
  restHttpGet("/v1/studentParentAssociations/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the parent "[^"]*")$/ do |arg1|
  restHttpGet("/v1/parents/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

