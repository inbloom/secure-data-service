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

Given /^the "shortNameOfInstitution" is "([^"]*)"$/ do |arg1|
  @shortName = arg1
end

Given /^the "nameOfInstitution" is "([^"]*)"$/ do |arg1|
  @fullName = arg1
end

Given /^the "website" is "([^"]*)"$/ do |arg1|
  @websiteName = arg1
end

Then /^I should see the "([^"]*)" is "([^"]*)"$/ do |arg1, arg2|
  if(arg1 == "telephoneNumber")
    assert(@result['telephone'][0][arg1] == arg2, "Expected attribute name not found in response")
  else
    assert(@result[arg1] == arg2, "Expected attribute name not found in response")
  end
end

#this is not generic....get the data first and then update it in a generic fashion
When /^I set the "([^"]*)" to "([^"]*)"$/ do |arg1, arg2|
  @fullName = arg2
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "nameOfInstitution" => @fullName,
      "shortNameOfInstitution" => @shortName,
      "stateOrganizationId" => "123456778",
      "gradesOffered" => [ "First_grade", "Second_grade" ],
      "webSite" => @websiteName]
    data = dataH.to_json
  elsif @format == "application/xml"
    #not valid below
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b| 
      b.fullName(@fullName)
      b.shortName(@shortName) 
      b.stateOrganizationId("50")
      b.webSite(@websiteName)}
  else
    assert(false, "Unsupported MIME type")
  end

  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")

end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |arg1|
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")

  if @format == "application/json"
    dataH = JSON.parse(@res.body)
    dataH['nameOfInstitution'].should_not == @fullName
    dataH['nameOfInstitution'] = @fullName
    data = dataH.to_json
  elsif @format == "application/xml"
    doc = Document.new(@res.body)  
    doc.root.elements["webSite"].text.should_not == @websiteName
    doc.root.elements["webSite"].text = @websiteName
    data = doc
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I attempt to update "([^"]*<[^"]*>)"$/ do |arg1|
  # NOTE: This step def is intended to be used for schools that do not exist.  Use the "I navigate to PUT" to update an existing school
  if @format == "application/json"
    dataH = Hash[
      "nameOfInstitution" => "",
      "shortNameOfInstitution" => "",
      "stateOrganizationId" => "123456778",
      "webSite" => ""
    ]
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b|
      b.fullName("")
      b.shortName("") 
      b.stateOrganizationId("50")
      b.webSite("")}
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
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



