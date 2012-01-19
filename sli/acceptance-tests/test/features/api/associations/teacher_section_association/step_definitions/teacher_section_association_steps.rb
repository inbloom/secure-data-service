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
  result = @newId                                 if arg == "'newly created teacher-section-association' ID"
  pre + result
end




Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key].to_s == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
end

When /^I navigate to POST "([^"]*)"$/ do |url|
  data = prepareData(@format, @fields)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end



Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end


Then /^I should receive a collection of (\d+) teacher\-section\-association links$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    counter=0
    @ids = Array.new
    @result.each do|link|
      if link["link"]["rel"]=="self"
        counter=counter+1
        @ids.push(link["id"])
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

When /^I navigate to PUT "(\/[^\/]*\/[^\/]*)"$/ do |url|
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end


