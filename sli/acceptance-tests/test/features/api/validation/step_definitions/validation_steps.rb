require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'

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


Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  idpLogin(arg1, arg2)
  assert(@cookie != nil, "Cookie retrieved was nil")
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

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
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
