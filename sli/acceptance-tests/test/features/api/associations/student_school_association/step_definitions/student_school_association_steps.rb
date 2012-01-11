require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "'Apple Alternative Elementary School' ID"
  id = arg1+"7a86a6a7-1f80-4581-b037-4a9328b9b650" if arg2 == "'Gil' ID"
  id = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992" if arg2 == "'Alfonso' ID"
  id = arg1+"e0e99028-6360-4247-ae48-d3bb3ecb606a" if arg2 == "'Sybill' ID"
  id = arg1+"fdacc41b-8133-f12d-5d47-358e6c0c791c" if arg2 == "'Orange Middle School' ID"
  id = arg1+"e1af7127-743a-4437-ab15-5b0dacd1bde0" if arg2 == "'Priscilla' ID"
  id = arg1+"8cc0a1ac-ccb5-dffc-1d74-32964722179b" if arg2 == "'Purple Middle School' ID"
  id = arg1+"122a340e-e237-4766-98e3-4d2d67786572" if arg2 == "'Alfonso at Apple Alternative Elementary School' ID"
  id = arg1+"d84ed04c-d922-44d6-91a2-7395afc69748" if arg2 == "Priscilla at Orange Middle School ID"
  id = arg1+"4f3cb2fc-d2e6-4333-93c8-849928c49a6a" if arg2 == "Priscilla at Ellington Middle School ID"
  id = arg1+"ded6e1c6-146f-4f68-8a21-99446abab492" if arg2 == "Donna at Purple Middle School ID"
  id = arg1+"f7d86a4e-4d4a-49f6-9b8b-80973f1ae501" if arg2 == "Rachel at Purple Middle School ID"
  id = arg1+"11111111-1111-1111-1111-111111111111" if arg2 == "Invalid ID"
  id = arg1+@assocId                               if arg2 == "'newly created student school association' ID"
  id = arg1                                        if arg2 == "No GUID"
  id
end

Transform /^href ends with "([^"]*)<([^"]*)>"$/ do |arg1, arg2|
  uri = arg1+"122a340e-e237-4766-98e3-4d2d67786572" if arg2 == "Alfonso at Apple Alternative Elementary School ID"
  uri = arg1+"53ec02fe-570b-4f05-a351-f8206b6d552a" if arg2 == "Gil at Apple Alternative Elementary School ID"
  uri = arg1+"4ef1498f-5dfc-4604-83c3-95e81146b59a" if arg2 == "Sybill at Apple Alternative Elementary School ID"
  uri = arg1+"6de7d3b6-54d7-48f0-92ad-0914fe229016" if arg2 == "Alfonso at Yellow Middle School ID"
  uri = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992" if arg2 == "Alfonso's ID"
  uri = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "Apple Alternative Elementary School ID"
  uri = arg1+"" if arg2 == ""
  uri = arg1+arg2 if uri == nil
  uri
end


Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end


Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?(key), "Response does not contain key #{key}")
  assert(@data[key] == value, "Expected #{key} to equal #{value}, received #{@data[key]}")
end

Then /^I should receive a return code of (\d+)$/ do |code|
  assert(@res.code == Integer(code), "Return code was not expected: #{@res.code.to_s} but expected #{code}")
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



Then /^I should receive a collection of (\d+) student\-school\-associations that resolve to$/ do |size|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
  assert(@data.length == Integer(size), "Expected response of size #{size}, received #{@data.length}");
end

Then /^the collection should contain a link where rel is "([^"]*)" and (href ends with "[^"]*")$/ do |rel, href|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
  found = false
  @data.each do |ref|
    if ref["link"]["rel"] == rel && ref["link"]["href"] =~ /#{Regexp.escape(href)}$/
      found = true;
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end


When /^I navigate to GET "([^"]*<[^"]*>)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  if @format == "application/json"
    begin
      @data = JSON.parse(@res.body);
    rescue
      @data = nil
    end
  elsif @format == "application/xml"
    assert(false, "XML not supported yet")
  else
    assert(false, "Unsupported MediaType")
  end
end

When /^I navigate to DELETE "([^"]*<[^"]*>)"$/ do |arg1|
  restHttpDelete(arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end


Given /^I have access to all students and schools$/ do
  idpLogin(@user,@passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Then /^I should receive a ID for the newly created student\-school\-association$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @assocId = s[s.rindex('/')+1..-1]
  assert(@assocId != nil, "Student-School-Association ID is nil")
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
