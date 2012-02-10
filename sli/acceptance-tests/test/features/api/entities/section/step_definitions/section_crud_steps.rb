require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = template
  id = @newId.to_s if template == "<'NEWLY CREATED SECTION' ID>"
  id = "11111111-1111-1111-1111-111111111111" if template == "<'Invalid' ID>"
  id = "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431" if template == "<'physicsS08' ID>"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2" if template == "<'APPLE ELEMENTARY (SCHOOL)' ID>"
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e" if template == "<'FALL 2011 (SESSION)' ID>"
  id = "53777181-3519-4111-9210-529350429899" if template == "<'FRENCH 1 (COURSE)' ID>"
  id = "67ce204b-9999-4a11-aacc-000000000003" if template == "<'RUSSIAN 1 (COURSE)' ID>"
  puts "#{template}->#{id}"
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


