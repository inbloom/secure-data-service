require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'

Transform /^\/student-assessment-associations\/<([^"]*)>$/ do |step_arg|
  s = "/student-assessment-associations/"
  id = s+"eb3b8c35-f582-df23-e406-6947249a19f2" if step_arg == "Apple Alternative Elementary School ID"
  id = s+"714c1304-8a04-4e23-b043-4ad80eb60992" if step_arg == "Alfonso's ID"
  id = s+"122a340e-e237-4766-98e3-4d2d67786572" if step_arg == "Alfonso at Apple Alternative Elementary School ID"
  id = s+"11111111-1111-1111-1111-111111111111" if step_arg == "Invalid ID"
  id = s                                        if step_arg == "No GUID"
  id
end

Transform /^ID is "([^"]*)"$/ do |step_arg|
  id = "a22532c4-6455-41da-b24d-4f93224f526d" if step_arg == "Mathematics Achievement  Assessment Test ID"
  id = "7afddec3-89ec-402c-8fe6-cced79ae3ef5" if step_arg == "Jane Doe ID"
  id = "11111111-1111-1111-1111-111111111111" if step_arg == "Invalid ID"
  id = ""                                      if step_arg == "No GUID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end


Given /^I have access to all students and assessments$/ do
  idpLogin(@user,@passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

Given /^Assessment (ID is "[^"]*")$/ do |arg1|
  @assessmentId = arg1
  @assessmentId.should_not == nil
  puts @assessmentId
 
end

Given /^Student (ID is "[^"]*")$/ do |arg1|
  @studentId = arg1
  @studentId.should_not == nil
  puts @studentId
end

Given /^AdministrationDate is "([^"]*)"$/ do |arg1|
  @administrationDate = arg1
  @administrationDate.should_not == nil
end

Given /^ScoreResults is "([^"]*)"$/ do |arg1|
  @scoreResults = arg1
  @scoreResults.should_not == nil
end

Given /^PerformanceLevel is "([^"]*)"$/ do |arg1|
   @performanceLevel = arg1
   @performanceLevel.should_not == nil
end

When /^I navigate to POST "([^"]*)"$/ do |uri|
  if @format == "application/json"
    dataH = Hash["studentId"=> @studentId,
      "assessmentId" => @assessmentId,
      "administrationDate" => @administrationDate,
      "performanceLevel"=> @performanceLevel]
    data=dataH.to_json
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: #{@res.code.to_s} but expected #{arg1}")
end

Then /^I should receive a ID for the newly created student\-assessment\-association$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  assocId = s[s.rindex('/')+1..-1]
  assert(assocId != nil, "Student-Assessment-Association ID is nil")
end