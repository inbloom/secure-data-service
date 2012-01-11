require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /.*/ do |step_arg|
  id = step_arg
  id = "1234" if step_arg == "<'Teacher Home State' ID>"
  id = "/teachers/" + @newId.to_s if step_arg == "/teachers/<'newly created teacher' ID>"
  id
end

# Transform /^teacher "([^"]*)"$/ do |step_arg|
#   id = "/teachers/fa45033c-5517-b14b-1d39-c9442ba95782" if step_arg == "Macey"
#   id = "/teachers/344cf68d-50fd-8dd7-e8d6-ed9df76c219c" if step_arg == "Belle"
#   id = "/teachers/824643f7-174b-4a50-9383-c9a6f762c49d" if step_arg == "Christian"
#   id = "/teachers/a249d5d9-f149-d348-9b10-b26d68e7cb9c" if step_arg == "Illiana"
#   id = "/teachers/0e43f14f-ead4-b09f-1ed5-ee5c7e3eeb8c" if step_arg == "Daphne"
#   id = "/teachers/d34fd432-a41c-67ce-1a86-5c2dc6957faf" if step_arg == "Harding"
#   id = "/teachers/eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94" if step_arg == "Simone"
#   id = "/teachers/f44e0c1f-908c-8432-960a-67f650206b88" if step_arg == "Micah"
#   id = "/teachers/e24b24aa-2556-994b-d1ed-6e6f71d1be97" if step_arg == "Quemby"
#   id = "/teachers/8f403e29-2a65-643e-6fac-5ccb53000db2" if step_arg == "Bert"
#   id = "/teachers/11111111-1111-1111-1111-111111111111" if step_arg == "Invalid"
#   id = "/teachers/2B5AB1CC-F082-46AA-BE47-36A310F6F5EA" if step_arg == "Unknown"
#   id = "/teacher/11111111-1111-1111-1111-111111111111"  if step_arg == "WrongURI"
#   id = "/teachers"                                      if step_arg == "NoGUID" or step_arg == nil
#   id
# end

Given /^I am logged in using "([^\"]*)" "([^\"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have access to all teachers$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^\"]*)"$/ do |fmt|
  ["application/json", "application/xml", "text/plain"].should include(fmt)
  @format = fmt
end

Given /^the "name" is "([^\"]+)" "([^\"]+)" "([^\"]+)"$/ do |first, mid, last|
  first.should_not == nil
  last.should_not == nil
  if !defined? @data
    @data = {}
  end
  @data["name"] = { "firstName" => first, "middleName" => mid, "lastSurname" => last };
end

Given /^the "([^\"]+)" is "([^\"]+)"$/ do |key, value|
  if !defined? @data
    @data = {}
  end
  value = convert(value)
  @data[key] = value
  puts "SET @data[#{key}] = #{value}"
end

Given /^the "([^\"]+)" status is "([^\"]+)"$/ do |key, value|
  step "the \"#{key}\" is \"#{value}\""
end

When /^I navigate to POST "([^\"]+)"$/ do |url|
  if @format == "application/json"
    data = @data.to_json
      
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.teacher { |b| 
      b.staffUniqueStateId(@staffUniqueStateId)
      b.name(@name) 
      b.sex(@sex)
      b.birthDate(@bdate)}
      
  else
    assert(false, "Unsupported MIME type")
  end

  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

And /^I should receive a return code of (\d+)$/ do |status|
  @res.code.should == Integer(status)
end

Then /^I should receive an ID for the newly created (\w+)$/ do |entity|
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  assert(@newId != nil, "Teacher ID is nil")
end

When /^I navigate to GET "([^\"]+)"$/ do |url|
  restHttpGet(url)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  @result = JSON.parse(@res.body)
end

Then /^the "name" should be "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |first, mid, last|
  assert(@result["name"] != nil, "Name is nil")
  @result["name"]["firstName"].should == first
  @result["name"]["lastSurname"].should == last
  if last || last.len > 0
    @result["name"]["middleName"].should == mid
  else
    @result["name"]["middleName"].should == nil
  end
end

Then /^the "([^\"]*)" should be "([^\"]*)"$/ do |key, value|
  value = convert(value)
  @result[key].should_not == nil
  @result[key].should == value
end

Given /^the "([^\"]+)" status should be "([^\"]+)"$/ do |key, value|
  step "the \"#{key}\" should be \"#{value}\""
end

### Util methods ###

def convert(value)
  if /^true$/.match value
    true;
  elsif /^false$/.match value
    false;
  elsif /^\d+\.\d+$/.match value
    Float(value)
  elsif /^\d+$/.match value
    Integer(value)
  else
    value
  end
end

# 
# Given /^the birth date is "([^"]*)"$/ do |arg1|
#   @bdate = arg1
# end
# 
# Given /^he is \"Male\"$/ do ||
#   @sex = "Male"
# end
# 
# Given /^she is \"Female\"$/ do ||
#   @sex = "Female"
# end
# 
# Given /^(?:his|her) \"Years of Prior Teaching Experience\" is "(\d+)"$/ do |arg1|
#   @teachingExperience = arg1.to_i
#   @teachingExperience.should_not == nil
# end
# 
# Given /^(?:his|her) \"Teacher Unique State ID\" is "(\d+)"$/ do |arg1|
#   @staffUniqueStateId = Integer(arg1)
#   @staffUniqueStateId.should_not == nil
# end
# 
# Given /^(?:his|her) \"Highly Qualified Teacher\" status is "(\w+)"$/ do |arg1|
#   if(arg1 == "true")
#     @highlyQualifiedTeacher = true
#   elsif(arg1 == "false")
#     @highlyQualifiedTeacher = false
#   else
#     raise "Valid values are true or false"
#   end
#   @highlyQualifiedTeacher.should_not == nil
# end
# 
# Given /^(?:his|her) \"Level of Education\" is "([^"]*)"$/ do |arg1|
#   ["Bachelors", "Masters", "Doctorate", "No_Degree"].should include(arg1)
#   @levelOfEducation = arg1
#   @levelOfEducation.should_not == nil
# end
# 
# Given /^I should see that (?:his|her) hispanic latino ethnicity is "([^"]*)"$/ do |arg1|
#   ["true","false"].should include(arg1)
#   @hispanicLatinoEthnicity = arg1
#   @hispanicLatinoEthnicity.should_not == nil
# end
# 
# When /^I navigate to POST (teacher "[^"]*)"$/ do |arg1|
#   if @format == "application/json"
#     dataH = Hash[
#       "name" => Hash[ "firstName" => @fname, "middleName" => @mname, "lastSurname" => @lname ],
#       "birthDate" => @bdate,
#       "sex" => @sex,
#       "yearsOfPriorTeachingExperience" => @teachingExperience,
#       "highestLevelOfEducationCompleted" => @levelOfEducation,
#       "staffUniqueStateId" => @staffUniqueStateId,
#       "highlyQualifiedTeacher" => @highlyQualifiedTeacher
#       ]
#     data = dataH.to_json
#       
#   elsif @format == "application/xml"
#     builder = Builder::XmlMarkup.new(:indent=>2)
#     data = builder.teacher { |b| 
#       b.staffUniqueStateId(@staffUniqueStateId)
#       b.name(@name) 
#       b.sex(@sex)
#       b.birthDate(@bdate)}
#       
#   else
#     assert(false, "Unsupported MIME type")
#   end
# 
#   restHttpPost("/teachers", data)
#   puts @res.body      
#   assert(@res != nil, "Response from rest-client POST is nil")
# 
# end
# 
# Then /^I should receive an ID for the newly created teacher$/ do
#   headers = @res.raw_headers
#   assert(headers != nil, "Headers are nil")
#   assert(headers['location'] != nil, "There is no location link from the previous request")
#   s = headers['location'][0]
#   newId = s[s.rindex('/')+1..-1]
#   assert(newId != nil, "Teacher ID is nil")
# end
# 
# When /^I navigate to GET (teacher "[^"]*")$/ do |arg1|
#   restHttpGet(arg1)
#   assert(@res != nil, "Response from rest-client GET is nil")
# end
# 
# When /^I navigate to PUT (teacher "[^"]*")$/ do |arg1|
# 
#   restHttpGet(arg1)
#   assert(@res != nil, "Response from rest-client GET is nil")
#   assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
# 
#   if @format == "application/json"  
#     dataH = JSON.parse(@res.body)
#     
#     dataH["highlyQualifiedTeacher"].should_not == @highlyQualifiedTeacher
#     dataH["highlyQualifiedTeacher"] = @highlyQualifiedTeacher
#     
#     data = dataH.to_json
# 
#   elsif @format == "application/xml"
#   
#     data = Document.new(@res.body)  
#     data.root.elements["highlyQualifiedTeacher"].text.should_not == @highlyQualifiedTeacher
#     data.root.elements["highlyQualifiedTeacher"].text = @highlyQualifiedTeacher
# 
#   else
#     assert(false, "Unsupported MIME type")
#   end
#   
#   restHttpPut(arg1, data)
#   puts @res.body
#   assert(@res != nil, "Response from rest-client PUT is nil")
#   
# end
# 
# When /^I navigate to DELETE (teacher "[^"]*")$/ do |arg1|
#   restHttpDelete(arg1)
#   assert(@res != nil, "Response from rest-client DELETE is nil")
# end
# 
# 
# Then /^I should receive a return code of (\d+)$/ do |arg1|
#   assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
# end
# 
# Then /^I should see that the name of the teacher is "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   assert(result["name"]["firstName"] == arg1, "Expected teacher firstname not found in response")
#   assert(result["name"]["middleName"] == arg2, "Expected teacher middlename not found in response")
#   assert(result["name"]["lastSurname"] == arg3, "Expected teacher lastname not found in response")
# end
# 
# Then /^I should see that (?:he|she) is "([^"]*)"$/ do |arg1|
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   result["sex"].should == arg1
# end
# 
# Then /^I should see that (?:he|she) was born on "([^"]*)"$/ do |arg1|
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   assert(result["birthDate"] == arg1, "Expected teacher birthdate not found in response")
# end
# 
# Then /^I should see that (?:his|her) \"Years of Prior Teaching Experience\" is "(\d+)"$/ do |arg1|
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   assert(result["yearsOfPriorTeachingExperience"] == arg1.to_i, "Expected teacher experience not found in response")
# end
# 
# Then /^I should see that (?:his|her) \"Teacher Unique State ID\" is "(\d+)"$/ do |arg1|
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   assert(result["staffUniqueStateId"] == arg1.to_i, "Expected teacher state id not found in response")
# end
# 
# Then /^I should see that (?:his|her) \"Highly Qualified Teacher\" status is "(\w+)"$/ do |arg1|
#   test = true if arg1 == "true"
#   test = false if arg1 == "false"
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   assert(result["highlyQualifiedTeacher"] == test, "Expected teacher highly qualified status not found in response")
# end
# 
# Then /^I should see that (?:his|her) \"Level of Education\" is "([^"]*)"$/ do |arg1|
#   result = JSON.parse(@res.body)
#   assert(result != nil, "Result of JSON parsing is nil")
#   assert(result["highestLevelOfEducationCompleted"] == arg1, "Expected teacher level of education not found in response")  
# end
# 
# When /^I attempt to update a non\-existing teacher "([^"]*)"$/ do |arg1|
#   if @format == "application/json"
#     dataH = Hash[
#       "staffUniqueStateId" => "",
#       "name" => Hash[ "firstName" => "Should", "middleName" => "Not", "lastSurname" => "Exist" ],
#       "sex" => "Male",
#       "birthDate" => "765432000000"]
#     
#     data = dataH.to_json
#   elsif @format == "application/xml"
#     builder = Builder::XmlMarkup.new(:indent=>2)
#     data = builder.school { |b|
#       b.studentSchoolId("")
#       b.firstName("Should") 
#       b.lastSurname("Exist")
#       b.middleName("Not")
#       b.sex("Male")
#       b.birthDate("765432000000")}
#     
#   else
#     assert(false, "Unsupported MIME type")
#   end
#   restHttpPut(arg1, data)
#   assert(@res != nil, "Response from rest-client PUT is nil")
# end
# 
# Then /^GET using that ID should return a code of (\d+)$/ do |arg1|
#   url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest/teachers/" + @newTeacherID
#   @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @sessionId}}){|response, request, result| response }
#   assert(@res != nil, "Response from rest-client GET is nil")
# end
