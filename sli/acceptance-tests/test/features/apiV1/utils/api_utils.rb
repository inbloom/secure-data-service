require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../utils/sli_utils.rb'

Transform /^\/(<[^"]*>)$/ do |uri_placeholder|
  uri = "/v1/" + Transform(uri_placeholder)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2|
  uri = "/v1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3|
  uri = "/v1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3)
  #puts "URI = #{uri}"
  uri
end

Transform /^\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)\/(<[^"]*>)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3, uri_placeholder4|
  uri = "/v1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3) + "/" + Transform(uri_placeholder4)
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

When /^I set the "([^"]*)" to "([^"]*)"$/ do |property,value|
  step "\"#{property}\" is \"#{value}\""
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

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?(key), "Response does not contain key #{key}")
  assert(@result[key] == convert(value), "Expected #{key} to equal #{value}, received #{@result[key]}")
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



