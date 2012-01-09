require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative './section_crud_helper'

Transform /^section "([^"]*)"$/ do |step_arg|
  id = "/sections/1e1cdb04-2094-46b7-8140-e3e481013480"  if step_arg == "chemistryF11"
  id = "/sections/5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431"  if step_arg == "physicsS08"
  id = "/sections/11111111-1111-1111-1111-111111111111"  if step_arg == "Invalid"
  id = "/sections/58c9ef19-c172-4798-8e6e-c73e68ffb5a3"  if step_arg == "algebraIIS10"
  id = "/sections/" + @newSectionId                     if step_arg == "SpanishB09"
  if step_arg == "biologyF09"
    if @format == "application/json" or @format == "application/vnd.slc+json"
      id = "/sections/2934f72d-f9e3-48fd-afdd-56b94e2a3454" # biologyF09J
    elsif @format == "application/xml" or @format == "application/vnd.slc+xml"
      id = "/sections/c2efa2b3-f0c6-472a-b0d3-2e7495554acc" # biologyF09X
    end
  end
  id = "/section/11111111-1111-1111-1111-111111111111"   if step_arg == "WrongURI"
  id = "/sections"                                       if step_arg == "NoGUID"
  id
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/vnd.slc+json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^the SLI_SMALL dataset is loaded$/ do
  # the assumption is now that the import script will be run
  # prior to running cucumber feature tests
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |usr, pass|
  idpLogin(usr, pass)
end

Given /^I have access to all students$/ do
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^the unique section code is "([^"]*)"$/ do |section_code|
  section_code.should_not be_nil
  @section_code = section_code
end

Given /^the educational environment is "([^"]*)"$/ do |edu_env|
  edu_env.should_not be_nil
  @edu_env = edu_env
end

Given /^the medium of instruction is "([^"]*)"$/ do |medium|
  medium.should_not be_nil
  @medium = medium
end

Given /^the population served is "([^"]*)"$/ do |pop|
  pop.should_not be_nil
  @pop = pop
end

When /^I navigate to POST "([^"]*)"$/ do |section_uri|
    data = data_builder
    restHttpPost(section_uri, data)
    @res.should_not be_nil
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: #{@res.code} but expected #{arg1}")
end

Then /^I should receive a ID for the newly created section$/ do
  headers = @res.raw_headers
  headers.should_not be_nil
  headers['location'].should_not be_nil
  s = headers['location'][0]
  @newSectionId = s[s.rindex('/')+1..-1]
  @newSectionId.should_not be_nil
end

When /^I navigate to GET (section "[^"]*")$/ do |section_uri|
  restHttpGet(section_uri)
end

Then /^I should see the sequence of course is (\d+)$/ do |course_seq|
  parsed_results.should_not be_nil
  getKeyValue('sequenceOfCourse').to_i.should == course_seq.to_i
end

Then /^I should see the educational environment is "([^"]*)"$/ do |edu_env|
  parsed_results.should_not be_nil
  getKeyValue('educationalEnvironment').should == edu_env
end

Then /^I should see the medium of instruction is "([^"]*)"$/ do |medium|
  parsed_results.should_not be_nil
  getKeyValue('mediumOfInstruction').should == medium
end

Then /^I should see the population served is "([^"]*)"$/ do |pop|
  parsed_results.should_not be_nil
  getKeyValue('populationServed').should == pop
end

Given /^the sequence of course is (\d+)$/ do |course_seq|
  course_seq.should_not be_nil
  @course_seq = course_seq.to_i
end

When /^I navigate to DELETE (section "[^"]*")$/ do |section_uri|
  section_uri.should_not be_nil
  restHttpDelete(section_uri)
end

When /^I navigate to PUT (section "[^"]*")$/ do |section_uri|

  restHttpGet(section_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH = JSON.parse(@res.body)
    dataH['sequenceOfCourse'].should_not == @course_seq
    dataH['sequenceOfCourse'] = @course_seq
    data = dataH.to_json
  elsif @format == "application/xml"
    doc = Document.new(@res.body)  
    data = doc
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(section_uri, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  
end

When /^I attempt to update a non\-existing (section "[^"]*")$/ do |section_uri|
  section_uri.should_not be_nil
  data = data_builder
  restHttpPut(section_uri, data)
end

