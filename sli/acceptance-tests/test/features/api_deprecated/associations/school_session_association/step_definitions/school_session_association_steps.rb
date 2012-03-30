require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  #schools
  id = "41baa245-ceea-4336-a9dd-0ba868526b9b"  if human_readable_id == "'Algebra Alternative' ID"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"  if human_readable_id == "'Biology High' ID"
  id = "b6ad1eb2-3cf7-41c4-96e7-2f393f0dd847"  if human_readable_id == "'Chemistry Elementary' ID"
  #sessions
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e"  if human_readable_id == "'Fall 2011 Session' ID"
  id = "cb1fb2d3-a906-446a-bdfd-06ad23823265"  if human_readable_id == "'Spring 2011 Session' ID"
  id = "31e8e04f-5b1a-4631-91b3-a5433a735d3b"  if human_readable_id == "'Spring 2010 Session' ID"
  id = "f5f042f8-9617-4a7b-bcee-5ff157240594"  if human_readable_id == "'Spring 2009 Session' ID"
  #school-session-associations
  id = "6b622b30-374c-4e0e-845f-74fb28d365eb"  if human_readable_id == "'Spring 2011 Session at Algebra Alternative' ID"
  id = "c5261b72-44fc-4f28-b532-6e969ae2b80a"  if human_readable_id == "'Spring 2011 Session at Biology High' ID"
  id = "99ff1be3-2f61-4679-8013-37164a98666c"  if human_readable_id == "'Spring 2010 Session at Biology High' ID"
  id = "2edcc808-c202-4f70-93e5-77f1c42e6446"  if human_readable_id == "'Spring 2009 Session at Biology High' ID"
  id = "eabddaed-6e7f-49ad-ae28-176ff0bbf38c"  if human_readable_id == "'Spring 2011 Session at Chemistry Elementary' ID"
  #general
  id = "11111111-1111-1111-1111-111111111111"  if human_readable_id == "'Invalid ID'"
  id = @newId                                  if human_readable_id == "'newly created school-session-association' ID"
  id
end

Transform /^([^"]*\/)(<[^"]*>)$/ do |path_prefix, raw_id|
    path = path_prefix + Transform(raw_id)
    path
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^grading period "([^"]*)" is "([^"]*)"$/ do |key, value|
  @gradingPeriod = {} if !defined? @gradingPeriod
  @gradingPeriod[key] = value
  @fields = {} if !defined? @fields
  @fields['gradingPeriod'] = @gradingPeriod
  
end

Given /^grading period "([^"]*)" is (\d+)$/ do |key, value|
  @gradingPeriod = {} if !defined? @gradingPeriod
  value = convert(value)
  @gradingPeriod[key] = value
  @fields = {} if !defined? @fields
  @fields['gradingPeriod'] = @gradingPeriod
  
end

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  @fields = {} if !defined? @fields
  
  if key == "instructionalGradeLevels"
    @fields[key] = [value]
  else
    @fields[key] = value
  end
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
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end


###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^grading period "([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?('gradingPeriod'), "Response does not contain gradingPeriod")
  assert(@result['gradingPeriod'][key] == value, "Grading period #{key} expected value #{value} != #{@result['gradingPeriod'][key]}")
end

Then /^grading period "([^"]*)" should be (\d+)$/ do |key, value|
  value = convert(value)
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?('gradingPeriod'), "Response does not contain gradingPeriod")
  assert(@result['gradingPeriod'][key] == value, "Grading period #{key} expected value #{value} != #{@result['gradingPeriod'][key]}")
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  if key == "instructionalGradeLevels"
    assert(@result.has_key?(key), "Response does not contain key #{key}")
    assert(@result[key][0] == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
  else
    assert(@result.has_key?(key), "Response does not contain key #{key}")
    assert(@result[key] == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
  end
  
end


Then /^I should receive a collection of (\d+) ([^"]*) links$/ do |number_of_links, link_type|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(number_of_links), "Expected response of size #{number_of_links}, received #{@result.length}");
  
  @ids = Array.new
    @result.each do |link|
      if link["link"]["rel"]=="self"
        @ids.push(link["id"])
      end
    end
end

Then /^after resolving each link, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  @ids.each do |id|
    found=false
    if findLink(id,"/school-session-associations/", rel,href)
      found = true
    end
    assert(found, "didnt receive link named #{rel} with URI #{href}")
  end
  
end

Then /^after resolution, I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  found = false
  @ids.each do |id|
    if findLink(id,"/school-session-associations/", rel,href)
      found = true
      break
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

When /^I attempt to update a non\-existing association "(\/school-session-associations\/<[^"]*>)"$/ do |uri|
  data = {}
  restHttpPut(uri, data.to_json)
  assert(@res != nil, "Response from rest-client PUT is nil")
end


# 
# Function data_builder
# Inputs: object_type - used only for error message when ID cannot be found
# Output: None
# Returns: ID of newly created object
# Description: Searches headers from an HTTP response for the newly created object's ID and returns it
#
def getIdOfNewlyCreatedObject (object_type="Object")
  headers = @res.raw_headers
  headers.should_not be_nil
  headers['location'].should_not be_nil
  s = headers['location'][0]
  newId = s[s.rindex('/')+1..-1]
  assert(newId != nil, "#{object_type} ID is nil")
  newId
end

# function: validateValueInResults
# inputs:   value: value to check for
#           property: what name to search under for the value
#           description: how to describe the value not found if/when value is not found
# outputs:  true/false
# Returns:  Nothing, see output
# Description: Helper function for all the tests that need to check the presence of a value
#               in an array that is in a result body
def validateValueInResults(value,property,description)
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    assert(result[property]==value, "Expected #{description} not found in response")
end

# function: validateValueInResultArray
# inputs:   value: value to check for
#           property: what name to search under for the value
#           description: how to describe the value not found if/when value is not found
# outputs:  true/false
# Returns:  Nothing, see output
# Description: Helper function for all the tests that need to check the presence of a value
#               in an array that is in a result body
def validateValueInResultArray(value,property,description)
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    arrayOfResults = result[property]
    valueFound = false
    arrayOfResults.each do |valueFromArray|
        if(value==valueFromArray)
            valueFound = (true)
        end
    end
    assert(valueFound, "Expected #{description} not found in response")
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
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent => 2)
    formatted_data = builder.section { |b| 
      b.teacherId(@fields["teacherId"])
      b.schoolId(@fields["schoolId"])  
      }
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
  formatted_data
end







