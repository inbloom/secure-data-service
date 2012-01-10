require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^\/student-assessment-associations\/<([^>]*)>$/ do |step_arg|
  s = "/student-assessment-associations/"
  id = s+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" if step_arg == "'Mathematics Achievement Assessment Test' ID"
  id
end

Transform /^assessment "([^"]*)"$/ do |step_arg|
  id = "/assessments/dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" if step_arg == "Mathematics Achievement Assessment Test ID"
  id = "/assessments/29f044bd-1449-4fb7-8e9a-5e2cf9ad252a" if step_arg == "Mathematics Assessment 2"
  id = "/assessments/542b0b38-ea57-4d81-aa9c-b55a629a3bd6" if step_arg == "Mathematics Assessment 3"
  id = "/assessments/6c572483-fe75-421c-9588-d82f1f5f3af5" if step_arg == "Writing Assessment 1"
  id = "/assessments/df897f7a-7ac4-42e4-bcbc-8cc6fd88b91a" if step_arg == "Writing Assessment 2"
  id = "/assessments/11111111-1111-1111-1111-111111111111" if step_arg == "NonExistentAssessment"
  id = "/assessments"                                      if step_arg == "NoGUID" or step_arg == nil
  id
end

Transform /^\/assessments\/<([^>]*)>$/ do |step_arg|
  s = "/assessments/"
  id = s+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" if step_arg =="'Mathematics Achievement Assessment Test' ID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all assessments$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^AssessmentTitle is "([^"]*)"$/ do |arg1|
  @assessmentTitle = arg1
  @assessmentTitle.should_not == nil
end

Given /^AssessmentIdentificationCode is "([^"]*)"$/ do |arg1|
  @assessmentIdentificationCode = arg1
  @assessmentIdentificationCode.should_not == nil
end

Given /^AcademicSubject is "([^"]*)"$/ do |arg1|
  @academicSubject = arg1
  @academicSubject.should_not == nil
end

Given /^AssessmentCategory is "([^"]*)"$/ do |arg1|
  @assessmentCategory = arg1
  @assessmentCategory.should_not == nil
end

Given /^GradeLevelAssessed is "([^"]*)"$/ do |arg1|
  @gradeLevelAssessed = arg1
  @gradeLevelAssessed.should_not == nil
end

Given /^ContentStandard is "([^"]*)"$/ do |arg1|
  @contentStandard = arg1
  @contentStandard.should_not == nil
end

Given /^Version is "([^"]*)"$/ do |arg1|
  @version = arg1
  @version.should_not == nil
end

When /^I navigate to POST (assessment "[^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "assessmentTitle" => @assessmentTitle,
      "assessmentIdentificationCode" => [Hash["identificationSystem"=>"School","id"=>@assessmentIdentificationCode]],
      "academicSubject" => @academicSubject,
      "assessmentCategory" => @assessmentCategory,
      "gradeLevelAssessed" => @gradeLevelAssessed,
      "contentStandard" => @contentStandard,
      "version" => @version
      ]
    data = dataH.to_json
      
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.teacher { |b| 
      b.teacherUniqueStateId(@teacherUniqueStateId)
      b.name(@name) 
      b.sex(@sex)
      b.birthDate(@bdate)}
      
  else
    assert(false, "Unsupported MIME type")
  end

  restHttpPost("/assessments", data)
  puts @res.body      
  assert(@res != nil, "Response from rest-client POST is nil")

end

Then /^I should receive an ID for a newly created assessment$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  newId = s[s.rindex('/')+1..-1]
  assert(newId != nil, "Assessment ID is nil")
end

When /^I navigate to GET (assessment "[^"]*")$/ do |arg1|
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I set the AssessmentTitle to "([^"]*)"$/ do |arg1|
  @assessmentTitle=arg1
end

When /^I navigate to PUT (assessment "[^"]*")$/ do |arg1|

  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  if @format == "application/json"  
    dataH = JSON.parse(@res.body)
    
    dataH["assessmentTitle"].should_not == @assessmentTitle
    dataH["assessmentTitle"] = @assessmentTitle
    
    data = dataH.to_json

  elsif @format == "application/xml"
  
    data = Document.new(@res.body)  
    data.root.elements["assessmentTitle"].text.should_not == @assessmentTitle
    data.root.elements["assessmentTitle"].text = @assessmentTitle

  else
    assert(false, "Unsupported MIME type")
  end
  
  restHttpPut(arg1, data)
  puts @res.body
  assert(@res != nil, "Response from rest-client PUT is nil")
  
end

When /^I navigate to DELETE (assessment "[^"]*")$/ do |arg1|
  restHttpDelete(arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end


Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^the AssessmentTitle should be "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["assessmentTitle"] == arg1, "Expected assessmentTitle not found in response")
end

Then /^the AcademicSubject should be "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["academicSubject"] == arg1, "Expected academicSubject not found in response")
end

Then /^the AssessmentCategory should be "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["assessmentCategory"] == arg1, "Expected assessmentCategory not found in response")
end

Then /^I should receive a link named "([^"]*)" with URI (\/assessments\/<[^>]*>)$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href
      found =true
      end
    end
      assert(found, "didnt receive link named #{rel} with URI #{href}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should receive a link named "([^"]*)" with URI (\/student\-assessment\-associations\/<[^>]*>)$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href
      found =true
      end
    end
      assert(found, "didnt receive link named #{rel} with URI #{href}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end


When /^I attempt to update a non\-existing (assessment "[^"]*")$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "assessmentTitle" => @assessmentTitle,
      "academicSubject" => @academicSubject,
      "assessmentCategory" => @assessmentCategory,
      "gradeLevelAssessed" => @gradeLevelAssessed,
      "contentStandard" => @contentStandard]
    
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.assessment { |b|
      b.assessmentTitle(@assessmentTitle)
      b.academicSubject(@academicSubject) 
      b.assessmentCategory(@assessmentCategory)
      b.gradeLevelAssessed(@gradeLevelAssessed)
      b.contentStandard(@contentStandard)
      }
    
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end