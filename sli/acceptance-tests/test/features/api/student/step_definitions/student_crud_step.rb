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
  @studentSchoolId = Integer(arg1)
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
  newStudentID = s[s.rindex('/')+1..-1]
  assert(newStudentID != nil, "Student ID is nil")
end

Then /^I should see the student "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['name']['firstName'] == arg1, "Expected student firstname not found in response")
  assert(result['name']['middleName'] == arg2, "Expected student middlename not found in response")
  assert(result['name']['lastSurname'] == arg3, "Expected student lastname not found in response")
end

Then /^I should see that he or she is "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  result['sex'].should == arg1
end

Then /^I should see that he or she was born on "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  #d = Date.strptime(arg1, '%m/%d/%Y')
  #assert(result['birthDate'] == d.to_time.to_i*1000, "Expected student birthdate not found in response")
  assert(result['birthData'] != nil, "Birthdata element was nil")
  assert(result['birthData']['birthDate'] == arg1, "Expected student birthdate not found in response")
end

When /^I attempt to update a non\-existing (student "[^"]*")$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "studentSchoolId" => "",
      "firstName" => "Should",
      "lastSurname" => "Exist",
      "middleName" => "Not",
      "sex" => "Male",
      "birthDate" => "765432000000"]
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b|
      b.studentSchoolId("")
      b.firstName("Should") 
      b.lastSurname("Exist")
      b.middleName("Not")
      b.sex("Male")
      b.birthDate("765432000000")}
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end



Transform /^student "([^"]*)"$/ do |step_arg|
  id = "/students/714c1304-8a04-4e23-b043-4ad80eb60992" if step_arg == "Alfonso"
  id = "/students/e1af7127-743a-4437-ab15-5b0dacd1bde0"  if step_arg == "Priscilla"
  id = "/students/61f13b73-92fa-4a86-aaab-84999c511148" if step_arg == "Alden"
  id = "/students/11111111-1111-1111-1111-111111111111"  if step_arg == "Invalid"
  id = "/students/289c933b-ca69-448c-9afd-2c5879b7d221" if step_arg == "Donna"
  id = "/students/c7146300-5bb9-4cc6-8b95-9e401ce34a03" if step_arg == "Rachel"
  id = "/student/11111111-1111-1111-1111-111111111111" if step_arg == "WrongURI"
  id = "/students"                                     if step_arg == "NoGUID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students$/ do
  idpLogin(@user, @passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "studentUniqueStateId" => @studentSchoolId,
      "name" => Hash[
        "firstName" => @fname,
        "lastSurname" => @lname,
        "middleName" => @mname],
      "sex" => @sex,
      "birthData" => Hash[
        "birthDate" => @bdate
        ]
      ]
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.student { |b| 
      b.studentSchoolId(@studentSchoolId)
      b.firstName(@fname) 
      b.lastSurname(@lname)
      b.middleName(@mname)
      b.sex(@sex)
      b.birthDate(@bdate)}      
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to GET (student "[^"]*")$/ do |student_uri|
  restHttpGet(student_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT (student "[^"]*")$/ do |student_uri|
  restHttpGet(student_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  if @format == "application/json"
    dataH = JSON.parse(@res.body)
    dataH['birthData']['birthDate'].should_not == @bdate
    dataH['birthData']['birthDate'] = @bdate
    data = dataH.to_json
  elsif @format == "application/xml"    
    doc = Document.new(@res.body)  
    doc.root.elements["birthDate"].text.should_not == @bdate
    doc.root.elements["birthDate"].text = @bdate
    data = doc
  else
    assert(false, "Unsupported MIME type")
  end
  
  restHttpPut(student_uri, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I navigate to DELETE (student "[^"]*")$/ do |student_uri|
  restHttpDelete(student_uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a link where rel is "([^"]*)" and href ends with "([^"]*)"$/ do |rel, href|
  @data = JSON.parse(@res.body)
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?("links"), "Response contains no links")
  found = false
  @data["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

