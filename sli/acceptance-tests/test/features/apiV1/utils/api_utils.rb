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
require File.expand_path("../../../utils/common", __FILE__)

Transform /^\/(<[^"]*>)$/ do |uri_placeholder|
  uri = "/v1.1/" + Transform(uri_placeholder)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2|
  uri = "/v1.1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/([^"<>]*)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2|
  uri = "/v1.1/" + uri_placeholder1 + "/" + Transform(uri_placeholder2)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3|
  uri = "/v1.1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3, uri_placeholder4|
  uri = "/v1.1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3) + "/" + Transform(uri_placeholder4)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3, uri_placeholder4, uri_placeholder5|
  uri = "/v1.1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3) + "/" + Transform(uri_placeholder4) + "/" + Transform(uri_placeholder5)
  #puts "URI = #{uri}"
  uri
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  @fields = {} if !defined? @fields
  @fields[key] = convert(value)
end

Given /^an invalid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = {}
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN 2WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I navigate to POST "([^"]*)"$/ do |post_uri|
  data = prepareData(@format, @fields)
  restHttpPost(post_uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I set the "([^"]*)" array to (.*)$/ do |property,value|
  @fields = {} if !defined? @fields
  if (value.is_a?(Array))
    step "\"#{property}\" is \"#{value}\""
  else
    @fields[property] = eval(value)
  end
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |property,value|
  step "\"#{property}\" is \"#{value}\""
end

When /^I navigate to PUT "([^<>"]*)"$/ do |url|
  @result = @fields if !defined? @result
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end


When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  @result = @fields if !defined? @result
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################
                                
Then /^the "([^\"]*)" should be "([^\"]*)"$/ do |arg1, arg2|
  @result = @res if !defined? @result
  if(arg1 == 'birthDate')
    assert(@result['birthData'][arg1] == arg2, "Expected data incorrect: Expected #{arg2} but got #{@result[arg1]}")
  else
    assert(@result[arg1].to_s == arg2, "Expected data incorrect: Expected #{arg2} but got #{@result[arg1]}")
  end
end
                                
                                
 Then /^the "name" should be "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |first_name, middle_name, last_name|
  @result = @res if !defined? @result
  assert(@result["name"] != nil, "Name is nil")
  
  expected_middle_name = ""
  expected_first_name = @result["name"]["firstName"]
  expected_middle_name = @result["name"]["middleName"] unless middle_name == ""
  expected_last_name = @result["name"]["lastSurname"]
  
  assert(expected_first_name == first_name, "Unexpected first name. Input: #{first_name} Expected: #{expected_first_name}")
  assert(expected_middle_name == middle_name, "Unexpected middle name. Input: #{middle_name} Expected: #{expected_middle_name}")
  assert(expected_last_name == last_name, "Unexpected last name. Input: #{last_name} Expected: #{expected_last_name}")
end
                                


Then /^I should receive a collection with (\d+) elements$/ do |count|;
  count = convert(count)
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Expected array of links, got #{@result}")
  @result.length.should == count
end

Then /^occurrence (\d+) of entity "([^"]*)" should be "([^"]*)"$/ do |position, key, value|
  assert(@result != nil, "Response contains no data")
  position = position.to_i - 1
  assert(@result[position].is_a?(Hash), "Response contains #{@result[position].class}, expected Hash")
  assert(@result[position].has_key?(key), "Response does not contain key #{key}")
  if @result[position][key].is_a?(Array)
    if value.is_a?(Array)
      assert(@result[position][key] == value, "Expected (array) #{key} to equal #{value}, received #{@result[position][key]}")
    else
      assert(@result[position][key] == eval(value), "Expected (array2) #{key} to equal #{value}, received #{@result[position][key]}")
    end
  else
    assert(@result[position][key] == convert(value), "Expected #{key} to equal #{value}, received #{@result[position][key]}")
  end
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  if @result[key].is_a?(Array)
    if value.is_a?(Array)
      assert(@result[key] == value, "Expected (array) #{key} to equal #{value}, received #{@result[key]}")
    else
      assert(@result[key] == eval(value), "Expected (array2) #{key} to equal #{value}, received #{@result[key]}")
    end
  else
    assert(@result[key] == convert(value), "Expected #{key} to equal #{value}, received #{@result[key]}")
  end
end

Then /^the response should contain the appropriate fields and values$/ do
  EntityProvider.verify_entities_match(@fields, @result)
end

Then /^the error message should indicate "([^"]*)"$/ do |error_message|
  if(@res.is_a?(String))
    assert(@res =~ /.*#{Regexp.escape(error_message)}.*$/, "Reference error not caught. Check association schema definition.")
  else
    assert(@res.is_a?(Hash), "Response contains #{@res.class}, expected Hash")
    assert(@res.has_key?('message'), "Server response does not contain 'message'")
    assert(@res['message'] =~ /#{Regexp.escape(error_message)}$/, "Reference error not caught. Check association schema definition.")
  end
end

Then /^each entity's "([^"]*)" should be "([^"]*)"$/ do |key, value|
   @result.each do |entity|
    assert(entity.has_key?(key), "Entity does not even contain key #{key}")
    assert(entity[key]==value, "Entity's value for key #{key} is not #{value} (was #{entity[key]})")
  end
end

Then /^each entity's "([^"]*)" should not be "([^"]*)"$/ do |key, value|
   @result.each do |entity|
    assert(entity.has_key?(key), "Entity does not even contain key #{key}")
    assert(entity[key] !=value, "Entity's value for key #{key} is not #{value} (was #{entity[key]})")
  end
end

Then /^each entity's "([^"]*)" should be in the array "([^"]*)"$/ do |key, value|
   @result.each do |entity|
    assert(entity.has_key?(key), "Entity does not even contain key #{key}")
    containsValue = false
    value.each do |valueSegment|
      if (valueSegment == entity[key])
        containsValue = true
      end
    end
    assert(containsValue, "Entity's value for key #{key} is not in array #{value} (was #{entity[key]})")
  end
end

Then /^each entity's "([^"]*)" should contain "([^"]*)"$/ do |key, value|
   @result.each do |entity|
    assert(entity.has_key?(key), "Entity does not even contain key #{key}")
    containsValue = false
    entity[key].each do |resultValue|
      if (resultValue == value)
        containsValue = true
      end
    end
    assert(containsValue, "Entity's value for key #{key} does not contain #{value} (was #{entity[key]})")
  end
end

Then /^in each entity, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  @result.each do |entity|
    foundInEntity = false
    entity["links"].each do |link|
      if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
        foundInEntity = true
      end
    end
    assert(foundInEntity, "A link labeled #{rel} with value #{href} was not present in one or more returned entities")
  end
end

Then /^I should find and store a link named "(.*?)"$/ do |rel|
  foundInEntity = false
  @result["links"].each do |link|
    if link["rel"] == rel
      @link = link["href"]
      foundInEntity = true
    end
  end
  assert(foundInEntity, "A link labeled #{rel} with value #{href} was not present in one or more returned entities")
end

Then /^in occurrence (\d+) I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |position, rel, href|
  position = position.to_i - 1
  foundInEntity = false
  @result[position]["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      foundInEntity = true
    end
  end
  assert(foundInEntity, "A link labeled #{rel} with value #{href} was not present in one or more returned entities")
end

Then /^in an entity, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  found = false
  @result.each do |entity|
    entity["links"].each do |link|
      if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
        found = true
      end
    end
  end
  assert(found, "A link labeled #{rel} with value #{href} was not present in one or more returned entities")
end

Then /^I should receive a collection of "([^"]*)" entities$/ do |number_of_entities|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(number_of_entities), "Expected response of size #{number_of_entities}, received #{@result.length}");
end

Then /^in each entity, I should receive a link named "([^"]*)" for a value from array "([^"]*)" with URI prefix "([^"]*)" and URI suffix "([^"]*)"$/ do |rel, list, prefix, suffix|
  @result.each do | entity|
    assert(entity.has_key?("links"), "Response contains no links")
    assert(list.is_a?(Array), "ID list is a #{list.class}, expected Array")

    found = false
  
    list.each do | id |
      new_link=prefix + "/" + id + "/" + suffix
      entity["links"].each do |link|
        if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(new_link)}$/
          found = true
        end
      end
    end
    assert(found, "Link not found rel=#{rel}")
  end
end

Then /^in each entity, I should receive a link named "([^"]*)" for a value from array "([^"]*)" with URI prefix "([^"]*)" and URI suffix "([^"]*)" and "([^"]*)"$/ do |rel, list, prefix, suffix1, suffix2|
  @result.each do | entity|
    assert(entity.has_key?("links"), "Response contains no links")
    assert(list.is_a?(Array), "ID list is a #{list.class}, expected Array")

    found = false
  
    list.each do | id |
      new_link=prefix + "/" + id + "/" + suffix1 + "/" + suffix2
      entity["links"].each do |link|
        if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(new_link)}$/
          found = true
        end
      end
    end
    assert(found, "Link not found rel=#{rel}")
  end
end

# Function data_builder
# Inputs: None
# Output: Data object in json or XML format depending on what the @format variable is set to
# Returns: Nothing, see Output
# Description: Helper function to create json or XML data structures to PUT or POST 
#                   to reduce replication of code
def data_builder
  if @format == "application/json"
  formatted_data = @fields.to_json
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
  formatted_data
end

Then /^the response should contain the "([^"]*)" field$/ do  |school_year|
    jsonresult = JSON.parse(@res.body)
    jsonresult.each do |data|
        assert(data["schoolYear"] == school_year, "School year matches")
    end
end


