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
  result = @new_id if id == "'newly created student-section-association' ID" or id == "the previous association"
  result = "11111111-1111-1111-1111-111111111111" if id == "'WrongURI' ID"
  result = "" if id == "'No GUID' ID"

  pre + result
end

#Givens

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

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

#Thens

Then /^I should receive a ID for the newly created .*$/ do
  headers = @res.raw_headers
  headers.should_not be_nil
  headers['location'].should_not be_nil
  s = headers['location'][0]
  @new_id = s[s.rindex('/')+1..-1]
  @new_id.should_not be_nil
end

Then /^I should receive a collection of (\d+) [^ ]* links$/ do |arg1|
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

Then /^I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>)"$/ do |rel, href|
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


