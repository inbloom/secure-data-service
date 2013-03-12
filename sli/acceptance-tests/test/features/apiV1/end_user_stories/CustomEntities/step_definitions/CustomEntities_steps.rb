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
require 'mongo'
require 'rest-client'
require 'rexml/document'
require 'base64'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'
require_relative '../../../../security/step_definitions/securityevent_util_steps.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "educationOrganizations"                     if human_readable_id == "EDUCATION ORGANIZATION URI"
  id = "studentSchoolAssociations"                  if human_readable_id == "STUDENT SCHOOL ASSOCIATION URI"
  id = "custom"                                     if human_readable_id == "CUSTOM URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id =  "<"+human_readable_id+">"                   if human_readable_id.include? "?"
 
  #education organizations
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"       if human_readable_id == "EDUCATION ORGANIZATION ID"
    
  #students
  id = "0cff1537-95e6-440b-ba2f-3003a2ecd7ed_id"       if human_readable_id == "STUDENT ID"
  
  #student school associations
  id = "3b638fbc-0b82-459a-8002-ee57717e02e9"       if human_readable_id == "STUDENT SCHOOL ASSOC ID"
  id
end

Given /^I am a valid SEA\/LEA end user "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^the clientID is "([^"]*)"$/ do |arg1|
  if arg1 == "SampleApplication"
    @user = "sampleUser"
    @passwd = "sampleUser1234"
  end
end

Given /^I am authenticated on "([^"]*)"$/ do |arg1|
  idpRealmLogin(@user, @passwd, arg1)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^a valid entity json object for a "([^"]*)"$/ do |arg1|
 # parent entity is setup in fixture data
end

Given /^I add a key value pair "([^"]*)" : "([^"]*)" to the object$/ do |key, value|
  @fields = {} if !defined? @fields
  @fields[key] = value
end

When /^I navigate to GET "([^"]*)"\?includeCustom="([^"]*)"$/ do |uri, flag|
  uri=uri+"?includeCustom="+flag
  step "I navigate to GET \"#{uri}\""
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a key value pair "([^"]*)" : "([^"]*)" in the result$/ do |key, value|
  if key!="" and value!=""
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key] == convert(value), "Expected #{key} to equal #{value}, received #{@result[key]}")
  end
end

Then /^I should receive a Location header for the custom entity$/ do
  headers = @res.raw_headers
  headers.should_not == nil
  headers['location'].should_not == nil
  headers['location'].length.should == 1
  headers['location'][0].should match %r{.+/educationOrganizations/[\w\d]+-[\w\d]+-[\w\d]+-[\w\d]+-[\w\d]+/custom}
end

Then /^I should recieve the "([^"]*)" object with "([^"]*)"$/ do |type, id|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result["entityType"] == convert(type), "did not receive #{type}")
  assert(@result["id"] == convert(id), "did not receive #{type} with ID specified")
end

Then /^additionally I should receive a key value pair "([^"]*)" : "([^"]*)" in the result$/ do |key, value|
  if key!="" and value!=""
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result["custom"].has_key?(key), "Response does not contain key #{key}")
  assert(@result["custom"][key] == convert(value), "Expected #{key} to equal #{value}, received #{@result["custom"][key]}")
  end
end

Then /^there is no other custom data returned$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result["custom"]==nil, "Response include custom data!")
end

Given /^I add a large random file with key "([^"]*)" to the object$/ do |data_key| 
  @fields = {} if !defined? @fields
  target_size = 1024 * 1024 * 10     # target size = 10MB 
  @random_data = (0...target_size).map { rand(255).chr }.join
  @fields[data_key] = Base64.encode64(@random_data)
end 

Then /^I should receive the same large random file in key "([^"]*)" in the result$/ do |data_key| 
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(data_key), "Response does not contain key #{data_key}")
  found_data = Base64.decode64(@result[data_key])
  assert(found_data == @random_data, "Expected random data of size #{@random_data.length}, but received #{found_data.length}")
end 

When /^I navigate to a truncated, faulty POST "([^"]*)"$/ do |post_uri|
  data = prepareData(@format, @fields)
  k_128 = 1024 * 128
  data = data[0..k_128]
  restHttpPost(post_uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end
