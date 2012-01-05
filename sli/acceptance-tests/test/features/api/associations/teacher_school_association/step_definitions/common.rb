require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

#vnd.slc+json format is not ready for testing
#remove this transform to switch to new format
Transform /^application\/vnd\.slc\+json$/ do |args|
  "application/json"
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |user, password|
  idpLogin(user,password)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
    @format = fmt
end

When /^I navigate to (\/[^"]*\/)(<[^"]*>)$/ do |path_type_arg,path_specific_arg|
    restHttpGet(path_type_arg+path_specific_arg)
    assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a return code of (\d+)$/ do |code|
  assert(@res.code == Integer(code), "Return code was not expected: #{@res.code.to_s} but expected #{code}")
end

Then /^the (.*) (should be|is) "([^"]*)"$/ do |property_name, dummy, program_arg|
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    assert(result[property_name]==program_arg, "Expected property not found in response name: #{property_name} val:#{result[property_name]}")
end

# 
# Function data_builder
# Inputs: object_type - used only for error message when ID cannot be found
# Output: None
# Returns: ID of newly created object
# Description: Searches headers from an HTTP response for the newly created object's ID and returns it
#
def getIdOfNewlyCreatedObject (object_type="Object")
  headers = @res.raw_headers
  headers.should_not be_nil
  headers['location'].should_not be_nil
  s = headers['location'][0]
  newId = s[s.rindex('/')+1..-1]
  assert(newId != nil, "#{object_type} ID is nil")
  newId
end

# function: validateValueInResults
# inputs:   value: value to check for
#           property: what name to search under for the value
#           description: how to describe the value not found if/when value is not found
# outputs:  true/false
# Returns:  Nothing, see output
# Description: Helper function for all the tests that need to check the presence of a value
#               in an array that is in a result body
def validateValueInResults(value,property,description)
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    assert(result[property]==value, "Expected #{description} not found in response")
end

# function: validateValueInResultArray
# inputs:   value: value to check for
#           property: what name to search under for the value
#           description: how to describe the value not found if/when value is not found
# outputs:  true/false
# Returns:  Nothing, see output
# Description: Helper function for all the tests that need to check the presence of a value
#               in an array that is in a result body
def validateValueInResultArray(value,property,description)
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    arrayOfResults = result[property]
    valueFound = false
    arrayOfResults.each do |valueFromArray|
        if(value==valueFromArray)
            valueFound = (true)
        end
    end
    assert(valueFound, "Expected #{description} not found in response")
end