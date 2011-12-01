require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^the birth date is "([^"]*)"$/ do |arg1|
  d = Date.strptime(arg1, '%m/%d/%Y')
  @bdate = d.to_time.to_i*1000
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
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  $newStudentID = s[s.rindex('/')+1..-1]
  assert($newStudentID != nil, "Student ID is nil")
end

Then /^GET using that ID should return a code of (\d+)$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see the student "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['firstName'] == arg1, "Expected student firstname not found in response")
  assert(result['middleName'] == arg2, "Expected student middlename not found in response")
  assert(result['lastSurname'] == arg3, "Expected student lastname not found in response")
end

Then /^I should see that he or she is "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  result['sex'].should == arg1
end

Then /^I should see that he or she was born on "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  d = Date.strptime(arg1, '%m/%d/%Y')
  assert(result['birthDate'] == d.to_time.to_i*1000, "Expected student birthdate not found in response")
end

Then /^GET "([^"]*)" should return a code of (\d+)$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I GET the newly created student by id$/ do
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+$newStudentID
  @res = RestClient.get(url,:accept => @format){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I PUT\/update the newly created students's birthdate$/ do
  if @format == "application/json"
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+$newStudentID
    @res = RestClient.get(url,:accept => @format){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    data = JSON.parse(@res.body)
    data['birthDate'].should_not == @bdate
    data['birthDate'] = @bdate
    
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+$newStudentID
    @res = RestClient.put(url, data.to_json, :content_type => @format){|response, request, result| response }
    assert(@res != nil, "Response from rest-client PUT is nil")
  elsif @format == "application/xml"
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+$newStudentID
    @res = RestClient.get(url,:accept => @format){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    
    doc = Document.new(@res.body)  
    doc.root.elements["birthDate"].text.should_not == @bdate
    doc.root.elements["birthDate"].text = @bdate
    
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+$newStudentID
    @res = RestClient.put(url, doc, :content_type => @format){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I DELETE the newly created student$/ do
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+$newStudentID
  @res = RestClient.delete(url,:accept => @format){|response, request, result| response }
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

When /^I attempt to update a non\-existing student "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = Hash[
      "studentSchoolId" => "",
      "firstName" => "Should",
      "lastSurname" => "Exist",
      "middleName" => "Not",
      "sex" => "Male",
      "birthDate" => "765432000000"]
    
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data.to_json, :content_type => @format){|response, request, result| response }
    assert(@res != nil, "Response from rest-client PUT is nil")
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b|
      b.studentSchoolId("")
      b.firstName("Should") 
      b.lastSurname("Exist")
      b.middleName("Not")
      b.sex("Male")
      b.birthDate("765432000000")}
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data, :content_type => @format){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to GET to said student$/ do
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+@tempID.to_s
  @res = RestClient.get(url,:accept => @format){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

Given /^a known student exists$/ do
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/students"
  @res = RestClient.get(url,:accept => "application/json"){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
  jsonData = JSON.parse(@res.body)
  @tempID = jsonData[0]['studentId']
  @tempID.should_not == ""
  @tempID.should_not == nil
end

When /^I navigate to GET to said student with "([^"]*)"$/ do |arg1|
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1+@tempID.to_s
  @res = RestClient.get(url,:accept => @format){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end