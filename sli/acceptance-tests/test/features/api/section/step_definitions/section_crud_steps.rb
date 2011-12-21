require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'
require_relative './section_crud_helper'

Transform /^section "([^"]*)"$/ do |step_arg|
  id = "/sections/f5532495-f338-4c32-afe2-e5452e2f8de2"  if step_arg == "123"
  id = "/sections/b5884643-01aa-474e-974f-5038a307ee62"  if step_arg == "897"
  id = "/sections/c76f8fb1-cef4-4839-bfeb-ab2afb8b8aec"  if step_arg == "1500"
  id = "/sections/11111111-1111-1111-1111-111111111111"  if step_arg == "Invalid"
  id = "/sections/dad5b409-9660-4843-82ba-07d7a4ff37cb"  if step_arg == "567"
  id = "/section/11111111-1111-1111-1111-111111111111"  if step_arg == "WrongURI"
  id = "/sections"                                       if step_arg == "NoGUID"
  id
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
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
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Given /^the unique section code is "([^"]*)"$/ do |section_code|
  section_code.should_not be_nil
  @section_code = section_code
end

Given /^the sequence of course is "([^"]*)"$/ do |course_seq|
  course_seq.should_not be_nil
  @course_seq = course_seq
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

Given /^the available credit is "([^"]*)"$/ do |credit|
  credit.should_not be_nil
  @credit = credit
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
  newSectionId = s[s.rindex('/')+1..-1]
  newSectionId.should_not be_nil
end

When /^I navigate to GET (section "[^"]*")$/ do |section_uri|
  restHttpGet(section_uri)
end

Then /^I should see the sequence of course is (\d+)$/ do |course_seq|
  parsed_results.should_not be_nil
  parsed_results['sequenceOfCourse'].should == course_seq
end

Then /^I should see the educational environment is "([^"]*)"$/ do |edu_env|
  parsed_results.should_not be_nil
  parsed_results['educationalEnvironment'].should == edu_env
end

Then /^I should see the medium of instruction is "([^"]*)"$/ do |medium|
  parsed_results.should_not be_nil
  parsed_results['mediumOfInstruction'].should == medium
end

Then /^I should see the population served is "([^"]*)"$/ do |pop|
  parsed_results.should_not be_nil
  parsed_results['populationServed'].should == pop
end

Then /^I should see the available credit is "([^"]*)"$/ do |credit|
  parsed_results.should_not be_nil
  parsed_results['availableCredit'].should == credit
end

Given /^the sequence of course is (\d+)$/ do |course_seq|
  course_seq.should_not be_nil
  @course_seq = course_seq
  @course_seq.should_not be_nil
end

When /^I navigate to DELETE (section "[^"]*")$/ do |section_uri|
  section_uri.should_not be_nil
  restHttpDelete(section_uri)
end

When /^I navigate to PUT (section "[^"]*")$/ do |section_uri|
  section_uri.should_not be_nil
  data = data_builder
  restHttpPut(section_uri, data)
end

When /^I attempt to update a non\-existing (section "[^"]*")$/ do |section_uri|
  section_uri.should_not be_nil
  data = data_builder
  restHttpPut(section_uri, data)
end

