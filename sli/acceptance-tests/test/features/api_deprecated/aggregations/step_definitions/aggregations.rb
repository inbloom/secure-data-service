require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'

Given /^mock district ID (<[^"]*>)$/ do |mock_id|
  @mock_id = mock_id
end

Transform /^<([^"]*)>$/ do |step_arg|
  id = "4f0c9368-8488-7b01-0000-000059f9ba56"  if step_arg == "mock ID"
  id = "/home"								   if step_arg == "home URI"
  id = "/aggregation"                          if step_arg == "aggregation URI"
  id = "/aggregation/district/4f0c9368-8488-7b01-0000-000059f9ba56" if step_arg == "district URI"
  id
end

Then /^I should receive a collection of ([^"]*) links$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    @collectionLinks = []
    @ids = Array.new
    dataH["links"].each do|link|
      if link["rel"]=="links"
        url = link["href"]  
        s = url[url[/.*?\/\/[^\/]*\//].size..-1]
        s = s.gsub(/api\/rest/, "")
        @ids.push(s)
      # puts @ids
      end
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should receive a link where rel is "([^"]*)" and href ends with "([^"]*)"$/ do |rel, href|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
   @result["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

Then /^I should receive a link where rel is "([^"]*)" and href ends with "([^"]*)" and appropriate ID$/ do |rel, href|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
   @result["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href+@mock_id)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

Then /^after resolution, I should receive an object with a link named "([^"]*)" with URI "([^"]*)" and appropriate ID and with district ID "([^"]*)"$/ do |rel, href, distId|
  found =false
  distFound = false
  @ids.each do |id|
    uri = id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      if dataH["groupBy"]["districtId"]==distId
      	distFound = true
      end
      dataH["links"].each do |link|
        if link["rel"]==rel && link["href"] =~ /#{Regexp.escape(id)}$/
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
  assert(distFound, "aggregation is not grouped by #{distId}")
end