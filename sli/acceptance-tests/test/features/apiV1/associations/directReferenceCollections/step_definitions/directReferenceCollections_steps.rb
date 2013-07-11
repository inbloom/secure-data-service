=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'
require_relative '../../../../security/step_definitions/securityevent_util_steps.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  # values to support the test for whether or not to display links
  id = "schools"                                if human_readable_id == "URI FOR ENTITY THAT CAN RETURN LINKS"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"   if human_readable_id == "ID OF ENTITY THAT CAN RETURN LINKS"

  #values to support direct entity reference tests
  id = @referring_collection_expose_name        if human_readable_id == "REFERRING COLLECTION URI"
  id = @testing_id                              if human_readable_id == "REFERRING ENTITY ID"
  id = @referred_collection_expose_name         if human_readable_id == "URI OF REFERENCED ENTITY"
  id = @reference_value                         if human_readable_id == "REFERRED ENTITY ID"
  id = @referring_field                         if human_readable_id == "REFERENCE FIELD"
  id = @new_valid_value                         if human_readable_id == "NEW VALID VALUE"



  #query URI
  id = @referring_collection_expose_name + "?" + @referring_field + "=" + @reference_value                    if human_readable_id == "URI OF ENTITIES THAT REFER TO TARGET"









  #general
  id = ["11111111-1111-1111-9999-111111111111"] if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME"
  id = @newId                                   if human_readable_id == "NEWLY CREATED ASSOCIATION ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"

  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^referring collection "([^"]*)" exposed as "([^"]*)"$/ do |referring_collection, referring_collection_expose_name|
  @referring_collection = referring_collection
  @referring_collection_expose_name = referring_collection_expose_name
end

Given /^referring field "([^"]*)" with value "([^"]*)"$/ do |referring_field, reference_value|
  @referring_field = referring_field
  @reference_value = reference_value
end

Given /^referring field "([^"]*)" with value \["([^"]*)"\]$/ do |referring_field, reference_value|
  @referring_field = referring_field
  @reference_value = reference_value
end

Given /^referred collection "([^"]*)" exposed as "([^"]*)"$/ do |referred_collection, referred_collection_expose_name|
  @referred_collection = referred_collection
  @referred_collection_expose_name = referred_collection_expose_name
end

Given /^the link from references to referred entity is "([^"]*)"$/ do |link_from_references|
  @link_from_references = link_from_references
end

Given /^the link from referred entity to referring entities is "([^"]*)"$/ do |link_from_referred|
  @link_from_referred = link_from_referred
end

Given /^the ID to use for testing is "([^"]*)"$/ do |testing_id|
  @testing_id = testing_id
end

Given /^the ID to use for testing a valid update is "([^"]*)"$/ do |new_valid_value|
  @new_valid_value = new_valid_value
end

Given /^the ID to use for testing a valid update is (\[.*\])$/ do |new_valid_value|
  @new_valid_value = new_valid_value
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I request "([^"]*)"$/ do |links|
  @should_get_links = (links=="links")
end

# Special collection property setter
When /^I set the list "([^"]*)" to (\[.*\])$/ do |property,value|
  @fields = {} if @fields == nil
  @fields[property] = eval(value)
end

# Default property setter (used for invalid references only?)
When /^I set the list "([^"]*)" to "([^"].*)"$/ do |property, value|
  @fields = {} if @fields == nil
  @fields[property] = value
end

When /^I navigate to GET the first value in list "([^"]*)" with URI prefix "([^"]*)"$/ do |list, prefix|
  list = eval(list)
  assert(list.is_a?(Array), "ID list is a #{list.class}, expected Array")
  assert(list.length > 0, "ID list length is 0, expected positive value")
  id = list.first
  uri = prefix + "/" + id

  if defined? @queryParams
    uri = uri + "?#{@queryParams.join('&')}"
  end
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  contentType = contentType(@res).gsub(/\s+/,"")
  jsonTypes = ["application/json;charset=utf-8", "application/vnd.slc.full+json;charset=utf-8", "application/vnd.slc+json;charset=utf-8"].to_set
  if jsonTypes.include? contentType
    @result = JSON.parse(@res.body)
    assert(@result != nil, "Result of JSON parsing is nil")
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
    puts @result
  else
    @result = {}
  end
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^the response should contain links if I requested them$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  has_links = @result.has_key?('links')
  if (@should_get_links)
    assert(has_links==true, "Links were requested in response, but they were not included.")
  else
    assert(has_links==false, "Links were not requested in response, but they were included.")
  end
end

Then /^list "([^"]*)" should be (\[.*\])$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key].is_a?(Array), "Response key is a #{@result[key].class}, expected Array")
  assert(@result[key].to_s() == value, "Expected #{key} to equal #{value}, received #{@result[key]}")

end

Then /^I should receive a link named "([^"]*)" for each value in list "([^"]*)" with URI prefix "([^"]*)"$/ do |rel, list, prefix|
  assert(@result.has_key?("links"), "Response contains no links")
  list = eval(list)
  assert(list.is_a?(Array), "ID list is a #{list.class}, expected Array")

  list.each do | id |
    new_link=prefix + "/" + id
    found = false
    @result["links"].each do |link|
      if (link["rel"].include? rel) && link["href"] =~ /#{Regexp.escape(new_link)}$/
        found = true
      end
    end
    assert(found, "Link not found rel=#{rel}, href ends with=#{new_link}")
  end
end

Then /^"([^"]*)" should be the first value in list "([^"]*)"$/ do |key, list|
  list = eval(list)
  assert(list.is_a?(Array), "ID list is a #{list.class}, expected Array")
  assert(list.length > 0, "ID list length is 0, expected positive value")
  value = list.first
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")

  if @result[key].is_a?(Array)
    assert(@result[key] == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
  else
    assert(@result[key] == convert(value), "Expected #{key} to equal #{value}, received #{@result[key]}")
  end
end
