require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<(.+)>$/ do |template|
  id = template
  id = "344cf68d-50fd-8dd7-e8d6-ed9df76c219c" if template == "'Belle' ID"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2" if template == "'Apple Alternative Elementary School' ID"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

def createBaseStudent()
  data = Hash[
      "studentUniqueStateId" => 123456,
      "name" => Hash[
        "firstName" => "fname",
        "lastSurname" => "lname",
        "middleName" => "mname"],
      "sex" => "Male",
      "birthData" => Hash[
        "birthDate" => "2012-01-01"
        ]
      ]
  return data
end

def createBaseSchool()
  data = Hash[
      "nameOfInstitution" => "school name",
      "stateOrganizationId" => "12345678",
      "gradesOffered" => [ "First_grade", "Second_grade" ],
      ]
  return data
end

def createXlengthString(x)
  value = ""
  x.times {value << 'a'}
  value
end

Given /^I create a valid base level student object$/ do
  @object = createBaseStudent()
end

Given /^I create a valid base level school object$/ do
  @object = createBaseSchool()
end

Given /^I create a blank json object$/ do
  @object = Hash[]
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  #fix below
  @format = "application/json"
  if @format == "application/json"
    data = @object.to_json
  elsif @format == "application/xml"
    data = @object
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Given /^I create a student object with "([^"]*)" set to Guy$/ do |arg1|
  @object = createBaseStudent()
  @object['sex'] = 'Guy'
end

Given /^I create a student object with sex equal to "([^"]*)" instead of "([^"]*)"$/ do |arg1, arg2|
  @object = createBaseStudent()
  @object['sex'] = arg1
end

Given /^I create a create a school object with "([^"]*)" set to a single map$/ do |arg1|
   @object = createBaseSchool()
   @object[arg1] = Hash["streetNumberName" => "123 Elm Street", 
                             'city'=>"New York", 
                             "stateAbbreviation" => "NY",
                             "postalCode" => "12345"
                             ]
end

Given /^I create the same school object with "([^"]*)" as an array with the same map$/ do |arg1|
  @object = createBaseSchool()
   @object[arg1] = [Hash["streetNumberName" => "123 Elm Street", 
                             'city'=>"New York", 
                             "stateAbbreviation" => "NY",
                             "postalCode" => "12345"
                             ]]
end


Given /^I create a student object with "([^"]*)" set to an array of names$/ do |arg1|
  @object = createBaseStudent()
  @object[arg1] = [@object[arg1]]
end

Given /^I create the same student object with "([^"]*)" as a map with the same data$/ do |arg1|
  @object = createBaseStudent()
end

Given /^an SSA object is valid except for "([^"]*)"$/ do |arg1|
  @object = Hash[
  "studentId" => "714c1304-8a04-4e23-b043-4ad80eb60992",
  "entryGradeLevel" => "EIGHTH_GRADE"
  ]
  @object[arg1] = "11111111-1111-1111-1111-111111111111"
end

Given /^I create a student object with "([^"]*)" equal to a string$/ do |arg1|
  @object = createBaseStudent()
  @object[arg1] = "123456"
end

Given /^I create a school object with "([^"]*)" equal to a integer$/ do |arg1|
  @object = createBaseSchool()
  @object[arg1] = 12345678
end

Given /^I create a school object with "([^"]*)" equal to a (\d+) character string$/ do |arg1, arg2|
  @object = createBaseSchool()
  @object[arg1] = createXlengthString(Integer(arg2))
end

Given /^I create a student object with "([^"]*)" set to a true string$/ do |arg1|
  @object = createBaseStudent()
  @object[arg1] = "true"
end

Given /^I create a student object with "([^"]*)" set to MM\-DD\-YYYY$/ do |arg1|
  @object = createBaseStudent()
  @object['birthData'][arg1] = "01-01-2012"
end

When /^I navigate to PUT "([^\"]*)"$/ do |url|
  @result = {} if !defined? @result 
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I create a blank request body object$/ do
  @result = {}
end

When /^I create a base school object$/ do
  @result = createBaseSchool()
end

Then /^"([^\"]*)" should be "([^\"]*)"$/ do |key, value|
  @result[key].should_not == nil
  @result[key].should == value
  @result.delete(key)
end

Then /^"([^"]*)" should contain "([^"]*)" and "([^"]*)"$/ do |key, val1, val2|
  @result[key].should_not == nil
  assert(@result[key].find(val1) != nil, "School's gradesOffered does not contain #{val1}")
  assert(@result[key].find(val2) != nil, "School's gradesOffered does not contain #{val2}")
  @result.delete(key)
end

Then /^there should be no other contents in the response body other than links$/ do
  @result.delete('links')
  @result.delete('id')
  assert(@result == {}, "The response body still contains data that was previously there but *not* in the PUT data")
end
