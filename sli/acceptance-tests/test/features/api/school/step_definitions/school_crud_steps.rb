require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'
#puts $:

$newSchoolID


Given /^the SLI_SMALL dataset is loaded$/ do
  
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all schools$/ do
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+@user+"&password="+@passwd
  res = RestClient.get(url){|response, request, result| response }
  @cookie = res.body[res.body.rindex('=')+1..-1]
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^the short name is "([^"]*)"$/ do |arg1|
  @shortName = arg1
end

Given /^the full name is "([^"]*)"$/ do |arg1|
  @fullName = arg1
end

Given /^the website is "([^"]*)"$/ do |arg1|
  @websiteName = arg1
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive a ID for the newly created school$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result of JSON parsing is nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  newSchoolID = s[s.rindex('/')+1..-1]
  assert(newSchoolID != nil, "School ID is nil")
end

When /^I GET the newly created school by id$/ do
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest/schools/"+$newSchoolID
  @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = Hash["fullName" => @fullName,
      "shortName" => @shortName,
      "stateOrganizationId" => "50","webSite" => @websiteName]
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client POST is nil")
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b| 
      b.fullName(@fullName)
      b.shortName(@shortName) 
      b.stateOrganizationId("50")
      b.webSite(@websiteName)}
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client POST is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to GET "([^"]*)"$/ do |arg1|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    data = JSON.parse(@res.body)
    data['nameOfInstitution'].should_not == @fullName
    data['nameOfInstitution'] = @fullName
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client PUT is nil")
  elsif @format == "application/xml"
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    
    doc = Document.new(@res.body)  
    doc.root.elements["webSite"].text.should_not == @websiteName
    doc.root.elements["webSite"].text = @websiteName
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, doc, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I attempt to update a non\-existing school "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = Hash["fullName" => "",
      "shortName" => "",
      "stateOrganizationId" => "50","webSite" => ""]
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client PUT is nil")
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b|
      b.fullName("")
      b.shortName("") 
      b.stateOrganizationId("50")
      b.webSite("")}
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to DELETE "([^"]*)"$/ do |arg1|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.delete(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
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

  
When /^I navigate to GET to said school$/ do
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest/schools/"+@tempID.to_s
  @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to GET to said school with "([^"]*)"$/ do |arg1|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1+@tempID.to_s
  @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should see a phone number of "([^"]*)"$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['telephone'][0]['number'] == arg1, "Expected website name not found in response")
end



