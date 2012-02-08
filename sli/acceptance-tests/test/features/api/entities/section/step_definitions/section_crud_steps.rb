require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = template
  id = @newId.to_s if template == "<'newly created section' ID>"
  id = "1e1cdb04-2094-46b7-8140-e3e481013480" if template == "<'chemistryF11' ID>"
  if template == "<'biologyF09' ID>"
    if @format == "application/json" or @format == "application/vnd.slc+json"
      id = "2934f72d-f9e3-48fd-afdd-56b94e2a3454" # biologyF09J
    elsif @format == "application/xml" or @format == "application/vnd.slc+xml"
      id = "c2efa2b3-f0c6-472a-b0d3-2e7495554acc" # biologyF09X
    end
  end
  id = "11111111-1111-1111-1111-111111111111" if template == "<'Invalid' ID>"
  id = "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431" if template == "<'physicsS08' ID>"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2" if template == "<'APPLE ELEMENTARY (SCHOOL)' ID>"
  id = "bbea7ac0-a016-4ece-bb1b-47b1fc251d56" if template == "<'SPRING 2011 (SESSION)' ID>"
  id = "53777181-3519-4111-9210-529350429899" if template == "<'FRENCH 1 (COURSE)' ID>"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end


### GIVEN ###


Given /^the "([^\"]+)" is "([^\"]+)"$/ do |key, value|
  if !defined? @data
    @data = {}
  end
  value = convert(value)
  @data[key] = value
end

### WHEN ###

When /^I navigate to POST "([^\"]+)"$/ do |url|
  data = prepareData(@format, @data)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end


When /^I navigate to PUT "([^\"]*)"$/ do |url|
  if !defined? @result
    @result = {}
  end
  data = prepareData(@format, @result)
  restHttpPut(url, data)
end


When /^I set the "([^\"]*)" to "([^\"]*)"$/ do |key, value|
  value = convert(value)
  @result[key] = value
end

### THEN ###


Then /^the "([^\"]*)" should be "([^\"]*)"$/ do |key, value|
  value = convert(value)
  @result[key].should_not == nil
  @result[key].should == value
end


