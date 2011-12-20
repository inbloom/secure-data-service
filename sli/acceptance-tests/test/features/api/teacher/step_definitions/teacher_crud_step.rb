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
  #d = Date.strptime(arg1, '%m/%d/%Y')
  #@bdate = d.to_time.to_i*1000
  @bdate = arg1
end

Given /^that his or her sex is "([^"]*)"$/ do |arg1|
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

Given /^his or her level of education is "([^"]*)"$/ do |arg1|
  ["Bachelor\'s", "Master\'s", "Doctorate", "No Degree"].should include(arg1)
  @levelOfEducation = arg1
end

Given /^the teacher_unique_state id is (\d+)$/ do |arg1|
  @teacherUniqueStateId = arg1
  @teacherUniqueStateId.should_not == nil
end

Given /^I should see that his or her hispanic latino ethnicity is "([^"]*)"$/ do |arg1|
  ["true","false"].should include(arg1)
  @hispanicLatinoEthnicity = arg1
  @hispanicLatinoEthnicity.should_not == nil
end


Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive a ID for the newly created teacher$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newTeacherID = s[s.rindex('/')+1..-1]
  assert(@newTeacherID != nil, "Teacher ID is nil")
end

Then /^I should see the teacher "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['name']['first'] == arg1, "Expected teacher firstname not found in response")
  assert(result['name']['middle'] == arg2, "Expected teacher middlename not found in response")
  assert(result['name']['last'] == arg3, "Expected teacher lastname not found in response")
end

Then /^I should see that he or she is "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  result['sex'].should == arg1
end

Then /^I should see that he or she was born on "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['birthDate'] == arg1, "Expected teacher birthdate not found in response")
end

When /^I attempt to update a non\-existing (teacher \w+)$/ do |arg1|
  if @format == "application/json"
    data = Hash[
      "teacherUniqueStateId" => "",
      "name" => Hash[ "first" => "Should", "middle" => "Not", "last" => "Exist" ],
      "sex" => "Male",
      "birthDate" => "765432000000"]
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data.to_json, {:content_type => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
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
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data, {:content_type => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^GET using that ID should return a code of (\d+)$/ do |arg1|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest/teachers/" + @newTeacherID
  @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end
