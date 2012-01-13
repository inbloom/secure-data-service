require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /(.*)<([^>]*)>$/ do |pre, arg|
  result = "Not found: "+arg
  result = "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94" if arg == "'Ms. Jones' ID"
  result = "e24b24aa-2556-994b-d1ed-6e6f71d1be97" if arg == "'Ms. Smith' ID"
  result = "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" if arg == "'Algebra II' ID"
  result = "1e1cdb04-2094-46b7-8140-e3e481013480" if arg == "'Chem I' ID"
  result = "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431" if arg == "'Physics II' ID"
  result = "4efb4262-bc49-f388-0000-0000c9355700" if arg == "'Biology III' ID"
  result = "12f25c0f-75d7-4e45-8f36-af1bcc342871" if arg == "'Teacher Ms. Jones and Section Algebra II' ID"
  result = "8d180486-3f3e-49c6-9f80-a3481e225151" if arg == "'Teacher Ms. Smith and Section Chem I' ID"
  result = "da3eff9f-d26e-49f6-8b8b-1af59241c91a" if arg == "'Teacher Ms. Smith and Section Physics II' ID"
  result = @newSectionId if arg == "'newly created teacher-section-association' ID"
  pre + result
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |user, passwd|
  idpLogin(user,passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

Then /^I should receive a return code of (\d+)$/ do |code|
  assert(@res.code == Integer(code), "Return code was not expected: #{@res.code.to_s} but expected #{code}\nbody was #{@res}")
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

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?(key), "Response does not contain key #{key}")
  assert(@data[key].to_s == value, "Expected #{key} to equal #{value}, received #{@data[key]}")
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

When /^I navigate to GET "([^"]*)"$/ do |uri|
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

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

Then /^I should receive a ID for the newly created teacher\-section\-association$/ do
  headers = @res.raw_headers
  headers.should_not be_nil
  headers['location'].should_not be_nil
  s = headers['location'][0]
  @newSectionId = s[s.rindex('/')+1..-1]
  @newSectionId.should_not be_nil
end

When /^I navigate to Teacher Section Associations for Teacher "([^"]*)" and Section "([^"]*)"$/ do |teacherarg, sectionarg| 
  id = "None"
  id = "12f25c0f-75d7-4e45-8f36-af1bcc342871" if teacherarg == "Ms. Jones" and sectionarg == "Algebra II"
  id = "da3eff9f-d26e-49f6-8b8b-1af59241c91a" if teacherarg == "Ms. Smith" and sectionarg == "Physics II"
  step "I navigate to GET \"/teacher-section-associations/#{id}\""
end

When /^I navigate to Teacher Section Associations for the Teacher "Ms. Jones"$/ do 
  step "I navigate to GET \"/teacher-section-associations/eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94\""
end

Then /^I should receive a collection of (\d+) teacher\-section\-association links$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    @collectionLinks = []
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self"
        counter=counter+1
        @ids.push(link["id"])
      # puts @ids
      end
    end
    assert(counter==Integer(arg1), "Expected response of size #{arg1}, received #{counter}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^after resolution, I should receive a link named "([^"]*)" with URI "(\/[^\/]*\/<[^>]*>)"$/ do |rel,href|
  found =false
  @ids.each do |id|
    uri = "/teacher-section-associations/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      dataH["links"].each do|link|
        if link["rel"]==rel and link["href"].include? href
        found =true
        end
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

Then /^after resolving each link, I should receive a link named "([^"]*)" with URI "(\/[^\/]*\/<[^>]*>)"$/ do |rel,href|
  @ids.each do |id|
    uri = "/teacher-section-associations/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      dataH["links"].each do|link|
        if link["rel"] == rel
          assert((link["href"].include? href), "didnt receive link named #{rel} with URI #{href}")
        end
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
end

When /^I set "([^"]*)" to "([^"]*)"$/ do |key, value|
  step "\"#{key}\" is \"#{value}\""
end

When /^I navigate to PUT "(\/[^\/]*\/[^\/]*)"$/ do |uri|
  @data.update(@fields)
  restHttpPut(uri, @data.to_json)
end

When /^I navigate to DELETE "(\/[^\/]*\/[^\/]*)"$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

