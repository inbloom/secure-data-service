require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "educationOrganizations"                     if human_readable_id == "EDUCATION ORGANIZATION URI"
  id = "studentSchoolAssociations"                  if human_readable_id == "STUDENT SCHOOL ASSOCIATION URI"
  id = "custom"                                     if human_readable_id == "CUSTOM URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id =  "<"+human_readable_id+">"                   if human_readable_id.include? "?"
 
  #education organizations
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"       if human_readable_id == "EDUCATION ORGANIZATION ID"
    
  #students
  id = "0cff1537-95e6-440b-ba2f-3003a2ecd7ed"       if human_readable_id == "STUDENT ID"
      
  #teachers
  id = "a936f73f-7745-b450-922f-87ad78fd6bd1"       if human_readable_id == "'Ms. Jones' ID"
  id = "e24b24aa-2556-994b-d1ed-6e6f71d1be97"       if human_readable_id == "'Ms. Smith' ID"
  
  #assessments
  id = "dd916592-7dfe-4e27-a8ac-bec5f4b757b7"       if human_readable_id == "'Grade 2 MOY READ2' ID"
  id = "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6"       if human_readable_id == "'Grade 2 BOY READ2' ID"
  id = "1e0ddefb-875a-ef7e-b8c3-33bb5676115a"       if human_readable_id == "'Most Recent Student Assessment Association' ID"
  
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
