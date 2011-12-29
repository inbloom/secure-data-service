require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'

Transform /^\/student-school-associations\/<([^"]*)>$/ do |step_arg|
  s = "/student-school-associations/"
  id = s+"eb3b8c35-f582-df23-e406-6947249a19f2" if step_arg == "Apple Alternative Elementary School ID"
  id = s+"714c1304-8a04-4e23-b043-4ad80eb60992" if step_arg == "Alfonso's ID"
  id = s+"122a340e-e237-4766-98e3-4d2d67786572" if step_arg == "Alfonso at Apple Alternative Elementary School ID"
  id = s+"11111111-1111-1111-1111-111111111111" if step_arg == "Invalid ID"
  id = s                                        if step_arg == "No GUID"
  id
end


Given /^I have access to all students and schools$/ do
  idpLogin(@user,@passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Then /^I should receive a ID for the newly created student\-school\-association$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  assocId = s[s.rindex('/')+1..-1]
  assert(assocId != nil, "Student-School-Association ID is nil")
end


When /^I navigate to POST "([^"]*)"$/ do |uri|
  if @format == "application/json"
    dataH = @fields
    data=dataH.to_json
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to PUT "(\/student-school-associations\/<[^"]*>)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  if @format == "application/json"
      modified = JSON.parse(@res.body)
      @fields.each do |key, value|
        modified[key] = value
      end
      data = modified.to_json
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
    restHttpPut(uri, data)
    assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I attempt to update a non\-existing association "(\/student-school-associations\/<[^"]*>)"$/ do |uri|
  data = {}
  restHttpPut(uri, data.to_json)
  assert(@res != nil, "Response from rest-client PUT is nil")
end
