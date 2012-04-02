require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "educationOrganizations"                     if human_readable_id == "EDUCATION ORGANIZATION URI"
  id = "custom"                                     if human_readable_id == "CUSTOM URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id =  "<"+human_readable_id+">"                   if human_readable_id.include? "?"
 
  #education organizations
  id = "4f0c9368-8488-7b01-0000-000059f9ba56"       if human_readable_id == "EDUCATION ORGANIZATION ID"
    
  #students
  id = "2899a720-4196-6112-9874-edde0e2541db"       if human_readable_id == "STUDENT ID"
      
  #teachers
  id = "a936f73f-7745-b450-922f-87ad78fd6bd1"       if human_readable_id == "'Ms. Jones' ID"
  id = "e24b24aa-2556-994b-d1ed-6e6f71d1be97"       if human_readable_id == "'Ms. Smith' ID"
  
  #assessments
  id = "dd916592-7dfe-4e27-a8ac-bec5f4b757b7"       if human_readable_id == "'Grade 2 MOY DIBELS' ID"
  id = "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6"       if human_readable_id == "'Grade 2 BOY DIBELS' ID"
  id = "1e0ddefb-875a-ef7e-b8c3-33bb5676115a"       if human_readable_id == "'Most Recent Student Assessment Association' ID"
  
  #teacher section associations
  id = "12f25c0f-75d7-4e45-8f36-af1bcc342871"       if human_readable_id == "'Teacher Ms. Jones and Section Algebra II' ID"
  id
end

Given /^I am a valid SEA\/LEA end user "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^the clientID is "([^"]*)"$/ do |arg1|
  if arg1 == "demoClient"
    @user = "demo"
    @passwd = "demo1234"
  elsif arg1 == "SampleApplication"
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

Then /^I should receive a key value pair "([^"]*)" : "([^"]*)" in the result$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key] == convert(value), "Expected #{key} to equal #{value}, received #{@result[key]}")
end

Then /^I should receive a Location header for the custom entity$/ do
  headers = @res.raw_headers
  headers.should_not == nil
  headers['location'].should_not == nil
  headers['location'].length.should == 1
  headers['location'][0].should match %r{.+/educationOrganizations/[\w\d]+-[\w\d]+-[\w\d]+-[\w\d]+-[\w\d]+/custom}
end