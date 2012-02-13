require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM       
###############################################################################

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  #schools
  id = "eb3b8c35-f582-df23-e406-6947249a19f2" if template == "<'APPLE ELEMENTARY (SCHOOL)' ID>"
  #sessions
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e" if template == "<'FALL 2011 (SESSION)' ID>"
  #courses
  id = "53777181-3519-4111-9210-529350429899" if template == "<'FRENCH 1 (COURSE)' ID>"
  id = "67ce204b-9999-4a11-aacc-000000000003" if template == "<'RUSSIAN 1 (COURSE)' ID>"
  #sections
  id = "2934f72d-f9e3-48fd-afdd-56b94e2a3454" if template == "<'BIOLOGY F09J (SECTION)' ID>"
  id = @newId.to_s                            if template == "<'NEWLY CREATED SECTION' ID>"
  #general
  id = "11111111-1111-1111-1111-111111111111" if template == "<'INVALID' ID>"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

#assign a given value for a field
Given /^the "([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = convert(value)
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

#when I POST
When /^I navigate to POST "([^\"]+)"$/ do |url|
  data = prepareData(@format, @fields)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

#when I PUT
When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  if(@res.code != 400)
    assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
  else
    assert(@res.body.length > 0, "Validation failed but no reason was given")
  end
end

#when I change a value (after a GET, before a PUT)
When /^I set "([^"]*)" to "([^"]*)"$/ do |key, value|
  step "the \"#{key}\" is \"#{value}\""
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

#then a specific key/value pair should be an expected value
Then /^the "([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result[key].to_s == value.to_s, "Expected data incorrect: Expected #{value} but got #{@result[key]}")
end
