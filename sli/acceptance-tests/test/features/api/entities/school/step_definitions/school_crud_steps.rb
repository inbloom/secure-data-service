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
  id = arg1+@newSchoolID                           if arg2 == "'newly created school' ID"
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


Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all schools$/ do
  idpLogin(@user,@passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
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
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  if(arg1 == "telephoneNumber")
    assert(result['telephone'][0][arg1] == arg2, "Expected attribute name not found in response")
  else
    assert(result[arg1] == arg2, "Expected attribute name not found in response")
  end
end

#this is not generic....get the data first and then update it in a generic fashion
When /^I set the "([^"]*)" to "([^"]*)"$/ do |arg1, arg2|
  @fullName = arg2
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive a ID for the newly created school$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result of JSON parsing is nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newSchoolID = s[s.rindex('/')+1..-1]
  assert(@newSchoolID != nil, "School ID is nil")
end

Then /^I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
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

When /^I navigate to GET "([^"]*<[^"]*>)"$/ do |arg1|
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
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

When /^I navigate to DELETE "([^"]*<[^"]*>)"$/ do |arg1|
  restHttpDelete(arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should see the school "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['nameOfInstitution'] == arg1, "Expected school name not found in response")
end

Then /^I should see a website of "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['webSite'] == arg1, "Expected website name not found in response")
end

Then /^I should see a phone number of "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['telephone'][0]['telephoneNumber'] == arg1, "Expected website name not found in response")
end



