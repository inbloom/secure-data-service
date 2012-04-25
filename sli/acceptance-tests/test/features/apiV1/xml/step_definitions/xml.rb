require 'rest-client'
require 'rexml/document'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'
include REXML

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "706ee3be-0dae-4e98-9525-f564e05aa388" if template == "LINDA KIM SECTION ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if template == "MARVIN MILLER STUDENT ID"
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^optional field "([^\"]*)"$/ do |field|
  if !defined? @queryParams
    @queryParams = [ "optionalFields=#{field}" ]
  else
    @fields = @queryParams[0].split("=")[1];
    @fields = @fields + ",#{field}"
    @queryParams[0] = "optionalFields=#{@fields}"
  end
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive an XML document$/ do
  assert(contentType(@res).match "application/xml")
end

Then /^I should see "([^\"]*)" is "([^\"]*)"$/ do |key, value|
  if !defined? @node
    @node = @result.elements[1]
  end
  assert(@node.elements["#{key}"] != nil, "Cannot find element #{key}")
  assert(@node.elements["#{key}"].text == value, "Value does not match")
end

Then /^I should see each entity's "([^\"]*)" is "([^\"]*)"$/ do |key, value|
  @result.elements.each do |element|
    ele = element.elements["#{key}"]
    assert(ele != nil, "Cannot find element #{key}")
    assert(ele.text == value, "Value does not match")
  end
end

Then /^I should receive ([\d]*) entities$/ do |count|
  assert(@result.elements.size == convert(count), "Expected #{count}, received #{@result.elements.size}")
end

Then /^I should find "([^"]*)" under "([^"]*)"$/ do |key, arg|
  path = arg.split("><").join("/")
  assert(@result.elements["#{path}/#{key}"] != nil, "Cannot find #{key} under #{path}")
  @node = @result.elements["#{path}/#{key}"]
end

Then /^I should find ([\d]*) "([^"]*)" under "([^"]*)"$/ do |count, key, arg|
  path = arg.split("><").join("/")
  assert(@result.elements["#{path}"].get_elements("#{key}") != nil, "Cannot find #{key} under #{path}")
  assert(@result.elements["#{path}"].get_elements("#{key}").size == convert(count), "Expected #{count}, received #{@result.elements["#{path}"].get_elements("#{key}").size}")
  @node = @result.elements["#{path}"].get_elements("#{key}")
end

Then /^I should see "([^"]*)" is "([^"]*)" for the one at position (\d+)$/ do |key, value, pos|
  assert(@node[convert(pos)-1].elements["#{key}"] != nil,  "Cannot find element #{key}")
  assert(@node[convert(pos)-1].elements["#{key}"].text == value, "Value does not match")
end

Then /^I should find (\d+) entries with "([^"]*)" including the string "([^"]*)"$/ do |count, key, value|
  cnt = 0
  @node.each do |element|
    if (element.elements["#{key}"].text.include? value)
      cnt = cnt + 1
    end
  end
  assert(cnt == convert(count), "Received #{cnt}, expected #{count}")
end