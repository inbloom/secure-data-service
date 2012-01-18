require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /(.*)<([^>]*)>$/ do |pre, id|
  result = "Not found: #{id}"
  result = "4efb3a11-bc49-f388-0000-0000c93556fb" if id == "'Jane Doe' ID"
  result = "4efb3a5e-bc49-f388-0000-0000c93556fc" if id == "'Albert Wright' ID"
  result = "4efb3a7f-bc49-f388-0000-0000c93556fd" if id == "'Kevin Smith' ID"
  result = "4efb4262-bc49-f388-0000-0000c9355700" if id == "'Biology II - C' ID"
  result = "4efb4292-bc49-f388-0000-0000c9355701" if id == "'Foreign Language - A' ID"
  result = "4efb4243-bc49-f388-0000-0000c93556ff" if id == "'Physics I - B' ID"
  result = "4efb4238-bc49-f388-0000-0000c93556fe" if id == "'Chemistry I - A' ID"
  result = "4efb7614-8488-7b01-0000-000059f9ba55" if id == "'Student Albert Wright and Section Foreign Language - A' ID"
  result = @newId                                 if id == "'newly created student-section-association' ID"
  result = "11111111-1111-1111-1111-111111111111" if id == "'WrongURI' ID"
  result = "" if id == "'No GUID' ID"

  pre + result
end

#Givens

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

#Whens
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


#Thens


Then /^I should receive a collection of (\d+) [^ ]* links$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    counter=0
    @ids = Array.new
    @result.each do|link|
      if link["link"]["rel"]=="self"
        counter += 1
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



Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key].to_s == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
end

When /^I set "([^"]*)" to "([^"]*)"$/ do |key, value|
  step "\"#{key}\" is \"#{value}\""
end

When /^I navigate to PUT "(\/[^\/]*\/[^\/]*)"$/ do |uri|
  @result.update(@fields)
  restHttpPut(uri, @result.to_json)
end


Then /^after resolution, I should receive a link named "([^"]*)" with URI "(\/[^\/]*\/<[^>]*>)"$/ do |rel,href|
  found =false
  @ids.each do |id|
    uri = "/student-section-associations/"+id
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
    uri = "/student-section-associations/"+id
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


