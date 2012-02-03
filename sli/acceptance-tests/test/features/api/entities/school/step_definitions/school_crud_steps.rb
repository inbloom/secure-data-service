require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
#puts $:


Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "'Apple Alternative Elementary School' ID"
  id = arg1+"2058ddfb-b5c6-70c4-3bee-b43e9e93307d" if arg2 == "'Yellow Middle School' ID"
  id = arg1+"fdacc41b-8133-f12d-5d47-358e6c0c791c" if arg2 == "'Delete Me Middle School' ID"
  id = arg1+@newId                                 if arg2 == "'newly created school' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111" if arg2 == "'that doesn't exist' ID"
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "'using a wrong URI' ID"
  id = arg1                                        if arg2 == "'with no GUID' ID"
  #id = step_arg if id == nil
  id
end

Transform /^([^"]*)<([^"]*)>\/targets$/ do |arg1, arg2|
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2/targets" if arg2 == "'Apple Alternative Elementary School' ID"
  id
end

Given /^the "([^\"]+)" is "([^\"]+)"$/ do |key, value|
  if !defined? @data
    @data = {}
  end
  value = convert(value)
  @data[key] = value
end

Then /^I should see the "([^"]*)" is "([^"]*)"$/ do |arg1, arg2|
  if(arg1 == "telephoneNumber")
    assert(@result['telephone'][0][arg1] == arg2, "Expected attribute name not found in response")
  else
    assert(@result[arg1] == arg2, "Expected attribute name not found in response")
  end
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |key, value|
  value = convert(value)
  @result[key] = value
end

When /^I navigate to POST "([^"]*)"$/ do |url|
  @data["gradesOffered"] = Hash["gradeLevel"=>["First grade", "Second grade"]]
  @data["stateOrganizationId"] = "555"
  @data["address"]=[]
  @data["organizationCategories"]= Hash["organizationCategory"=>["School"]]
  @data["schoolCategories"]=Hash["schoolCategory"=>["Elementary School"]]
  data = prepareData(@format, @data)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end

When /^I attempt to update "([^"]*<[^"]*>)"$/ do |url|
  @result = {}
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

Then /^I should see the school "([^"]*)"$/ do |arg1| 
  assert(@result['nameOfInstitution'] == arg1, "Expected school name not found in response")
end

Then /^I should see a website of "([^"]*)"$/ do |arg1|
  assert(@result['webSite'] == arg1, "Expected website name not found in response")
end

Then /^I should see a phone number of "([^"]*)"$/ do |arg1|
  assert(@result['telephone'][0]['telephoneNumber'] == arg1, "Expected website name not found in response")
end



