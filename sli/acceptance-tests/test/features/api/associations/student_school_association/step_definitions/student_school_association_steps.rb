require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "'Apple Alternative Elementary School' ID"
  id = arg1+"2058ddfb-b5c6-70c4-3bee-b43e9e93307d" if arg2 == "'Yellow Middle School' ID"
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
  id = arg1+"11111111-1111-1111-1111-111111111111" if arg2 == "'Invalid ID'"
  id = arg1+@newId                                 if arg2 == "'newly created student school association' ID"
  id = arg1                                        if arg2 == "'No GUID'"
  id
end



Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

When /^I set "([^"]*)" to "([^"]*)"$/ do |arg1, arg2|
  step "\"#{arg1}\" is \"#{arg2}\""
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key] == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
end

Then /^I should receive a collection of (\d+) student\-school\-association links$/ do |size|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(size), "Expected response of size #{size}, received #{@result.length}");
  
  @ids = Array.new
    @result.each do |link|
      if link["link"]["rel"]=="self"
        @ids.push(link["id"])
      end
    end
end

Then /^after resolving each link, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  @ids.each do |id|
    found=false
    if findLink(id,"/student-school-associations/", rel,href)
      found = true
    end
    assert(found, "didnt receive link named #{rel} with URI #{href}")
  end
  
end

Then /^after resolution, I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  found = false
  @ids.each do |id|
    if findLink(id,"/student-school-associations/", rel,href)
      found = true
      break
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
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
