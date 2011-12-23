require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'


Transform /^teacher "([^"]*)"$/ do |step_arg|
  id = "/teachers/4bb11755-3c03-45fa-8257-a92b44c9391d" if step_arg == "Macey"
  id = "/teachers/d78dfd50-8df6-4c34-9c21-6cf79dedd6e8" if step_arg == "Belle"
  id = "/teachers/504a4b17-f743-4682-9dc4-62f7a6c98393" if step_arg == "Christian"
  id = "/teachers/48d349f1-d9d5-49a2-9ccb-e7686db2109b" if step_arg == "Illiana"
  id = "/teachers/9fb0d4ea-4ff1-430e-8ceb-3e7e5ceed51e" if step_arg == "Daphne"
  id = "/teachers/ce671ca4-32d4-4fd3-af7f-95c62d5c861a" if step_arg == "Harding"
  id = "/teachers/9ba6ff6c-cc4d-42eb-942c-6bc81f2bb3c1" if step_arg == "Simone"
  id = "/teachers/32848c90-1f0c-4ef4-886b-2050f6670a96" if step_arg == "Micah"
  id = "/teachers/4b995625-aa24-4be2-97be-d1716f6eedd1" if step_arg == "Quemby"
  id = "/teachers/3e64652a-293e-408f-b20d-0053cb5cac6f" if step_arg == "Bert"
  id = "/teachers/11111111-1111-1111-1111-111111111111" if step_arg == "Invalid"
  id = "/teachers/2B5AB1CC-F082-46AA-BE47-36A310F6F5EA" if step_arg == "Unknown"
  id = "/teachers/11111111-1111-1111-1111-111111111111" if step_arg == "WrongURI"
  id = "/teachers"                                      if step_arg == "" or step_arg == nil
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all teachers$/ do
  idpLogin(@user, @passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^the name is "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  @fname = arg1
  @fname.should_not == nil
  @mname = arg2
  @lname = arg3
  @lname.should_not == nil
end

Given /^the birth date is "([^"]*)"$/ do |arg1|
  @bdate = arg1
end

Given /^he is \"Male\"$/ do ||
  @sex = "Male"
end

Given /^she is \"Female\"$/ do ||
  @sex = "Female"
end

Given /^(?:his|her) \"Years of Prior Teaching Experience\" is "(\d+)"$/ do |arg1|
  @teachingExperience = arg1
  @teachingExperience.should_not == nil
end

Given /^(?:his|her) \"Teacher Unique State ID\" is "(\d+)"$/ do |arg1|
  @teacherUniqueStateId = arg1
  @teacherUniqueStateId.should_not == nil
end

Given /^(?:his|her) \"Highly Qualified Teacher\" status is "(\d+)"$/ do |arg1|
  @highlyQualifiedTeacher = arg1
  @highlyQualifiedTeacher.should_not == nil
end

Given /^(?:his|her) \"Level of Education\" is "([^"]*)"$/ do |arg1|
  ["Bachelor\'s", "Master\'s", "Doctorate", "No Degree"].should include(arg1)
  @levelOfEducation = arg1
  @levelOfEducation.should_not == nil
end

Given /^I should see that (?:his|her) hispanic latino ethnicity is "([^"]*)"$/ do |arg1|
  ["true","false"].should include(arg1)
  @hispanicLatinoEthnicity = arg1
  @hispanicLatinoEthnicity.should_not == nil
end

When /^I navigate to POST (teacher "[^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
        "name" => Hash[ "firstName" => @fname, "middleName" => @mname, "lastName" => @lname ],
        "birthDate" => @bdate,
        "sex" => @sex,
        "yearsOfPriorTeachingExperience" => @teachingExperience,
        "highestLevelOfEducationCompleted" => @levelOfEducation],
        "teacherUniqueStateId" => @teacherUniqueStateId,
        "highlyQualifiedTeacher" => @highlyQualifiedTeacher]
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

  restHttpPost("/teachers", data)      
  assert(@res != nil, "Response from rest-client POST is nil")

end

Then /^I should receive an ID for the newly created teacher$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  assert(@newId != nil, "Teacher ID is nil")
end

When /^I navigate to GET (teacher "[^"]*")$/ do |arg1|
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT (teacher "[^"]*")$/ do |arg1|

  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  if @format == "application/json"  
    dataH = JSON.parse(@res.body)
    
    dataH["highlyQualifiedTeacher"].should_not == @highlyQualifiedTeacher
    dataH["highlyQualifiedTeacher"] = @highlyQualifiedTeacher
    
    data = dataH.to_json

  elsif @format == "application/xml"
  
    data = Document.new(@res.body)  
    data.root.elements["highlyQualifiedTeacher"].text.should_not == @highlyQualifiedTeacher
    data.root.elements["highlyQualifiedTeacher"].text = @highlyQualifiedTeacher

  else
    assert(false, "Unsupported MIME type")
  end
  
  restHttpPut(arg1, data)    
  assert(@res != nil, "Response from rest-client PUT is nil")
  
end

When /^I navigate to DELETE (teacher "[^"]*")$/ do |arg1|
  restHttpDelete(arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end


Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should see that the name of the teacher is "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["name"]["firstName"] == arg1, "Expected teacher firstname not found in response")
  assert(result["name"]["middleName"] == arg2, "Expected teacher middlename not found in response")
  assert(result["name"]["lastName"] == arg3, "Expected teacher lastname not found in response")
end

Then /^I should see that (?:he|she) is "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  result["sex"].should == arg1
end

Then /^I should see that (?:he|she) was born on "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["birthDate"] == arg1, "Expected teacher birthdate not found in response")
end

Then /^I should see that (?:his|her) \"Years of Prior Teaching Experience\" is "(\d+)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["yearsOfPriorTeachingExperience"] == arg1, "Expected teacher experience not found in response")
end

Then /^I should see that (?:his|her) \"Teacher Unique State ID\" is "(\d+)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["teacherUniqueStateId"] == arg1, "Expected teacher state id not found in response")
end

Then /^I should see that (?:his|her) \"Highly Qualified Teacher\" status is "(\d+)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["highlyQualifiedTeacher"] == arg1, "Expected teacher highly qualified status not found in response")
end

Then /^I should see that (?:his|her) \"Level of Education\" is "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["staff"]["levelOfEducation"] == arg1, "Expected teacher level of education not found in response")  
end

When /^I attempt to update a non\-existing teacher "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = Hash[
      "teacherUniqueStateId" => "",
      "name" => Hash[ "firstName" => "Should", "middleName" => "Not", "lastName" => "Exist" ],
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
