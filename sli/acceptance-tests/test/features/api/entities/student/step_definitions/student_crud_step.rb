require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992"   if arg2 == "'Alfonso' ID"
  id = arg1+"e1af7127-743a-4437-ab15-5b0dacd1bde0"   if arg2 == "'Priscilla' ID"
  id = arg1+"61f13b73-92fa-4a86-aaab-84999c511148"   if arg2 == "'Alden' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111"   if arg2 == "'Invalid' ID"
  id = arg1+"289c933b-ca69-448c-9afd-2c5879b7d221"   if arg2 == "'Donna' ID"
  id = arg1+"c7146300-5bb9-4cc6-8b95-9e401ce34a03"   if arg2 == "'Rachel' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111"   if arg2 == "'WrongURI' ID"
  id = arg1+@newId                                   if arg2 == "'newly created student' ID"
  id = arg1                                          if arg2 == "'NoGUID' ID"
  id
end

Transform /^([^"]*)<([^"]*)>\/targets$/ do |arg1, arg2|
  id = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992/targets" if arg2 == "'Alfonso' ID"
  id
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
  if !/^studentUniqueStateId$/.match key
    value = convert(value)
  end
  if key == 'birthDate'
    @data['birthData'] = Hash[key => value]
  else
    @data[key] = value
  end
end


When /^I set the "([^"]*)" to "([^"]*)"$/ do |key, value|
  if !/^studentUniqueStateId$/.match key
    value = convert(value)
  end
  if key == 'birthDate'
    @result['birthData'] = Hash[key => value]
  else
    @result[key] = value
  end
end



Then /^the "([^"]*)" should be "([^"]*)"$/ do |arg1, arg2|
  if(arg1 == 'birthDate')
    assert(@result['birthData'][arg1] == arg2, "Expected data incorrect: Expected #{arg2} but got #{@result[arg1]}")
  else
    assert(@result[arg1].to_s == arg2, "Expected data incorrect: Expected #{arg2} but got #{@result[arg1]}")
  end
  
end

Then /^the "([^"]*)" should be "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  assert(@result[arg1]['firstName'] == arg2, "Expected data incorrect")
  assert(@result[arg1]['middleName'] == arg3, "Expected data incorrect")
  assert(@result[arg1]['lastSurname'] == arg4, "Expected data incorrect")
end


When /^I attempt to update "([^"]*<[^"]*>)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "studentUniqueStateId" => "",
      "name" => Hash[
        "firstName" => "should",
        "lastSurname" => "not",
        "middleName" => "exist"],
      "sex" => "",
      "birthData" => Hash[
        "birthDate" => ""
        ]
      ]
    data = dataH.to_json
  elsif @format == "application/xml"
    #not supported
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end


When /^I navigate to POST "([^"]*)"$/ do |arg1|
  data = prepareData(@format, @data)
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end



When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end



