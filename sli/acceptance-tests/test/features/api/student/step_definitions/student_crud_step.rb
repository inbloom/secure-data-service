require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../utils/sli_utils.rb'

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^the birth date is "([^"]*)"$/ do |arg1|
  time = Time.new(1996, 9,23)
  @bdate = time.to_i*1000
end

Given /^that he or she is "([^"]*)"$/ do |arg1|
  ["Male","Female"].should include(arg1)
  @sex = arg1
end

Given /^the name is "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  @fname = arg1
  @fname.should_not == nil
  @mname = arg2
  @lname = arg3
  @lname.should_not == nil
end

Given /^the student_school id is "([^"]*)"$/ do |arg1|
  @studentSchoolId = arg1
  @studentSchoolId.should_not == nil
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive a ID for the newly created student$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^GET using that ID should return a code of (\d+)$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see the student "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see that he or she is "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see that he or she was born on "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^GET "([^"]*)" should return a code of (\d+)$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I GET the newly created student by id$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I should see the student "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end

When /^I PUT\/update the newly created students's birthdate$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I GET the newly created school by id$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I DELETE the newly created student$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I attempt to update a non\-existing student "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^a known students exists$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to GET to said student$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^a known student exists$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to GET to said student with "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end