require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "Apple Alternative Elementary School ID"
  id = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992" if arg2 == "Alfonso's ID"
  id = arg1+"e1af7127-743a-4437-ab15-5b0dacd1bde0" if arg2 == "Priscilla's ID"
  id = arg1+"8cc0a1ac-ccb5-dffc-1d74-32964722179b" if arg2 == "Purple Middle School ID"
  id = arg1+"122a340e-e237-4766-98e3-4d2d67786572" if arg2 == "Alfonso at Apple Alternative Elementary School ID"
  id = arg1+"d84ed04c-d922-44d6-91a2-7395afc69748" if arg2 == "Priscilla at Orange Middle School ID"
  id = arg1+"4f3cb2fc-d2e6-4333-93c8-849928c49a6a" if arg2 == "Priscilla at Ellington Middle School ID"
  id = arg1+"ded6e1c6-146f-4f68-8a21-99446abab492" if arg2 == "Donna at Purple Middle School ID"
  id = arg1+"f7d86a4e-4d4a-49f6-9b8b-80973f1ae501" if arg2 == "Rachel at Purple Middle School ID"
  id = arg1+"11111111-1111-1111-1111-111111111111" if arg2 == "Invalid ID"
  id = arg1                                        if arg2 == "No GUID"
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

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |uri|
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
