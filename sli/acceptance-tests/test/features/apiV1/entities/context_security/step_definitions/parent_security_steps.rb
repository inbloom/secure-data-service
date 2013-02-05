=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the studentParentAssociation "([^"]*)"/ do |arg1|
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_iddd69083f-a053-4819-a3cd-a162cdc627d7_id" if arg1 == "Marvin Miller to Mr. Miller"
  id = "6a98d5d3-d508-4b9c-aec2-59fce7e16825_id83279cab-ec94-4a04-a1f3-c87752d1d725_id" if arg1 == "Delilah D. Sims to Mrs. Sims"
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

